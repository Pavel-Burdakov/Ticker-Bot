package com.example.exchangeRate.services.impl;

import com.example.exchangeRate.client.imoexClient;
import com.example.exchangeRate.exception.serviceException;

import com.example.exchangeRate.model.Ticker;
import com.example.exchangeRate.pojo.imoexData;
import com.example.exchangeRate.repo.TickerRepository;
import com.example.exchangeRate.services.TickerPriceService;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialException;
import java.util.Map;

@Service
public class TickerPriceServiceImpl implements TickerPriceService {
    @Autowired
    private imoexClient imoexClient;
    @Autowired
    private TickerRepository tickerRepository;

    @Override
    public String getTickerCurrencyPrice(String ticker) throws SerialException, serviceException, JsonProcessingException {

        Map<String, String> map = getAllImoexData();
        if (map.containsKey(ticker.toUpperCase())) {
            return map.get(ticker.toUpperCase());
        }
        return "не удалось найти указнный тикер";
    }

    @Override
    public Map<String, String> getAllImoexData()  {
        String json = null;
        try {
            json = imoexClient.getCurrencyPrice();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        imoexData data = null;
        try {
            data = mapper.readValue(json, imoexData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> map = data.createMap();

        return map;
    }

}
