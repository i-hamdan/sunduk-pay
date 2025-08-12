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


//
//    @Transactional
//    public String createWallet(WalletRequest walletRequest) throws RuntimeException {
//        log.info("Creating wallet for userId: {}", walletRequest.getUuid());
//
//        User user = userRepository.findById(walletRequest.getUuid()).orElseThrow(() -> {
//            log.error("User not found with ID: {}", walletRequest.getUuid());
//            return new UserNotFoundException("Cannot find user with id:" + walletRequest.getUuid() + " ! Please provide a valid user Id.");
//        });
//
//        if (user.getIsDeleted()) {
//            log.error("User with ID {} is marked as deleted.", walletRequest.getUuid());
//            throw new UserNotFoundException("This user is already deleted and does not exist.");
//        }
//
//        if (mainWalletRepository.findByUser_Uuid(user.getUuid()).isPresent()) {
//            log.error("Wallet already exists for user {} with uuid : {}", user.getFullName(), user.getUuid());
//            throw new CannotCreateWalletException("User with uuid : " + walletRequest.getUuid() + " already has a wallet.");
//        }
//
//        SubWallet mainSubWallet = SubWallet.builder()
//                .subWalletId(UUID.randomUUID().toString())
//                .subWalletName("Main subWallet")
//                .balance(0d)
//                .subWalletType(SubWalletType.MAIN)
//                .build();
//        List<SubWallet> subWallets = new ArrayList<>();
//        subWallets.add(mainSubWallet);
//// main wallet created
//        MainWallet wallet = MainWallet.builder()
//                .mainWalletId(UUID.randomUUID().toString())
//                .user(user)
//                .balance(0d)
//                .isDeleted(false)
//                .subWallets(subWallets)
//                .build();
//
//        mainWalletRepository.save(wallet);
//
//        log.info("Wallet successfully created with ID: {}", wallet.getMainWalletId());
//
//        return "Wallet creation successful of user : " + walletRequest.getUuid() + "," + "\n" + "WalletId : " + wallet.getMainWalletId() + ".";
//    }
//
//



//    @Override
//    public String addMoneyToWallet(String uuid, double amount, String paymentIntentId) {
//        log.info("Adding amount ${} to wallet for uuid: {}", amount, uuid);
//
//        MasterWallet wallet = masterWalletRepository.findByUser_Uuid(uuid).orElseThrow(() -> {
//            log.error("User not found with ID: {}", uuid);
//            return new UserNotFoundException("User not found with ID: " + uuid);
//        });
//
//        if (wallet.getIsDeleted()) {
//            log.error("Wallet not found for uuid: {}", uuid);
//            throw new WalletNotFoundException("Cannot add money. Wallet is deleted.");
//        }
//
//
//        wallet.setBalance(wallet.getBalance() == null ? amount : wallet.getBalance() + amount);
//
//        // Fetch user
//        User user = userRepository.findById(wallet.getUser().getUuid()).orElseThrow(() -> new UserNotFoundException("User not found with ID: " + uuid));
//
//        Transaction transaction = Transaction.builder()
//                .user(user)
//                .transactionId(UUID.randomUUID().toString())
//                .amount(amount)
//                .transactionType(TransactionType.CREDIT)
//                .transactionLevel(TransactionLevel.EXTERNAL)
//                .status("SUCCESS")
////                .masterWalletId(wallet)
//                .stripePaymentIntentId(paymentIntentId)
//                .dateTime(LocalDateTime.now())
//                .description("money Added Via Strupe")
//                .build();
//        transactionRepository.save(transaction);
//
//        wallet.getMainWallet().getTransactionHistory().add(transaction);
//
////        wallet.getTransactionHistory().add(transaction);
//        masterWalletRepository.save(wallet);
//
//        TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(transaction);
//        kafkaTemplate.send("transaction-topic", transactionEvent);
//
//        String message = "An amount of $" + amount + " has been added to your wallet successfully.";
//        log.info(message);
//        return message;
//
//    }

    @Override
    public MainWalletResponse payMoney(MainWalletRequest request) {

        MasterWallet masterWallet=validations.getMasterWalletInfo(request.getUuid());
        MainWallet mainWallet=validations.getMainWalletInfo(request.getUuid());

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, request.getSourceWalletId());

        Double previousSourceWalletBalance;
        if (subWallet!=null){
            previousSourceWalletBalance= subWallet.getBalance();
        }else {
            previousSourceWalletBalance=null;
        }


        // deduct balance from master wallet
        masterWallet.setBalance(masterWallet.getBalance()- request.getAmount());
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
        if (subWallet!=null && subWallet.getSubWalletId().equals(request.getSubWalletId())){
            subWallet.setBalance(subWallet.getBalance()-request.getAmount());
           debitTxn=Transaction.builder().transactionId(UUID.randomUUID().toString())
                   .amount(request.getAmount())
                   .transactionType(request.getTransactionType())
                   .transactionLevel(TransactionLevel.EXTERNAL)
                   .description("deducted from sub wallet" + subWallet.getSubWalletName())
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
       return null;
    }


//    @Override
//    public String payMoneyFromWallet(String uuid, double amount, String description) {
//        // Fetch wallet
//        MasterWallet wallet = masterWalletRepository.findByUser_Uuid(uuid).orElseThrow(() -> {
//            log.error("Wallet not found for userId: {}", uuid);
//            return new WalletNotFoundException("Wallet not found for userId: " + uuid);
//        });
//
//
//        User user = userRepository.findById(wallet.getUser().getUuid()).orElseThrow(() -> {
//            log.error("User not found with ID: {}", uuid);
//            return new UserNotFoundException("uuid not found");
//        });
//
//
//        if (wallet.getIsDeleted()) {
//            log.error("Wallet is deleted. Cannot perform payment for userId: {}", uuid);
//            throw new WalletNotFoundException("Cannot perform transaction. Wallet is deleted.");
//        }
//
//MainWallet mainWallet = wallet.getMainWallet();
//
//        if (mainWallet==null) {
//            throw new ResourceNotFoundException("Wallet doesn't have a mainWallet!");
//        }
//
//        if (wallet.getBalance() == null || mainWallet.getBalance() < amount) {
//            log.error("Insufficient balance for userId: {}", uuid);
//            throw new RuntimeException("Insufficient balance in wallet.");
//        }
//
//        // Deduct balance of both wallets
//
//        wallet.setBalance(wallet.getBalance() - amount);
//        mainWallet.setBalance(mainWallet.getBalance() - amount);
//        log.info("Wallet balance deducted. Remaining balance: {}", wallet.getBalance());
//
//
//        // Create transaction
//
//        Transaction transaction = Transaction.builder()
//                .user(user)
//                .transactionId(UUID.randomUUID().toString())
//                .amount(amount)
//                .transactionType(TransactionType.DEBIT)
//                .transactionLevel(TransactionLevel.EXTERNAL)
//                .status("SUCCESS")
////                .masterWalletId(wallet)
//                .dateTime(LocalDateTime.now())
//                .description("money Debited from wallet")
//                .build();
//        transactionRepository.save(transaction);
//
//        mainWallet.getTransactionHistory().add(transaction);
//
//        TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(transaction);
//        kafkaTemplate.send("transaction-topic", transactionEvent);
//
//        String message = "Payment of $" + amount + " from wallet successful.";
//        log.info(message);
//        return message;
//    }


























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




















