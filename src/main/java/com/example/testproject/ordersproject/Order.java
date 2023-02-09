package com.example.testproject.ordersproject;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;


@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToMany
    @JoinTable(name = "order_goods",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "goods_id"))
    private List<Goods> goodsList;

    private Calendar date;

    public Order(Client client, List<Goods> goodsList) {
        this.client = client;
        this.goodsList = goodsList;
        this.date = Calendar.getInstance();
    }

    public Order() {    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}



