package com.example.smartspacesblindshopping;

import java.util.Comparator;

public class SortByDistance implements Comparator<MyBeacon> {
    @Override
    public int compare(MyBeacon beacon, MyBeacon beacon1) {
        if(beacon.getDistance()-beacon1.getDistance()<0)
        {
            return -1;
        }
        else if(beacon.getDistance()-beacon1.getDistance()>0)
        {
            return 1;
        }
        else return 0;

    }
}
