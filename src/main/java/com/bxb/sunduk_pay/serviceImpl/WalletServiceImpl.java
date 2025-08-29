package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapperImpl;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
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
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public MainWalletResponse payMoney(MainWalletRequest request) {
        log.info("=== Deduct Money Request Started ===");
        log.debug("Request: {}", request);
        validations.getUserInfo(request.getUuid());
        MasterWallet masterWallet = validations.getMasterWalletInfo(request.getUuid());
        MainWallet mainWallet = validations.getMainWalletInfo(request.getUuid());
        SubWallet sourcesubWallet = validations.findSubWalletIfExists(mainWallet, request.getSourceWalletId());

        log.info("MasterWallet balance before: {}", masterWallet.getBalance());
        log.info("MainWallet balance before: {}", mainWallet.getBalance());

        Double previousSourceWalletBalance;
        if (sourcesubWallet != null) {
            log.info("Source SubWallet [{}] balance before: {}", sourcesubWallet.getSubWalletName(), sourcesubWallet.getBalance());
            previousSourceWalletBalance = sourcesubWallet.getBalance();
        } else {
            previousSourceWalletBalance = mainWallet.getBalance();
        }

        // deduct balance from master wallet
        masterWallet.setBalance(masterWallet.getBalance() - request.getAmount());
        log.info("Deducted {} from MasterWallet. New balance: {}", request.getAmount(), masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .description("Deducted from master wallet")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .user(mainWallet.getUser())
                .fromWallet("Master Wallet")
                .fromWalletId(masterWallet.getMasterWalletId())
                .toWallet("Some external source")
                .toWalletId(request.getTargetWalletId())
                .isMaster(true)
                .build();

        transactions.add(masterWalletTxn);

        Transaction debitTxn = null;
        if (sourcesubWallet != null) {
            validations.validateBalance(sourcesubWallet.getBalance(), request.getAmount());
            sourcesubWallet.setBalance(sourcesubWallet.getBalance() - request.getAmount());
            log.info("Deducted {} from SubWallet [{}]. New balance: {}", request.getAmount(), sourcesubWallet.getSubWalletName(), sourcesubWallet.getBalance());

            debitTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("Deducted from sub wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet(sourcesubWallet.getSubWalletName())
                    .fromWalletId(sourcesubWallet.getSubWalletId())
                    .toWallet("some external source")
                    .toWalletId(request.getTargetWalletId()).build();
            transactions.add(debitTxn);
            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(debitTxn);
            kafkaTemplate.send("transaction-topic", transactionEvent);

        } else {
            validations.validateBalance(mainWallet.getBalance(), request.getAmount());
            mainWallet.setBalance(mainWallet.getBalance() - request.getAmount());
            log.info("Deducted {} from MainWallet. New balance: {}", request.getAmount(), mainWallet.getBalance());

            debitTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("Deducted from main wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet("Main Wallet")
                    .fromWalletId(mainWallet.getMainWalletId())
                    .toWallet("some external entity")
                    .toWalletId(request.getTargetWalletId()).build();
            transactions.add(debitTxn);

            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(debitTxn);
            kafkaTemplate.send("transaction-topic", transactionEvent);
        }

        transactionRepository.saveAll(transactions);
        masterWalletRepository.save(masterWallet);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(debitTxn.getTransactionId())
                .previousSourceWalletBalance(previousSourceWalletBalance)
                .newSourceWalletBalance(mainWallet.getBalance())
                .message("Transfer Successful")
                .build();

        log.info("=== Deduct Money Request Completed Successfully ===");
        log.debug("Response: {}", response);
        return response;
    }

    @Transactional
    @Override
    public MainWalletResponse addMoney(MainWalletRequest mainWalletRequest) {
        log.info("=== Add Money Request Started ===");
        log.debug("Request: {}", mainWalletRequest);

        User user = validations.getUserInfo(mainWalletRequest.getUuid());
        MasterWallet masterWallet = validations.getMasterWalletInfo(mainWalletRequest.getUuid());
        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());
        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, mainWalletRequest.getTargetWalletId());

        log.info("MasterWallet balance before: {}", masterWallet.getBalance());
        log.info("MainWallet balance before: {}", mainWallet.getBalance());

        Double previousTargetWalletBalance;
        if (subWallet != null) {
            log.info("Target SubWallet [{}] balance before: {}", subWallet.getSubWalletName(), subWallet.getBalance());
            previousTargetWalletBalance = subWallet.getBalance();
        } else {
            previousTargetWalletBalance = mainWallet.getBalance();
        }

        // adding amount on master wallet
        masterWallet.setBalance(masterWallet.getBalance() + mainWalletRequest.getAmount());
        log.info("Added {} to MasterWallet. New balance: {}", mainWalletRequest.getAmount(), masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                .amount(mainWalletRequest.getAmount())
                .user(user)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .description("Credited to master wallet.")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .user(mainWallet.getUser())
                .fromWallet("Some external source.")
                .fromWalletId(mainWalletRequest.getSourceWalletId())
                .toWallet("Master wallet")
                .toWalletId(masterWallet.getMasterWalletId())
                .isMaster(true)
                .build();
        transactions.add(masterWalletTxn);

        Transaction creditTxn;
        Double newTargetWalletBalance = null;
        if (subWallet != null) {

            subWallet.setBalance(subWallet.getBalance() + mainWalletRequest.getAmount());
            newTargetWalletBalance = subWallet.getBalance();
            log.info("Added {} to SubWallet [{}]. New balance: {}", mainWalletRequest.getAmount(), subWallet.getSubWalletName(), subWallet.getBalance());

            creditTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("Credited to sub wallet : " + subWallet.getSubWalletName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet("Some external source.")
                    .fromWalletId(mainWalletRequest.getSourceWalletId())
                    .toWallet(subWallet.getSubWalletName())
                    .toWalletId(subWallet.getSubWalletId()).build();

            transactions.add(creditTxn);
            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(creditTxn);
            kafkaTemplate.send("transaction-topic", transactionEvent);

        } else {
            mainWallet.setBalance(mainWallet.getBalance() + mainWalletRequest.getAmount());
            newTargetWalletBalance = mainWallet.getBalance();
            log.info("Added {} to MainWallet. New balance: {}", mainWalletRequest.getAmount(), mainWallet.getBalance());

            creditTxn = Transaction.builder().transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .description("Credited to main wallet.")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet("Some external source.")
                    .fromWalletId(mainWalletRequest.getSourceWalletId())
                    .toWallet("Main wallet")
                    .toWalletId(mainWallet.getMainWalletId()).build();

            transactions.add(creditTxn);
            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(creditTxn);
            kafkaTemplate.send("transaction-topic", transactionEvent);

        }

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory().addAll(transactions);
        masterWalletRepository.save(masterWallet);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .targetTransactionId(transactions.get(1).getTransactionId())
                .previousTargetWalletBalance(previousTargetWalletBalance)
                .newTargetWalletBalance(newTargetWalletBalance)
                .message("amount recived successfull")
                .build();

        log.info("=== Add Money Request Completed Successfully ===");
        log.debug("Response: {}", response);
        return response;
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

//        if (wallet.getIsDeleted()) {
//            log.error("Wallet with ID {} is deleted.", walletId);
//            throw new WalletNotFoundException("This wallet has been already deleted! Cannot retrieve info.");
//        }

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

        List<Transaction> list = transactionRepository.findByMainWallet_mainWalletIdAndUser_Uuid(walletId, wallet.getUser().getUuid());
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




















