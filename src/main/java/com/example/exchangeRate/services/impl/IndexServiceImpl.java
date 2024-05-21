package com.example.exchangeRate.services.impl;

import com.example.exchangeRate.client.imoexClient;
import com.example.exchangeRate.exception.serviceException;
import com.example.exchangeRate.model.Ticker;
import com.example.exchangeRate.pojo.indexImoex;
import com.example.exchangeRate.repo.TickerRepository;
import com.example.exchangeRate.services.IndexService;
import com.example.exchangeRate.services.TickerPriceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    imoexClient imoexClient;
    @Autowired
    TickerPriceService tickerPriceService;


    @Autowired
    TickerRepository tickerRepository;

    @Override
    public Map<String, String> getIndexList()  {
        String json = null;
        try {
            json = imoexClient.getCurrencyIndexList();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        indexImoex indexImoex = null;
        try {
            indexImoex = mapper.readValue(json, com.example.exchangeRate.pojo.indexImoex.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> map = indexImoex.createMap();
        return map;
    }
    @Override
    public Map<String, String> getIndexImoexData(){
        Map<String,String> returnMap = null;
        Map<String, String> mapAllTickers = null;
        try {
            mapAllTickers = tickerPriceService.getAllImoexData();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> mapIndexTickers = null;
        mapIndexTickers = getIndexList();
        //StringBuilder text = new StringBuilder();
        //text.append("Котировки акций IMOEX, входящих в индекс с долей более 1% на " + LocalDateTime.now().format(dtf) + "\n");
        for (Map.Entry<String, String> item : mapIndexTickers.entrySet()) {
            if (mapAllTickers.containsKey(item.getKey())) {
                returnMap.put(item.getKey(), mapAllTickers.get(item.getKey()));
                //text.append(item.getValue() + " " + item.getKey() + " = " + mapAllTickers.get(item.getKey()) + "\n");
            }
        }
        return returnMap;
    }


    @Override
    public void saveIndexPriceToDataBase() {
        Map<String, String> mapAllTickers = null;
        try {
            mapAllTickers = tickerPriceService.getAllImoexData();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> mapIndextickers = null;
        mapIndextickers = getIndexList();

        for (Map.Entry<String, String> item : mapIndextickers.entrySet()) {
            if (mapAllTickers.containsKey(item.getKey())) {
                if (!tickerRepository.findByTicker(item.getKey()).isPresent()){
                    //text.append(item.getValue() + " " + item.getKey() + " = " + mapAllTickers.get(item.getKey()) + "\n");

                    Ticker ticker = new Ticker();
                    ticker.setTicker(item.getKey());
                    ticker.setPrice(mapAllTickers.get(item.getKey()));
                    tickerRepository.save(ticker);
                }
            }
        }
    }
}
