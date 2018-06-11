package com.example.rahulstudy.blubeekon_admin.Data;

import com.google.firebase.database.FirebaseDatabase;

public class BleDevice {
    public String name;
    public String macid;
    public int rssi;
    public String distance;

    public BleDevice(String macid, int rssi, String distance)
    {
        this.macid=macid;
        this.rssi=rssi;
        this.distance=distance;
    }

    public String getName()
    {return name;}
    public int getRssi()
    {return rssi;}
    public String getDistance()
    {return distance;}
    public String getMacId()
    {return this.macid;}
    public void setMacId(String macid)
    {this.macid=macid;}

    public BleDevice()
    {

    }
    public void setName(String name)
    {this.name=name;}

}
