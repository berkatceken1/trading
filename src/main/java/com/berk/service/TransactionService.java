package com.berk.service;

import com.berk.domain.WalletTransactionType;
import com.berk.model.Wallet;
import com.berk.model.WalletTransaction;

import java.util.List;

public interface TransactionService {
    List<WalletTransaction> getTransactionsByWallet(Wallet wallet);
    WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, String transferId, String purpose, Long amount);
}
