package com.mtsc.mview.fragment;

import static com.mtsc.mview.MainActivity.crc8;
import static com.mtsc.mview.MainActivity.firstNibble;
import static com.mtsc.mview.MainActivity.secondNibble;
import static com.mtsc.mview.MainActivity.tbKetnois;
import static com.mtsc.mview.MainActivity.trangThai;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.model.ConnectedDevice;
import com.mtsc.mview.model.TrangThaiKetNoi;
import com.mtsc.mview.ultis.Uuid;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalibFragment extends DialogFragment {
    AutoCompleteTextView txtTencambien;
    ArrayAdapter<String> adapterConDevice;
    String[] stringConDevice;
    boolean[] checkedItems;
    List<ConnectedDevice> listHieuchinh;
    Button btnCalib1, btnCalib21, btnCalib22;
    EditText edtCalib1, edtCalib21, edtCalib22;
    int vitri = -1;

    public static CalibFragment newInstance() {
        return new CalibFragment();
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
            int width = getResources().getDisplayMetrics().widthPixels * 8 / 10;
            int height = getResources().getDisplayMetrics().heightPixels;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_calibration, container, false);
        txtTencambien = (AutoCompleteTextView) view.findViewById(R.id.textviewCalibSensor);
        btnCalib1 = (Button) view.findViewById(R.id.buttonCalib1);
        btnCalib21 = (Button) view.findViewById(R.id.buttonCalib21);
        btnCalib22 = (Button) view.findViewById(R.id.buttonCalib22);
        edtCalib1 = (EditText) view.findViewById(R.id.textviewCalib1);
        edtCalib21 = (EditText) view.findViewById(R.id.textviewCalib21);
        edtCalib22 = (EditText) view.findViewById(R.id.textviewCalib22);
        stringConDevice = new String[MainActivity.tbKetnois.size()];
        checkedItems = new boolean[MainActivity.tbKetnois.size()];
        listHieuchinh = new ArrayList<>();
        for (int i = 0; i < MainActivity.tbKetnois.size(); i++) {
            BleDevice device = tbKetnois.get(i).getDevice();
            stringConDevice[i] = device.getName();
            checkedItems[i] = false;
        }
//        adapterConDevice = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, stringConDevice);

//        txtTencambien.setAdapter(adapterConDevice);
//        txtTencambien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                vitri = i;
//                btnCalib1.setEnabled(true);
//                btnCalib21.setEnabled(true);
//                btnCalib22.setEnabled(true);
//            }
//        });
        List<String> selectedItems = new ArrayList<>();
        txtTencambien.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.chon_cam_bien));

            builder.setMultiChoiceItems(stringConDevice, checkedItems, (dialog, which, isChecked) -> {
                checkedItems[which] = isChecked;
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                listHieuchinh.clear();
                selectedItems.clear();
                for (int i = 0; i < stringConDevice.length; i++) {
                    if (checkedItems[i]) {
                        listHieuchinh.add(tbKetnois.get(i));
                        selectedItems.add(stringConDevice[i]);
                    }
                }
                txtTencambien.setText(TextUtils.join(", ", selectedItems));
            });

            builder.setNeutralButton(getContext().getString(R.string.chon_tat_ca), (dialog, which) -> {
                Arrays.fill(checkedItems, true);
                listHieuchinh.clear();
                selectedItems.clear();
                listHieuchinh.addAll(tbKetnois);
                selectedItems.addAll(Arrays.asList(stringConDevice));
                txtTencambien.setText(TextUtils.join(", ", selectedItems));
            });

            builder.setNegativeButton(getContext().getString(R.string.cancel), null);
            builder.show();
        });
        btnCalib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtCalib1.getText().toString().equals("")) {
                    float value = Float.parseFloat(edtCalib1.getText().toString());
                    byte[] tempArray = ByteBuffer.allocate(4).putFloat(value).array();
                    byte[] reversedArray = new byte[tempArray.length];
                    for (int i = 0; i < tempArray.length; i++) {
                        reversedArray[i] = tempArray[tempArray.length - i - 1];
                    }
                    if (trangThai == TrangThaiKetNoi.BLE) {
                        byte[] byteArray = new byte[tempArray.length + 3];
                        byteArray[0] = 0x05;
                        byteArray[1] = 0x01;
                        byteArray[6] = 0x03;
                        System.arraycopy(reversedArray, 0, byteArray, 2, tempArray.length);
                        for (ConnectedDevice device : listHieuchinh) {
                            BleManager.getInstance().write(device.getDevice(),
                                    device.getServiceUuid(), device.getReadUuid(), byteArray, new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                            Toast.makeText(view.getContext(), getContext().getString(R.string.hieu_chinh_tai_diem) + " " + value, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                        }
                    } else if (trangThai == TrangThaiKetNoi.USB) {
                        int tenCambien = Uuid.camBiens.indexOf(MainActivity.tbScansUsb.get(0).getCamBien()) + 1;
                        int idCambien = MainActivity.tbScansUsb.get(0).getId();
                        byte[] data = {(byte) tenCambien, (byte) idCambien, 0x05};
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
                    }
                }
            }
        });

        btnCalib21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectedDevice device = MainActivity.tbKetnois.get(vitri);
                int daugach = device.getDevice().getName().indexOf('-');
                String tencambien = device.getDevice().getName().substring(0, daugach);
                if (!edtCalib21.getText().toString().equals("")) {
                    if (tencambien.equals("V&A")) {
                        String giatri[] = edtCalib21.getText().toString().split(",");
                        float valueDong = Float.parseFloat(giatri[0]);
                        float valueAp = Float.parseFloat(giatri[1]);
                        byte[] tempArrayDong = ByteBuffer.allocate(4).putFloat(valueDong).array();
                        byte[] reversedArrayDong = new byte[tempArrayDong.length];
                        for (int i = 0; i < tempArrayDong.length; i++) {
                            reversedArrayDong[i] = tempArrayDong[tempArrayDong.length - i - 1];
                        }
                        byte[] tempArrayAp = ByteBuffer.allocate(4).putFloat(valueAp).array();
                        byte[] reversedArrayAp = new byte[tempArrayAp.length];
                        for (int i = 0; i < tempArrayAp.length; i++) {
                            reversedArrayAp[i] = tempArrayAp[tempArrayAp.length - i - 1];
                        }
                        byte[] byteArray = new byte[tempArrayDong.length * 2 + 4];
                        byteArray[0] = 0x05;
                        byteArray[1] = 0x02;
                        byteArray[2] = 0x01;
                        byteArray[byteArray.length - 1] = 0x03;
                        System.arraycopy(reversedArrayDong, 0, byteArray, 3, tempArrayDong.length);
                        System.arraycopy(reversedArrayAp, 0, byteArray, 7, tempArrayAp.length);
                        BleManager.getInstance().write(device.getDevice(),
                                device.getServiceUuid(), device.getReadUuid(), byteArray, new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Toast.makeText(view.getContext(), getContext().getString(R.string.hieu_chinh_diem_1) + " " + valueDong + "," + valueAp, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });

                    } else {
                        float value = Float.parseFloat(edtCalib21.getText().toString());
                        byte[] tempArray = ByteBuffer.allocate(4).putFloat(value).array();
                        byte[] reversedArray = new byte[tempArray.length];
                        for (int i = 0; i < tempArray.length; i++) {
                            reversedArray[i] = tempArray[tempArray.length - i - 1];
                        }
                        byte[] byteArray = new byte[tempArray.length + 4];
                        byteArray[0] = 0x05;
                        byteArray[1] = 0x02;
                        byteArray[2] = 0x01;
                        byteArray[7] = 0x03;
                        System.arraycopy(reversedArray, 0, byteArray, 3, tempArray.length);
                        BleManager.getInstance().write(device.getDevice(),
                                device.getServiceUuid(), device.getReadUuid(), byteArray, new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Toast.makeText(view.getContext(), getContext().getString(R.string.hieu_chinh_diem_1) + " " + value, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });
                    }
                }
            }
        });
        btnCalib22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtCalib22.getText().toString().equals("")) {
                    ConnectedDevice device = MainActivity.tbKetnois.get(vitri);
                    int daugach = device.getDevice().getName().indexOf('-');
                    String tencambien = device.getDevice().getName().substring(0, daugach);
                    if (tencambien.equals("V&A")) {
                        String giatri[] = edtCalib22.getText().toString().split(",");
                        float valueDong = Float.parseFloat(giatri[0]);
                        float valueAp = Float.parseFloat(giatri[1]);
                        byte[] tempArrayDong = ByteBuffer.allocate(4).putFloat(valueDong).array();
                        byte[] reversedArrayDong = new byte[tempArrayDong.length];
                        for (int i = 0; i < tempArrayDong.length; i++) {
                            reversedArrayDong[i] = tempArrayDong[tempArrayDong.length - i - 1];
                        }
                        byte[] tempArrayAp = ByteBuffer.allocate(4).putFloat(valueAp).array();
                        byte[] reversedArrayAp = new byte[tempArrayAp.length];
                        for (int i = 0; i < tempArrayAp.length; i++) {
                            reversedArrayAp[i] = tempArrayAp[tempArrayAp.length - i - 1];
                        }
                        byte[] byteArray = new byte[tempArrayDong.length * 2 + 4];
                        byteArray[0] = 0x05;
                        byteArray[1] = 0x02;
                        byteArray[2] = 0x02;
                        byteArray[byteArray.length - 1] = 0x03;
                        System.arraycopy(reversedArrayDong, 0, byteArray, 3, tempArrayDong.length);
                        System.arraycopy(reversedArrayAp, 0, byteArray, 7, tempArrayAp.length);
                        BleManager.getInstance().write(device.getDevice(),
                                device.getServiceUuid(), device.getReadUuid(), byteArray, new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Toast.makeText(view.getContext(), getContext().getString(R.string.hieu_chinh_diem_2) + " " + valueDong + "," + valueAp, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });

                    } else {
                        float value = Float.parseFloat(edtCalib22.getText().toString());
                        byte[] tempArray = ByteBuffer.allocate(4).putFloat(value).array();
                        byte[] reversedArray = new byte[tempArray.length];
                        for (int i = 0; i < tempArray.length; i++) {
                            reversedArray[i] = tempArray[tempArray.length - i - 1];
                        }
                        byte[] byteArray = new byte[tempArray.length + 4];
                        byteArray[0] = 0x05;
                        byteArray[1] = 0x02;
                        byteArray[2] = 0x02;
                        byteArray[7] = 0x03;
                        System.arraycopy(reversedArray, 0, byteArray, 3, tempArray.length);

                        BleManager.getInstance().write(device.getDevice(), device.getServiceUuid(), device.getReadUuid(),
                                byteArray, new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        Toast.makeText(view.getContext(), getContext().getString(R.string.hieu_chinh_diem_2) + " " + value, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });
                    }
                }
            }
        });
        return view;
    }

    private void send(byte[] data) {
        if (trangThai == TrangThaiKetNoi.KHAC) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            MainActivity.usbSerialPort.write(data, 2000);
            Toast.makeText(getActivity(), "hieu chinh thanh cong", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
//            onRunError(e);
        }
    }
}
