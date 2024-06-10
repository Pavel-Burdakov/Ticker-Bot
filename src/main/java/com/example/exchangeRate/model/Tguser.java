package com.example.exchangeRate.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tguser")

public class Tguser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long chatid;
    @Null
    Double delta;
    @Null
    int interval;

    private String tickers;

    @OneToMany(mappedBy = "tguser", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private List<Ticker> tickerList;

    public Tguser() {
    }

    public Tguser(int id, long chatid, String tickers) {
        this.id = id;
        this.chatid = chatid;
        this.tickers = tickers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getChatid() {
        return chatid;
    }

    public void setChatid(long chatid) {
        this.chatid = chatid;
    }

    public String getTickers() {
        return tickers;
    }

    public void setTickers(String tickers) {
        this.tickers = tickers;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public List<Ticker> getTickerList() {
        return tickerList;
    }

    public void setTickerList(List<Ticker> tickerList) {
        this.tickerList = tickerList;
    }

    private void addTicker(Ticker ticker) {
        if (this.tickerList == null) {
            this.tickerList = new ArrayList<>();
            this.tickerList.add(ticker);
            ticker.setTguser(this);
        }
    }
}
