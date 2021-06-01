package org.jeonfeel.withlol2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jeonfeel.withlol2.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_spinner extends BaseAdapter {

    Context context;
    String[] data;
    LayoutInflater inflater;


    public Adapter_spinner(Context context, String[] data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.selected_position_item, parent, false);
        }

        if(data!=null){
            //데이터세팅
            String text = data[position];
            ((TextView)convertView.findViewById(R.id.tv_position)).setText(text);
        }

        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.selected_position_item_dropdown, parent, false);
        }

        //데이터세팅
        String text = data[position];
        ((TextView)convertView.findViewById(R.id.tv_dropdown)).setText(text);

        return convertView;
    }
}
