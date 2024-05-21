package com.example.exchangeRate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Tguser")
public class Tguser {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long chatid;

    private String tickers;

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
}
