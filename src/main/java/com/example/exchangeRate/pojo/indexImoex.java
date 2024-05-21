package com.example.exchangeRate.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class indexImoex {
    private analytics analytics;

    public Map<String, String> createMap() {
        Map<String, String> map = new HashMap<>();
        String[][] strings = analytics.getData();
        for (String[] str : strings) {
            if (Double.parseDouble(str[5])>1) {
                map.put(str[2], str[3]);
            }
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
class analytics{

    private String[][] data;

    public String[][] getData() {
        return data;
    }
}



