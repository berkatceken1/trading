package com.berk.service;

import com.berk.model.Order;
import com.berk.model.User;
import com.berk.model.Wallet;

public interface WalletService {

    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, Long money);

    Wallet findWalletById(Long walletId) throws Exception;

    Wallet walletToWalletTransfer(User sender, Wallet receiver, Long amount, String purpose) throws Exception;

    Wallet payOrderPayment(Order order, User user) throws Exception;
}
