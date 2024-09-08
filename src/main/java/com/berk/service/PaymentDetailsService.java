package com.berk.service;

import com.berk.model.PaymentDetails;
import com.berk.model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber,
                                            String accountHolderName,
                                            String ifscCode,
                                            String bankName,
                                            User user);

    public PaymentDetails getPaymentDetailsByUserId(User user);
}
