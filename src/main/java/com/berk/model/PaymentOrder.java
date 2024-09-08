package com.berk.model;


import com.berk.domain.PaymentMethod;
import com.berk.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus status;

    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;

    // değiştirilebilir
    private String transactionId; // Iyzico veya PayTR'den dönecek işlem kimliği
    private String paymentLink;   // Ödeme bağlantısı, eğer link oluşturuluyorsa
}
