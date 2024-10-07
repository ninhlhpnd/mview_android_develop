package com.mtsc.mview.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.mtsc.mview.BuildConfig;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;

import com.mtsc.mview.adapter.tbScanAdapterUSB;
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.model.CustomProber;
import com.mtsc.mview.model.ListItemUSB;
import com.mtsc.mview.ultis.Uuid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class USBFragment extends DialogFragment {

    Handler handler;
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private SerialInputOutputManager usbIoManager;

    LinearLayout linearLayoutScan;
    Button btnScan;
    public static tbScanAdapterUSB tbScanAdapterUSB;
    ListView lvScan, lvUSB;
    ArrayAdapter<ListItemUSB> adapter;
    private int baudRate = 57600;
    private boolean withIoManager = true;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;


    private enum UsbPermission {Unknown, Requested, Granted, Denied}

    private UsbPermission usbPermission = UsbPermission.Unknown;
    private final BroadcastReceiver broadcastReceiver;
    private final Handler mainLooper;

    public static USBFragment newInstance() {
        return new USBFragment();
    }
    public USBFragment() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
//                    connect();
                }
            }
        };
        mainLooper = new Handler(Looper.getMainLooper());
    }
    private OnDataReceivedListener listener;

    public interface OnDataReceivedListener {
        void onDataReceived(boolean isConnected);
    }

    public void setOnDataChangeListener(OnDataReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnDataReceivedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataReceivedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        ContextCompat.registerReceiver(getActivity(), broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB), ContextCompat.RECEIVER_NOT_EXPORTED);

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
//        if (timeScan != null) {
//            timeScan.cancel();
//        }
//        if (usbIoManager != null) {
//            usbIoManager.setListener(null);
//            usbIoManager.stop();
//        }
//        usbIoManager = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_usb, container, false);
        lvScan = (ListView) view.findViewById(R.id.listviewCambien_dialogUsb);
//        lvKetnoi=(ListView)view.findViewById(R.id.listviewTbdaketnoi_usb);
        lvUSB = (ListView) view.findViewById(R.id.listviewUSB_dialogUsb);
        btnScan = (Button) view.findViewById(R.id.buttonScan_dialogUsb);
        linearLayoutScan = (LinearLayout) view.findViewById(R.id.linearLayoutScan_dialogUsb);

        tbScanAdapterUSB = new tbScanAdapterUSB(getContext(), R.layout.dong_scandevice, MainActivity.tbScansUsb);
        lvScan.setAdapter(tbScanAdapterUSB);
        handler = new Handler(Looper.getMainLooper());
        adapter = new ArrayAdapter<ListItemUSB>(getContext(), 0, MainActivity.tbUSB) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
                ListItemUSB item = MainActivity.tbUSB.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);
                TextView text1 = view.findViewById(R.id.textviewTen_deviceListitem);
                TextView text2 = view.findViewById(R.id.textviewPort_deviceListitem);
                Button btnConnect = view.findViewById(R.id.buttonConnect_devicelistitem);
                if(MainActivity.isConnectedUSB){
                    int colorDo = ContextCompat.getColor(getContext(), R.color.btnStop);
                    btnConnect.setBackgroundColor(colorDo);
                    btnConnect.setText("Disconnect");
                    linearLayoutScan.setVisibility(View.VISIBLE);
                }else{
                    btnConnect.setText("Connect");
                    int colorDo = ContextCompat.getColor(getContext(), R.color.xanhduongnhat);
                    btnConnect.setBackgroundColor(colorDo);
                    linearLayoutScan.setVisibility(View.INVISIBLE);
                }
                if (item.getDriver() == null)
                    text1.setText("<no driver>");
                else if (item.getDriver().getPorts().size() == 1)
                    text1.setText(item.getDriver().getClass().getSimpleName().replace("SerialDriver", ""));
                else
                    text1.setText(item.getDriver().getClass().getSimpleName());
                text2.setText("Com: " + item.getPort());
                btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!MainActivity.isConnectedUSB) {
                            connect();
                            if (MainActivity.isConnectedUSB) {
                                int colorDo = ContextCompat.getColor(getContext(), R.color.btnStop);
                                btnConnect.setBackgroundColor(colorDo);
                                btnConnect.setText("Disconnect");

                            }
                        } else {
                            disconnect();
                            if (!MainActivity.isConnectedUSB) {
                                int colorDo = ContextCompat.getColor(getContext(), R.color.xanhduongnhat);
                                btnConnect.setBackgroundColor(colorDo);
                                btnConnect.setText("Connect");
                            }
                        }

                    }
                });
                return view;
            }
        };
        lvUSB.setAdapter(adapter);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data = {0x01, 0x01, 0x01};
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
                usbSerialPort=MainActivity.usbSerialPort;
                send(senddata);

            }
        });

        return view;
    }
    public int firstNibble(int what) {
        int c = (what >> 4);
        return ((c << 4) | (c ^ 0x0F));
    }

    public int secondNibble(int what) {
        int c = (what & 0x0F);
        return ((c << 4) | (c ^ 0x0F));
    }

    public int crc8(byte[] data, int length) {
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



    private void send(byte[] data) {
        if (!MainActivity.isConnectedUSB) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            MainActivity.tbScansUsb.clear();
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {
//            onRunError(e);
        }
    }


    private void connect() {
        deviceId = MainActivity.tbUSB.get(0).getDevice().getDeviceId();
        portNum = MainActivity.tbUSB.get(0).getPort();
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        for (UsbDevice v : usbManager.getDeviceList().values())
            if (v.getDeviceId() == deviceId)
                device = v;
        if (device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if (driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if (driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        MainActivity.usbSerialPort = usbSerialPort;

        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            Intent intent = new Intent(INTENT_ACTION_GRANT_USB);
            intent.setPackage(getActivity().getPackageName());
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            try {
                usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
            } catch (UnsupportedOperationException e) {
                status("unsupport setparameters");
            }
//            if (withIoManager) {
//                usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
//                usbIoManager.start();
//            }
            status("connected");
            MainActivity.isConnectedUSB = true;
            withIoManager = true;
            linearLayoutScan.setVisibility(View.VISIBLE);
            if (listener != null) {
                listener.onDataReceived(withIoManager);
            }
        } catch (Exception e) {
            status("connection failed: " + e.getMessage());
            disconnect();
        }
    }

    private void disconnect() {
        MainActivity.isConnectedUSB = false;
        withIoManager = false;
//        if (usbIoManager != null) {
//            usbIoManager.setListener(null);
//            usbIoManager.stop();
//        }
//        usbIoManager = null;
        try {
            usbSerialPort=MainActivity.usbSerialPort;
            usbSerialPort.close();
            status("Disconnected");
            linearLayoutScan.setVisibility(View.INVISIBLE);
            if (listener != null) {
                listener.onDataReceived(withIoManager);
            }
        } catch (IOException ignored) {
        }
        usbSerialPort = null;

    }

    private void status(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }


}
