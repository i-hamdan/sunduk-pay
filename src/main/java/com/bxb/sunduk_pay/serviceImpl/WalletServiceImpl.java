package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.WalletMapperImpl;
import com.bxb.sunduk_pay.exception.CannotDeleteWalletException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.request.PaginationRequest;
import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.response.WalletsResponse;
import com.bxb.sunduk_pay.service.WalletService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapperImpl walletMapper;

    public WalletServiceImpl(UserRepository userRepository, WalletRepository walletRepository, TransactionRepository transactionRepository, WalletMapperImpl walletMapper) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletMapper = walletMapper;
    }


    public String createWallet(WalletRequest walletRequest) throws RuntimeException {

        User user = userRepository.findById(walletRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Cannot find user with id:" + walletRequest.getUserId() + " ! Please provide a valid user Id."));
        if (user.getIsDeleted()) {
            throw new UserNotFoundException("This user is already deleted and does not exist.");
        }
        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.randomUUID().toString());
        wallet.setBalance(0d);
        wallet.setUser(user);
        wallet.setIsDeleted(false);
        walletRepository.save(wallet);
        return "Wallet creation successful of user : " + walletRequest.getUserId() + "," + "\n" + "WalletId : " + wallet.getWalletId() + ".";
    }



    //This method will return a wallet and it's transactions.
    public WalletResponse getInfoByWalletId(String id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow
                (() -> new WalletNotFoundException("Invalid wallet id! please provide a valid wallet id."));
        if (wallet.getIsDeleted()) {
            throw new WalletNotFoundException("This wallet is already deleted!");
        }
        return walletMapper.toWalletResponse(wallet);
    }



    //This method will return all the wallets of a user since a user can have multiple wallets.
    public List<WalletsResponse> getAllWalletsByUuid(String uuid) {
        List<Wallet> wallets = walletRepository.findByUser_Uuid(uuid).stream().filter(wallet1 -> !wallet1.getIsDeleted()).toList();
        if (wallets.isEmpty()) {
            throw new WalletNotFoundException("Cannot retrieve wallets of user with uuid:" + uuid + ", Either uuid is invalid or user does not have wallets.");
        }
        return walletMapper.toWalletsResponse(wallets);
    }


     //This will simply return the current balance of a wallet.
    public String showBalance(String walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet Id is not valid!"));
        if (wallet.getIsDeleted()) {
            throw new WalletNotFoundException("This wallet has been already deleted! Cannot retrieve info.");
        }
        return "current balance in wallet " + wallet.getWalletId() + " is " + wallet.getBalance() + ".";
    }


    //method for downloading transactions in Excel file format
    public void downloadTransactions(String walletId, HttpServletResponse response) throws IOException {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("invalid wallet id"));
        if (wallet.getIsDeleted()) {
            throw new WalletNotFoundException("The wallet has been already deleted! Cannot download transactions.");
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.xlsx");

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
        List<Transaction> list = wallet.getTransactionHistory().stream().filter(transaction -> !transaction.getIsDeleted()).toList();
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
    }


    public String deleteWallet(String walletId) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("invalid id"));
        if (wallet.getBalance() != 0) {
            throw new CannotDeleteWalletException("Cannot delete wallet because the wallet has some balance in it.");
        }
        wallet.setIsDeleted(true);
        List<Transaction> transactions = transactionRepository.findByWallet_walletId(wallet.getWalletId());
        for (Transaction transaction : transactions) {
            transaction.setIsDeleted(true);
        }
        transactionRepository.saveAll(transactions);
        walletRepository.save(wallet);
        return "Wallet deleted successfully";
    }

    //This method will return the recent transactions , Pagination is implemented here.
    public List<TransactionResponse> getRecentTransactionsByWalletId(String walletId, PaginationRequest request) {
        if (request.getPageSize() != null && request.getPageNumber() != null) {
            Pageable pageable = PageRequest.of(request.getPageNumber() - 1,
                    request.getPageSize(),
                    Sort.by(Sort.Direction.DESC,
                            request.getSortBy()));
            List<Transaction> transactions = transactionRepository.findByWallet_walletId(walletId, pageable).stream().filter(transaction -> !transaction.getIsDeleted()).toList();
            return walletMapper.toTransactionsResponse(transactions);
        } else {
            Sort sort = Sort.by(request.getSortBy()).descending();
            List<Transaction> transactions = transactionRepository.findByWallet_walletId(walletId, sort).stream().filter(transaction -> !transaction.getIsDeleted()).toList();
            return walletMapper.toTransactionsResponse(transactions);
        }
    }

}
