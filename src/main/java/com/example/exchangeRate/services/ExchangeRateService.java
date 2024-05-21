package com.example.exchangeRate.services;
import com.example.exchangeRate.exception.serviceException;

public interface ExchangeRateService {
    String getUSDCurrencyRate() throws serviceException;
    String getEURCurrencyRate() throws serviceException;
}
