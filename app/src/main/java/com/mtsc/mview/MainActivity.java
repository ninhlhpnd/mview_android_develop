package com.mtsc.mview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.github.mikephil.charting.data.Entry;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.mtsc.mview.adapter.LichsuAdapter;
import com.mtsc.mview.adapter.thanhcongcuAdapter;
import com.mtsc.mview.fragment.BluetoothFragment;
import com.mtsc.mview.fragment.CalibFragment;
import com.mtsc.mview.fragment.FragmentBocuc1;
import com.mtsc.mview.fragment.FragmentBocuc2;
import com.mtsc.mview.fragment.FragmentBocuc3;
import com.mtsc.mview.fragment.FragmentBocuc4;
import com.mtsc.mview.fragment.FragmentKieuDocDulieu;
import com.mtsc.mview.fragment.FragmentMachDienXC;
import com.mtsc.mview.fragment.FragmentSongam;
import com.mtsc.mview.fragment.USBFragment;
import com.mtsc.mview.model.CamBien;
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.model.ConnectedDevice;
import com.mtsc.mview.model.CustomProber;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuCacCamBien;
import com.mtsc.mview.model.KalmanFilterWrapper;
import com.mtsc.mview.model.ListItemUSB;
import com.mtsc.mview.model.Run;
import com.mtsc.mview.model.SensorData;
import com.mtsc.mview.model.SodoCambien;
import com.mtsc.mview.model.sodoCambienUSB;
import com.mtsc.mview.model.thanhcongcuClass;
import com.mtsc.mview.my_interface.ItemClickListener;
import com.mtsc.mview.ultis.DataEvent;
import com.mtsc.mview.ultis.Uuid;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MainActivity extends AppCompatActivity implements ItemClickListener, USBFragment.OnDataReceivedListener {
    RecyclerView recyclerViewThanhcongcu;
    Handler handler;
    ArrayList<thanhcongcuClass> thanhcongcuClasses;
    com.mtsc.mview.adapter.thanhcongcuAdapter thanhcongcuAdapter;
    public static List<ConnectedDevice> tbKetnois;
    public static List<SodoCambien> sodoCambienList;
    FrameLayout frmFragment;
    FragmentManager fragmentManager;
    Button btnStart, btnChontanso;
    boolean isStart = false;
    public static float thoigian = 0;
    public static double tansoLayMau = 1000;
    public static float solanchay = 0;
    public List<Run> allRuns;
    Timer timer1;
    public static boolean isConnectedUSB = false;
    public static UsbSerialPort usbSerialPort;
    public static List<CamBienUSB> tbScansUsb;
    public static List<sodoCambienUSB> soCambienUSB;
    public static List<ListItemUSB> tbUSB;
    List<DulieuCacCamBien> listDulieucaccambien;
    private SerialInputOutputManager usbIoManager;

    //    public static List<KalmanFilterWrapper> kalmanFilterWrapperList;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static final int REQUEST_WRITE_PERMISSION = 100;

    private static final int WRITE_WAIT_MILLIS = 2000;
    private boolean haveSTX = false, haveETX = false, firstNibble_ = false, isDataReceiving = false;
    private int inputPos = 0, currentByte;
    private int bufferSize = 100;
    int chayCambien = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Ẩn tiêu đề của Activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Ẩn thanh trạng thái
        setContentView(R.layout.activity_main);
//        hideNavigationBar();
//        kalmanFilterWrapperList=new ArrayList<>();
        checkPermissions();
        anhXa();
        initBLE();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = !isStart;
                if (isStart == true) {
                    if (!isConnectedUSB) {
                        Fragment currentFragment = fragmentManager.findFragmentById(R.id.layoutFragment_main);
                        byte[] byteArray = new byte[6];
                        if (currentFragment instanceof FragmentMachDienXC || currentFragment instanceof FragmentSongam) {
                            byteArray[0] = 0x03;
                            byteArray[5] = 0x03;
                            byte[] tempArray = ByteBuffer.allocate(4).putInt((int) (1000 / tansoLayMau)).array();
                            System.arraycopy(tempArray, 0, byteArray, 1, tempArray.length);
                        } else {
                            byteArray[0] = 0x01;
                            byteArray[5] = 0x03;
                            byte[] tempArray = ByteBuffer.allocate(4).putInt((int) tansoLayMau).array();

                            System.arraycopy(tempArray, 0, byteArray, 1, tempArray.length);
                        }
                        btnStart.setText("Dừng Lại");
                        solanchay++;
                        Run currentRun = new Run((int) solanchay, tansoLayMau);
                        allRuns.add(currentRun);
                        List<Float> manglanchay = new ArrayList<>();
                        manglanchay.add(solanchay);
                        manglanchay.add((float) tansoLayMau);
                        DulieuCB dulieuCB = new DulieuCB("lanchay", "lanchay", manglanchay);
                        EventBus.getDefault().post(new DataEvent(dulieuCB));

                        int colorDo = ContextCompat.getColor(getBaseContext(), R.color.btnStop);
                        btnStart.setBackgroundColor(colorDo);
                        btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_stop, 0, 0, 0);
                        for (ConnectedDevice connectedDevice : tbKetnois) {
                            final BleDevice bleDevice = connectedDevice.getDevice();
//                            SensorData sensorData = currentRun.getSensorDataByName(bleDevice.getName());

//                            if (sensorData == null) {
                            SensorData sensorData = new SensorData(connectedDevice.getDevice().getName());
                            currentRun.addSensorData(sensorData);
//                            }
//                            SensorData finalSensorData = sensorData;
                            BleManager.getInstance().write(connectedDevice.getDevice(), connectedDevice.getServiceUuid(), connectedDevice.getReadUuid(), byteArray, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

//                                for (BleDevice bleDevice: tbKetnois
//                                ) {

                                    BleManager.getInstance().notify(bleDevice, connectedDevice.getServiceUuid(), connectedDevice.getReadUuid(), new BleNotifyCallback() {
                                        @Override
                                        public void onNotifySuccess() {

                                        }

                                        @Override
                                        public void onNotifyFailure(BleException exception) {

                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {
                                            int vitri = bleDevice.getName().indexOf('-');

                                            if (data != null) {
                                                if (data[0] == 0x02 && data[data.length - 1] == 0x03) {
//                                                    for(int i=0;i<data.length;i++){
//                                                        Log.d("dulieu",String.valueOf(data[i]));
//                                                    }
                                                    if (bleDevice.getName().substring(0, vitri).equals("V&A") && data.length >= 10) {
                                                        List<Float> mangAp = new ArrayList<>();
                                                        List<Float> mangDong = new ArrayList<>();
                                                        for (int i = 0; i < (data.length - 2) / 8; i++) {
                                                            float dienap = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                                            mangAp.add(dienap);
                                                            float dongdien = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//                                                        Log.d("dulieu",String.valueOf(dienap));
                                                            mangDong.add(dongdien);
                                                        }
                                                        DulieuCB dulieuAp = new DulieuCB(bleDevice.getName(), Uuid.Voltage, mangAp);
                                                        DulieuCB dulieuDong = new DulieuCB(bleDevice.getName(), Uuid.Current, mangDong);
                                                        EventBus.getDefault().post(new DataEvent(dulieuAp));
                                                        EventBus.getDefault().post(new DataEvent(dulieuDong));
                                                    } else if (bleDevice.getName().substring(0, vitri).equals("P&T") && data.length >= 10) {
                                                        List<Float> mangApsuat = new ArrayList<>();
                                                        List<Float> mangNhietdo = new ArrayList<>();
                                                        for (int i = 0; i < (data.length - 2) / 8; i++) {
                                                            float apsuat = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                                            mangApsuat.add(apsuat);
                                                            float nhietdo = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//                                                        Log.d("dulieu",String.valueOf(dienap));
                                                            mangNhietdo.add(nhietdo);
                                                        }
                                                        DulieuCB dulieuApsuat = new DulieuCB(bleDevice.getName(), Uuid.Pressure, mangApsuat);
                                                        DulieuCB dulieuNhietdo = new DulieuCB(bleDevice.getName(), Uuid.Temp, mangNhietdo);
                                                        EventBus.getDefault().post(new DataEvent(dulieuApsuat));
                                                        EventBus.getDefault().post(new DataEvent(dulieuNhietdo));
                                                    } else if (bleDevice.getName().substring(0, vitri).equals("H&T") && data.length >= 10) {
                                                        List<Float> mangDoam = new ArrayList<>();
                                                        List<Float> mangNhietdo = new ArrayList<>();
                                                        for (int i = 0; i < (data.length - 2) / 8; i++) {
                                                            float doam = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                                            mangDoam.add(doam);
                                                            float nhietdo = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//                                                        Log.d("dulieu",String.valueOf(dienap));
                                                            mangNhietdo.add(nhietdo);
                                                        }
                                                        DulieuCB dulieuDoam = new DulieuCB(bleDevice.getName(), Uuid.Humid, mangDoam);
                                                        DulieuCB dulieuNhietdo = new DulieuCB(bleDevice.getName(), Uuid.Temp, mangNhietdo);
                                                        EventBus.getDefault().post(new DataEvent(dulieuDoam));
                                                        EventBus.getDefault().post(new DataEvent(dulieuNhietdo));
                                                    } else {
                                                        int vtrithietbi = 0;
                                                        for (int i = 0; i < sodoCambienList.size(); i++) {
                                                            if (sodoCambienList.get(i).getBleDevice() == bleDevice) {
                                                                vtrithietbi = i;
                                                                break;
                                                            }
                                                        }
                                                        List<Float> mangValue = new ArrayList<>();

                                                        for (int i = 0; i < (data.length - 2) / 4; i++) {
                                                            float value = ByteBuffer.wrap(data, i * 4 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                                            mangValue.add(value);
//                                                            if(listDulieucaccambien.size()<=10000) {
//                                                                listDulieucaccambien.add(new DulieuCacCamBien(1, (float) tansoLayMau, bleDevice.getName(), value));
//                                                            }
//                                                            Log.d("data", String.valueOf(value));
                                                            sensorData.addValue(value);
                                                        }

                                                        DulieuCB dulieuCB1 = new DulieuCB(bleDevice.getName(), sodoCambienList.get(vtrithietbi).getTencambien(), mangValue);
                                                        EventBus.getDefault().post(new DataEvent(dulieuCB1));
                                                    }


//                                        listDulieuCambien.put(bleDevice.getName(),value);
                                                }
                                            }

                                        }
                                    });
//                                }
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                    } else {
                        List<Float> manglanchay = new ArrayList<>();
                        manglanchay.add(solanchay);
                        manglanchay.add((float) tansoLayMau);
                        DulieuCB dulieuCB = new DulieuCB("lanchay", "lanchay", manglanchay);
                        EventBus.getDefault().post(new DataEvent(dulieuCB));

                        btnStart.setText("Dừng Lại");
                        int colorDo = ContextCompat.getColor(getBaseContext(), R.color.btnStop);
                        btnStart.setBackgroundColor(colorDo);
                        btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_stop, 0, 0, 0);
//                        Log.d("tag", tbScans.get(0).getCamBien().getName());
//                        getUSBSerial();

//                        usbIoManager = new SerialInputOutputManager(usbSerialPort,MainActivity.this);
//                        usbIoManager.start();
                        timer1 = new Timer();
                        chayCambien = 0;

                        timer1.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                int tenCambien = Uuid.camBiens.indexOf(tbScansUsb.get(chayCambien).getCamBien()) + 1;
                                int idCambien = tbScansUsb.get(chayCambien).getId();
                                byte[] data = {(byte) tenCambien, (byte) idCambien, 0x02};
                                byte[] senddata = new byte[10];
                                senddata[0] = 0x02;
                                senddata[1] = (byte) firstNibble(data[0]);
                                senddata[2] = (byte) secondNibble(data[0]);
                                senddata[3] = (byte) firstNibble(data[1]);
                                senddata[4] = (byte) secondNibble(data[1]);
                                senddata[5] = (byte) firstNibble(data[2]);
                                senddata[6] = (byte) secondNibble(data[2]);
                                senddata[7] = 0x03;
                                int crc = crc8(data, data.length);
                                senddata[8] = (byte) firstNibble(crc);
                                senddata[9] = (byte) secondNibble(crc);
                                send(senddata);
                                chayCambien = (chayCambien + 1) % tbScansUsb.size();
                            }
                        }, 0, (long) (tansoLayMau / tbScansUsb.size()));

                    }
                } else {
                    btnStart.setText("Bắt Đầu ");
                    int colorXanh = ContextCompat.getColor(getBaseContext(), R.color.xanhduongnhat);
                    btnStart.setBackgroundColor(colorXanh);
                    btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_start, 0, 0, 0);
                    listDulieucaccambien.clear();
                    if (!isConnectedUSB) {
                        byte[] byteArray = new byte[6];
                        byteArray[0] = 0x02;
                        byteArray[1] = 0x79;
                        byteArray[5] = 0x03;
                        for (ConnectedDevice connectedDevice : tbKetnois) {
                            final BleDevice bleDevice = connectedDevice.getDevice();
                            BleManager.getInstance().write(bleDevice, connectedDevice.getServiceUuid(), connectedDevice.getReadUuid(), byteArray, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    BleManager.getInstance().stopNotify(bleDevice, connectedDevice.getServiceUuid(), connectedDevice.getReadUuid());
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                    } else {
//                        Log.d("tag", tbScans.get(0).getCamBien().getName() + " cancle");
                        timer1.cancel();

                    }
                }
            }
        });
        btnChontanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_tansolaymau, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.tanso1Hz:
                                tansoLayMau = 1000;
                                break;
                            case R.id.tanso2Hz:
                                tansoLayMau = 500;
                                break;
                            case R.id.tanso5Hz:
                                tansoLayMau = 200;
                                break;
                            case R.id.tanso10Hz:
                                tansoLayMau = 100;
                                break;
                            case R.id.tanso20Hz:
                                tansoLayMau = 50;
                                break;
                            case R.id.tanso50Hz:
                                tansoLayMau = 20;
                                break;
                            case R.id.tanso100Hz:
                                tansoLayMau = 10;
                                break;
                            case R.id.tanso200Hz:
                                tansoLayMau = 5;
                                break;
                            case R.id.tanso500Hz:
                                tansoLayMau = 2;
                                break;
                            case R.id.tanso1000Hz:
                                tansoLayMau = 1;
                                break;
                            case R.id.tanso2000Hz:
                                tansoLayMau = 0.5;
                                break;
                            case R.id.tanso5000Hz:
                                tansoLayMau = 0.2;
                                break;
                            case R.id.tanso10000Hz:
                                tansoLayMau = 0.1;
                                break;
                        }
                        btnChontanso.setText("Tần Số: " + (int) (1000 / tansoLayMau) + "Hz");

                        return false;
                    }
                });
            }
        });
    }


    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void anhXa() {
        recyclerViewThanhcongcu = (RecyclerView) findViewById(R.id.recycleviewThanhcongcu);
        thanhcongcuClasses = new ArrayList<>();
        tbKetnois = new ArrayList<>();
        sodoCambienList = new ArrayList<>();
        thanhcongcuClasses.add(new thanhcongcuClass(1, R.drawable.bluetooth, "Bluetooth"));
        thanhcongcuClasses.add(new thanhcongcuClass(2, R.drawable.icon_usb, "USB"));
        thanhcongcuClasses.add(new thanhcongcuClass(3, R.drawable.home, "Home"));
        thanhcongcuClasses.add(new thanhcongcuClass(4, R.drawable.tool, "Công cụ"));
        thanhcongcuClasses.add(new thanhcongcuClass(5, R.drawable.bocuc, "Bố cục"));
        thanhcongcuClasses.add(new thanhcongcuClass(6, R.drawable.file_icon, "Lịch sử"));

        thanhcongcuAdapter = new thanhcongcuAdapter(getApplicationContext(), thanhcongcuClasses, this);
        recyclerViewThanhcongcu.setHasFixedSize(true);
        recyclerViewThanhcongcu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewThanhcongcu.setAdapter(thanhcongcuAdapter);
        Uuid.camBiens.add(new CamBien("Temp", Uuid.Temp, Uuid.dvTemp, Uuid.iconDevice[0], Uuid.hesoTemp, Uuid.tansoTemp, Uuid.daiDoTemp));
        Uuid.camBiens.add(new CamBien("Humid", Uuid.Humid, Uuid.dvHumid, Uuid.iconDevice[1], Uuid.hesoHumid, Uuid.tansoHumid, Uuid.daiDoHumid));
        Uuid.camBiens.add(new CamBien("Pressure", Uuid.Pressure, Uuid.dvPressure, Uuid.iconDevice[2], Uuid.hesoPressure, Uuid.tansoPressure, Uuid.daiDoPressure));
        Uuid.camBiens.add(new CamBien("Oxygen", Uuid.Oxygen, Uuid.dvOxygen, Uuid.iconDevice[3], Uuid.hesoOxygen, Uuid.tansoOxygen, Uuid.daiDoOxygen));
        Uuid.camBiens.add(new CamBien("CO2", Uuid.CO2, Uuid.dvCO2, Uuid.iconDevice[4], Uuid.hesoCO2, Uuid.tansoCO2, Uuid.daiDoCO2));
        Uuid.camBiens.add(new CamBien("SoundI", Uuid.SoundI, Uuid.dvSoundI, Uuid.iconDevice[5], Uuid.hesoSoundI, Uuid.tansoSoundI, Uuid.daiDoSoundI));
        Uuid.camBiens.add(new CamBien("PH", Uuid.PH, Uuid.dvPH, Uuid.iconDevice[6], Uuid.hesoPH, Uuid.tansoPH, Uuid.daiDoPH));
        Uuid.camBiens.add(new CamBien("Salinity", Uuid.Salinity, Uuid.dvSalinity, Uuid.iconDevice[7], Uuid.hesoSalinity, Uuid.tansoSalinity, Uuid.daiDoSalinity));
        Uuid.camBiens.add(new CamBien("DissolveOxy", Uuid.DissolveOxy, Uuid.dvDissolveOxy, Uuid.iconDevice[8], Uuid.hesoDissolveOxy, Uuid.tansoDissolveOxy, Uuid.daiDoDissolveOxy));
        Uuid.camBiens.add(new CamBien("Force", Uuid.Force, Uuid.dvForce, Uuid.iconDevice[9], Uuid.hesoForce, Uuid.tansoForce, Uuid.daiDoForce));
        Uuid.camBiens.add(new CamBien("Current", Uuid.Current, Uuid.dvCurrent, Uuid.iconDevice[10], Uuid.hesoCurrent, Uuid.tansoCurrent, Uuid.daiDoCurrent));
        Uuid.camBiens.add(new CamBien("Voltage", Uuid.Voltage, Uuid.dvVoltage, Uuid.iconDevice[11], Uuid.hesoVoltage, Uuid.tansoVoltage, Uuid.daiDoVoltage));
        Uuid.camBiens.add(new CamBien("SoundF", Uuid.Frequency, Uuid.dvFrequency, Uuid.iconDevice[12], Uuid.hesoFrequency, Uuid.tansoFrequency, Uuid.daiDoFrequency));
        Uuid.camBiens.add(new CamBien("Distance", Uuid.Vitri, Uuid.dvVitri, Uuid.iconDevice[13], Uuid.hesoVitri, Uuid.tansoVitri, Uuid.daiDoVitri));
        Uuid.camBiens.add(new CamBien("Conductivity", Uuid.Dodan, Uuid.dvDodan, Uuid.iconDevice[14], Uuid.hesoDodan, Uuid.tansoDodan, Uuid.daiDoDodan));
        Uuid.camBiens.add(new CamBien("V&A", "Dòng Áp", null, Uuid.iconDevice[15], null, null, Uuid.daiDoDodan));
        Uuid.camBiens.add(new CamBien("Light", Uuid.Dosang, Uuid.dvDosang, Uuid.iconDevice[16], Uuid.hesoDosang, Uuid.tansoDosang, Uuid.daiDoDosang));


//        frmFragment=(FrameLayout) findViewById(R.id.framelayoutFragment);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentKieuDocDulieu(fragmentManager)).commit();
        btnStart = (Button) findViewById(R.id.buttonBatdau_mainActivity);
        btnChontanso = (Button) findViewById(R.id.buttonChontanso_mainActivity);
        tbUSB = new ArrayList<>();
        tbScansUsb = new ArrayList<>();
        soCambienUSB = new ArrayList<>();
        listDulieucaccambien = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
        allRuns = new ArrayList<>();
        initUSB();
    }

    private void initBLE() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000).setConnectOverTime(20000).setOperateTimeout(5000);
    }

    @Override
    public void onItemClick(thanhcongcuClass thanhconcu, View view) {
        switch (thanhconcu.getId()) {
            case 1: {
                ketNoiBluetooth();
                break;
            }
            case 2: {
                ketNoiUSB();
                break;
            }
            case 3: {
                quayVeHome();
                break;
            }
            case 4: {
                hieuChinhCamBien();
                break;
            }
            case 5: {
                hienThiBocuc(view);
                break;
            }
            case 6: {
                hienThiFile(view);
                break;
            }
        }
    }

    private void hieuChinhCamBien() {
        DialogFragment calibration = CalibFragment.newInstance();
        calibration.show(getSupportFragmentManager(), "calib");
    }

    private void ketNoiBluetooth() {
        DialogFragment bluetooth = BluetoothFragment.newInstance();
        bluetooth.show(getSupportFragmentManager(), "tag");

    }

    private void ketNoiUSB() {
        USBFragment usb = USBFragment.newInstance();
        usb.setOnDataChangeListener(this);
        usb.show(getSupportFragmentManager(), "usb");
//        Intent intent = new Intent(MainActivity.this, UsbActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void quayVeHome() {
        fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentKieuDocDulieu(fragmentManager)).commit();
    }

    private void hienThiFile(View view) {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_lichsu, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.heightPixels;
        int maxHeight = screenWidth *2/ 3;

        ListView lvSolanchay = (ListView) popupView.findViewById(R.id.listview_lichsu);
        TextView txtEmpty = (TextView) popupView.findViewById(R.id.textviewEmpty_lichsu);
        ImageView imgOpen = (ImageView) popupView.findViewById(R.id.imageOpen_lichsu);
        ImageView imgSave = (ImageView) popupView.findViewById(R.id.imageSave_lichsu);

        LichsuAdapter lichsuAdapter = new LichsuAdapter(getBaseContext(), R.layout.dong_lichsu, allRuns);
        lichsuAdapter.setOnItemActionListener(new LichsuAdapter.OnItemActionListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getBaseContext(), "Chon lan " + (position + 1), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeleteClick(int position) {
                allRuns.remove(position);
                lichsuAdapter.notifyDataSetChanged();
            }
        });
        lvSolanchay.setEmptyView(txtEmpty);

        lvSolanchay.setAdapter(lichsuAdapter);

        PopupWindow popupWindow;
        if (allRuns.size() > 4) {
            popupWindow = new PopupWindow(popupView, 400, maxHeight, true);

        } else {
            popupWindow = new PopupWindow(popupView, 400, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        imgOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                lichsu.add("lan" + lichsu.size());
//                lichsuAdapter.notifyDataSetChanged();
//                int newHeight = (lichsu.size() > 4) ? maxHeight : ViewGroup.LayoutParams.WRAP_CONTENT;
//                popupWindow.update(400, newHeight);
            }
        });
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog(MainActivity.this);
            }
        });
    }

    public void showSaveDialog(Context context) {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lưu file");

        // Tạo EditText để nhập tên file
        final EditText input = new EditText(context);
        input.setHint("Nhập tên file");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Nút Lưu
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String fileName = input.getText().toString().trim();
            if (!fileName.isEmpty()) {
                // Gọi phương thức lưu file Excel
                exportToExcel(context, fileName, listDulieucaccambien);
            } else {
                Toast.makeText(context, "Tên file không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void exportToExcel(Context context, String fileName, List<DulieuCacCamBien> dataList) {
        // Kiểm tra và yêu cầu quyền bộ nhớ ngoài
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            return;
        }

        // Tạo Workbook và Sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sensor Data");
        if (allRuns.isEmpty()) return;

        int sensorCount = allRuns.get(0).getSensors().size();
        int groupSize = sensorCount + 1;
        Row headerRow1 = sheet.createRow(0);
        int col = 0;
        for (int i = 0; i < allRuns.size(); i++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, col, col + groupSize - 1));
            Cell cell = headerRow1.createCell(col);
            cell.setCellValue("Chạy " + allRuns.get(i).getRunNumber());
            col += groupSize;
        }
        Row headerRow2 = sheet.createRow(1);
        col = 0;
        for (Run run : allRuns) {
            headerRow2.createCell(col++).setCellValue("Thời gian (s)");
            for (SensorData sensor : run.getSensors()) {
                headerRow2.createCell(col++).setCellValue(sensor.getSensorName() + " (DV)");
            }
        }
        int maxRowCount = 0;
        for (Run run : allRuns) {
            for (SensorData sensor : run.getSensors()) {
                maxRowCount = Math.max(maxRowCount, sensor.values.size());
            }
        }

        for (int rowIdx = 0; rowIdx < maxRowCount; rowIdx++) {
            Row row = sheet.getRow(rowIdx + 2);
            if (row == null) row = sheet.createRow(rowIdx + 2);

            col = 0;
            for (Run run : runList) {
                double timeStep = 1.0 / run.frequency;
                row.createCell(col++).setCellValue(rowIdx * timeStep);

                for (SensorData sensor : run.sensors) {
                    if (rowIdx < sensor.values.size()) {
                        row.createCell(col++).setCellValue(sensor.values.get(rowIdx));
                    } else {
                        row.createCell(col++).setBlank(); // nếu thiếu giá trị thì để trống
                    }
                }
            }
        }
        // Lưu Workbook vào bộ nhớ ngoài
        try {
            File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS); // an toàn hơn
            File file = new File(downloadsDir, fileName + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            Toast.makeText(context, "Đã lưu file tại: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi khi lưu file", Toast.LENGTH_SHORT).show();
        }
    }

    private void hienThiBocuc(View view) {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_bocuc, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 400, 400, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        ImageView imgBocuc1, imgBocuc2, imgBocuc3, imgBocuc4;
        imgBocuc1 = (ImageView) popupView.findViewById(R.id.imageviewBocuc1);
        imgBocuc2 = (ImageView) popupView.findViewById(R.id.imageviewBocuc2);
        imgBocuc3 = (ImageView) popupView.findViewById(R.id.imageviewBocuc3);
        imgBocuc4 = (ImageView) popupView.findViewById(R.id.imageviewBocuc4);

        imgBocuc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentBocuc1(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentBocuc2(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentBocuc3(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentBocuc4(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
    }

    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            // Ẩn thanh điều hướng và thiết lập kích thước cửa sổ đầy đủ
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
//    }
    private void checkPermissions() {
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        Log.d("life","onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventToast(DataEvent dataEvent) {
    }

    private void send(byte[] data) {
        if (!isConnectedUSB) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {

        }
    }

    public static boolean isNibbleReversed(byte value) {
        int x = (int) (value & 0xFF);
        // Lấy nibble thứ nhất (4 bit đầu)
        int firstNibble = x >> 4;

        // Lấy nibble thứ hai (4 bit cuối)
        int secondNibble = x & 0x0F;

        // Đảo ngược nibble thứ nhất
        int reversedFirstNibble = secondNibble ^ 0x0F;

        // So sánh với nibble thứ hai
        return reversedFirstNibble == firstNibble;
    }

    public static int connect2Nibble(byte a, byte b) {
        int first = (int) (a & 0xff) >> 4;
        int second = (int) (b & 0xff) >> 4;
        System.out.println("first=" + first);
        System.out.println("second=" + second);
        return (first << 4) | second;
    }

    public static int firstNibble(int what) {
        int c = (what >> 4);
        return ((c << 4) | (c ^ 0x0F));
    }

    public static int secondNibble(int what) {
        int c = (what & 0x0F);
        return ((c << 4) | (c ^ 0x0F));
    }

    public static int crc8(byte[] data, int length) {
        int crc = 0;  // Khởi tạo CRC
        for (int j = 0; j < length; j++) {
            int inbyte = data[j] & 0xFF;  // Chuyển byte thành int không dấu
            for (int i = 0; i < 8; i++) {
                int mix = (crc ^ inbyte) & 0x01;
                crc >>= 1;
                if (mix != 0) {
                    crc ^= 0x8C;  // XOR với polynom (0x8C cho CRC-8)
                }
                inbyte >>= 1;
            }
        }
        return crc & 0xFF;  // Đảm bảo giá trị trả về trong phạm vi 0-255
    }

    //
//    @Override
//    protected void onResume() {
//        super.onResume();
//        EventBus.getDefault().register(this);
//    }


    @Override
    public void onDataReceived(boolean isConnected) {
//        for (byte inByte : data) {
//            Log.d("data", Integer.toHexString(inByte).toUpperCase());
//        }
//        Log.d("data", isConnected + "");
        if (isConnected) {
            usbIoManager = new SerialInputOutputManager(usbSerialPort, new SerialInputOutputManager.Listener() {
                @Override
                public void onNewData(byte[] data) {
//                    String hex = HexDump.dumpHexString(data);
//                    Log.d("data", hex + " Length: " + data.length);
                    byte[] dataByte = new byte[100];
                    for (byte inByte : data) {
                        int unsignedInByte = inByte & 0xFF;
                        switch (unsignedInByte) {
                            case 2:
                                haveSTX = true;
                                haveETX = false;
                                inputPos = 0;
                                firstNibble_ = true;
                                break;
                            case 3:
                                haveETX = true;
                                break;
                            default:
                                if (!haveSTX)
                                    break;
                                if ((unsignedInByte >> 4) != ((unsignedInByte & 0x0F) ^ 0x0F)) {
                                    reset();
                                    break;  // Ký tự không hợp lệ
                                }
                                unsignedInByte >>= 4;
                                if (firstNibble_) {
                                    currentByte = unsignedInByte;  // Lưu giá trị nibble đầu tiên
                                    firstNibble_ = false;
                                    break;
                                }
                                currentByte <<= 4;  // Dịch trái 4 bit để chừa chỗ cho nibble thấp
                                currentByte |= unsignedInByte;  // Ghép nibble thấp với currentByte_
                                firstNibble_ = true;
                                if (haveETX) {
                                    if (crc8(dataByte, inputPos) != currentByte) {
                                        reset();
                                        break;  // bad crc
                                    } // end of bad CRC

                                    isDataReceiving = true;
                                    //return true;  // show data ready
                                }  // end if have ETX already

                                // keep adding if not full
                                if (inputPos < bufferSize)
                                    dataByte[inputPos++] = (byte) currentByte;
                                else {
                                    reset(); // overflow, start again
                                }
                                break;
                        }
                    }
                    if (isDataReceiving) {
                        for (int i = 0; i < inputPos - 1; i++) {
                            Log.d("data", dataByte[i] + "");
                        }
                        if (dataByte[0] == 0) {
                            if (dataByte[1] == 1) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (CamBienUSB tbScan : tbScansUsb) {
                                            if (tbScan.getCamBien().getName().equals(Uuid.camBiens.get((int) dataByte[3] - 1).getName()) &&
                                                    tbScan.getId() == (int) dataByte[2]) {
                                                return;
                                            }
                                        }
                                        CamBien cambien = Uuid.camBiens.get((int) dataByte[3] - 1);
                                        if (dataByte[3] == 2) {

                                            soCambienUSB.add(new sodoCambienUSB(cambien.getId(), (int) dataByte[2], Uuid.Temp,
                                                    Uuid.dvTemp, Uuid.iconDevice[0], Uuid.hesoTemp, Uuid.tansoTemp, 1));
                                            soCambienUSB.add(new sodoCambienUSB(cambien.getId(), (int) dataByte[2], Uuid.Humid,
                                                    Uuid.dvHumid, Uuid.iconDevice[1], Uuid.hesoHumid, Uuid.tansoHumid, 1));
                                        } else {
                                            soCambienUSB.add(new sodoCambienUSB(cambien.getId(), (int) dataByte[2], cambien.getName(), cambien.getDonvi(), cambien.getIcon(), cambien.getHeso(), cambien.getTanso(), 2));
                                        }
                                        tbScansUsb.add(new CamBienUSB(cambien, (int) dataByte[2]));
                                        USBFragment.tbScanAdapterUSB.notifyDataSetChanged();
                                    }
                                });
//                    Log.d("data", "OK");

                            }
                            if (dataByte[1] == 2) {
                                int cambien = dataByte[3];
                                int thuTuCambien = dataByte[2];
                                if (cambien == 2) {
                                    int length = dataByte[4];  // Lấy độ dài từ msg[4]
                                    StringBuilder decodedString = new StringBuilder();
                                    for (int i = 0; i < length; i++) {
                                        decodedString.append((char) dataByte[i + 5]);
                                    }

                                    String result = decodedString.toString();
                                    Log.d("data", result);
                                    String nhietdo = "0.00";
                                    int lengthNhietdo = dataByte[5 + length];
                                    StringBuilder nhietdoString = new StringBuilder();
                                    for (int i = 0; i < lengthNhietdo; i++) {
                                        nhietdoString.append((char) dataByte[i + 6 + length]);
                                    }
                                    nhietdo = nhietdoString.toString();
                                    List<Float> mangValue = new ArrayList<>();
                                    try {
                                        float value = Float.parseFloat(result);
                                        mangValue.add(value);

                                    } catch (NumberFormatException e) {
                                        System.out.println("Chuỗi không hợp lệ để chuyển thành float.");
                                    }
                                    List<Float> mangNhietdo = new ArrayList<>();
                                    try {
                                        float value = Float.parseFloat(nhietdo);
                                        mangNhietdo.add(value);

                                    } catch (NumberFormatException e) {
                                        System.out.println("Chuỗi không hợp lệ để chuyển thành float.");
                                    }
                                    if (tbScansUsb.size() > 0) {
                                        int vitri = 0;
                                        for (int i = 0; i < tbScansUsb.size(); i++) {
                                            if (tbScansUsb.get(i).getCamBien().getId().equals(Uuid.camBiens.get(1).getId()) &&
                                                    tbScansUsb.get(i).getId() == thuTuCambien) {
                                                vitri = i;
                                            }
                                        }
                                        String deviceId = tbScansUsb.get(vitri).getCamBien().getId() + "-" + tbScansUsb.get(vitri).getId();
                                        DulieuCB dulieuCB1 = new DulieuCB(deviceId, Uuid.Humid, mangValue);
                                        EventBus.getDefault().post(new DataEvent(dulieuCB1));
                                        DulieuCB dulieuNhietdo = new DulieuCB(deviceId, Uuid.Temp, mangNhietdo);
                                        EventBus.getDefault().post(new DataEvent(dulieuNhietdo));
                                    }
                                } else {
                                    int length = dataByte[4];  // Lấy độ dài từ msg[4]
                                    StringBuilder decodedString = new StringBuilder();
                                    for (int i = 0; i < length; i++) {
                                        decodedString.append((char) dataByte[i + 5]);
                                    }

                                    String result = decodedString.toString();
                                    Log.d("data", result);
                                    List<Float> mangValue = new ArrayList<>();
                                    try {
                                        float value = Float.parseFloat(result);
                                        mangValue.add(value);

                                    } catch (NumberFormatException e) {
                                        System.out.println("Chuỗi không hợp lệ để chuyển thành float.");
                                    }
                                    if (tbScansUsb.size() > 0) {
                                        int vitri = 0;
                                        for (int i = 0; i < tbScansUsb.size(); i++) {
                                            if (tbScansUsb.get(i).getCamBien().getId().equals(Uuid.camBiens.get(cambien - 1).getId()) &&
                                                    tbScansUsb.get(i).getId() == thuTuCambien) {
                                                vitri = i;
                                            }
                                        }
                                        String deviceId = tbScansUsb.get(vitri).getCamBien().getId() + "-" + tbScansUsb.get(vitri).getId();
                                        DulieuCB dulieuCB1 = new DulieuCB(deviceId, tbScansUsb.get(vitri).getCamBien().getName(), mangValue);
                                        EventBus.getDefault().post(new DataEvent(dulieuCB1));
                                    }
                                }


//                                List<Byte> receivedData = new ArrayList<>();
//                                for (int i = 4; i < inputPos - 1; i++) {
//                                    receivedData.add(dataByte[i]);
//
//                                }
//                                byte[] dataReceived = new byte[receivedData.size()];
//                                for (int i = 0; i < receivedData.size(); i++) {
//                                    dataReceived[i] = receivedData.get(i);
//                                }

//                                for (int i = 0; i < receivedData.size() / 4; i++) {
////                                    float value = ByteBuffer.wrap(dataReceived, i * 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
////                                    mangValue.add(value);
////                                    Log.d("data", value + "");
//                                }

//                                Log.d("data", receivedData.toString());
                            }

                        }
                        isDataReceiving = false;
                    }
                }

                @Override
                public void onRunError(Exception e) {

                }
            });
            usbIoManager.start();
        } else {
            if (usbIoManager != null) {
                usbIoManager.setListener(null);
                usbIoManager.stop();
            }
            usbIoManager = null;
        }
    }

    public void reset() {
        haveSTX = false;
        inputPos = 0;
    }

    private void initUSB() {
        // Find all available drivers from attached devices.
        UsbManager usbManager = (UsbManager) getBaseContext().getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        MainActivity.tbUSB.clear();
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if (driver == null) {
                driver = usbCustomProber.probeDevice(device);
            }
            if (driver != null) {
                for (int port = 0; port < driver.getPorts().size(); port++)
                    MainActivity.tbUSB.add(new ListItemUSB(device, port, driver));
            }
//            else {
//                MainActivity.tbUSB.add(new ListItemUSB(device, 0, null));
//            }
        }

    }

}