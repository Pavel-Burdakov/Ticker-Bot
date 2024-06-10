package com.example.exchangeRate.repo;

import com.example.exchangeRate.model.Ticker;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional // нотация нужна для корректной работы пользовательский методов удаления данных импорт из jakarta!
public interface TickerRepository extends JpaRepository<Ticker, Integer> {
    Optional<Ticker> findByTicker(String ticker);
    Optional<Ticker> findByTickerAndTguser_id(String ticker, Long tguser_id);

    List<Ticker> findAllByTguserId(long tguser_id);
    void deleteByTguser_id(long tguser_id);

    Optional<Ticker> findByTguser_id(long tguser_id);
}
