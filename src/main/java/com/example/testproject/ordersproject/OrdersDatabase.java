package com.example.testproject.ordersproject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class OrdersDatabase {

    private static final String NAME = "TestConnect";

    private EntityManagerFactory emFactory;
    private EntityManager em;

    public OrdersDatabase(){
        emFactory = Persistence.createEntityManagerFactory(NAME);
        em = emFactory.createEntityManager();
    }

    private  <T> T performTransaction(Callable<T> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            T result = action.call();
            transaction.commit();

            return result;
        } catch (Exception ex) {
            if (transaction.isActive())
                transaction.rollback();

            throw new RuntimeException(ex);
        }
    }

    public int addClient(Client client){
        return performTransaction(() -> {
            em.persist(client);
            return client.getId();
        });
    }

    public int addGoods(Goods goods){
        return performTransaction(() -> {
            em.persist(goods);
            return goods.getId();
        });
    }

    public Calendar createOrder(Client client, List<Goods> goods){
        Order newOrder = new Order(client, goods);
        return performTransaction(() -> {
            em.persist(newOrder);
            return newOrder.getDate();
        });
    }

    private void close(){
        em.close();
        emFactory.close();
    }

    public static void main(String[] args) {
        OrdersDatabase od = new OrdersDatabase();

        Client client1 = new Client("Mihail", "Lychakiv");
        Client client2 = new Client("Roma", "Monastir");
        Client client3 = new Client("Kerno", "Mozaiv 3");

        Goods goods1 = new Goods("Ball", 43);
        Goods goods2 = new Goods("Beat", 22);
        Goods goods3 = new Goods("Simple-dimple", 50);
        Goods goods4 = new Goods("nike", 12.43);

        od.addClient(client1);
        od.addClient(client2);
        int client3Id = od.addClient(client3);
        System.out.println(client3Id);

        od.addGoods(goods1);
        od.addGoods(goods2);
        od.addGoods(goods3);
        od.addGoods(goods4);

        od.createOrder(client2, List.of(goods1, goods2, goods3));
        Calendar cal = od.createOrder(client1, List.of(goods1, goods4, goods3));
        System.out.println(cal.getTime());

        for ( ; ;){
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            if(s.equals("q")){
                break;
            }
        }

        od.close();

    }
}
