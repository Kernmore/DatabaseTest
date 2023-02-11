package com.example.testproject.bankproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

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

    public static Double getPrivatBankAPI(Currency currency1, Currency currency2, Double amount) {
        Gson gson = new GsonBuilder().create();
        Double exchangeRate = 0d;
        try {
            URL url = new URL("https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5");
            HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
            InputStream is = httpUrl.getInputStream();
            try {
                byte[] buf = responseBodyToArray(is);
                String strBuf = new String(buf, StandardCharsets.UTF_8);

                //a new class created to take the values from Gson
                PrivatAPI[] apis = gson.fromJson(strBuf, PrivatAPI[].class);
                List<PrivatAPI> strings = Arrays.asList(apis);

                for (PrivatAPI api : strings) {
                    if (api.getCcy().equals(currency1.toString())) {
                        exchangeRate = api.getBuy();
                        amount *= exchangeRate;

                    } else if (api.getBase_ccy().equals(currency1.toString())) {
                        if (api.getCcy().equals(currency2.toString())) {
                            exchangeRate = api.getSale();
                            amount /= exchangeRate;
                        }
                    }
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return amount;
    }

    private static byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
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


}