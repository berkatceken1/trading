package com.berk.controller;

import com.berk.domain.OrderType;
import com.berk.model.Coin;
import com.berk.model.Order;
import com.berk.model.User;
import com.berk.request.CreateOrderRequest;
import com.berk.service.CoinService;
import com.berk.service.OrderService;
import com.berk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

//    @Autowired
//    private WalletTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequest createOrderRequest
    ) throws Exception {
        User user = userService.findUserByJwtToken(token);
        Coin coin = coinService.findCoinById(createOrderRequest.getCoinId());

        Order order = orderService.processOrder(coin, createOrderRequest.getQuantity(),
                createOrderRequest.getOrderType(), user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId
    ) throws Exception {
        if (token == null) {
            throw new Exception("Token missing");
        }

        User user = userService.findUserByJwtToken(token);

        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order);
        } else {
            throw new Exception("You don't have access to this order");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) OrderType orderType,
            @RequestParam(required = false) String assetSymbol
    ) throws Exception {
        if (token == null) {
            throw new Exception("Token missing");
        }

        Long userId = userService.findUserByJwtToken(token).getId();

        List<Order> orders = orderService.getAllOrdersOfUser(userId, orderType, assetSymbol);
        return ResponseEntity.ok(orders);
    }

}
