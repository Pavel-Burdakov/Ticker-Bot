package com.example.exchangeRate.client;

import com.example.exchangeRate.exception.serviceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class imoexClient {
    @Autowired
    OkHttpClient okHttpClient;

    @Value("${imoex.currency.rates.all.url}")
    String urlRates;

    @Value("${imoex.list.index.url}")
    String urlIndexList;

    public String getCurrencyPrice() throws serviceException {
        // формируем запрос
        var request = new Request.Builder()
                .url(urlRates)
                .build();

        // выполняем запрос и сохраняем ответ в переменной response
        // конструкция auto close и может выбрасывать исключения, поэтому try with resources
        try (var response = okHttpClient.newCall(request).execute();) {
            var body = response.body();
            if (body == null) {
                return null;
            }
            return body.string();

        } catch (IOException e) {
            throw new serviceException("Ошибка получения котировок акций", e);
        }

    }

    public String getCurrencyIndexList() throws serviceException {
        {
            // формируем запрос
            var request = new Request.Builder()
                    .url(urlIndexList)
                    .build();

            // выполняем запрос и сохраняем ответ в переменной response
            // конструкция auto close и может выбрасывать исключения, поэтому try with resources
            try (var response = okHttpClient.newCall(request).execute();) {
                var body = response.body();
                if (body == null) {
                    return null;
                }
                return body.string();

            } catch (IOException e) {
                throw new serviceException("Ошибка получения котировок акций", e);
            }

        }

    }


}
