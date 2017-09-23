package com.honeywell.hts.devfestblegatt;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ListDeviceActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback, ListAdapter.OnDeviceSelectedListener {

    private static final String TAG = ListDeviceActivity.class.getSimpleName();

    private BluetoothAdapter adapter;
    private Map<String, String> devices;
    private ListAdapter listAdapter;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);

        listAdapter = new ListAdapter(this);
        devices = new HashMap<>();

        RecyclerView listDevices = (RecyclerView) findViewById(R.id.list_devices);
        listDevices.setLayoutManager(new LinearLayoutManager(this));
        listDevices.setAdapter(listAdapter);

        loading = new ProgressDialog(this);
        loading.setTitle(R.string.finding);
        loading.setIndeterminate(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager == null) {
            Toast.makeText(this, R.string.bt_not_found, Toast.LENGTH_LONG).show();
            finish();
        }

        adapter = manager.getAdapter();
        if(adapter == null) {
            Toast.makeText(this, R.string.bt_error, Toast.LENGTH_LONG).show();
            finish();
        }

        if(!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 23);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                searchDevice(true);
                return true;
            case R.id.menu_stop:
                searchDevice(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchDevice(boolean active) {
        if(active) {
            loading.show();
            adapter.startLeScan(this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchDevice(false);
                }
            }, 10000);
        } else {
            loading.dismiss();
            adapter.stopLeScan(this);
            listAdapter.setDevices(devices);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "Found");
        devices.put(device.getAddress(), device.getName());
    }

    @Override
    public void onDeviceSelected(String macAddress) {

    }
}
