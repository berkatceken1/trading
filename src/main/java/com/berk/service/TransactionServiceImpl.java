package com.berk.service;

import com.berk.domain.WalletTransactionType;
import com.berk.model.Wallet;
import com.berk.model.WalletTransaction;
import com.berk.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) {
        List<WalletTransaction> transactions = walletTransactionRepository.findByWallet(wallet);
        return transactions;
    }

    @Override
    public WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, String transferId, String purpose, Long amount) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(wallet); // İşlem yapılan cüzdan
        transaction.setType(type); // İşlem tipi (WITHDRAW, WALLET_TRANSFER, vb.)
        transaction.setDate(LocalDate.now()); // Tarih
        transaction.setTransferId(transferId != null ? transferId : "0"); // Transfer ID, null değilse set edilir
        transaction.setPurpose(purpose); // İşlem amacı
        transaction.setAmount(amount); // İşlem miktarı

        // İşlemi kaydet
        return walletTransactionRepository.save(transaction);
    }


}
