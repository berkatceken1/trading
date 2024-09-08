package com.berk.repository;

import com.berk.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<Watchlist, Long> {
    Watchlist findByUserId(Long userId);
}
