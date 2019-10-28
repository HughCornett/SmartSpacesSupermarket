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

public class CustomAdapter extends BaseAdapter
{

    ArrayList<BluetoothDevice> objects;
    private LayoutInflater inflater;
    private OnPairButtonClickListener listener;

    CustomAdapter(Context context, ArrayList<BluetoothDevice> objects, OnPairButtonClickListener listener)
    {
        inflater = LayoutInflater.from(context);
        this.objects = objects;
        this.listener = listener;
    }

    public OnPairButtonClickListener getListener() {
        return listener;
    }

    public void setListener(OnPairButtonClickListener listener) {
        this.listener = listener;
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
        CustomAdapter.ViewHolder holder = null;
        if(view ==null)
        {
            holder = new CustomAdapter.ViewHolder();

            view = inflater.inflate(R.layout.bluetooth_list, null);

            holder.device = (TextView) view.findViewById(R.id.bluetoothDevice);
            holder.mac = (TextView) view.findViewById(R.id.MAC);
            holder.pairButton = (Button) view.findViewById(R.id.pairButton);

            view.setTag(holder);
        }
        else
            holder = (CustomAdapter.ViewHolder) view.getTag();

        holder.device.setText(objects.get(i).getName());
        holder.mac.setText(objects.get(i).getAddress());
        holder.pairButton.setText("connect");
        holder.pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                    listener.onPairButtonClick(i);

            }
        });

        return view;
    }
    public interface OnPairButtonClickListener {
        public abstract void onPairButtonClick(int position);
    }


    static class ViewHolder
    {
        TextView device;
        TextView mac;
        Button pairButton;
    };
}
