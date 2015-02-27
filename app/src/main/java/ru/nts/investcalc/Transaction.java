package ru.nts.investcalc;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Николай on 01.02.2015.
 */
@DatabaseTable(tableName="Transactions")
public class Transaction {

    public enum TransactionType { BUY, SELL, }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String stockCode;

    @DatabaseField
    private TransactionType transactionType;

    @DatabaseField
    private Date transactionDate;

    @DatabaseField
    private double price;

    @DatabaseField
    private double count;

    @DatabaseField
    private double sum;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private StockQuote stockQuote;

    public Transaction () {}

    public Transaction(StockQuote stockQuote, TransactionType transactionType, Date transactionDate, double price, double count, double sum) {
        this.stockQuote = stockQuote;
        this.stockCode = stockQuote.getCode();
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.price = price;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
        return stockQuote;
    }

    public void setStockQuote(StockQuote stockQuote) {
        this.stockQuote = stockQuote;
    }

}
