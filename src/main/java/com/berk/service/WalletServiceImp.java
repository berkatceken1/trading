package com.berk.service;

import com.berk.domain.OrderType;
import com.berk.domain.WalletTransactionType;
import com.berk.model.Order;
import com.berk.model.User;
import com.berk.model.Wallet;
import com.berk.model.WalletTransaction;
import com.berk.repository.WalletRepository;
import com.berk.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class WalletServiceImp implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;



    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Long money) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));
        wallet.setBalance(newBalance);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long walletId) throws Exception {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isPresent()) {
            return wallet.get();
        }
        throw new Exception("Wallet not found");
    }

    @Override
    public Wallet walletToWalletTransfer(User sender, Wallet receiver, Long amount, String purpose) throws Exception {
        Wallet senderWallet = getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new Exception("Insufficient balance");
        }
        BigDecimal senderBalance = senderWallet
                .getBalance()
                .subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

        BigDecimal receiverBalance = receiver
                .getBalance()
                .add(BigDecimal.valueOf(amount));
        receiver.setBalance(receiverBalance);
        walletRepository.save(receiver);


        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(senderWallet); // veya receiver wallet
        transaction.setType(WalletTransactionType.WALLET_TRANSFER); // ya da uygun olan türü ayarla
        transaction.setDate(LocalDate.now());

        if (transaction.getType() == WalletTransactionType.WALLET_TRANSFER) {
            transaction.setTransferId(generateTransferId());
        } else {
            transaction.setTransferId("0");
        }

        transaction.setPurpose(purpose);
        transaction.setAmount(amount);

        walletTransactionRepository.save(transaction);
        return senderWallet;
    }

    private String generateTransferId() {
        return String.valueOf(walletTransactionRepository.count() + 1);
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);

        if (order.getOrderType().equals(OrderType.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            if (newBalance.compareTo(order.getPrice()) < 0) {
                throw new Exception("Insufficient funds for this transaction");
            }
            wallet.setBalance(newBalance);
        }
        else if (order.getOrderType().equals(OrderType.SELL)) {
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }
        else {
            throw new Exception("Invalid order type");
        }
        walletRepository.save(wallet);
        return wallet;
    }
}
