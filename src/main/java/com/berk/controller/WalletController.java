package com.berk.controller;

import com.berk.model.Order;
import com.berk.model.User;
import com.berk.model.Wallet;
import com.berk.model.WalletTransaction;
import com.berk.service.UserService;
import com.berk.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    private UserService userService;

    @GetMapping
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserByJwtToken(token);

        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @RequestHeader("Authorization")
            String token,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction walletTransactionReq) throws Exception {

        User senderUser = userService.findUserByJwtToken(token);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        Wallet wallet = walletService.walletToWalletTransfer(
                senderUser,
                receiverWallet,
                walletTransactionReq.getAmount());

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/order/{orderId}/payment")
    public ResponseEntity<Wallet> payOrderPayment(
            @RequestHeader("Authorization")
            String token,
            @PathVariable Long orderId) throws Exception {
        User user = userService.findUserByJwtToken(token);

        Order order = orderService.getOrderById(orderId);

        Wallet wallet = walletService.payOrderPayment(order, user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
}
