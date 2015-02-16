package ru.nts.investcalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements SecuritiesFragment.onShareSelected {

    SecuritiesFragment securitiesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        securitiesFragment = new SecuritiesFragment();

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.frmSecurities, securitiesFragment);
        fTrans.commit();

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quoteListActivity = new Intent(MainActivity.this, QuoteListActivity.class);
                startActivityForResult(quoteListActivity, 1);
                // запустить обновление фрагмента
                //securitiesFragment.updateQuotes();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if ((requestCode == 1) && (resultCode == RESULT_OK)) {
            String shareQuoteCode = data.getStringExtra("ShareQuote");
            Log.d("LOG", "From main activity: " + shareQuoteCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.add_transaction_menu_item:
                Intent addTransactionIntent = new Intent(this, AddTransactionActivity.class);
                startActivity(addTransactionIntent);
                break;
            case R.id.quote_list:
                Intent quoteListIntent = new Intent(this, QuoteListActivity.class);
                startActivity(quoteListIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void shareSelected(StockQuote value) {
        Log.d("LOG", value.getCode());
    }

}
