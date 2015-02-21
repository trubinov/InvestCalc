package ru.nts.investcalc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Николай on 21.02.2015.
 */
public class PortfolioAdapter extends ArrayAdapter<Portfolio> {

    private Context context;

    private class ViewHolder {
        TextView tvStockCode;
        TextView tvPrice;
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     */
    public PortfolioAdapter(Context context) {
        super(context, R.layout.portfolio_row);
        this.context = context;
        MyDatabaseHelper databaseHelper = MyDatabaseManager.getInstance().getDatabaseHelper();
        try {
            List<Portfolio> portfolioList = databaseHelper.getPortfolios().queryForAll();
            this.addAll(portfolioList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Portfolio item = getItem(position);
        ViewHolder viewHolder = null;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.portfolio_row, null);
            viewHolder = new ViewHolder();
            viewHolder.tvStockCode = (TextView) convertView.findViewById(R.id.tvStockCode);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvStockCode.setText(item.getStockCode());
        viewHolder.tvPrice.setText(String.format("%.6f", item.getAvePrice()));

        return convertView;

    }

}
