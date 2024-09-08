package com.berk.service;

import com.berk.model.Coin;
import com.berk.model.User;
import com.berk.model.Watchlist;
import com.berk.repository.WatchListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchListServiceImpl implements WatchListService {

    @Autowired
    private WatchListRepository watchListRepository;

    @Override
    public Watchlist findUserWatchList(Long userId) throws Exception {
        Watchlist watchlist = watchListRepository.findByUserId(userId);
        if (watchlist == null) {
            throw new Exception("Watchlist not found");
        }
        return watchlist;
    }

    @Override
    public Watchlist createWatchList(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);

        return watchListRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        Optional<Watchlist> watchlist = watchListRepository.findById(id);
        if (watchlist.isEmpty()) {
            throw new Exception("Watchlist not found");
        }

        return watchlist.get();
    }

    @Override
    public Coin addItemToWatchList(Coin coin, User user) throws Exception {
        Watchlist watchlist = findUserWatchList(user.getId());

        if(watchlist.getCoins().contains(coin)) {
            watchlist.getCoins().remove(coin);
        }
        else  {
            watchlist.getCoins().add(coin);
        }
        watchListRepository.save(watchlist);
        return coin;
    }
}
