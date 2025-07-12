package com.mtsc.mview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

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
import com.clj.fastble.callback.BleIndicateCallback;
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
import com.mtsc.mview.adapter.tbKetNoiAdapter;
import com.mtsc.mview.adapter.thanhcongcuAdapter;
import com.mtsc.mview.fragment.BluetoothFragment;
import com.mtsc.mview.fragment.CalibFragment;
import com.mtsc.mview.fragment.FragmentBocuc1;
import com.mtsc.mview.fragment.FragmentBocuc2;
import com.mtsc.mview.fragment.FragmentBocuc3;
import com.mtsc.mview.fragment.FragmentBocuc4;
import com.mtsc.mview.fragment.FragmentKieuDocDulieu;
import com.mtsc.mview.fragment.FragmentMachDienXC;
import com.mtsc.mview.fragment.FragmentNhapbangtay;
import com.mtsc.mview.fragment.FragmentSongam;
import com.mtsc.mview.fragment.USBFragment;
import com.mtsc.mview.model.CamBien;
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.model.ConnectedDevice;
import com.mtsc.mview.model.CustomProber;
import com.mtsc.mview.model.DeviceDataBuffer;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuCacCamBien;
import com.mtsc.mview.model.KalmanFilterWrapper;
import com.mtsc.mview.model.ListItemUSB;
import com.mtsc.mview.model.Run;
import com.mtsc.mview.model.SensorData;
import com.mtsc.mview.model.SodoCambien;
import com.mtsc.mview.model.TrangThaiKetNoi;
import com.mtsc.mview.model.sodoCambienUSB;
import com.mtsc.mview.model.thanhcongcuClass;
import com.mtsc.mview.my_interface.DataListener;
import com.mtsc.mview.my_interface.ItemClickListener;
import com.mtsc.mview.ultis.DataEvent;
import com.mtsc.mview.ultis.Uuid;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ItemClickListener, USBFragment.OnDataReceivedListener, BluetoothFragment.OnDeviceDisconnectedListener, DataListener {
    RecyclerView recyclerViewThanhcongcu;
    Handler handler, bleHandler;
    ArrayList<thanhcongcuClass> thanhcongcuClasses;
    com.mtsc.mview.adapter.thanhcongcuAdapter thanhcongcuAdapter;
    public static tbKetNoiAdapter tbKetnoiAdapter;
    public static List<ConnectedDevice> tbKetnois;
    public static List<SodoCambien> sodoCambienList;
    FrameLayout frmFragment;
    FragmentManager fragmentManager;
    Button btnStart, btnChontanso, btnHieuchinh;
    boolean isStart = false;
    public static float thoigian = 0;
    public static double tansoLayMau = 1000;
    private int timeBleHandler = 1000;
    public float solanchay = 0;
    public static List<Run> allRuns;
    public LichsuAdapter lichsuAdapter;
    Timer timer1;
    public static TrangThaiKetNoi trangThai = TrangThaiKetNoi.KHAC;
    public static UsbSerialPort usbSerialPort;
    public static List<CamBienUSB> tbScansUsb;
    public static List<sodoCambienUSB> soCambienUSB;
    public static List<ListItemUSB> tbUSB;
    private SerialInputOutputManager usbIoManager;

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static final int REQUEST_WRITE_PERMISSION = 100;

    private static final int WRITE_WAIT_MILLIS = 2000;
    private boolean haveSTX = false, haveETX = false, firstNibble_ = false, isDataReceiving = false;
    private int inputPos = 0, currentByte;
    private int bufferSize = 100;
    int chayCambien = 0;

    private DeviceDataBuffer dataBufferBle;
    private static final int BUFFER_SIZE = 10000; // Kích thước bộ đệm

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("app_lang", "vi");
        Context context = updateLocale(newBase, lang);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Ẩn tiêu đề của Activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Ẩn thanh trạng thái
        setContentView(R.layout.activity_main);
//        hideNavigationBar();
        checkPermissions();
        anhXa();
        initBLE();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = !isStart;
                if (isStart == true) {
                    if (trangThai == TrangThaiKetNoi.BLE) {
                        handleStartBLE();
                    } else if (trangThai == TrangThaiKetNoi.USB) {
                        handleStartUSB();
                    }
                } else {
                    btnStart.setText(getString(R.string.start_button) + " ");
                    int colorXanh = ContextCompat.getColor(getBaseContext(), R.color.xanhduongnhat);
                    btnStart.setBackgroundColor(colorXanh);
                    btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_start, 0, 0, 0);
                    if (trangThai == TrangThaiKetNoi.BLE) {
                        handleStopBLE();
                    } else if (trangThai == TrangThaiKetNoi.USB) {
//                        Log.d("tag", tbScans.get(0).getCamBien().getName() + " cancle");
                        handleStopUSB();
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
                        btnChontanso.setText(getString(R.string.frequency) + " " + (int) (1000 / tansoLayMau) + "Hz");
                        timeBleHandler = tansoLayMau > 200 ? (int) tansoLayMau : 200;
                        return false;
                    }
                });
            }
        });
        btnHieuchinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hieuChinhCamBien();
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

        Uuid.initSensors(getBaseContext());

        thanhcongcuClasses.add(new thanhcongcuClass(1, R.drawable.bluetooth, "Bluetooth"));
        thanhcongcuClasses.add(new thanhcongcuClass(2, R.drawable.icon_usb, "USB"));
        thanhcongcuClasses.add(new thanhcongcuClass(3, R.drawable.home, "Home"));
        thanhcongcuClasses.add(new thanhcongcuClass(4, R.drawable.tool, getString(R.string.setting)));
        thanhcongcuClasses.add(new thanhcongcuClass(5, R.drawable.bocuc, getString(R.string.layout)));
        thanhcongcuClasses.add(new thanhcongcuClass(6, R.drawable.file_icon, getString(R.string.history)));

        thanhcongcuAdapter = new thanhcongcuAdapter(getApplicationContext(), thanhcongcuClasses, this);
        recyclerViewThanhcongcu.setHasFixedSize(true);
        recyclerViewThanhcongcu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewThanhcongcu.setAdapter(thanhcongcuAdapter);

        tbKetnoiAdapter = new tbKetNoiAdapter(MainActivity.this, R.layout.dong_thietbidaketnoi, tbKetnois);
        tbKetnoiAdapter.setOnDeviceClickListener(new tbKetNoiAdapter.OnDeviceClickListener() {
            @Override
            public void onDisConnect(BleDevice bleDevice) {
                BleManager.getInstance().disconnect(bleDevice);
            }
        });
//        frmFragment=(FrameLayout) findViewById(R.id.framelayoutFragment);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layoutFragment_main, new FragmentKieuDocDulieu(fragmentManager)).commit();
        btnStart = (Button) findViewById(R.id.buttonBatdau_mainActivity);
        btnChontanso = (Button) findViewById(R.id.buttonChontanso_mainActivity);
        btnChontanso.setText(getString(R.string.frequency) + " " + (int) (1000 / tansoLayMau) + "Hz");
        btnHieuchinh = (Button) findViewById(R.id.buttonHieuchinh_mainActivity);
        tbUSB = new ArrayList<>();
        tbScansUsb = new ArrayList<>();
        soCambienUSB = new ArrayList<>();
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
                if (allRuns.size() > 0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Xác nhận")
                            .setMessage("Nếu kết nối với thiết bị khác thì các lần chạy trước sẽ bị xóa. Bạn có muốn tiếp tục?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Xử lý khi người dùng nhấn OK - ví dụ: chuyển sang trang kết nối
                                    ketNoiBluetooth();
                                    MainActivity.allRuns.clear();
                                }
                            })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Đóng dialog, không làm gì cả
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    ketNoiBluetooth();
                }
                break;
            }
            case 2: {
                if (allRuns.size() > 0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Xác nhận")
                            .setMessage("Nếu kết nối với thiết bị khác thì các lần chạy trước sẽ bị xóa. Bạn có muốn tiếp tục?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Xử lý khi người dùng nhấn OK - ví dụ: chuyển sang trang kết nối
                                    ketNoiUSB();
                                    MainActivity.allRuns.clear();

                                }
                            })
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Đóng dialog, không làm gì cả
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    ketNoiUSB();

                }
                break;
            }
            case 3: {
                quayVeHome();
                break;
            }
            case 4: {
                showLanguageDialog();
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

    private void showLanguageDialog() {
        final String[] languages = {"English", "Tiếng Việt"};
        final String[] languageCodes = {"en", "vi"};
        int checkedItem = 0; // Default selection

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn ngôn ngữ / Choose language");
        builder.setSingleChoiceItems(languages, checkedItem, null);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            if (selectedPosition == -1) selectedPosition = 0;

            setLocale(languageCodes[selectedPosition]);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);

        // For API 24 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            getApplicationContext().createConfigurationContext(config);
        } else {
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        prefs.edit().putString("app_lang", langCode).apply();
        Intent intent = new Intent(this, MainActivity.class);
        for(ConnectedDevice device:tbKetnois){
            if (device.getDevice() != null) {
                BleManager.getInstance().disconnect(device.getDevice());
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static Context updateLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            return context;
        }
    }

    private void hienThiFile(View view) {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_lichsu, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.heightPixels;
        int maxHeight = screenWidth * 2 / 3;

        ListView lvSolanchay = (ListView) popupView.findViewById(R.id.listview_lichsu);
        TextView txtEmpty = (TextView) popupView.findViewById(R.id.textviewEmpty_lichsu);
        ImageView imgOpen = (ImageView) popupView.findViewById(R.id.imageOpen_lichsu);
        ImageView imgSave = (ImageView) popupView.findViewById(R.id.imageSave_lichsu);
        if (lichsuAdapter == null) {
            lichsuAdapter = new LichsuAdapter(getBaseContext(), R.layout.dong_lichsu, allRuns);
            lichsuAdapter.setOnItemActionListener(new LichsuAdapter.OnItemActionListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(getBaseContext(), "Chon lan " + (position + 1), Toast.LENGTH_SHORT).show();
                    List<Float> manglanchay = new ArrayList<>();
                    manglanchay.add((float) position);
                    DulieuCB dulieuCB = new DulieuCB("xemlai", "xemlai", manglanchay);
                    EventBus.getDefault().post(new DataEvent(dulieuCB));
                }

                @Override
                public void onDeleteClick(int position) {
                    allRuns.remove(position);
                    lichsuAdapter.notifyDataSetChanged();
                }
            });

        }
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

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog(MainActivity.this);
            }
        });
        imgOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOpenFile(MainActivity.this);
                trangThai = TrangThaiKetNoi.HISTORY;
                BleManager.getInstance().disconnectAllDevice();
            }
        });
    }

    public void showSaveDialog(Context context) {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.luu_file));

        // Tạo EditText để nhập tên file
        final EditText input = new EditText(context);
        input.setHint(getString(R.string.nhap_ten_file));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Nút Lưu
        builder.setPositiveButton(getString(R.string.luu), (dialog, which) -> {
            String fileName = input.getText().toString().trim();
            if (!fileName.isEmpty()) {
                // Gọi phương thức lưu file Excel
                exportToExcel(context, fileName, allRuns);
            } else {
                Toast.makeText(context, getString(R.string.ten_file_khong_hop_le), Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Hủy
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void exportToExcel(Context context, String fileName, List<Run> runList) {
        // Kiểm tra và yêu cầu quyền bộ nhớ ngoài
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//            return;
//        }
        // Tạo Workbook và Sheet
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sensor Data");
        if (runList.isEmpty()) return;

        int sensorCount = runList.get(0).getSensors().size();
        int groupSize = sensorCount + 1;
        Row headerRow1 = sheet.createRow(0);
        int col = 0;
        for (int i = 0; i < runList.size(); i++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, col, col + groupSize - 1));
            Cell cell = headerRow1.createCell(col);
            cell.setCellValue(getString(R.string.run_history) + " " + runList.get(i).getRunNumber());
            col += groupSize;
        }
        Row headerRow2 = sheet.createRow(1);
        col = 0;
        for (Run run : runList) {
            headerRow2.createCell(col++).setCellValue("Thời gian (s)");
            for (SensorData sensor : run.getSensors()) {
                headerRow2.createCell(col++).setCellValue(sensor.getSensorName() + "(" + sensor.getSodo().get("donvi")[0] + ")");
            }
        }
        int maxRowCount = 0;
        for (Run run : runList) {
            for (SensorData sensor : run.getSensors()) {
                maxRowCount = Math.max(maxRowCount, sensor.getValues().size());
            }
        }

        for (int rowIdx = 0; rowIdx < maxRowCount; rowIdx++) {
            Row row = sheet.getRow(rowIdx + 2);
            if (row == null) row = sheet.createRow(rowIdx + 2);

            col = 0;
            for (Run run : runList) {
                int maxSensorSize = 0;
                for (SensorData sensor : run.getSensors()) {
                    maxSensorSize = Math.max(maxSensorSize, sensor.getValues().size());
                }
                double timeStep = 1.0 / run.getFrequency();
                if (rowIdx < maxSensorSize) {
                    row.createCell(col++).setCellValue(rowIdx * timeStep);
                } else {
                    row.createCell(col++).setBlank(); // nếu không đủ giá trị thì để trống cả thời gian
                }

                for (SensorData sensor : run.getSensors()) {
                    if (rowIdx < sensor.getValues().size()) {
                        row.createCell(col++).setCellValue(sensor.getValues().get(rowIdx));
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
            outputStream.flush();           // thêm dòng này để đảm bảo dữ liệu được đẩy ra file
            outputStream.close();           // đóng stream ghi trước
            workbook.close();               // rồi mới đóng workbook (nội bộ POI cleanup)
            Toast.makeText(context, getString(R.string.da_luu_file_tai) + " " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d("EXPORT", "Đã lưu file tại: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, getString(R.string.loi_luu_file), Toast.LENGTH_SHORT).show();
        }
    }

    public void showOpenFile(Context context) {
        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS); // an toàn hơn
        File[] files = downloadsDir.listFiles((d, name) -> name.endsWith(".xlsx"));

        if (files != null && files.length > 0) {
            String[] fileNames = Arrays.stream(files).map(File::getName).toArray(String[]::new);

            new AlertDialog.Builder(context)
                    .setTitle(getString(R.string.chon_file_de_mo))
                    .setItems(fileNames, (dialog, which) -> {
                        File selectedFile = new File(downloadsDir, fileNames[which]);
                        // Gọi hàm đọc file
                        Log.d("DEBUG", "Selected file: " + selectedFile);

                        List<Run> runs = importFromExcel(selectedFile);
                        allRuns.clear();
                        allRuns.addAll(runs);
                        if (lichsuAdapter != null) {
                            lichsuAdapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.khong_thay_file_nao), Toast.LENGTH_SHORT).show();
        }
    }

    public List<Run> importFromExcel(File file) {
        List<Run> runList = new ArrayList<>();

        try {
            FileInputStream inputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row headerRow1 = sheet.getRow(0); // "Chạy n"
            Row headerRow2 = sheet.getRow(1); // "Thời gian (s)", "SensorName"

            if (headerRow1 == null || headerRow2 == null) {
                Log.e("DEBUG_IMPORT", "headerRow1 hoặc headerRow2 null -> Dừng");
                return runList;
            }

            int col = 0;
            while (col < headerRow1.getLastCellNum()) {
                // --- Lấy thông tin Run ---
                Cell cell = headerRow1.getCell(col);
                if (cell == null || cell.getCellType() != CellType.STRING) break;

                String title = cell.getStringCellValue(); // VD: "Chạy 1"
                int runNumber = Integer.parseInt(title.replace("Chạy", "").trim());
                int startCol = col;

                // Đếm số cảm biến
                int sensorCount = 0;
                for (int i = startCol + 1; i < headerRow2.getLastCellNum(); i++) {
                    Cell c = headerRow2.getCell(i);
                    if (c == null || c.getStringCellValue().startsWith("Thời gian")) break;
                    sensorCount++;
                }

                Run run = new Run(runNumber, 1.0); // giả định freq = 1Hz, có thể tính sau

                // --- Tạo SensorData ---
                List<SensorData> sensors = new ArrayList<>();
                for (int i = 0; i < sensorCount; i++) {
                    Cell nameCell = headerRow2.getCell(startCol + 1 + i);
                    if (nameCell != null) {
                        String nameWithUnit = nameCell.getStringCellValue(); // VD: "Device 1 (DV)"
                        String name = nameWithUnit.replaceAll("\\s*\\(.*?\\)", ""); // Xoá phần " (DV)" nếu có
                        sensors.add(new SensorData(name));
                    }
                }

                // --- Đọc dữ liệu ---
                int rowIndex = 2;
                List<Double> timeValues = new ArrayList<>();
                while (true) {
                    Row dataRow = sheet.getRow(rowIndex++);
                    if (dataRow == null) break;

                    Cell timeCell = dataRow.getCell(startCol);
                    if (timeCell == null || timeCell.getCellType() != CellType.NUMERIC) break;

                    timeValues.add(timeCell.getNumericCellValue());

                    for (int i = 0; i < sensorCount; i++) {
                        Cell valueCell = dataRow.getCell(startCol + 1 + i);
                        if (valueCell != null && valueCell.getCellType() == CellType.NUMERIC) {
                            sensors.get(i).addValue(valueCell.getNumericCellValue());
                        } else {
                            sensors.get(i).addValue(0.0); // hoặc null nếu bạn thích
                        }
                    }
                }

                // Tính tần số nếu có thể
                if (timeValues.size() >= 2) {
                    double delta = timeValues.get(1) - timeValues.get(0);
                    if (delta > 0) {
                        run = new Run(runNumber, 1.0 / delta);
                    }
                }

                for (SensorData sd : sensors) {
                    run.addSensorData(sd);
                }

                runList.add(run);
                col = startCol + sensorCount + 1; // nhảy đến nhóm tiếp theo
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e("IMPORT_ERROR", "Lỗi đọc file Excel: " + e.getMessage(), e);

        }

        return runList;
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
        if (trangThai == TrangThaiKetNoi.KHAC) {
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

    @Override
    public void onDeviceDisconnected(BleDevice bleDevice, boolean isActive) {
        for (int i = 0; i < tbKetnois.size(); i++) {
            BleDevice device = tbKetnois.get(i).getDevice();
            if (bleDevice.getKey().equals(device.getKey())) {
                tbKetnois.remove(i);
                break;
            }
        }
        tbKetnoiAdapter.notifyDataSetChanged();
        for (int i = 0; i < sodoCambienList.size(); i++) {
            if (sodoCambienList.get(i).getBleDevice().getKey().equals(bleDevice.getKey())) {
                sodoCambienList.remove(i);
            }
        }
        if (isActive == false) {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Mất kết nối")
                    .setMessage("Thiết bị " + bleDevice.getName() + " đã bị ngắt kết nối.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public void onDataReceived(String deviceName, String sensorname, List<Float> value) {

        DulieuCB dulieuCB = new DulieuCB(deviceName, sensorname, value);
        EventBus.getDefault().post(new DataEvent(dulieuCB));
    }

    private void handleStartBLE() {
        // All logic for starting BLE
        // (move code from btnStart's BLE start block here)
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
        solanchay++;
        Run currentRun = new Run((int) solanchay, 1000 / tansoLayMau);
        if (currentFragment instanceof FragmentMachDienXC || currentFragment instanceof FragmentSongam || currentFragment instanceof FragmentNhapbangtay) {

        } else {
            allRuns.add(currentRun);
        }
        List<Float> manglanchay = new ArrayList<>();
        manglanchay.add(solanchay);
        manglanchay.add((float) tansoLayMau);
        DulieuCB dulieuCB = new DulieuCB("lanchay", "lanchay", manglanchay);
        EventBus.getDefault().post(new DataEvent(dulieuCB));

        btnStart.setText(getString(R.string.stop_button));
        int colorDo = ContextCompat.getColor(getBaseContext(), R.color.btnStop);
        btnStart.setBackgroundColor(colorDo);
        btnStart.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_stop, 0, 0, 0);

        for (ConnectedDevice connectedDevice : tbKetnois) {
            final BleDevice bleDevice = connectedDevice.getDevice();
            SensorData sensorData = currentRun.getSensorDataByName(bleDevice.getName());

            if (sensorData == null) {
                sensorData = new SensorData(connectedDevice.getDevice().getName());
                currentRun.addSensorData(sensorData);
            }
            final SensorData finalSensorData = sensorData; // Để dùng trong callback


            BleManager.getInstance().notify(bleDevice, connectedDevice.getServiceUuid(), connectedDevice.getReadUuid(), new BleNotifyCallback() {
                @Override
                public void onNotifySuccess() {
                    BleManager.getInstance().write(bleDevice, connectedDevice.getServiceUuid(), connectedDevice.getReadUuid(), byteArray, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {


                        }


                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }

                @Override
                public void onNotifyFailure(BleException exception) {

                }

                @Override
                public void onCharacteristicChanged(byte[] data) {
                    int vitri = bleDevice.getName().indexOf('-');

                    if (data != null) {
                        if (data[0] == 0x02 && data[data.length - 1] == 0x03) {
//                            for (int i = 0; i < data.length; i++) {
//                                Log.d("dulieu", String.valueOf(data[i]));
//                            }
                            if (bleDevice.getName().substring(0, vitri).equals("V&A") && data.length >= 10) {
                                xuLyApVaDong(data, bleDevice);

                            } else if (bleDevice.getName().substring(0, vitri).equals("P&T") && data.length >= 10) {
                                xuLyApSuatVaNhietDo(data, bleDevice);

                            } else if (bleDevice.getName().substring(0, vitri).equals("H&T") && data.length >= 10) {
                                xuLyDoAmVaNhietDo(data, bleDevice);
                            } else if (bleDevice.getName().substring(0, vitri).equals("SoundF")) {
                                xuLyAmthanh(data, bleDevice);
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
                                    if (finalSensorData != null) {
                                        finalSensorData.addValue(value); // Example value
                                    }
                                }
                                DulieuCB dulieuCB1 = new DulieuCB(bleDevice.getName(), sodoCambienList.get(vtrithietbi).getTencambien(), mangValue);

                                EventBus.getDefault().post(new DataEvent(dulieuCB1));
                            }
                        }
                    }
                }
            });
        }
    }

    private void xuLyApVaDong(byte[] data, BleDevice bleDevice) {
        List<Float> mangAp = new ArrayList<>();
        List<Float> mangDong = new ArrayList<>();

        for (int i = 0; i < (data.length - 2) / 8; i++) {
            float dienap = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float dongdien = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            mangAp.add(dienap);
            mangDong.add(dongdien);
        }

        postDataEvent(bleDevice, Uuid.Voltage, mangAp);
        postDataEvent(bleDevice, Uuid.Current, mangDong);
    }

    private void xuLyApSuatVaNhietDo(byte[] data, BleDevice bleDevice) {
        List<Float> mangApsuat = new ArrayList<>();
        List<Float> mangNhietdo = new ArrayList<>();

        for (int i = 0; i < (data.length - 2) / 8; i++) {
            float apsuat = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float nhietdo = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            mangApsuat.add(apsuat);
            mangNhietdo.add(nhietdo);
        }

        postDataEvent(bleDevice, Uuid.Pressure, mangApsuat);
        postDataEvent(bleDevice, Uuid.Temp, mangNhietdo);
    }

    private void xuLyDoAmVaNhietDo(byte[] data, BleDevice bleDevice) {
        List<Float> mangDoam = new ArrayList<>();
        List<Float> mangNhietdo = new ArrayList<>();

        for (int i = 0; i < (data.length - 2) / 8; i++) {
            float doam = ByteBuffer.wrap(data, i * 8 + 1, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            float nhietdo = ByteBuffer.wrap(data, i * 8 + 5, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            mangDoam.add(doam);
            mangNhietdo.add(nhietdo);
        }

        postDataEvent(bleDevice, Uuid.Humid, mangDoam);
        postDataEvent(bleDevice, Uuid.Temp, mangNhietdo);
    }

    private void xuLyAmthanh(byte[] data, BleDevice bleDevice) {
        List<Float> mangAmthanh = new ArrayList<>();
        for (int i = 0; i < (data.length - 3) / 4; i++) {
            float amthanh = ByteBuffer.wrap(data, i * 4 + 2, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            mangAmthanh.add(amthanh);
        }
        int packIndex = data[1];
        if (packIndex == 20) {
            postDataEvent(bleDevice, Uuid.Frequency, mangAmthanh);
        } else {
            postDataEvent(bleDevice, String.valueOf(data[1]), mangAmthanh);

        }
//        StringBuilder sb = new StringBuilder();
//        sb.append("Giá trị mangValue " + String.valueOf(data[1]) + ": [");
//        for (int i = 0; i < mangAmthanh.size(); i++) {
//            sb.append(String.format("%.3f", mangAmthanh.get(i)));
//            if (i != mangAmthanh.size() - 1) sb.append(", ");
//            if ((i + 1) % 10 == 0)
//                sb.append("\n");  // Xuống dòng sau mỗi 10 giá trị
//        }
//        sb.append("]");
//        Log.d("dulieu", sb.toString());
//        postDataEvent(bleDevice, String.valueOf(data[1]), mangAmthanh);
    }

    private void postDataEvent(BleDevice bleDevice, String tencambien, List<Float> values) {
        DulieuCB dulieu = new DulieuCB(bleDevice.getName(), tencambien, values);
        EventBus.getDefault().post(new DataEvent(dulieu));
    }

    private void handleStopBLE() {
        // All logic for stopping BLE
        // (move code from btnStart's BLE stop block here)
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
    }

    private void handleStartUSB() {
        // All logic for starting USB
        // (move code from btnStart's USB start block here)
        List<Float> manglanchay = new ArrayList<>();
        manglanchay.add(solanchay);
        manglanchay.add((float) tansoLayMau);
        DulieuCB dulieuCB = new DulieuCB("lanchay", "lanchay", manglanchay);
        EventBus.getDefault().post(new DataEvent(dulieuCB));

        btnStart.setText(getString(R.string.stop_button));
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

    private void handleStopUSB() {
        // All logic for stopping USB
        // (move code from btnStart's USB stop block here)
        if (timer1 != null) {
            timer1.cancel();
        }
    }
}

