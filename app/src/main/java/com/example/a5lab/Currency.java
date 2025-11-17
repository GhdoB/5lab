package com.example.a5lab;

public class Currency {
    private String code;
    private String name;
    private double exchangeRate;

    public Currency(String code, String name, double exchangeRate) {
        this.code = code;
        this.name = name;
        this.exchangeRate = exchangeRate;
        System.out.println("Currency: Created " + code + " with rate " + exchangeRate);
    }

    public String getCode() {
        System.out.println("Currency: Getting code " + code);
        return code;
    }

    public String getName() {
        System.out.println("Currency: Getting name " + name);
        return name;
    }

    public double getExchangeRate() {
        System.out.println("Currency: Getting exchange rate " + exchangeRate);
        return exchangeRate;
    }

    @Override
    public String toString() {
        return code + " - " + exchangeRate;
    }
}