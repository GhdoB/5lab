package com.example.a5lab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    public CurrencyAdapter(Context context, List<Currency> currencies) {
        super(context, 0, currencies);
        System.out.println("CurrencyAdapter: Created with " + currencies.size() + " items");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("CurrencyAdapter: getView called for position " + position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            System.out.println("CurrencyAdapter: Inflated new view");
        }

        Currency currency = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);

        if (currency != null) {
            String displayText = currency.getCode() + " - " + currency.getExchangeRate();
            textView.setText(displayText);
            System.out.println("CurrencyAdapter: Set text: " + displayText);
        }

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        System.out.println("CurrencyAdapter: notifyDataSetChanged called");
        super.notifyDataSetChanged();
    }
}
