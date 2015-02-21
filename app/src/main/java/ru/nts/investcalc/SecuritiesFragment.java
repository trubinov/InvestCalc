package ru.nts.investcalc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Николай on 24.01.2015.
 */
public class SecuritiesFragment extends ListFragment {

    public static String RBC_URL = "http://stock.quote.rbc.ru/demo/micex.0/intraday/index.rus.js?format=json";
    public static int IND_CODE = 10;
    public static int IND_NAME = 0;
    public static int IND_HIGH = 3;
    public static int IND_LOW = 4;
    public static int IND_WA = 5;

    QuoteArrayAdapter adapter;
    onShareSelected shareSelected;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new QuoteArrayAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            shareSelected = (onShareSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onShareSelected");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        StockQuote item = (StockQuote) getListView().getItemAtPosition(position);
        shareSelected.shareSelected(item);
    }

    public void updateQuotes() {
        SecuritiesUpdater securitiesUpdater = new SecuritiesUpdater();
        securitiesUpdater.execute();
    }

    public interface onShareSelected {
        public void shareSelected(StockQuote value);
    }

    public class SecuritiesUpdater extends AsyncTask<String, Void, ArrayList<StockQuote>> {

        private final String LOG_TAG = SecuritiesUpdater.class.getName();

        @Override
        protected ArrayList<StockQuote> doInBackground(String... params) {

            ArrayList<StockQuote> stockQuotesList = null;

            String jsonResponse = null;
            JSONObject jsonObject = null;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(RBC_URL);
            HttpResponse httpResponse = null;
            InputStream is = null;
            try {
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (is != null) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line + "\n");
                    }
                    is.close();
                    jsonResponse = stringBuilder.toString();
                    jsonResponse = jsonResponse.replace("try{ var xdata = [", "");
                    jsonResponse = jsonResponse.replace("]; jsonp(xdata); } catch(e){}", "");
                    jsonObject = new JSONObject(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (jsonObject != null) {
                Dao stockQuotes = null;
                try {
                    stockQuotes = MyDatabaseManager.getInstance().getDatabaseHelper().getStockQuotes();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    stockQuotesList = new ArrayList<StockQuote>();
                    JSONArray rowsArray;
                    rowsArray = jsonObject.getJSONArray("rows");
                    for (int k = 0; k < rowsArray.length(); k++) {
                        JSONArray rowArray = rowsArray.getJSONArray(k);
                        String shareCode = rowArray.getString(IND_CODE);
                        String shareName = rowArray.getString(IND_NAME);
                        double sharePrice;
                        try {
                            sharePrice = rowArray.getDouble(IND_WA);
                        } catch (JSONException ex1) {
                            sharePrice = 0;
                        }

                        StockQuote stockQuote = new StockQuote(shareCode, shareName, sharePrice);
                        stockQuotesList.add(stockQuote);
                        if (stockQuotes != null) {
                            stockQuotes.createOrUpdate(stockQuote);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG + "/doInBackground", "Update FAIL");
            }

            return stockQuotesList;
        }

        @Override
        protected void onPostExecute(ArrayList<StockQuote> stockQuotesList) {

            if (stockQuotesList != null) {
                adapter.ReloadData(stockQuotesList);
                Log.i(LOG_TAG + "/onPostExecute", "Update OK");
            } else {
                Log.d(LOG_TAG + "/onPostExecute", "Update FAILED!");
            }

        }
    }

}
