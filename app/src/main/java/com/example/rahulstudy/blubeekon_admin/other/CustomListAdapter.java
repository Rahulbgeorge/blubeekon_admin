package com.example.rahulstudy.blubeekon_admin.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rahulstudy.blubeekon_admin.Data.BleDevice;
import com.example.rahulstudy.blubeekon_admin.R;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {

    private final Context context;
    ArrayList<BleDevice> devices=new ArrayList<>();

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position=i;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view=inflater.inflate(R.layout.custom_llist_view,null);
        TextView devicename= view.findViewById(R.id.beaconname);
        TextView devicedistance= view.findViewById(R.id.beacondistance);
        TextView devicerssi= view.findViewById(R.id.beaconrssi);

        devicename.setText(devices.get(i).getName());
        devicedistance.setText(devices.get(i).getDistance());
        devicerssi.setText("Rssi:"+devices.get(i).getRssi());






        return view;
    }


    public CustomListAdapter(Context context, ArrayList<BleDevice> devices)
    {
     this.context=context;
     this.devices=devices;
    }
}
