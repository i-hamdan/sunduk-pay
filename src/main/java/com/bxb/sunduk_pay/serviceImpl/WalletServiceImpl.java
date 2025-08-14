package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapperImpl;
import com.bxb.sunduk_pay.event.TransactionEvent;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.factoryPattern.WalletOperation;
import com.bxb.sunduk_pay.factoryPattern.WalletOperationFactory;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final MasterWalletRepository masterWalletRepository;
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
private final WalletOperationFactory walletOperationFactory;
    private final Validations validations;

    public WalletServiceImpl(UserRepository userRepository, MasterWalletRepository masterWalletRepository, MainWalletRepository mainWalletRepository, TransactionRepository transactionRepository, WalletMapperImpl walletMapper, TransactionMapper transactionMapper, KafkaTemplate<String, TransactionEvent> kafkaTemplate, WalletOperationFactory walletOperationFactory, Validations validations) {
        this.userRepository = userRepository;
        this.masterWalletRepository = masterWalletRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.walletMapper = walletMapper;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.walletOperationFactory = walletOperationFactory;
        this.validations = validations;
    }


    @Override
    public MainWalletResponse payMoney(MainWalletRequest request) {

        MasterWallet masterWallet=validations.getMasterWalletInfo(request.getUuid());
        MainWallet mainWallet=validations.getMainWalletInfo(request.getUuid());
        SubWallet SourcesubWallet = validations.findSubWalletIfExists(mainWallet, request.getSourceWalletId());

        Double previousSourceWalletBalance;
        if (SourcesubWallet!=null){
            previousSourceWalletBalance= SourcesubWallet.getBalance();
        }else {
            previousSourceWalletBalance=null;
        }


        // deduct balance from master wallet
        masterWallet.setBalance(masterWallet.getBalance()- request.getAmount());
        masterWalletRepository.save(masterWallet);
        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn= Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .transactionLevel(TransactionLevel.EXTERNAL)
                .description("Deducted from master wallet")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .build();
            transactions.add(masterWalletTxn);

            Transaction debitTxn;
        if (SourcesubWallet!=null){
            SourcesubWallet.setBalance(SourcesubWallet.getBalance()-request.getAmount());
           debitTxn=Transaction.builder().transactionId(UUID.randomUUID().toString())
                   .amount(request.getAmount())
                   .transactionType(request.getTransactionType())
                   .transactionLevel(TransactionLevel.EXTERNAL)
                   .description("deducted from sub wallet" + SourcesubWallet.getSubWalletName())
                   .dateTime(LocalDateTime.now())
                   .mainWallet(mainWallet)
                   .build();
                    transactions.add(debitTxn);
        }
        else {
            mainWallet.setBalance(mainWallet.getBalance() - request.getAmount());
            debitTxn=Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(request.getTransactionType())
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("deducted from main wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .build();
        transactions.add(debitTxn);
        }

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(debitTxn.getTransactionId())
                .previousSourceWalletBalance(previousSourceWalletBalance)
                .newSourceWalletBalance(mainWallet.getBalance())
                .message("Transfer Successful")
                .build();
        log.info(response);
        return response;
    }



    @Override
    public MainWalletResponse addMoney(MainWalletRequest mainWalletRequest) {
        User user = validations.getUserInfo(mainWalletRequest.getUuid());

        MasterWallet masterWallet = validations.getMasterWalletInfo(mainWalletRequest.getUuid());


        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());

        SubWallet targetSubWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getTargetWalletId());

        Double previousSourceWalletBalance;
        if (targetSubWallet!=null){
            previousSourceWalletBalance= targetSubWallet.getBalance();
        }else {
            previousSourceWalletBalance=null;
        }

        // adding amount on master wallet
        masterWallet.setBalance(masterWallet.getBalance() + mainWalletRequest.getAmount());
        masterWalletRepository.save(masterWallet);
        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn= Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(mainWalletRequest.getAmount())
                .transactionType(mainWalletRequest.getTransactionType())
                .transactionLevel(TransactionLevel.EXTERNAL)
                .description("amount added successfully")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .build();
            transactions.add(masterWalletTxn);

            Transaction creditTxn;

            // adding amount on main wallet

            mainWallet.setBalance(mainWallet.getBalance()+mainWalletRequest.getAmount());
            creditTxn=Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(mainWalletRequest.getTransactionType())
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("Added amount to main wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .build();
            transactions.add(creditTxn);

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(creditTxn.getTransactionId())
                .previousSourceWalletBalance(previousSourceWalletBalance)
                .newSourceWalletBalance(mainWallet.getBalance())
                .message("amount recived successfull")
                .build();
     
        return response;

    }


























    @Override
    public List<TransactionResponse> getAllTransactions(String uuid, String walletId) {
        log.info("Fetching all transactions for userId: {} and walletId: {}", uuid, walletId);

        List<Transaction> allTransactionsByWalletId = transactionRepository
                .findByMainWallet_mainWalletIdAndUser_Uuid(walletId, uuid);

        log.info("Found {} transactions for walletId: {}", allTransactionsByWalletId.size(), walletId);

        List<TransactionResponse> responseList = transactionMapper.toTransactionsResponse(allTransactionsByWalletId);

        log.debug("Mapped transactions to response DTOs: {}", responseList);

        return responseList;

    }

    @Override
    public MainWalletResponse walletCrud(MainWalletRequest mainWalletRequest) {
            WalletOperation walletService = walletOperationFactory.getWalletService(mainWalletRequest.getRequestType());
            return walletService.perform(mainWalletRequest);
        }



    //This will simply return the current balance of a wallet.
    public String showBalance(String walletId) {
        log.info("Fetching balance for walletId: {}", walletId);

        MainWallet wallet = mainWalletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Invalid wallet ID: {}", walletId);
                    return new WalletNotFoundException("Wallet Id is not valid!");
                });

        if (wallet.getIsDeleted()) {
            log.error("Wallet with ID {} is deleted.", walletId);
            throw new WalletNotFoundException("This wallet has been already deleted! Cannot retrieve info.");
        }

        String balanceMsg = "Current balance in wallet " + wallet.getMainWalletId() + " is " + wallet.getBalance() + ".";
        log.info(balanceMsg);
        return balanceMsg;
    }


    //method for downloading transactions in Excel file format
    @Override
    public void downloadTransactions(String walletId, HttpServletResponse response) throws IOException {
        log.info("Starting to download transactions for walletId: {}", walletId);

        MainWallet wallet = mainWalletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Wallet not found with ID: {}", walletId);
                    return new WalletNotFoundException("invalid wallet id");
                });

        if (wallet.getIsDeleted()) {
            log.error("Wallet is deleted. Cannot download transactions for walletId: {}", walletId);
            throw new WalletNotFoundException("The wallet has been already deleted! Cannot download transactions.");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

        log.info("Generating Excel sheet for walletId: {}", walletId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Transactions");

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("S.No");
        row.createCell(1).setCellValue("Type");
        row.createCell(2).setCellValue("Amount");
        row.createCell(3).setCellValue("Description");
        row.createCell(4).setCellValue("Date&Time");

        int rowNum = 1;
        int count = 1;

        List<Transaction> list = transactionRepository.findByMainWallet_mainWalletIdAndUser_Uuid(walletId,wallet.getUser().getUuid());
        log.info("Writing {} transactions into Excel for walletId: {}", list.size(), walletId);

        for (Transaction transaction : list) {
            XSSFRow row1 = sheet.createRow(rowNum++);
            row1.createCell(0).setCellValue(count++);
            row1.createCell(1).setCellValue(transaction.getTransactionType().toString());
            row1.createCell(2).setCellValue(transaction.getAmount());
            row1.createCell(3).setCellValue(transaction.getDescription());

            Cell dateCell = row1.createCell(4);
            dateCell.setCellValue(java.sql.Timestamp.valueOf(transaction.getDateTime()));
            dateCell.setCellStyle(dateStyle);
        }

        workbook.write(response.getOutputStream());
        workbook.close();

        log.info("Excel file successfully written and sent in response for walletId: {}", walletId);
    }
}




















