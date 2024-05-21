package com.example.exchangeRate.services;

import com.example.exchangeRate.exception.serviceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface IndexService {

    Map<String, String> getIndexList() throws serviceException, JsonProcessingException;
    void saveIndexPriceToDataBase();
    public Map<String, String> getIndexImoexData();
}
