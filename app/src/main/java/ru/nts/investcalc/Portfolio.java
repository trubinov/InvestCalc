package ru.nts.investcalc;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Николай on 01.02.2015.
 */
@DatabaseTable(tableName = "Portfolio")
public class Portfolio {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String stockCode;

    @DatabaseField
    private double avePrice;

    @DatabaseField
    private double count;

    @DatabaseField
    private double sum;

    public Portfolio() { }

    public Portfolio(String stockCode, double avePrice, double count, double sum) {
        this.stockCode = stockCode;
        this.avePrice = avePrice;
        this.count = count;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public double getAvePrice() {
        return avePrice;
    }

    public void setAvePrice(double avePrice) {
        this.avePrice = avePrice;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
