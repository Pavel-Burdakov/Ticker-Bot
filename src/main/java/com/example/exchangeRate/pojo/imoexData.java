package com.example.exchangeRate.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class imoexData {
    @Autowired
    private marketdata marketdata;

    public com.example.exchangeRate.pojo.marketdata getMarketdata() {
        return marketdata;
    }

    public Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        String[][] strings = marketdata.getData();
        for (String[] str : strings) {
            map.put(str[0], str[1]);
        }
        //System.out.println(map);
        return map;
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
class marketdata {
    // private String[] columns;
    private String[][] data;
    public String[][] getData() {
        return data;
    }

}
