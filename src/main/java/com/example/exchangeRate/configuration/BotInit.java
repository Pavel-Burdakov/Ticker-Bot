package com.example.exchangeRate.configuration;

import com.example.exchangeRate.bot.Bot;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableScheduling
@Component
public class BotInit {

    /**
     * Этим методом мы сообщаем библиотеке, что создали класс для обработки телеграмм запросов
     * в качестве аргумента прилетает экземпляр нашего класса и нам нужно зарегистрировать его
     * в TelegramBotsApi возвращаем экземпляр класса с зарегистрированным ботом.
     */

    @Bean
    public TelegramBotsApi telegramBotsApi(Bot Bot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(Bot);
        return api;
    }

    /**
     * http клиент для запросов к API cbr и  IMOEX
     */

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

}
