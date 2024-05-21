package com.example.exchangeRate.repo;

import com.example.exchangeRate.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Integer> {
    Optional<Ticker> findByTicker(String ticker);
}
