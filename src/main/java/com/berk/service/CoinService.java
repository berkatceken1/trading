package com.berk.service;

import com.berk.model.Coin;

import java.util.List;

public interface CoinService {
    List<Coin> getCoinsList(int page) throws Exception;

    String getMarketChart(String coinId, int days) throws Exception;

    String getCoinInfo(String coinId) throws Exception;

    Coin findCoinById(String coinId) throws Exception;

    String searchCoin(String query) throws Exception;

    String getTop50CoinsByMarketCap() throws Exception;

    String getTradingCoins() throws Exception;
}
