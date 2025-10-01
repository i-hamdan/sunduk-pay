package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.factoryPattern.WalletOperation;
import com.bxb.sunduk_pay.factoryPattern.WalletOperationFactory;
//import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for wallet operations.
 * Such as adding money, paying money,
 * handling failed transactions, wallet CRUD operations,
 * and exporting transactions.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    /**Transaction type column index in Excel sheet.*/
    private static final int TRANSACTION_TYPE_COLUMN = 1;
    /**Amount column index in Excel sheet.*/
    private static final int AMOUNT_COLUMN = 2;
    /**Description column index in Excel sheet.*/
    private static final int DESCRIPTION_COLUMN = 3;
    /**Date column index in Excel sheet.*/
    private static final int DATE_COLUMN = 4;

    /**MasterWalletRepository for database operations on MasterWallets.*/
    private final MasterWalletRepository masterWalletRepository;
    /**MainWalletRepository for database operations on MainWallets.*/
    private final MainWalletRepository mainWalletRepository;
    /**TransactionRepository for database operations on Transactions.*/
    private final TransactionRepository transactionRepository;
    /**TransactionMapper for mapping Transaction entities to DTOs.*/
    private final TransactionMapper transactionMapper;
//    /**KafkaTemplate for sending TransactionEvent messages to Kafka topics.*/
//    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    /**Wallet operation implementations based on request type.*/
    private final WalletOperationFactory walletOperationFactory;
    /**Validations for performing various validation checks.*/
    private final Validations validations;


    /**
     * Deducts money from the user's wallet(s) and records the transaction.
     *
     * @param request The request containing amount,
     *  source wallet, target wallet, and user UUID.
     * @return MainWalletResponse containing
     * transaction details and updated balances.
     */
    @Override
    @Transactional
    public MainWalletResponse payMoney(final MainWalletRequest request) {
        log.info("=== Deduct Money Request Started ===");
        log.debug("Request: {}", request);
        validations.getUserInfo(request.getUuid());
        MasterWallet masterWallet = validations
                .getMasterWalletInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(request.getUuid());
        SubWallet sourcesubWallet = validations
                .findSubWalletIfExists(mainWallet,
                request.getSourceWalletId());

        log.info("MasterWallet balance before: {}",
                masterWallet.getBalance());
        log.info("MainWallet balance before: {}",
                mainWallet.getBalance());

        Double previousSourceWalletBalance;
        if (sourcesubWallet != null) {
            log.info("Source SubWallet [{}] balance before: {}",
                    sourcesubWallet.getSubWalletName(),
                    sourcesubWallet.getBalance());
            previousSourceWalletBalance = sourcesubWallet.getBalance();
        } else {
            previousSourceWalletBalance = mainWallet.getBalance();
        }

        // deduct balance from master wallet
        masterWallet.setBalance(
                masterWallet.getBalance() - request.getAmount());
        log.info("Deducted {} from MasterWallet. New balance: {}",
                request.getAmount(),
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.CARD)
                .status("SUCCESS")
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
            validations.validateBalance(sourcesubWallet.getBalance(),
                    request.getAmount());
            sourcesubWallet.setBalance(
                    sourcesubWallet.getBalance() - request.getAmount());
            log.info(
                    "Deducted {} from SubWallet [{}]. New balance: {}",
                    request.getAmount(), sourcesubWallet.getSubWalletName(),
                    sourcesubWallet.getBalance());

            debitTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .status("SUCCESS")
                    .description("Deducted from sub wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet(sourcesubWallet.getSubWalletName())
                    .fromWalletId(sourcesubWallet.getSubWalletId())
                    .toWallet("some external source")
                    .toWalletId(UUID.randomUUID().toString()).build();
            transactions.add(debitTxn);
////            TransactionEvent transactionEvent = transactionMapper
////                    .toTransactionEvent(debitTxn);
////            kafkaTemplate.send("transaction-topic",
//                    transactionEvent);

        } else {
            validations.validateBalance(mainWallet.getBalance(),
                    request.getAmount());
            mainWallet.setBalance(
                    mainWallet.getBalance() - request.getAmount());
            log.info("Deducted {} from MainWallet. New balance: {}",
                    request.getAmount(),
                    mainWallet.getBalance());

            debitTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .status("SUCCESS")
                    .description("Deducted from main wallet")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet("Main Wallet")
                    .fromWalletId(mainWallet.getMainWalletId())
                    .toWallet("some external target")
                    .toWalletId(UUID.randomUUID().toString()).build();
            transactions.add(debitTxn);

//            TransactionEvent transactionEvent = transactionMapper
//                    .toTransactionEvent(debitTxn);
//            kafkaTemplate.send("transaction-topic", transactionEvent);
        }

        transactionRepository.saveAll(transactions);
        masterWalletRepository.save(masterWallet);
        mainWallet.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(
                        debitTxn.getTransactionId())
                .previousSourceWalletBalance(
                        previousSourceWalletBalance)
                .newSourceWalletBalance(
                        mainWallet.getBalance())
                .message("Transfer Successful")
                .build();

        log.info(" === Deduct Money Request Completed Successfully === ");
        log.debug("Response: {}", response);
        return response;
    }
    /**
     * Adds money to the user's wallet(s) and records the transaction.
     *
     * @param mainWalletRequest The request containing amount ,
     *                          source wallet ,
     *                          target wallet ,
     *                          and user UUID.
     * @return MainWalletResponse containing
     * transaction details and updated balances.
     */
    @Transactional
    @Override
    public MainWalletResponse addMoney(
            final MainWalletRequest mainWalletRequest) {
        log.info("=== Add Money Request Started ===");
        log.debug("Request: {}", mainWalletRequest);

        User user = validations
                .getUserInfo(mainWalletRequest.getUuid());
        MasterWallet masterWallet = validations
                .getMasterWalletInfo(mainWalletRequest.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(mainWalletRequest.getUuid());
        SubWallet subWallet = validations
                .findSubWalletIfExists(mainWallet,
                        mainWalletRequest.getTargetWalletId());

        log.info("MasterWallet balance before: {}",
                masterWallet.getBalance());
        log.info("MainWallet balance before: {}",
                mainWallet.getBalance());

        Double previousTargetWalletBalance;
        if (subWallet != null) {
            log.info("Target SubWallet [{}] balance before: {}",
                    subWallet.getSubWalletName(),
                    subWallet.getBalance());
            previousTargetWalletBalance = subWallet.getBalance();
        } else {
            previousTargetWalletBalance = mainWallet.getBalance();
        }

        // adding amount on master wallet
        masterWallet.setBalance(masterWallet.getBalance()
                + mainWalletRequest.getAmount());
        log.info("Added {} to MasterWallet. New balance: {}",
                mainWalletRequest.getAmount(),
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(mainWalletRequest.getAmount())
                .user(user)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.CARD)
                .status("SUCCESS")
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

            subWallet.setBalance(subWallet.getBalance()
                    + mainWalletRequest.getAmount());
            newTargetWalletBalance = subWallet.getBalance();
            log.info("Added {} to SubWallet [{}]. New balance: {}",
                    mainWalletRequest.getAmount(),
                    subWallet.getSubWalletName(),
                    subWallet.getBalance());

            creditTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .status("SUCCESS")
                    .description("Credited to sub wallet : "
                            + subWallet.getSubWalletName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet("Some external source.")
                    .fromWalletId(UUID.randomUUID().toString())
                    .toWallet(subWallet.getSubWalletName())
                    .toWalletId(subWallet.getSubWalletId()).build();

            transactions.add(creditTxn);
//            TransactionEvent transactionEvent = transactionMapper
//                    .toTransactionEvent(creditTxn);
//            kafkaTemplate.send("transaction-topic",
//                    transactionEvent);

        } else {
            mainWallet.setBalance(mainWallet.getBalance()
                    + mainWalletRequest.getAmount());
            newTargetWalletBalance = mainWallet.getBalance();
            log.info("Added {} to MainWallet. New balance: {}",
                    mainWalletRequest.getAmount(),
                    mainWallet.getBalance());

            creditTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .status("SUCCESS")
                    .description("Credited to main wallet.")
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .fromWallet("Some external source.")
                    .fromWalletId(UUID.randomUUID().toString())
                    .toWallet("Main wallet")
                    .toWalletId(mainWallet.getMainWalletId()).build();

            transactions.add(creditTxn);
//            TransactionEvent transactionEvent = transactionMapper
//                    .toTransactionEvent(creditTxn);
//            kafkaTemplate.send("transaction-topic",
          //          transactionEvent);

        }

        transactionRepository.saveAll(transactions);
        mainWallet.getTransactionHistory()
                .addAll(transactions);
        masterWalletRepository.save(masterWallet);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse
                .builder()
                .status("SUCCESS")
                .targetTransactionId(transactions.get(1)
                        .getTransactionId())
                .previousTargetWalletBalance(previousTargetWalletBalance)
                .newTargetWalletBalance(newTargetWalletBalance)
                .message("amount recived successfull")
                .build();

        log.info(" === Add Money Request Completed Successfully === ");
        log.debug("Response: {}", response);
        return response;
    }
    /**
     * Provides wallet-related operations such as CRUD operations.
     * And recording failed transactions,
     * adding dummy transactions, checking balances,
     * and downloading transaction history.
     */

    @Override
    public MainWalletResponse walletCrud(
            final MainWalletRequest mainWalletRequest) {
        WalletOperation walletService = walletOperationFactory.
                getWalletService(mainWalletRequest.getRequestType());
        return walletService.perform(mainWalletRequest);
    }

    /**
     * Records a failed transaction in the database.
     * @param request The request containing
     *        transaction details such as source ,
     *         target , amount , and type.
     * @return a MainWalletResponse indicating transaction failure.
     */
    @Override
        public MainWalletResponse recordFailedTxn(
                final MainWalletRequest request) {

        User user = validations.getUserInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(request.getUuid());
        SubWallet sourceSubWallet = validations
                .findSubWalletIfExists(mainWallet,
                request.getSourceWalletId());
        SubWallet targetSubwallet = validations
                .findSubWalletIfExists(mainWallet,
                request.getTargetWalletId());

        String fromWallet = null;
        if (mainWallet.getMainWalletId().
                equals(request.getSourceWalletId())) {
            fromWallet = "Main wallet";
        } else if (sourceSubWallet != null
                && sourceSubWallet.getSubWalletId()
                .equals(request.getSourceWalletId())) {
            fromWallet = sourceSubWallet.getSubWalletName();
        } else {
            fromWallet = "Some external source";
        }

        String toWallet = null;
        if (mainWallet.getMainWalletId()
                .equals(request.getTargetWalletId())) {
            toWallet = "Main wallet";
        } else if (targetSubwallet != null
                && targetSubwallet.getSubWalletId()
                .equals(request.getTargetWalletId())) {
            toWallet = targetSubwallet.getSubWalletName();
        } else {
            toWallet = "Some external target";
        }


        Transaction failedTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .description("Transaction failed")
                .dateTime(LocalDateTime.now())
                .mainWallet(mainWallet)
                .status("FAILED")
                .fromWallet(fromWallet)
                .fromWalletId(request.getSourceWalletId())
                .toWallet(toWallet)
                .toWalletId(request.getTargetWalletId())
                .build();

        transactionRepository.save(failedTransaction);
        return MainWalletResponse.builder()
                    .message("Transaction failed!")
                    .build();
        }

    /**
     * Records a failed transaction in the database.
     *
     * @param request The request containing
     *        transaction details such as source ,
     *         target , amount , and type.
     */
    @Override
    public void addDummy(final MainWalletRequest request) {
        User user = validations.getUserInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(user.getUuid());

        Transaction txn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .mainWallet(mainWallet)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .paymentMethod(request.getPaymentMethod())
                .dateTime(LocalDateTime.now())
                .toWallet("Dummy Wallet")
                .toWalletId("123")
                .fromWallet("Dummy Wallet")
                .fromWalletId("ABC")
                .build();
        transactionRepository.save(txn);
    }

    /**
     * Returns the current balance of a wallet.
     *
     * @param walletId The ID of the wallet
     *                 to fetch balance for.
     * @return String A message containing
     * the wallet ID and its current balance.
     * @throws WalletNotFoundException If
     * the wallet with given ID does not exist.
     */
    //This will simply return the current balance of a wallet.
    public String showBalance(final String walletId) {
        log.info("Fetching balance for walletId: {}",
                walletId);

        MainWallet wallet = mainWalletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Invalid wallet ID: {}",
                            walletId);
                    return new WalletNotFoundException(
                            "Wallet Id is not valid!");
                });

        String balanceMsg = "Current balance in wallet "
                + wallet.getMainWalletId()
                + " is " + wallet.getBalance() + ".";
        log.info(balanceMsg);
        return balanceMsg;
    }



    /**
     * Exports the transaction history of a wallet to an Excel file
     * and sends it in the HTTP response.
     * @param walletId The ID of the wallet whose
      transactions are to be exported.
     * @param response The HttpServletResponse
     to write the Excel file to.
     * @throws IOException If an I/O error occurs
     *during file writing.
     * @throws WalletNotFoundException If the wallet
     *with given ID does not exist.
     */
    @Override
    public void downloadTransactions(
            final String walletId,
            final HttpServletResponse response)
            throws IOException {
        log.info(
             "Starting to download transactions for walletId: {}",
                walletId);

        MainWallet wallet = mainWalletRepository.findById(walletId)
             .orElseThrow(() -> {
            log.error("Wallet not found with ID: {}", walletId);
           return new WalletNotFoundException("invalid wallet id");
                });
        response.setContentType(
         "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
        "Content-Disposition", "attachment; filename=transactions.xlsx");

        log.info("Generating Excel sheet for walletId: {}", walletId);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Transactions");

        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(
                "yyyy-MM-dd HH:mm:ss"));

        XSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("S.No");
        row.createCell(TRANSACTION_TYPE_COLUMN).setCellValue("Type");
        row.createCell(AMOUNT_COLUMN).setCellValue("Amount");
        row.createCell(DESCRIPTION_COLUMN).setCellValue("Description");
        row.createCell(DATE_COLUMN).setCellValue("Date&Time");

        int rowNum = 1;
        int count = 1;

        List<Transaction> list = transactionRepository.
                findByMainWalletMainWalletIdAndUserUuid(walletId,
                        wallet.getUser().getUuid());
        log.info("Writing {} transactions into Excel for walletId: {}",
                list.size(), walletId);

        for (Transaction transaction : list) {
            XSSFRow row1 = sheet.createRow(rowNum++);
            row1.createCell(0).setCellValue(
                    count++);
            row1.createCell(TRANSACTION_TYPE_COLUMN).setCellValue(
                    transaction.getTransactionType().toString());
            row1.createCell(AMOUNT_COLUMN).setCellValue(
                    transaction.getAmount());
            row1.createCell(DESCRIPTION_COLUMN).setCellValue(
                    transaction.getDescription());
            Cell dateCell = row1.createCell(DATE_COLUMN);
            dateCell.setCellValue(java.sql.Timestamp.valueOf(
                    transaction.getDateTime()));
            dateCell.setCellStyle(dateStyle);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
        log.info(
       "Excel file successfully written and sent in response for walletId: {}",
                walletId);
    }
}
