package com.example.sensorcollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.sensorcollection.adapter.thanhcongcuAdapter;
import com.example.sensorcollection.fragment.BluetoothFragment;
import com.example.sensorcollection.fragment.FragmentBocuc1;
import com.example.sensorcollection.fragment.FragmentBocuc2;
import com.example.sensorcollection.fragment.FragmentBocuc3;
import com.example.sensorcollection.fragment.FragmentBocuc4;
import com.example.sensorcollection.model.CamBien;
import com.example.sensorcollection.model.DulieuCacCamBien;
import com.example.sensorcollection.model.thanhcongcuClass;
import com.example.sensorcollection.my_interface.ItemClickListener;
import com.example.sensorcollection.ultis.DataEvent;
import com.example.sensorcollection.ultis.Uuid;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    RecyclerView recyclerViewThanhcongcu;
    ArrayList<thanhcongcuClass> thanhcongcuClasses;
    com.example.sensorcollection.adapter.thanhcongcuAdapter thanhcongcuAdapter;
    public static List<BleDevice> tbKetnois;
    FrameLayout frmFragment;
    FragmentManager fragmentManager;
    Button btnStart, btnChontanso;
    boolean isStart=false;
    Map<String,Float> listDulieuCambien;
    public static float thoigian=0;
    public static int tansoLayMau=1000;
    public static float solanchay=0;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        decorView.setSystemUiVisibility(uiOptions);
        setContentView(R.layout.activity_main);
        checkPermissions();
        anhXa();
        initBLE();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart=!isStart;
                if(isStart == true){
                    byte[] tempArray = ByteBuffer.allocate(4).putInt(tansoLayMau).array();
                    byte[] byteArray = new byte[tempArray.length + 1];
                    byteArray[0] = 0x01;
                    System.arraycopy(tempArray, 0, byteArray, 1, tempArray.length);
                    for (BleDevice bleDevice:tbKetnois
                    ) {
                        BleManager.getInstance().write(bleDevice, Uuid.serviceUuid, Uuid.readUuid, byteArray, new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                btnStart.setText("Dừng Lại");
                                solanchay++;
                                Map<String,Float[]> lanchay=new HashMap<>();
                                Float[] manglanchay= new Float[2];
                                manglanchay[0]=solanchay;
                                manglanchay[1]=Float.valueOf(tansoLayMau);
                                lanchay.put("lanchay",manglanchay);
                                EventBus.getDefault().post(new DataEvent(lanchay));

                                int colorDo = ContextCompat.getColor(getBaseContext(), R.color.btnStop);
                                btnStart.setBackgroundColor(colorDo);
                                btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_stop,0,0,0);
//                                for (BleDevice bleDevice: tbKetnois
//                                ) {

                                    BleManager.getInstance().notify(bleDevice, Uuid.serviceUuid, Uuid.readUuid, new BleNotifyCallback() {
                                        @Override
                                        public void onNotifySuccess() {

                                        }

                                        @Override
                                        public void onNotifyFailure(BleException exception) {

                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {
                                            Map<String,Float[]> mapDulieu =new HashMap<>();
                                            Float[] mangValue = new Float[(data.length - 2) / 4];
                                            if(data!=null){
                                                if(data[0] == 0x02 && data[data.length-1] == 0x03 ){
                                                    for(int i=0;i< (data.length-2)/4;i++){
                                                        float value =ByteBuffer.wrap(data, i*4+1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                                                        mangValue[i] =value;
                                                    }
                                                    mapDulieu.put(bleDevice.getName(),mangValue);
                                                    EventBus.getDefault().post(new DataEvent(mapDulieu));
//                                        value = ByteBuffer.wrap(data, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
////                                        Log.d("dulieu",String.valueOf(value));
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

                }else{
                    btnStart.setText("Bắt Đầu ");
                    int colorXanh = ContextCompat.getColor(getBaseContext(), R.color.xanhduongnhat);
                    btnStart.setBackgroundColor(colorXanh);
                    btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_start,0,0,0);
                    byte[] byteArray = new byte[5];
                    byteArray[0] = 0x02;
                    byteArray[1] = 0x79;
                    for (BleDevice bleDevice: tbKetnois
                    ) {
                        BleManager.getInstance().write(bleDevice, Uuid.serviceUuid, Uuid.readUuid, byteArray, new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                BleManager.getInstance().stopNotify(bleDevice,Uuid.serviceUuid,Uuid.readUuid);
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                    }
                    listDulieuCambien.clear();
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
                                tansoLayMau=1000;
                                break;
                            case R.id.tanso2Hz:
                                tansoLayMau=500;
                                break;
                            case R.id.tanso5Hz:
                                tansoLayMau=200;
                                break;
                            case R.id.tanso10Hz:
                                tansoLayMau=100;
                                break;
                            case R.id.tanso20Hz:
                                tansoLayMau=50;
                                break;
                            case R.id.tanso50Hz:
                                tansoLayMau=20;
                                break;
                            case R.id.tanso100Hz:
                                tansoLayMau=10;
                                break;
                            case R.id.tanso200Hz:
                                tansoLayMau=5;
                                break;
                            case R.id.tanso500Hz:
                                tansoLayMau=2;
                                break;
                            case R.id.tanso1000Hz:
                                tansoLayMau=1;
                                break;
                        }
                        btnChontanso.setText("Tần Số: " + (1000/tansoLayMau) + "Hz");

                        return false;
                    }
                });
            }
        });
    }
    private void anhXa() {
        recyclerViewThanhcongcu=(RecyclerView) findViewById(R.id.recycleviewThanhcongcu);
        thanhcongcuClasses=new ArrayList<>();
        tbKetnois=new ArrayList<>();
        thanhcongcuClasses.add(new thanhcongcuClass(1,R.drawable.bluetooth,"Bluetooth"));
        thanhcongcuClasses.add(new thanhcongcuClass(2,R.drawable.home,"Home"));
//        thanhcongcuClasses.add(new thanhcongcuClass(3,R.drawable.share,"Chia sẻ"));
//        thanhcongcuClasses.add(new thanhcongcuClass(4,R.drawable.tool,"Công cụ"));
        thanhcongcuClasses.add(new thanhcongcuClass(5,R.drawable.bocuc,"Bố cục"));
        thanhcongcuAdapter=new thanhcongcuAdapter(getApplicationContext(),thanhcongcuClasses,this);
        recyclerViewThanhcongcu.setHasFixedSize(true);
        recyclerViewThanhcongcu.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerViewThanhcongcu.setAdapter(thanhcongcuAdapter);
        Uuid.camBiens.add(new CamBien("Temp",Uuid.Temp,Uuid.dvTemp,Uuid.iconDevice[0],Uuid.hesoTemp));
        Uuid.camBiens.add(new CamBien("Humid",Uuid.Humid,Uuid.dvHumid,Uuid.iconDevice[1],Uuid.hesoHumid));
        Uuid.camBiens.add(new CamBien("Pressure",Uuid.Pressure,Uuid.dvPressure,Uuid.iconDevice[2],Uuid.hesoPressure));
        Uuid.camBiens.add(new CamBien("Oxygen",Uuid.Oxygen,Uuid.dvOxygen,Uuid.iconDevice[3],Uuid.hesoOxygen));
        Uuid.camBiens.add(new CamBien("CO2",Uuid.CO2,Uuid.dvCO2,Uuid.iconDevice[4],Uuid.hesoCO2));
        Uuid.camBiens.add(new CamBien("SoundI",Uuid.SoundI,Uuid.dvSoundI,Uuid.iconDevice[5],Uuid.hesoSoundI));
        Uuid.camBiens.add(new CamBien("PH",Uuid.PH,Uuid.dvPH,Uuid.iconDevice[6],Uuid.hesoPH));
        Uuid.camBiens.add(new CamBien("Salinity",Uuid.Salinity,Uuid.dvSalinity,Uuid.iconDevice[7],Uuid.hesoSalinity));
        Uuid.camBiens.add(new CamBien("DissolveOxy",Uuid.DissolveOxy,Uuid.dvDissolveOxy,Uuid.iconDevice[8],Uuid.hesoDissolveOxy));
        Uuid.camBiens.add(new CamBien("Force",Uuid.Force,Uuid.dvForce,Uuid.iconDevice[9],Uuid.hesoForce));
        Uuid.camBiens.add(new CamBien("Current",Uuid.Current,Uuid.dvCurrent,Uuid.iconDevice[10],Uuid.hesoCurrent));
        Uuid.camBiens.add(new CamBien("Voltage",Uuid.Voltage,Uuid.dvVoltage,Uuid.iconDevice[11],Uuid.hesoVoltage));
        Uuid.camBiens.add(new CamBien("SoundF",Uuid.Frequency,Uuid.dvFrequency,Uuid.iconDevice[12],Uuid.hesoFrequency));
        Uuid.camBiens.add(new CamBien("Distance",Uuid.Vitri,Uuid.dvVitri,Uuid.iconDevice[13],Uuid.hesoVitri));
        Uuid.camBiens.add(new CamBien("Conductivity",Uuid.Dodan,Uuid.dvDodan,Uuid.iconDevice[14],Uuid.hesoDodan));

//        frmFragment=(FrameLayout) findViewById(R.id.framelayoutFragment);
        fragmentManager=getSupportFragmentManager();
         btnStart=(Button) findViewById(R.id.buttonBatdau_mainActivity);
         btnChontanso=(Button) findViewById(R.id.buttonChontanso_mainActivity);
        listDulieuCambien=new HashMap<>();
    }

    private void initBLE() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }
    @Override
    public void onItemClick(thanhcongcuClass thanhconcu,View view) {
        switch (thanhconcu.getId()){
            case 1:{
                ketNoiBluetooth();
                break;
            }
            case 5:{
                hienThiBocuc(view);
                break;
            }
        }
    }
    private void ketNoiBluetooth(){
        DialogFragment bluetooth= BluetoothFragment.newInstance();
        bluetooth.show(getSupportFragmentManager(),"tag");
    }
//    private void hienThiBocuc(){
 //        frg_trans.add(R.id.framelayoutFragment,new BocucFragment());
//        frg_trans.commit();
//    }
    private void hienThiBocuc(View view){
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_bocuc, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 400, 400, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        ImageView imgBocuc1,imgBocuc2,imgBocuc3,imgBocuc4;
        imgBocuc1=(ImageView) popupView.findViewById(R.id.imageviewBocuc1);
        imgBocuc2=(ImageView) popupView.findViewById(R.id.imageviewBocuc2);
        imgBocuc3=(ImageView) popupView.findViewById(R.id.imageviewBocuc3);
        imgBocuc4=(ImageView) popupView.findViewById(R.id.imageviewBocuc4);

        imgBocuc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main,new FragmentBocuc1(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main,new FragmentBocuc2(fragmentManager)).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main,new FragmentBocuc3()).commit();
                popupWindow.dismiss();
            }
        });
        imgBocuc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.layoutFragment_main,new FragmentBocuc4()).commit();
                popupWindow.dismiss();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Ẩn thanh điều hướng và thiết lập kích thước cửa sổ đầy đủ
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
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
    @Subscribe (threadMode = ThreadMode.MAIN)
    public void eventToast(DataEvent dataEvent){
    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        EventBus.getDefault().register(this);
//    }


}