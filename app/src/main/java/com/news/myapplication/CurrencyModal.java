package com.news.myapplication;

public class CurrencyModal {
    // variable for currency name,
    // currency symbol and price.
    private String name;


    public CurrencyModal(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
