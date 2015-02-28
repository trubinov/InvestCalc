package ru.nts.investcalc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
    private int positiveColor;
    private int negativeColor;
    private int neutralColor;

    private class ViewHolder {
        TextView tvStockCode;
        TextView tvStockName;
        TextView tvPrice;
        TextView tvNowPrice;
        TextView tvSum;
        TextView tvNowSum;
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     */
    public PortfolioAdapter(Context context) {
        super(context, R.layout.portfolio_row);
        this.context = context;
        this.positiveColor = context.getResources().getColor(R.color.PositiveColor);
        this.negativeColor = context.getResources().getColor(R.color.NegativeColor);
        this.neutralColor = context.getResources().getColor(R.color.NeutralColor);
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
            viewHolder.tvStockName = (TextView) convertView.findViewById(R.id.tvStockName);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.tvNowPrice = (TextView) convertView.findViewById(R.id.tvNowPrice);
            viewHolder.tvSum = (TextView) convertView.findViewById(R.id.tvBuySum);
            viewHolder.tvNowSum = (TextView) convertView.findViewById(R.id.tvNowSum);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        double incomingSum = item.getSum();
        double actualSum = item.getActualPrice() * item.getCount();
        if (incomingSum - actualSum >= incomingSum * 0.05f) {
            viewHolder.tvStockCode.setTextColor(this.negativeColor);
            viewHolder.tvNowSum.setTextColor(this.negativeColor);
        } else if (actualSum - incomingSum >= incomingSum * 0.05f) {
            viewHolder.tvStockCode.setTextColor(this.positiveColor);
            viewHolder.tvNowSum.setTextColor(this.positiveColor);
        } else {
            viewHolder.tvStockCode.setTextColor(this.neutralColor);
            viewHolder.tvNowSum.setTextColor(this.neutralColor);
        }

        viewHolder.tvStockCode.setText(item.getStockCode());
        viewHolder.tvStockName.setText(item.getStockName());
        viewHolder.tvPrice.setText(String.format("%.6f", item.getAvePrice()));
        viewHolder.tvNowPrice.setText(String.format("%.6f", item.getActualPrice()));
        viewHolder.tvSum.setText(String.format("%.2f", incomingSum));
        viewHolder.tvNowSum.setText(String.format("%.2f", actualSum));

        return convertView;

    }

}
