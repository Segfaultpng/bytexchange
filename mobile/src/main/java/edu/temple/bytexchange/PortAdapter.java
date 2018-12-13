package edu.temple.bytexchange;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PortAdapter extends ArrayAdapter<StockShort> {

    private int resourceLayout;
    private Context context;
    private List<StockShort> searchedStocks;
    private LayoutInflater mInflater;


    public PortAdapter(Context context ,List<StockShort> items) {
        super(context, 0, items);
        this.context = context;
        this.searchedStocks = items;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StockShort shortstock = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.port_stock_adapter, parent, false);
        }

        TextView symbol = (TextView) convertView.findViewById(R.id.portsymbol);
        TextView currentPrice = (TextView) convertView.findViewById(R.id.portprice);

        ConstraintLayout background = convertView.findViewById(R.id.portback);

        symbol.setText(shortstock.getSymbol());
        currentPrice.setText(Double.toString(shortstock.getCurrentPrice()));

        if(shortstock.stockDifference() > 0){
            background.setBackgroundColor(Color.parseColor("green"));
        }else {
            background.setBackgroundColor(Color.parseColor("red"));
        }

        return convertView;
    }
}
