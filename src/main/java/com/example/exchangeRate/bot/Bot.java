package com.example.exchangeRate.bot;

import com.example.exchangeRate.exception.serviceException;
import com.example.exchangeRate.model.Tguser;
import com.example.exchangeRate.repo.TgUserRepository;
import com.example.exchangeRate.services.ExchangeRateService;
import com.example.exchangeRate.services.TickerPriceService;
import com.example.exchangeRate.services.IndexService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private TickerPriceService tickerPriceService;

    @Autowired
    private IndexService indexService;

    @Autowired
    TgUserRepository userRepository;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM.dd.yyyy, HH:mm:ss");
    private final int countRowInLine = 3; // количество строк кнопок с выбором акций для отслеживания

    private Set<String> setUserChoise = new HashSet<>(); // сюда складываем названия акций, выбранных пользователем
    private Set<String> setUserChoiseTickers = new HashSet<>(); // сюда тикеры выбранных акций
    private static final String START = "/start";
    private static final String EXCHANGERATES = "/exchangerates";
    private static final String SETTINGS = "/settings";
    private static final String HELP = "/help";
    private static final String ALL = "/all";

    private long idChat = 0;

    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Привет"));
        listofCommands.add(new BotCommand("/exchangerates", "курс валют к рублю"));
        listofCommands.add(new BotCommand("/settings", "выбрать тикеры для отслеживания"));
        listofCommands.add(new BotCommand("/all", "получить текущие котировки акций из IMOEX"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    /**
     * Основной метод боты для обработки команд пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {
        // для проверки есть ли вообще сообщение от пользователя
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText(); //если сообщение пришло вытаскиваем его в отдельную переменную
            var chatId = update.getMessage().getChatId(); //идентификатор чата, чтобы ответить тому пользователю от которого получен запрос
            idChat = chatId;
            //  проверяем значение из сообщения на соответствие одной из команд и обрабатываем его пока обработка в методах здесь же, но лучше вынести для каждого обработку в отдельный класс
            switch (message) {
                case START -> {
                    String userName = update.getMessage().getChat().getUserName();
                    startCommand(chatId, userName);
                }
                case EXCHANGERATES -> ExchangeRatesCommand(chatId);
                case SETTINGS -> {
                    setUserChoise.clear();
                    setUserChoiseTickers.clear();
                    register(chatId);
                }
                case HELP -> helpCommand(chatId);
                case ALL -> sendIndexImoexData();
                default -> unknownCommand(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            createUserTickerList(update);
        }

    }

    // возвращает имя бота
    @Override
    public String getBotUsername() {
        return "pavel_b_exchangeBot";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                                                
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                Следить за изменением цены акций, входящих в индекс Московской биржи
                
                Выбрать тикеры для отслеживания и получать уведомления об изменении цен на них.
                    - Эта опция в разработке
                    Е ху!
                                
                Для этого воспользуйтесь командами:
                /exchangerates - курс доллара
                /all - курс акций, входящих в индекс Московской биржи
                /settings - выбрать акции для отслеживания
                                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);

        if (userRepository.findByChatid(chatId).isEmpty()) {
            Tguser tguser = new Tguser();
            tguser.setChatid(chatId);
            userRepository.save(tguser);
        }
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //LOG.log("Ошибка отправки сообщения", e);
        }
    }

    private void ExchangeRatesCommand(Long chatId) {
        StringBuilder formattedText = new StringBuilder();
        try {
            var usd = exchangeRateService.getUSDCurrencyRate();
            var eur = exchangeRateService.getEURCurrencyRate();
            formattedText.append("Курс ключевых валют к рублю на " + LocalDateTime.now().format(dtf) + " составляет \n" + usd + " рублей за доллар США \n" + eur + " рублей за евро");
        } catch (serviceException e) {
            e.getMessage();
        }
        sendMessage(chatId, formattedText.toString());
    }

    /**
     * Метод для получения текущих котировки заданной пользователем акции
     */
    private void sendCurrentTickerPrice(Long chatId, Update update) throws Exception {
        String formattedText = null;
        try {
            var ticker = tickerPriceService.getTickerCurrencyPrice(update.getMessage().getText());
            var text = "Цена %s на %s составляет %s рублей за акцию";
            formattedText = String.format(text, update.getMessage().getText(), LocalDateTime.now().format(dtf), ticker);
        } catch (serviceException e) {
            e.getMessage();
        }
        sendMessage(chatId, formattedText);
    }

    /**
     * Метод для получения текущей цены всех акций, торгуемых на Московской бирже
     */
    private void sendAllImoexData(Long chatId) throws serviceException, JsonProcessingException {
        Map<String, String> map = tickerPriceService.getAllImoexData();
        StringBuilder text = new StringBuilder();
        text.append("Котировки акций IMOEX на " + LocalDateTime.now().format(dtf) + "\n");
        String formattedText = null;
        int cnt = 0;
        for (Map.Entry<String, String> item : map.entrySet()) {
            text.append(item.getKey() + " = " + item.getValue() + "\n");
        }
        sendMessage(idChat, String.valueOf(text));
    }

    /**
     * Метод для получения текущих котировок акций IMOEX, входящих в индекс с долей более 1%
     */
    private void sendIndexImoexData() {
        Map<String, String> mapAllTickers = null;
        try {
            mapAllTickers = tickerPriceService.getAllImoexData();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> mapIndextickers = null;
        try {
            mapIndextickers = indexService.getIndexList();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder text = new StringBuilder();
        text.append("Котировки акций IMOEX, входящих в индекс с долей более 1% на " + LocalDateTime.now().format(dtf) + "\n");
        for (Map.Entry<String, String> item : mapIndextickers.entrySet()) {
            if (mapAllTickers.containsKey(item.getKey())) {
                text.append(item.getValue() + " " + item.getKey() + " = " + mapAllTickers.get(item.getKey()) + "\n");
            }
        }
        sendMessage(idChat, String.valueOf(text));
    }

    @Scheduled(fixedRate = 30000)
    private void sendCurrentPrices() throws serviceException, JsonProcessingException {
        //sendIndexImoexData();

    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                              
                                
                                
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

    private void register(long chatId) {

        Map<String, String> mapIndextickers = null;

        try {
            mapIndextickers = indexService.getIndexList();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите акции, котировки которых вы хотите отслеживать");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        for (Map.Entry<String, String> item : mapIndextickers.entrySet()) {

            rowInLine.add(new InlineKeyboardButton());
            rowInLine.getLast().setText(item.getValue());
            rowInLine.getLast().setCallbackData(item.getValue());

        }
        rowInLine.add(new InlineKeyboardButton());
        rowInLine.getLast().setText("Сохранить");
        rowInLine.getLast().setCallbackData("Save");

        rowInLine.add(new InlineKeyboardButton());
        rowInLine.getLast().setText("Отменить");
        rowInLine.getLast().setCallbackData("Cancel");


/*
        for (int j = 0; j < rowInLine.size(); j += countRowInLine) {
            rowsInLine.add(new ArrayList<>(rowInLine.subList(j, Math.min(rowInLine.size(), j + countRowInLine))));

        }
*/
        rowsInLine = Lists.partition(rowInLine, countRowInLine); // guava для разделения листа

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);

    }

    /**
     * Метод формирует список тикеров для последующего отслеживания цены для и записывает его в БД     *
     */
    private void createUserTickerList(Update update) {
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        Tguser tguser = new Tguser();

        Map<String, String> map = null;
        try {
            map = indexService.getIndexList();
        } catch (serviceException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HashBiMap hashBiMap = HashBiMap.create(map);

        switch (update.getCallbackQuery().getData()) {
            case "Save" -> {
                String text = "Вы выбрали для отслеживания следующие акции  " + "\n\n" + setUserChoise.toString() + "\n\n" + "Если необходимо изменить выбор нажмите /settings ";
                executeEditMessageText(text, chatId, messageId);

                if (userRepository.findByChatid(chatId).isEmpty()) {
                    tguser.setChatid(chatId);
                    tguser.setTickers(setUserChoiseTickers.toString());
                    userRepository.save(tguser);
                } else {
                    tguser = userRepository.findByChatid(chatId).orElseThrow();
                    tguser.setTickers(setUserChoiseTickers.toString());
                    userRepository.save(tguser);
                }
            }
            case "Cancel" -> setUserChoise.clear();
            default -> {
                setUserChoise.add(update.getCallbackQuery().getData());
                setUserChoiseTickers.add((String) hashBiMap.inverse().get(update.getCallbackQuery().getData()));
            }
        }

    }

    /**
     * Метод изменяет текст, после выбора пользователем значений в меню
     */

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Вспомогательный метод отправки сообщения, для того чтобы сократить количество блоков try-cath
     */
    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

}
