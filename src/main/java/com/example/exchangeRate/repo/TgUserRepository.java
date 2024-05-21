package com.example.exchangeRate.repo;

import com.example.exchangeRate.model.Tguser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TgUserRepository extends JpaRepository<Tguser, Integer> {
    Optional<Tguser> findByChatid(Long chatid);

}
