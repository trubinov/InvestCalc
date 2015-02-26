package ru.nts.investcalc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddTransactionActivity extends ActionBarActivity {

    EditText etDate;
    Spinner spnOpType;
    EditText etPrice;
    EditText etCount;
    Button btnSelectStock;
    Button btnAddTransaction;
    SimpleDateFormat simpleDateFormat;
    String stockCode;
    StockQuote stockQuote;
    Date transactionDate;
    Transaction.TransactionType transactionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etDate = (EditText) findViewById(R.id.etTransactionDate);
        spnOpType = (Spinner) findViewById(R.id.spnOpType);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etCount = (EditText) findViewById(R.id.etCount);
        btnSelectStock = (Button) findViewById(R.id.btnSelectStock);
        btnAddTransaction = (Button) findViewById(R.id.btnAddTransaction);

        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                Calendar nowCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(AddTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dateText = String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear+1, year);
                        try {
                            transactionDate = simpleDateFormat.parse(dateText);
                        } catch (ParseException e) {
                            transactionDate = new Date();
                            e.printStackTrace();
                        }
                        etDate.setText(dateText);
                    }
                }, nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        btnSelectStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentQuoteList = new Intent(AddTransactionActivity.this, QuoteListActivity.class);
                intentQuoteList.putExtra("ChooseQuote", true);
                startActivityForResult(intentQuoteList, 1);
            }
        });

        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDatabaseHelper = MyDatabaseManager.getInstance().getDatabaseHelper();
                Transaction transaction = new Transaction();
                transaction.setTransactionDate(transactionDate);
                transaction.setStockCode(stockCode);
                transaction.setStockQuote(stockQuote);
                transaction.setPrice(Double.parseDouble(etPrice.getText().toString()));
                transaction.setCount(Double.parseDouble(etCount.getText().toString()));
                transaction.setSum(transaction.getPrice() * transaction.getCount());
                if (transactionType != null) {
                    transaction.setTransactionType(transactionType);
                    myDatabaseHelper.safeAddTransaction(transaction);
                    finish();
                } else {
                    // Error: empty transaction type
                }
            }
        });

        // выбор типа транзакции
        ArrayAdapter<Transaction.TransactionType> opTypeAdapter;
        opTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Transaction.TransactionType.values());
        spnOpType.setAdapter(opTypeAdapter);
        spnOpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transactionType = (Transaction.TransactionType) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                transactionType = null;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_transaction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if ((requestCode == 1) && (resultCode == RESULT_OK)) {
                // share quote selected from child activity
                stockCode = data.getStringExtra("ShareQuote");
                try {
                    Dao<StockQuote, String> stockQuotes = MyDatabaseManager.getInstance().getDatabaseHelper().getStockQuotes();
                    stockQuote = stockQuotes.queryForId(stockCode);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                btnSelectStock.setText(stockCode);
            }
        }
    }
}
