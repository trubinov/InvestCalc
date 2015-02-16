package ru.nts.investcalc;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AddTransactionActivity extends ActionBarActivity {

    EditText etDate;
    Spinner spnOpType;
    EditText etPrice;
    EditText etCount;
    Button btnSelectStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etDate = (EditText) findViewById(R.id.etTransactionDate);
        spnOpType = (Spinner) findViewById(R.id.spnOpType);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etCount = (EditText) findViewById(R.id.etCount);
        btnSelectStock = (Button) findViewById(R.id.btnSelectStock);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                sdf.format(now);
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(AddTransactionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dateText = String.format("%02d.%02d.%04d", dayOfMonth, monthOfYear+1, year);
                        etDate.setText(dateText);
                    }
                }, 2015, 0, 1);
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

        ArrayAdapter<Transaction.TransactionType> opTypeAdapter;
        opTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Transaction.TransactionType.values());
        spnOpType.setAdapter(opTypeAdapter);

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
                String shareQuoteCode = data.getStringExtra("ShareQuote");
                btnSelectStock.setText(shareQuoteCode);
            }
        }
    }
}
