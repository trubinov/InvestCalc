package ru.nts.investcalc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Николай on 01.02.2015.
 */
public class MyDatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String LOG_TAG = MyDatabaseHelper.class.getName();

    // name of the database file for your application
    private static final String DATABASE_NAME = "investcalc.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 2;

    private Dao<Transaction, Integer> transactions = null;
    private RuntimeExceptionDao<Transaction, Integer> transactionRuntimeDao = null;

    private Dao<StockQuote, String> stockQuotes = null;
    private RuntimeExceptionDao<StockQuote, String> stockQuoteRuntimeDao = null;

    private Dao<Portfolio, Integer> portfolios = null;
    private RuntimeExceptionDao<Portfolio, Integer> portfolioRuntimeDao = null;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, StockQuote.class);
            Log.i(LOG_TAG, "Create StockQuotes table");
            TableUtils.createTable(connectionSource, Transaction.class);
            Log.i(LOG_TAG, "Create Transactions table");
            TableUtils.createTable(connectionSource, Portfolio.class);
            Log.i(LOG_TAG, "Create Portfolio table");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, StockQuote.class, true);
            TableUtils.dropTable(connectionSource, Transaction.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(MyDatabaseHelper.class.getName(), "Can't upgrade database", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Transaction, Integer> getTransactions() throws SQLException {
        if (transactions == null) {
            transactions = getDao(Transaction.class);
        }
        return transactions;
    }

    public RuntimeExceptionDao<Transaction, Integer> getTransactionRuntimeDao () {
        if (transactionRuntimeDao == null) {
            transactionRuntimeDao = getRuntimeExceptionDao(Transaction.class);
        }
        return transactionRuntimeDao;
    }

    public Dao<StockQuote, String> getStockQuotes() throws SQLException {
        if (stockQuotes == null) {
            stockQuotes = getDao(StockQuote.class);
        }
        return stockQuotes;
    }

    public RuntimeExceptionDao<StockQuote, String> getStockQuoteRuntimeDao() {
        if (stockQuoteRuntimeDao == null) {
            stockQuoteRuntimeDao = getRuntimeExceptionDao(StockQuote.class);
        }
        return stockQuoteRuntimeDao;
    }

    public Dao<Portfolio, Integer> getPortfolios() throws SQLException {
        if (portfolios == null) {
            portfolios = getDao(Portfolio.class);
        }
        return portfolios;
    }

    public RuntimeExceptionDao<Portfolio, Integer> getPortfolioRuntimeDao () {
        if (portfolioRuntimeDao == null) {
            portfolioRuntimeDao = getRuntimeExceptionDao(Portfolio.class);
        }
        return portfolioRuntimeDao;
    }

    private int addBuyTransaction(Transaction newTransaction) {
        // В транзакция покупки нужно высчитать новую стоимость бумаги
        // при необходимости добавить новую
        Dao<Portfolio, Integer> _portfolio;
        try {
            _portfolio = getPortfolios();
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

        Portfolio p = null;
        try {
            QueryBuilder<Portfolio, Integer> qbPortfolio = _portfolio.queryBuilder();
            qbPortfolio.where().eq("stockCode", newTransaction.getStockCode());
            PreparedQuery<Portfolio> portfolioPreparedQuery = qbPortfolio.prepare();
            List<Portfolio> portfolioList = _portfolio.query(portfolioPreparedQuery);
            if (portfolioList.size() > 0) {
                p = portfolioList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }

        double newPrice = 0;
        double newCount = 0;
        double newSum = 0;
        if (p != null) {
            newCount = p.getCount() + newTransaction.getCount();
            newSum = p.getSum() + newTransaction.getSum();
            if (newCount > 0) {
                newPrice = newSum / newCount;
            }
        } else {
            p = new Portfolio();
            p.setStockCode(newTransaction.getStockCode());
            newPrice = newTransaction.getPrice();
            newCount = newTransaction.getCount();
            newSum = newTransaction.getSum();
        }
        p.setAvePrice(newPrice);
        p.setCount(newCount);
        p.setSum(newSum);

        try {
            _portfolio.createOrUpdate(p);
        } catch (SQLException e) {
            e.printStackTrace();
            return 3;
        }

        try {
            Dao<Transaction, Integer> _transactions = getTransactions();
            _transactions.createOrUpdate(newTransaction);
        } catch (SQLException e) {
            e.printStackTrace();
            return 4;
        }

        return 0;   // OK
    }

    private int addSellTransaction(Transaction newTransaction) {
        // В транзакция продажи нужно высчитать новую стоимость бумаги
        // при необходимости удалить ненужную запись из таблицы Портфолио
        Dao<Portfolio, Integer> _portfolio;
        try {
            _portfolio = getPortfolios();
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

        Portfolio p = null;
        try {
            QueryBuilder<Portfolio, Integer> qbPortfolio = _portfolio.queryBuilder();
            qbPortfolio.where().eq("stockCode", newTransaction.getStockCode());
            PreparedQuery<Portfolio> portfolioPreparedQuery = qbPortfolio.prepare();
            List<Portfolio> portfolioList = _portfolio.query(portfolioPreparedQuery);
            if (portfolioList.size() > 0) {
                p = portfolioList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }

        if (p != null) {
            double newPrice = 0;
            double newCount = 0;
            double newSum = 0;
            newCount = p.getCount() - newTransaction.getCount();
            newSum = p.getSum() - newTransaction.getSum();
            if (newCount > 0) {
                // высчитаем новую цену бумаги и установим значения в таблицу
                newPrice = newSum / newCount;
                p.setAvePrice(newPrice);
                p.setCount(newCount);
                p.setSum(newSum);
                try {
                    _portfolio.createOrUpdate(p);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return 3;
                }
            } else {
                // продаются остатки бумаг - нужно удалить запись из таблицы Портфолио
                try {
                    _portfolio.delete(p);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return 4;
                }
            }
        }

        try {
            Dao<Transaction, Integer> _transactions = getTransactions();
            _transactions.createOrUpdate(newTransaction);
        } catch (SQLException e) {
            e.printStackTrace();
            return 5;
        }

        return 0;   // OK
    }

    public void safeAddTransaction(Transaction newTransaction) {
        switch (newTransaction.getTransactionType()) {
            case BUY:
                addBuyTransaction(newTransaction);
                break;
            case SELL:
                addSellTransaction(newTransaction);
                break;
            default:
                break;
        }
    }

    @Override
    public void close() {
        super.close();
        transactions = null;
        transactionRuntimeDao = null;
    }

}
