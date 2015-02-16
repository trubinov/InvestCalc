package ru.nts.investcalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class QuoteListActivity extends ActionBarActivity implements SecuritiesFragment.onShareSelected {

    SecuritiesFragment securitiesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_list);
        if (savedInstanceState == null) {
            securitiesFragment = new SecuritiesFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, securitiesFragment);
            fragmentTransaction.commit();
            securitiesFragment.updateQuotes();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_quote_list, menu);
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
    public void shareSelected(StockQuote value) {
        Log.d("LOG", value.getCode());
        Intent intentResult = new Intent();
        intentResult.putExtra("ShareQuote", value.getCode());
        setResult(RESULT_OK, intentResult);
        finish();
    }

}
