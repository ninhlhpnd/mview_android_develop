package com.example.sensorcollection.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleMtuChangedCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.sensorcollection.MainActivity;
import com.example.sensorcollection.R;
import com.example.sensorcollection.adapter.tbKetNoiAdapter;
import com.example.sensorcollection.adapter.tbScanAdapter;
import com.example.sensorcollection.ultis.Uuid;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BluetoothFragment extends DialogFragment {
    List<BleDevice>  tbScans;
    com.example.sensorcollection.adapter.tbScanAdapter tbScanAdapter;
    com.example.sensorcollection.adapter.tbKetNoiAdapter tbKetNoiAdapter;
    ListView lvScan,lvKetnoi;
    int REQUEST_BLUETOOTH_SCAN_PERMISSION=1;
    private static final int REQUEST_ENABLE_BT = 1;
    private ProgressDialog progressDialog;
    Timer timeScan;
    public static BluetoothFragment newInstance(){
        return new BluetoothFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN_PERMISSION);
//        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = getResources().getDisplayMetrics().widthPixels * 2 / 3;
            int height = getResources().getDisplayMetrics().heightPixels;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        timeScan.cancel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_bluetooth,container,false);
        lvScan=(ListView)view.findViewById(R.id.listviewTbcotheketnoi);
        lvKetnoi=(ListView)view.findViewById(R.id.listviewTbdaketnoi);

        tbScans=new ArrayList<>();
        tbScanAdapter=new tbScanAdapter(view.getContext(),R.layout.dong_scandevice,tbScans);
        tbKetNoiAdapter=new tbKetNoiAdapter(view.getContext(),R.layout.dong_thietbidaketnoi, MainActivity.tbKetnois);
        lvScan.setAdapter(tbScanAdapter);
        lvKetnoi.setAdapter(tbKetNoiAdapter);
        progressDialog = new ProgressDialog(getContext());
        checkPermissions();
        setScanRule();
        timeScan=new Timer();
        timeScan.schedule(new TimerTask() {
            @Override
            public void run() {
                startScan();
            }
        },0,5500);

        lvScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BleDevice bleDevice= tbScans.get(i);
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }
        });
        tbKetNoiAdapter.setOnDeviceClickListener(new tbKetNoiAdapter.OnDeviceClickListener() {
            @Override
            public void onDisConnect(BleDevice bleDevice) {
                if(BleManager.getInstance().isConnected(bleDevice)){
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }
        });
        return view;
    }
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
//                tbScanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                for(BleDevice d :tbScans){
                    if(bleDevice.getKey().equals(d.getKey())){
                        return;
                    }
                }
                tbScans.add(bleDevice);
                tbScanAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }
    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Thiết bị không hỗ trợ Bluetooth
        } else if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth chưa được bật, yêu cầu người dùng bật Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // Bluetooth đã được bật, bạn có thể thực hiện kết nối Bluetooth ở đây
        }

    }
    private void setScanRule() {
        String[] names= Uuid.nameFilter.split(",");
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true,names)
                .setScanTimeOut(5000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }
    private void connect( BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Connection Fail", Toast.LENGTH_SHORT).show();
                    Log.e("ble","Connection Fail");
                }
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
//                Toast.makeText(MainActivity.this, "Connect success", Toast.LENGTH_SHORT).show();
                MainActivity.tbKetnois.add(bleDevice);
                tbKetNoiAdapter.notifyDataSetChanged();
                if(tbScans.size()>0) {
                    for(int i=0;i<tbScans.size();i++){
                        if(tbScans.get(i).getKey().equals(bleDevice.getKey())){
                            tbScans.remove(i);
                            tbScanAdapter.notifyDataSetChanged();
                        }
                    }
                }
                List<String> sodocambien=new ArrayList<>();
                int vitridaugach=bleDevice.getName().indexOf('-');
                String tenCamBien=bleDevice.getName().substring(0,vitridaugach);
                for (int i=0;i<Uuid.camBiens.size();i++){
                    if(Uuid.camBiens.get(i).getId().equals(tenCamBien)) {
                        sodocambien.add(Uuid.camBiens.get(i).getName());
                    }
                }
                BleManager.getInstance().setMtu(bleDevice, 512, new BleMtuChangedCallback() {
                    @Override
                    public void onSetMTUFailure(BleException exception) {

                    }

                    @Override
                    public void onMtuChanged(int mtu) {

                    }
                });
//                listSodocambien.put(bleDevice,sodocambien);
//                sodocambienAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                for(int i=0;i<MainActivity.tbKetnois.size();i++){
                    BleDevice device=MainActivity.tbKetnois.get(i);
                    if(bleDevice.getKey().equals(device.getKey())){
                        MainActivity.tbKetnois.remove(i);
                    }
                }
                tbKetNoiAdapter.notifyDataSetChanged();
            }
        });
    }

}
