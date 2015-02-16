package ru.nts.investcalc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Николай on 25.01.2015.
 */
public class QuoteArrayAdapter extends ArrayAdapter<StockQuote> {

    private Context context;

    private class ViewHolder {
        TextView txtName;
        TextView txtCode;
        TextView txtPrice;
    }

    public QuoteArrayAdapter(Context context) {
        super(context, R.layout.quote_row);
        this.context = context;
    }

    public void ReloadData(ArrayList<StockQuote> newValues) {
        this.clear();
        this.addAll(newValues);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StockQuote item = getItem(position);
        ViewHolder viewHolder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.quote_row, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtQuoteName);
            viewHolder.txtCode = (TextView) convertView.findViewById(R.id.txtQuoteCode);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.txtQuotePrice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(item.getName());
        viewHolder.txtCode.setText(item.getCode());
        viewHolder.txtPrice.setText(item.getPriceString());

        return convertView;

    }
}
