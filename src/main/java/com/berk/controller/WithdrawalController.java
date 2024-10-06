package com.berk.controller;

import com.berk.domain.WalletTransactionType;
import com.berk.model.User;
import com.berk.model.Wallet;
import com.berk.model.WalletTransaction;
import com.berk.model.Withdrawal;
import com.berk.service.TransactionService;
import com.berk.service.UserService;
import com.berk.service.WalletService;
import com.berk.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

//    @Autowired
//    private TransactionService transactionService;

    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<?> withdrawalRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String token)
        throws Exception {
        User user = userService.findUserByJwtToken(token);
        Wallet userWallet = walletService.getUserWallet(user);

        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
        walletService.addBalance(userWallet, -withdrawal.getAmount());

        WalletTransaction walletTransaction = transactionService.createTransaction(
                userWallet,
                WalletTransactionType.WITHDRAWAL,null,
                "Bank account withdrawal",
                withdrawal.getAmount()
        );

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/{accept}")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserByJwtToken(token);

        Withdrawal withdrawal = withdrawalService.procedWithdrawal(id, accept);

        Wallet userWallet = walletService.getUserWallet(user);

        if (!accept) {
            walletService.addBalance(userWallet, withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(
            @RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserByJwtToken(token);

        List<Withdrawal> withdrawalList = withdrawalService.getUserWithdrawalHistory(user);

        return new ResponseEntity<>(withdrawalList, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequests(
            @RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserByJwtToken(token);

        List<Withdrawal> withdrawalList = withdrawalService.getAllWithdrawalRequests();

        return new ResponseEntity<>(withdrawalList, HttpStatus.OK);
    }
}
