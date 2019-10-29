package com.example.smartspacesblindshopping;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomItemAdapter extends BaseAdapter
{

    ArrayList<Item> objects;
    private LayoutInflater inflater;

    CustomItemAdapter(Context context, ArrayList<Item> objects)
    {
        inflater = LayoutInflater.from(context);
        this.objects = objects;

    }



    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        CustomItemAdapter.ViewHolder holder = null;
        if(view ==null)
        {
            holder = new CustomItemAdapter.ViewHolder();

            view = inflater.inflate(R.layout.textinadapter, null);

            holder.itemName = (TextView) view.findViewById(R.id.textthing);


            view.setTag(holder);
        }
        else
            holder = (CustomItemAdapter.ViewHolder) view.getTag();

        holder.itemName.setText(objects.get(i).getProductName());


        return view;
    }



    static class ViewHolder
    {
        TextView itemName;

    };
}
