package com.example.smartspacesblindshopping;

import org.altbeacon.beacon.Beacon;

import java.util.Collections;
import java.util.Vector;

public class MyBeacon {

    private final double N = 2;

    Beacon beacon;

    double distance;

    private Vector<Integer> rssis = new Vector<>();

    MyBeacon(Beacon beacon)
    {
        this.beacon=beacon;
    }

    public double getDistance()
    {
        calculateDistance();
        return distance;
    }



    public String getMac() {
        return beacon.getBluetoothAddress();
    }

    public void addRssi(int rssi)
    {
        this.rssis.add(rssi);
    }


    private double getRssiAverage()
    {
        double sum=0;
        //Vector<Double> temp = new Vector<>();
        //temp.addAll(distances);
        Collections.sort(rssis);
        int i = rssis.size()/10 ;
        for (; i<rssis.size()*9/10; ++i) {

            sum+=rssis.get(i).doubleValue();

        }

        double denominator = ((rssis.size()*9/10)-(rssis.size()/10));
        if(denominator != 0)
        {
            return sum/denominator;
        }
        else {
            return rssis.get(0);
        }
    }

    private double getRssiMin()
    {

        Collections.sort(rssis);
        //Vector<Double> result = new Vector<>();

        //double median;


        return rssis.get(rssis.size()/10);
    }



    public void calculateDistance()
    {

        //Log.d("calcdist", ""+measuredPower+ " " + getRssiAverage());
        distance = Math.pow(10.0, ((getMeasuredPower()-getRssiAverage())/(10*N)));


    }

    public int getMeasuredPower() {
        return beacon.getTxPower();
    }


    public Beacon getBeacon()
    {
        return beacon;
    }
}
