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

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private StockQuote stockQuote;

    public Portfolio() { }

    public Portfolio(StockQuote stockQuote, double avePrice, double count, double sum) {
        this.stockQuote = stockQuote;
        this.stockCode = stockQuote.getCode();
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

    public StockQuote getStockQuote() {
        return this.stockQuote;
    }

    public void setStockQuote(StockQuote stockQuote) {
        this.stockQuote = stockQuote;
    }

    public String getStockName() {
        if (stockQuote != null)
            return stockQuote.getName();
        return "неизвестная бумага";
    }

    public double getActualPrice() {
        if (stockQuote != null)
            return stockQuote.getPrice();
        return 0;
    }
}
