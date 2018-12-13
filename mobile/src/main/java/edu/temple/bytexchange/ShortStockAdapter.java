package edu.temple.bytexchange;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ShortStockAdapter extends ArrayAdapter<StockShort> {

    private int resourceLayout;
    private Context context;
    private List<StockShort> searchedStocks;
    private LayoutInflater mInflater;


    public ShortStockAdapter(Context context ,List<StockShort> items) {
        super(context, 0, items);
        this.context = context;
        this.searchedStocks = items;
        mInflater = LayoutInflater.from(context);
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StockShort shortstock = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.short_stock_adapter, parent, false);
        }

        TextView symbol = (TextView) convertView.findViewById(R.id.symbol);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView exchange = (TextView) convertView.findViewById(R.id.exchange);

        symbol.setText(shortstock.getSymbol());
        name.setText(shortstock.getName());
        exchange.setText(shortstock.getExchange());


        return convertView;
    }
}
