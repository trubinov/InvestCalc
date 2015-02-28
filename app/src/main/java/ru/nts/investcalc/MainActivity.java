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
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class MainActivity extends ActionBarActivity {

    PortfolioFragment portfolioFragment;
    TextView tvIncomingSum;
    TextView tvActualSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portfolioFragment = new PortfolioFragment();
        tvIncomingSum = (TextView) findViewById(R.id.tvSum);
        tvActualSum = (TextView) findViewById(R.id.tvActualSum);

        FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.add(R.id.frmPortfolio, portfolioFragment);
        fTrans.commit();

        refreshTotalData();

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update data in fragment
                portfolioFragment.RefreshData();
                // update totals
                refreshTotalData();
            }
        });
    }

    private void refreshTotalData() {
        // update total sum and visualise it >>
        double incomingSum = 0.0f;
        double actualSum = 0.0f;
        try {
            MyDatabaseHelper databaseHelper = MyDatabaseManager.getInstance().getDatabaseHelper();
            Dao<Portfolio, Integer> portfolios = databaseHelper.getPortfolios();
            Dao<StockQuote, String> stockQuotes = databaseHelper.getStockQuotes();
            QueryBuilder<Portfolio, Integer> portfolioQueryBuilder = portfolios.queryBuilder();
            QueryBuilder<StockQuote, String> stockQuoteQueryBuilder = stockQuotes.queryBuilder();
            portfolioQueryBuilder.selectRaw("SUM(`sum`), SUM(`count` * `StockQuotes`.`price`)");
            //portfolioQueryBuilder.selectRaw("SUM(`sum`)");
            portfolioQueryBuilder.leftJoin(stockQuoteQueryBuilder);
            String statementString = portfolioQueryBuilder.prepareStatementString();
            GenericRawResults<Object[]> rawResults = portfolios.queryRaw(statementString, new DataType[]{DataType.DOUBLE, DataType.DOUBLE});
            Object[] firstResult = rawResults.getFirstResult();
            incomingSum = (double) firstResult[0];
            actualSum = (double) firstResult[1];
            rawResults.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tvIncomingSum.setText(String.format("%.2f", incomingSum));
        tvActualSum.setText(String.format("%.2f", actualSum));

        if (incomingSum - actualSum >= incomingSum * 0.05f) {
            tvActualSum.setTextColor(getResources().getColor(R.color.NegativeColor));
        } else if (actualSum - incomingSum >= incomingSum * 0.05f) {
            tvActualSum.setTextColor(getResources().getColor(R.color.PositiveColor));
        } else {
            tvActualSum.setTextColor(getResources().getColor(R.color.NeutralColor));
        }
        // update total sum and visualise it <<
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

}
