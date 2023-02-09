package com.example.testproject.bankproject;

import javax.persistence.*;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Currency fromCurrency;

    private Currency toCurrency;

    private Double rate;

    public ExchangeRate(Currency fromCurrency, Currency toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public ExchangeRate() {
    }

    public Long getId() {
        return id;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getRate() {
        return formulaRate(fromCurrency, toCurrency);
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    private Double formulaRate(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(Currency.UAH)) {
            if (toCurrency.equals(Currency.EUR)) {
                rate = 0.025;
            } else {
                rate = 0.027;
            }
        } else if (fromCurrency.equals(Currency.EUR)) {
            if (toCurrency.equals(Currency.USD)) {
                rate = 1.07;
            } else {
                rate = 39.44;
            }
        } else if (fromCurrency.equals(Currency.USD)) {
            if (toCurrency.equals(Currency.EUR)) {
                rate = 0.93;
            } else {
                rate = 36.72;
            }
        } else {
            rate = 1d;
        }
        return rate;
    }

}