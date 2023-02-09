package com.example.testproject.ordersproject;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "customers")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Order> listOrders;

    public Client(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Client() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setListOrders(List<Order> listOrders) {
        this.listOrders = listOrders;
    }
}
