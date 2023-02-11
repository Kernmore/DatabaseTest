package com.example.testproject.bankproject;

import javax.persistence.*;
import java.util.List;

public class BankOperations {
    private static EntityManagerFactory emFactory;
    private static EntityManager em;


    public static void main(String[] args) {
        emFactory = Persistence.createEntityManagerFactory("Bank");
        em = emFactory.createEntityManager();

        User user1 = new User("John");
        user1.setAccounts(Account.autoCreation(user1));
        User user2 = new User("Mark");
        user2.setAccounts(Account.autoCreation(user1));
        em.persist(user1);
        em.persist(user2);

        // Depositing funds in a required currency
        depositFunds(user1.getId(), Currency.USD, 100d);

        // Transferring funds from one account to another
        transferFunds(user1.getId(), Currency.USD, user2.getId(), Currency.UAH, 50d);

        // Currency conversion at the exchange rate within accounts of one user
        convertCurrency(user1.getId(), Currency.EUR, Currency.UAH, 70d);

        // Get total funds on one user's account in UA
        Double total1 = getTotalFundsInUAH(user1.getId());
        System.out.printf("Total funds in UAH: %.2f\n", total1);
        System.out.println();
        Double total2 = getTotalFundsInUAH(user2.getId());
        System.out.printf("Total funds in UAH: %.2f\n", total2);

        // Check every account balance for one user
        checkEveryBalance(user1.getId());

        checkEveryBalance(user2.getId());


        em.close();
        emFactory.close();
    }

    private static void depositFunds(Long userId, Currency currency, Double amount) {
        User user = em.find(User.class, userId);
        Account account = getAccountForUserAndType(user, currency);
        account.setBalance(account.getBalance() + amount);
        em.persist(account);
    }

    private static void transferFunds(Long fromUserId, Currency fromCurrency, Long toUserId, Currency toCurrency, Double amount) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        User fromUser = em.find(User.class, fromUserId);
        Account fromAccount = getAccountForUserAndType(fromUser, fromCurrency);
        Double converted = amount;
        if (fromCurrency.equals(toCurrency)) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            em.persist(fromAccount);
        } else {
            Account sameCurrencyAcc = getAccountForUserAndType(fromUser, toCurrency);
            converted = convertCurrency(fromUserId, fromCurrency, toCurrency, amount);
            sameCurrencyAcc.setBalance(sameCurrencyAcc.getBalance() - converted);
        }


        User toUser = em.find(User.class, toUserId);
        Account toAccount = getAccountForUserAndType(toUser, toCurrency);
        toAccount.setBalance(toAccount.getBalance() + (converted));

        em.persist(toAccount);

        transaction.commit();
    }

    //The method uses the PrivatBank API to get the exchange rate, it is only possible to change the currency from/to UAH
    private static Double convertCurrency(Long userId, Currency fromCurrency, Currency toCurrency, Double amount) {

        User user = em.find(User.class, userId);
        Account fromAccount = getAccountForUserAndType(user, fromCurrency);
        Account toAccount = getAccountForUserAndType(user, toCurrency);

//        ExchangeRate ex = new ExchangeRate(fromCurrency, toCurrency);

//        Double exchangeRate =  //ex.getRate();
        //the method takes the from & to currencies, and the amount to calculate the difference
        Double convertedAmount = ExchangeRate.getPrivatBankAPI(fromCurrency, toCurrency, amount);

        fromAccount.setBalance(fromAccount.getBalance() - (amount));
        toAccount.setBalance(toAccount.getBalance() + (convertedAmount));

        em.persist(fromAccount);
        em.persist(toAccount);

        return convertedAmount;

    }

    private static Double getTotalFundsInUAH(Long userId) {
        User user = em.find(User.class, userId);
        List<Account> accounts = user.getAccounts();

        Double totalAmountInUAH = 0d;
        for (Account account : accounts) {

            Currency accountType = account.getCurrency();
            ExchangeRate ex = new ExchangeRate(accountType, Currency.UAH);
            Double exchangeRate = ex.getRate();
//            System.out.println(account.getBalance() + " " + accountType);
            if (accountType.equals(Currency.UAH)) {
                totalAmountInUAH += account.getBalance();
            } else {
                totalAmountInUAH += Math.abs(account.getBalance() * (exchangeRate));
            }


        }
        return totalAmountInUAH;
    }

    private static void checkEveryBalance(Long userId) {
        User user = em.find(User.class, userId);
        List<Account> accounts = user.getAccounts();

        for (Account account : accounts) {
            Currency accountType = account.getCurrency();
            ExchangeRate ex = new ExchangeRate(accountType, Currency.UAH);
            Double exchangeRate = ex.getRate();
            System.out.println(account.getBalance() + " " + accountType);
        }
    }


    private static Account getAccountForUserAndType(User user, Currency currency) {
        List<Account> accounts = user.getAccounts();
        for (Account account : accounts) {
            if (account.getCurrency().equals(currency)) {
                return account;
            }
        }
        return null;
    }

//    private Double getExchangeRate(String fromAccountType, String toAccountType) {
//        TypedQuery<ExchangeRate> query = em.createQuery("SELECT e FROM ExchangeRate e WHERE  ")
//
//    }
}
