package com.berk.service;

import com.berk.domain.OrderType;
import com.berk.model.Coin;
import com.berk.model.Order;
import com.berk.model.OrderItem;
import com.berk.model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;
}
