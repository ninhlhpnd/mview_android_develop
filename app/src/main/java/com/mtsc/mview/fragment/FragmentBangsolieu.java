package com.mtsc.mview.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.adapter.DulieuDothiAdapter;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuDothi;
import com.mtsc.mview.model.SensorData;
import com.mtsc.mview.ultis.Uuid;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class FragmentBangsolieu extends Fragment implements FragmentBaseMain.OnDataChangeListener {
    String tencambien, donvi, macambien;
    List<DulieuDothi> listDulieubang;
    NestedScrollView scrollView;
    TableLayout tableLayout;
    List<Integer> mangRowindex;
    DulieuDothiAdapter dulieuBangAdapter;
    ListView lvCacsodo;
    List<Integer> listRow;
    Fragment currentFragment;
    FragmentManager fragmentManager;
    FragmentPhantichBang fragmentPhantichBang;
    int currentColumnCount = 1;
    int longclick = 0, row1 = 0, row2 = 0;
    boolean isPhantich = false;

    private int[] mamaudothi = {Color.parseColor("#FF0000"),
            Color.parseColor("#000000"),
            Color.parseColor("#FFFF00"),
            Color.parseColor("#008000"),
            Color.parseColor("#0000FF"),
            Color.parseColor("#800080"),
            Color.parseColor("#FFC0CB"),
            Color.parseColor("#00FFFF"),
            Color.parseColor("#808080"),
            Color.parseColor("#FFA500")};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bangsolieu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentBaseMain fragmentBaseMain = (FragmentBaseMain) getParentFragment();
        if (fragmentBaseMain != null) {
            fragmentBaseMain.setOnDataChangeListener(this);
        }
        listDulieubang = new ArrayList<>();
        mangRowindex = new ArrayList<>();
        fragmentPhantichBang = new FragmentPhantichBang();
        fragmentManager = getChildFragmentManager();

        tableLayout = (TableLayout) view.findViewById(R.id.tablelayout);
        lvCacsodo = (ListView) view.findViewById(R.id.listview_fragmentbangsolieu);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrooviewBangsolieu);
        listRow = new ArrayList<>();
        dulieuBangAdapter = new DulieuDothiAdapter(getContext(), R.layout.dong_listviewcacsodo, listDulieubang);
        lvCacsodo.setAdapter(dulieuBangAdapter);
        tableLayout.setStretchAllColumns(true);
        lvCacsodo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DulieuDothi dulieubang = listDulieubang.get(i);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Xác nhận xóa dữ liệu");
                builder.setMessage("Bạn có chắc chắn muốn xóa " + dulieubang.getTencambien() + "-" + dulieubang.getMacambien() + "?");
                builder.setBackground(getResources().getDrawable(R.drawable.dialog_background)); // Thiết lập background tùy chỉnh cho dialog
                builder.setIcon(R.drawable.ic_delete);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listDulieubang.remove(i);
                        for (int j = 0; j < tableLayout.getChildCount(); j++) {
                            TableRow tableRow = (TableRow) tableLayout.getChildAt(j);
                            tableRow.removeViewAt(i + 1);

                        }
                        currentColumnCount--;
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                positiveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Thay thế your_color bằng mã màu tùy chọn
                negativeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                return false;
            }
        });

    }

    private void setLongClickListenerForRow(final TableRow row) {
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int rowIndex = tableLayout.indexOfChild(row);
                if (rowIndex == 0) {
                    isPhantich = !isPhantich;
                    if (isPhantich) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) listDulieubang);
                        fragmentPhantichBang.setArguments(bundle);
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.layoutPhantich_fragmentbangsolieu, fragmentPhantichBang);
                        transaction.commit();
                        currentFragment = fragmentPhantichBang;

                    } else {
                        if (currentFragment != null) {
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.remove(currentFragment);
                            transaction.commit();
                        }
                    }
                } else {
                    if (isPhantich) {
                        longclick++;
                        if (longclick == 1) {
                            clearAllRowBackgroundColors();
                            setRowBackgroundColor(row, R.color.xanhduongnhat);
                            row1 = rowIndex;
                        } else if (longclick == 2) {
                            row2 = rowIndex;
                            int rowmin = Math.min(row1, row2);
                            int rowmax = Math.max(row1, row2);
                            highlightRowsInRange(rowmin, rowmax);

                            for (int i = 0; i < listDulieubang.size(); i++) {
                                double[] arrayData = new double[rowmax - rowmin + 1];
                                for (int j = rowmin; j < rowmax + 1; j++) {
                                    TableRow tableRow = (TableRow) tableLayout.getChildAt(j);
                                    TextView txtData = (TextView) tableRow.getChildAt(i + 1);
                                    arrayData[j - rowmin] = Double.parseDouble(txtData.getText().toString());
                                }
                                double dataMax = Arrays.stream(arrayData).max().getAsDouble();
                                double dataMin = Arrays.stream(arrayData).min().getAsDouble();
                                double dataAver = Arrays.stream(arrayData).average().getAsDouble();
                                listener.guiGiatriphantich(i, (float) dataMin, (float) dataMax, (float) dataAver);
                            }

                            longclick = 0;
                        }
                    }
                }
                return true;
            }
        });
    }

    private void clearAllRowBackgroundColors() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow selectrow = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < selectrow.getChildCount(); j++) {
                TextView txt = (TextView) selectrow.getChildAt(j);
                txt.setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void setRowBackgroundColor(TableRow row, int colorResId) {
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView txt = (TextView) row.getChildAt(i);
            txt.setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));
        }
    }

    private void highlightRowsInRange(int startRow, int endRow) {
        for (int i = startRow; i <= endRow; i++) {
            TableRow selectrow = (TableRow) tableLayout.getChildAt(i);
            setRowBackgroundColor(selectrow, R.color.xanhduongnhat);
        }
    }

    @Override
    public void chonCamBien(Bundle bundle) {
        tencambien = bundle.getString("tencambien");
        donvi = bundle.getString("donvi");
        macambien = bundle.getString("macambien");
        double slope = 1, offset = 0;
        for (int i = 0; i < Uuid.camBiens.size(); i++) {
            if (Uuid.camBiens.get(i).getName().equals(tencambien)) {
                for (int j = 0; j < Uuid.camBiens.get(i).getDonvi().length; j++) {
                    if (Uuid.camBiens.get(i).getDonvi()[j].equals(donvi)) {
                        slope = Uuid.camBiens.get(i).getHeso()[j][0];
                        offset = Uuid.camBiens.get(i).getHeso()[j][1];
                        break;
                    }
                }
                break;
            }
        }
        for (DulieuDothi dulieubang : listDulieubang
        ) {
            if (dulieubang.getMacambien().equals(macambien)) {
                return;
            }
        }

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            setLongClickListenerForRow(row);
            TextView textView = new TextView(getContext());
            textView.setPadding(2, 2, 2, 2);
            textView.setBackgroundColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            if (i == 0) {
                textView.setText(tencambien + "(" + donvi + ")");
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 1, 1, 1);
                textView.setLayoutParams(params);

            } else {
                textView.setText("");
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 1, 1);
                textView.setLayoutParams(params);

            }
            if (currentColumnCount > 0) {
                textView.setTextColor(mamaudothi[listDulieubang.size()]);
            }
            row.addView(textView);

        }
        currentColumnCount++;

        listDulieubang.add(new DulieuDothi(mamaudothi[listDulieubang.size()], tencambien, macambien, donvi, new double[]{slope, offset}));
        listRow.add(0);
        dulieuBangAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDataReceived(DulieuCB dulieuCB) {
        for (int i = 0; i < listDulieubang.size(); i++) {
            String cambienma = listDulieubang.get(i).getMacambien();
            String cambienten = listDulieubang.get(i).getTencambien();
            double slope = listDulieubang.get(i).getHeso()[0];
            double offset = listDulieubang.get(i).getHeso()[1];
            if (dulieuCB.getMacambien().equals(cambienma) && dulieuCB.getTencambien().equals(cambienten)
                    && dulieuCB.getGiatricambien() != null) {
                List<Float> mangdulieu = dulieuCB.getGiatricambien();
                int rowIndex = listRow.get(i);
                for (float x : mangdulieu
                ) {
                    addDataColumn(i, rowIndex, (float) (x * slope + offset), (float) MainActivity.tansoLayMau);
                    rowIndex = rowIndex + 1;
                }
                listRow.set(i, rowIndex);
            }
        }
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollBy(0, listRow.get(0));
            }
        }, 100);
    }

    private TableRow createNewRow() {
        TableRow newRow = new TableRow(getContext());
        for (int columnIndex = 0; columnIndex < currentColumnCount; columnIndex++) {
            TextView textView = new TextView(getContext());
            textView.setPadding(2, 2, 2, 2);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setBackgroundColor(Color.WHITE);

            if (columnIndex == 0) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(1, 0, 1, 1);
                textView.setLayoutParams(params);
            } else {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 1, 1);
                textView.setLayoutParams(params);
            }
            newRow.addView(textView);
        }

        return newRow;
    }

    public void addDataColumn(int column, int rowIndex, float data, float tanso) {
        TableRow dulieudong = (TableRow) tableLayout.getChildAt(rowIndex + 1);
        TextView textView = (TextView) dulieudong.getChildAt(column + 1);
        if (column == 0) {
            TextView tgian = (TextView) dulieudong.getChildAt(0);
            tgian.setText(String.valueOf(rowIndex * tanso / 1000));
            tgian.setTextColor(Color.BLACK);

        }
        textView.setText(floatToString(data));
        textView.setTextColor(mamaudothi[column]);
        int rowCount = tableLayout.getChildCount();
        if (rowIndex + 2 >= rowCount) {

            TableRow newRow = createNewRow();
            setLongClickListenerForRow(newRow);
            tableLayout.addView(newRow);
        }

    }

    private String floatToString(float number) {
        DecimalFormat df = new DecimalFormat("#.00");
        df.setMinimumIntegerDigits(1);
        String dfdulieu = df.format(number).replace(',', '.');
        return dfdulieu;
    }

    @Override
    public void xoaDulieucu() {
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView txt = (TextView) row.getChildAt(j);
                txt.setText("");
            }
        }
        for (int i = 0; i < listRow.size(); i++) {
            listRow.set(i, 0);
        }
    }

    @Override
    public void xemLaiDulieuCu(List<SensorData> sensorDataList, Float tanso) {
        for (int i = 0; i < listDulieubang.size(); i++) {
            String cambien = listDulieubang.get(i).getMacambien();
            double slope = listDulieubang.get(i).getHeso()[0];
            double offset = listDulieubang.get(i).getHeso()[1];
            for (SensorData sensor : sensorDataList) {
                if (sensor.getSensorName().equals(cambien) && sensor.getValues().size() > 0) {
                    int rowIndex = listRow.get(i);
                    for (Double value : sensor.getValues()
                    ) {
                        addDataColumn(i, rowIndex, (float) (value * slope + offset), tanso);
                        rowIndex = rowIndex + 1;
                    }
                    listRow.set(i, rowIndex);
                }
            }
        }
    }

    @Override
    public void phantichDothi(int phantich) {

    }

    private PhantichBang listener;

    // Khai báo interface
    public interface PhantichBang {
        void guiGiatriphantich(int position, Float giatri1, Float giatri2, Float giatri3);
    }

    // Cài đặt phương thức của interface để truyền dữ liệu sang Fragment con
    public void setOnPhanTichBang(PhantichBang listener) {
        this.listener = listener;
    }
}
