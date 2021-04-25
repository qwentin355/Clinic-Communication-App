package com.saskpolytech.clinicapp;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Network {
    public static String readIP(Context context) {
        try {
            AssetManager assets = context.getAssets();
            BufferedReader br = new BufferedReader(new InputStreamReader(assets.open("ip.csv"), "UTF-8"));
            return br.readLine();
        } catch(Exception e) {
            System.out.println("ERROR: Unable to read ip address: " + e.getMessage() + " Defaulting to 127.0.0.1");
            return "127.0.0.1";
        }
    }
}
