package com.berk.service;

import com.berk.model.Coin;
import com.berk.model.User;
import com.berk.model.Watchlist;

public interface WatchListService {

    Watchlist findUserWatchList(Long userId) throws Exception;

    Watchlist createWatchList(User user);

    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchList(Coin coin, User user) throws Exception;
}
