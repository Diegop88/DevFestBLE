package com.honeywell.hts.devfestblegatt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.UUID;

public class ListDeviceActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = ListDeviceActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST = 23;
    private BluetoothAdapter adapter;

    private static final String MACADDRES = "34:81:F4:06:C1:A2";                                                //Dirección MAC de nuestro dispositivo BLE
    private static final String SERVICE = "00001800-0000-1000-8000-00805f9b34fb";                               //UUID del servicio a leer
    private static final String CHARACTERISTIC = "00002a01-0000-1000-8000-00805f9b34fb";                        //UUID de characteristica a leer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_device);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Validar en tiempo real el permiso de localización, esto es necesario para dispositivos BLE
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Obtener Bluetooth manager para asegurarnos que el dispositivo cuenta con Bluetooth
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(manager == null) {
            Log.d(TAG, "Device doesn't have bluetooth");
            return;
        }

        //Obtener el Bluetooth adapter para asegurarnos que podemos realizar una conexión bluetooth
        adapter = manager.getAdapter();
        if(adapter == null) {
            Log.d(TAG, "Bluetooth adapter doesn't work. Exiting app");
            return;
        }

        //Iniciar scaneado de Bluetooth BLE
        adapter.startLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "M: " + device.getAddress());
        if(device.getAddress().equals(MACADDRES)){
            adapter.stopLeScan(this);       //Detener scaneado
            connectDevice(device);
        }
    }

    private void connectDevice(BluetoothDevice device) {
        device.createBond();                //Crear emparejamiento con el dispositivo

        /**Conectar los servicios Gatt del dispositivo
         * @Param this = Contexto
         * @Param false = Autoconectar
         * @Param BluetoothGattCallback = Callback donde recibimos las respuestas del dispositivo
         */
        device.connectGatt(this, false, new BluetoothGattCallback() {

            /**
             * En este método recibimos los cambios de conexión con el dispositivo
             * @param gatt Objeto gatt de conexión
             * @param status Estado del cambio de conexión
             * @param newState nuevo estado de conexión
             */
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    if(newState == BluetoothGatt.STATE_CONNECTED) {
                        gatt.discoverServices();
                    }
                } else {
                    Log.d(TAG, "Error connect");
                }
            }

            /**
             * Método que nos regresa la lista de servicios en el profile del dispositivo
             * @param gatt Objeto gatt de conexión
             * @param status Estado de solicitud
             */
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if(status == BluetoothGatt.GATT_SUCCESS){
                    for(BluetoothGattService service : gatt.getServices()) {
                        Log.d(TAG, "S: " + service.getUuid().toString());
                    }
                    connectService(gatt);
                } else {
                    Log.d(TAG, "Error services");
                }
            }

            /**
             * Método que lee la información de un servicio
             * @param gatt Objeto Gatt de conexión
             */
            private void connectService(BluetoothGatt gatt) {
                BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE));
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.d(TAG, "C: " + characteristic.getUuid());
                }

                readCharacteristic(gatt);
            }

            /**
             * Método que solicita la lectura de una caracteristica al dispositivo
             * @param gatt Objeto Gatt de conexión
             */
            private void readCharacteristic(BluetoothGatt gatt) {
                BluetoothGattCharacteristic characteristic = gatt
                        .getService(UUID.fromString(SERVICE))
                        .getCharacteristic(UUID.fromString(CHARACTERISTIC));
                gatt.readCharacteristic(characteristic);
            }

            /**
             * Método que nos regresa la respueta de lectura de caracteristica.
             * @param gatt Objeto Gatt de conexión
             * @param characteristic Characteristica que se solicito leer
             * @param status Estado de solicitud
             */
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if(status == BluetoothGatt.GATT_SUCCESS){
                    for(int i = 0; i< characteristic.getValue().length; i++) {
                        Log.d(TAG, "Value: " + characteristic.getValue()[i]);
                    }
                } else {
                    Log.d(TAG, "Error on characteristic");
                }
            }
        });
    }
}
