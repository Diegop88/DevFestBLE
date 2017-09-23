package com.honeywell.hts.devfestblegatt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

public class ListDeviceActivity extends AppCompatActivity {

    private RecyclerView listDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);

        listDevices = (RecyclerView) findViewById(R.id.list_devices);
    }
}
