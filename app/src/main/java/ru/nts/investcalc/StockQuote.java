package ru.nts.investcalc;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Николай on 01.02.2015.
 */
@DatabaseTable(tableName = "StockQuotes")
public class StockQuote {

    @DatabaseField(id = true, index = true)
    private String code;

    @DatabaseField
    private String name;

    @DatabaseField
    private double price;

    public StockQuote() { }

    public StockQuote(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceString() { return String.format("%.6f", this.price); }

    public void setPrice(double price) {
        this.price = price;
    }

}
