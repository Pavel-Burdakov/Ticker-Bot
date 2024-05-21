package com.example.exchangeRate.services;

import com.example.exchangeRate.exception.serviceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.sql.rowset.serial.SerialException;
import java.util.Map;

/**
 * Метод String getTickerCurrencyPrice(String ticker) - получение текущей цены котировки по указанному тикеру акции.
 * Метод Map<String, String> getAllImoexData() - получение всех тикеров и их текущих котировок
 */

public interface TickerPriceService {

    String getTickerCurrencyPrice(String ticker) throws SerialException, serviceException, JsonProcessingException;

    Map<String, String> getAllImoexData() throws serviceException, JsonProcessingException;

}
