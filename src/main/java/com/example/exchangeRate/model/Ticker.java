package com.example.exchangeRate.model;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "tickers")

public class Ticker {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ticker;

    private String price;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "tguser_id", referencedColumnName = "id")
    private Tguser tguser;

    public Ticker() {
    }

    public Ticker(int id, String ticker, String price) {
        this.id = id;
        this.ticker = ticker;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Tguser getTguser() {
        return tguser;
    }

    public void setTguser(Tguser tguser) {
        this.tguser = tguser;
    }
}
