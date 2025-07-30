package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.Mappers.WalletMapperImpl;
import com.bxb.sunduk_pay.event.TransactionEvent;
import com.bxb.sunduk_pay.exception.CannotCreateWalletException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.TransactionType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;


    public WalletServiceImpl(UserRepository userRepository, WalletRepository walletRepository, TransactionRepository transactionRepository, WalletMapperImpl walletMapper, TransactionMapper transactionMapper, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletMapper = walletMapper;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public String createWallet(WalletRequest walletRequest) throws RuntimeException {
        log.info("Creating wallet for userId: {}", walletRequest.getUserId());

        User user = userRepository.findById(walletRequest.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", walletRequest.getUserId());
                    return new UserNotFoundException("Cannot find user with id:" + walletRequest.getUserId() + " ! Please provide a valid user Id.");
                });

        if (user.getIsDeleted()) {
            log.error("User with ID {} is marked as deleted.", walletRequest.getUserId());
            throw new UserNotFoundException("This user is already deleted and does not exist.");
        }

        if (walletRepository.findByUser_Uuid(user.getUuid()).isPresent()){
            log.error("Wallet already exists for user {} with uuid : {}",user.getFullName(),user.getUuid());
            throw new CannotCreateWalletException("User with uuid : "+walletRequest.getUserId()+" already has a wallet.");
        }

        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.randomUUID().toString());
        wallet.setBalance(0d);
        wallet.setUser(user);
        wallet.setIsDeleted(false);

        walletRepository.save(wallet);

        log.info("Wallet successfully created with ID: {}", wallet.getWalletId());

        return "Wallet creation successful of user : " + walletRequest.getUserId() + "," + "\n" + "WalletId : " + wallet.getWalletId() + ".";
    }

    /// taiyyab bhai integrated code
    @Override
    public String addMoneyToWallet(String uuid, double amount, String paymentIntentId) {
        log.info("Adding amount ${} to wallet for uuid: {}", amount, uuid);

        Wallet wallet = walletRepository.findByUser_Uuid(uuid).orElseThrow(() -> {
            log.error("User not found with ID: {}", uuid);
            return new UserNotFoundException("User not found with ID: " + uuid);
        });

        if (wallet.getIsDeleted()) {
            log.error("Wallet not found for uuid: {}", uuid);
            throw new WalletNotFoundException("Cannot add money. Wallet is deleted.");
        }

        // Update balance
        wallet.setBalance(wallet.getBalance() == null ? amount : wallet.getBalance() + amount);
        walletRepository.save(wallet);

        // Fetch user
        User user = userRepository.findById(wallet.getUser().getUuid())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + uuid));

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.CREDIT);
        transaction.setStatus("SUCCESS");
        transaction.setWallet(wallet);
        transaction.setStripePaymentIntentId(paymentIntentId);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription("Money added via Stripe");
        transactionRepository.save(transaction);

        TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(transaction);
        kafkaTemplate.send("transaction-topic",transactionEvent);

        String message = "An amount of $" + amount + " has been added to your wallet successfully.";
        log.info(message);
        return message;

    }

    @Override
    public String payMoneyFromWallet(String uuid, double amount, String description) {
        // Fetch wallet
        Wallet wallet = walletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> {
                    log.error("Wallet not found for userId: {}", uuid);
                    return new WalletNotFoundException("Wallet not found for userId: " + uuid);
                });


        User user = userRepository.findById(wallet.getUser().getUuid())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", uuid);
                    return new UserNotFoundException("uuid not found");
                });


        if (wallet.getIsDeleted()) {
            log.error("Wallet is deleted. Cannot perform payment for userId: {}", uuid);
            throw new WalletNotFoundException("Cannot perform transaction. Wallet is deleted.");
        }

        if (wallet.getBalance() == null || wallet.getBalance() < amount) {
            log.error("Insufficient balance for userId: {}", uuid);
            throw new RuntimeException("Insufficient balance in wallet.");
        }

        // Deduct balance
        wallet.setBalance(wallet.getBalance() - amount);
        log.info("Wallet balance deducted. Remaining balance: {}", wallet.getBalance());


        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEBIT);
        transaction.setStatus("SUCCESS");
        transaction.setWallet(wallet);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setDescription(description != null ? description : "Money debited from wallet");

        walletRepository.save(wallet);
        transactionRepository.save(transaction);


        TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(transaction);
        kafkaTemplate.send("transaction-topic",transactionEvent);

        String message = "Payment of $" + amount + " from wallet successful.";
        log.info(message);
        return message;
    }


    @Override
    public List<TransactionResponse> getAllTransactions(String uuid, String walletId) {
        log.info("Fetching all transactions for userId: {} and walletId: {}", uuid, walletId);

        List<Transaction> allTransactionsByWalletId = transactionRepository
                .findByWallet_walletIdAndUser_Uuid(walletId, uuid);

        log.info("Found {} transactions for walletId: {}", allTransactionsByWalletId.size(), walletId);

        List<TransactionResponse> responseList = transactionMapper.toTransactionsResponse(allTransactionsByWalletId);

        log.debug("Mapped transactions to response DTOs: {}", responseList);

        return responseList;

    }

    //This will simply return the current balance of a wallet.
    public String showBalance(String walletId) {
        log.info("Fetching balance for walletId: {}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    log.error("Invalid wallet ID: {}", walletId);
                    return new WalletNotFoundException("Wallet Id is not valid!");
                });

        if (wallet.getIsDeleted()) {
            log.error("Wallet with ID {} is deleted.", walletId);
            throw new WalletNotFoundException("This wallet has been already deleted! Cannot retrieve info.");
        }

        String balanceMsg = "Current balance in wallet " + wallet.getWalletId() + " is " + wallet.getBalance() + ".";
        log.info(balanceMsg);
        return balanceMsg;
    }


    //method for downloading transactions in Excel file format
    @Override
    public void downloadTransactions(String walletId, HttpServletResponse response) throws IOException {
        log.info("Starting to download transactions for walletId: {}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
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

        List<Transaction> list = transactionRepository.findByWallet_walletIdAndUser_Uuid(walletId,wallet.getUser().getUuid());
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




















