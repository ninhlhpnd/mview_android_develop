package com.mtsc.mview.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.clj.fastble.data.BleDevice;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointD;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.adapter.NhapBangTayAdapter;
import com.mtsc.mview.adapter.chonsodoAdapter;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuCacCamBien;
import com.mtsc.mview.model.DulieuNhapbangtay;
import com.mtsc.mview.model.MovingAverageFilter;
import com.mtsc.mview.ultis.DataEvent;
import com.mtsc.mview.ultis.Uuid;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jtransforms.fft.DoubleFFT_1D;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentNhapbangtay extends Fragment {
    private View layout;
    private FragmentManager fragmentManager;
    Button btnDulieux, btnDulieuy, btnPhantich, btnTare;
    TextView txtSlope, txtOffset;
    ListView lvDulieu;
    LineChart lineChart;
    List<DulieuNhapbangtay> nhapbangtayList;
    NhapBangTayAdapter nhapBangTayAdapter;
    ILineDataSet dataSet;
    ImageView imgLaydulieu;
    String maCambienX, maCambienY, tencambienX, tencambienY;
    float valueSlowX = 0, valueSlowY = 0;
    List<Float> mangValueX, mangvalueY;
    int position = 0;
    float rmsFastX = 0, rmsFastY = 0;
    double slopeX = 1, offsetX = 0, offsetY = 0, slopeY = 1;
    MovingAverageFilter dataProcessorX, dataProcessorY;
    boolean isPhantich = false, isSetDulieu = false;

    class OldValues {
        String XValue;
        String YValue;
        int OldPosition;
    }

    public FragmentNhapbangtay(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_nhapbangtay, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
        OldValues oldValue = new OldValues();
        btnDulieux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuChonsodo(view);
            }
        });
        btnDulieuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuChonsodo(view);
            }
        });
        lvDulieu.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((NhapBangTayAdapter) adapterView.getAdapter()).setSelectedItem(i);
                if (isSetDulieu == false) {
                    nhapbangtayList.get(oldValue.OldPosition).setDulieuy(oldValue.XValue);
                    nhapbangtayList.get(oldValue.OldPosition).setDulieuy(oldValue.YValue);
                } else {
                    oldValue.XValue = nhapbangtayList.get(i).getDulieux();
                    oldValue.YValue = nhapbangtayList.get(i).getDulieuy();
                    oldValue.OldPosition = i;
                }
                nhapbangtayList.get(nhapbangtayList.size() - 1).setDulieux("");
                nhapbangtayList.get(nhapbangtayList.size() - 1).setDulieuy("");
                nhapBangTayAdapter.notifyDataSetChanged();
                position = i;
                isSetDulieu = false;
                return true;
            }
        });
        imgLaydulieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSetDulieu = true;
                float xValue = Float.parseFloat(nhapbangtayList.get(position).getDulieux());
                float yValue = Float.parseFloat(nhapbangtayList.get(position).getDulieuy());
                if (nhapbangtayList.size() - 1 == position) {
                    nhapbangtayList.add(new DulieuNhapbangtay("", ""));
                    addEntry(xValue, yValue);
                } else if (position < nhapbangtayList.size() - 1) {
//                    LineData data = lineChart.getLineData();
//                    if (data != null) {
//                        ILineDataSet dataSet = data.getDataSetByIndex(0);
//                        if (dataSet != null) {
//                            for (Entry entry : dataSet.getEntriesForXValue(xValue)) {
//                                if (entry.getY() == yValue) {
//                                    entry.setY(yValue); // Cập nhật giá trị y
//                                    break;
//                                }
//                            }
//                            data.notifyDataChanged();
//                            lineChart.notifyDataSetChanged();
//                            lineChart.invalidate();
//                        }
//                    }
                }
                Log.d("inf", nhapbangtayList.size() + "," + position);
                position = position + 1;
                nhapBangTayAdapter.setSelectedItem(position);

            }
        });
        btnPhantich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPhantich = !isPhantich;
                if (isPhantich) {

                    float xmin = dataSet.getEntryForIndex(0).getX();
                    float xmax = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX();
                    LimitLine limitLine1 = new LimitLine(xmin, "");
                    limitLine1.setLineColor(Color.BLACK);
                    limitLine1.setLineWidth(1f);
                    LimitLine limitLine2 = new LimitLine(xmax, "");
                    limitLine2.setLineColor(Color.BLACK);
                    limitLine2.setLineWidth(1f);
                    lineChart.getXAxis().addLimitLine(limitLine1);
                    lineChart.getXAxis().addLimitLine(limitLine2);

                } else {
                    lineChart.getXAxis().removeAllLimitLines();
                    LineData data = lineChart.getLineData();
                    data.removeDataSet(1);
                }
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isPhantich == false) {
                    lineChart.setDragXEnabled(true);
                    lineChart.onTouchEvent(motionEvent);
                } else {
                    lineChart.setDragXEnabled(false);
                }
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        if (isPhantich) {
                            float xTouch = motionEvent.getX();
                            float yTouch = motionEvent.getY();
                            MPPointD point = lineChart.getValuesByTouchPoint(xTouch, yTouch, YAxis.AxisDependency.LEFT);
                            float xValue = (float) point.x;
                            LineData data = lineChart.getLineData();
                            IDataSet dataSet = data.getDataSetByIndex(0);
                            Entry closestEntry = dataSet.getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST);
                            float closestX = closestEntry.getX();

                            LimitLine limit1 = lineChart.getXAxis().getLimitLines().get(0);
                            LimitLine limit2 = lineChart.getXAxis().getLimitLines().get(1);
                            float distanceTolimit1 = Math.abs(limit1.getLimit() - closestX);
                            float distanceTolimit2 = Math.abs(limit2.getLimit() - closestX);
                            LimitLine closeLimit = new LimitLine(closestX, "");
                            closeLimit.setLineColor(Color.BLACK);
                            closeLimit.setLineWidth(1f);

                            if (distanceTolimit1 <= distanceTolimit2) {
                                lineChart.getXAxis().removeLimitLine(limit1);
                                lineChart.getXAxis().addLimitLine(closeLimit);

                            } else {
                                lineChart.getXAxis().removeLimitLine(limit2);
                                lineChart.getXAxis().addLimitLine(closeLimit);
                            }
                            LimitLine gioihan1 = lineChart.getXAxis().getLimitLines().get(0);
                            LimitLine gioihan2 = lineChart.getXAxis().getLimitLines().get(1);
                            float xmax = Math.max(gioihan1.getLimit(), gioihan2.getLimit());
                            float xmin = Math.min(gioihan1.getLimit(), gioihan2.getLimit());

                            List<Entry> dataPoint = new ArrayList<>();
                            for (int i = 0; i < dataSet.getEntryCount(); i++) {
                                Entry entry = data.getDataSetByIndex(0).getEntryForIndex(i);
                                if (entry.getX() >= xmin && entry.getX() <= xmax) {
                                    dataPoint.add(entry);
                                }
                            }
                            double[] xValues = new double[dataPoint.size()];
                            double[] yValues = new double[dataPoint.size()];
                            for (int i = 0; i < dataPoint.size(); i++) {
                                Entry entry = dataPoint.get(i);
                                xValues[i] = entry.getX();
                                yValues[i] = entry.getY();
                            }
                            SimpleRegression regression = new SimpleRegression();
                            for (int i = 0; i < xValues.length; i++) {
                                regression.addData(xValues[i], yValues[i]);
                            }
                            double slopeChart = regression.getSlope();
                            double offsetChart = regression.getIntercept();
                            double rSquare = regression.getRSquare();
                            DecimalFormat df = new DecimalFormat("#.00");
                            df.setMinimumIntegerDigits(1);
                            String dfSlope = df.format(slopeChart).replace(',', '.');
                            String dfOffset = df.format(offsetChart).replace(',', '.');
                            txtSlope.setText("m=" + dfSlope);
                            txtOffset.setText("b=" + dfOffset);
                            float ymin = (float) ((xmin - 1) * slopeChart + offsetChart);
                            float ymax = (float) ((xmax + 1) * slopeChart + offsetChart);
                            List<Entry> entries = new ArrayList<>();
                            entries.add(new Entry(xmin - 1, ymin)); // Điểm A
                            entries.add(new Entry(xmax + 1, ymax)); // Điểm B

                            LineDataSet bestfitLine = (LineDataSet) data.getDataSetByIndex(1);
                            if (bestfitLine == null) {
                                // Nếu chưa tồn tại ILineDataSet với label "bestfit", tạo mới và thêm vào biểu đồ
                                LineDataSet bestfit = new LineDataSet(entries, "");
                                bestfit.setColor(Color.BLACK); // Thiết lập màu đỏ cho đường thẳng
                                bestfit.setLineWidth(2f); // Thiết lập độ rộng của đường thẳng
                                bestfit.setDrawCircles(false); // Không vẽ điểm tròn tại các điểm dữ liệu
                                bestfit.enableDashedLine(15f, 10f, 0f);
                                data.addDataSet(bestfit);
                            } else {
                                // Nếu đã tồn tại ILineDataSet với label "bestfit", cập nhật lại dữ liệu
                                bestfitLine.setValues(entries);
                            }
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                }
                return true;
            }
        });
        btnTare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.tansoLayMau >= 60) {
                    offsetX = offsetX - valueSlowX;
                    offsetY = offsetY - valueSlowY;
                }
            }
        });
    }

    private void anhXa(View view) {
        btnDulieux = (Button) view.findViewById(R.id.buttonDulieux_fragmentNhapbangtay);
        btnDulieuy = (Button) view.findViewById(R.id.buttonDulieuy_fragmentNhapbangtay);
        btnPhantich = (Button) view.findViewById(R.id.buttonPhantich_fragmentNhapbangtay);
        btnTare = (Button) view.findViewById(R.id.buttonTare_fragmentNhapbangtay);
        lvDulieu = (ListView) view.findViewById(R.id.listview_fragmentNhapbangtay);
        lineChart = (LineChart) view.findViewById(R.id.linechart_fragmentNhapbangtay);
        imgLaydulieu = (ImageView) view.findViewById(R.id.imageview_fragmentNhapbangtay);
        txtSlope = (TextView) view.findViewById(R.id.textviewDodoc_fragment_nhapbangtay);
        txtOffset = (TextView) view.findViewById(R.id.textviewoffset_fragment_nhapbangtay);
        dataProcessorX = new MovingAverageFilter(10);
        dataProcessorY = new MovingAverageFilter(10);
        mangValueX = new ArrayList<>();
        mangvalueY = new ArrayList<>();
        khoitaodothi();
        nhapbangtayList = new ArrayList<>();
        nhapbangtayList.add(new DulieuNhapbangtay("0", "0"));
        nhapBangTayAdapter = new NhapBangTayAdapter(view.getContext(), R.layout.dong_fragment_nhapbangtay, nhapbangtayList);
        nhapBangTayAdapter.setSelectedItem(position);
        lvDulieu.setAdapter(nhapBangTayAdapter);
        dataSet = creatSet("Dữ liệu điểm số", Color.RED);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
    }

    public void taoMenuChonsodo(View v) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_chonsodo, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v);
        ListView lvSodo = (ListView) popupView.findViewById(R.id.listview_chonsodo);
        TextView txtEmpty = (TextView) popupView.findViewById(R.id.textviewEmpty_chonsodo);
        chonsodoAdapter chonsodoAdapter = new chonsodoAdapter(getContext(), R.layout.dong_popupwindowchonsodo, MainActivity.sodoCambienList);
        lvSodo.setAdapter(chonsodoAdapter);
        lvSodo.setEmptyView(txtEmpty);
        lvSodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String donvi = ((Button) view.findViewById(R.id.buttonDonvi_chonsodo)).getText().toString();
                String tencambien = ((TextView) view.findViewById(R.id.textviewTen_chonsodo)).getText().toString();
                String maCambien = ((TextView) view.findViewById(R.id.textviewMa_chonsodo)).getText().toString();
//                BleDevice bleDevice = MainActivity.tbKetnois.get(i);
                double[] hesoDonvi = {0, 1};
                for (int j = 0; j < Uuid.camBiens.size(); j++) {
                    if (Uuid.camBiens.get(j).getName().equals(tencambien)) {
                        for (int k = 0; k < Uuid.camBiens.get(j).getDonvi().length; k++) {
                            if (Uuid.camBiens.get(j).getDonvi()[k].equals(donvi)) {
                                hesoDonvi[0] = Uuid.camBiens.get(i).getHeso()[k][0];
                                hesoDonvi[1] = Uuid.camBiens.get(i).getHeso()[k][1];
                                break;
                            }
                        }
                        break;
                    }
                }
                popupWindow.dismiss();
                Button btn = (Button) v;
                if (v.getId() == R.id.buttonDulieux_fragmentNhapbangtay) {
                    maCambienX = maCambien;
                    tencambienX = tencambien;
                    slopeX = hesoDonvi[0];
                    offsetX = hesoDonvi[1];
                }
                if (v.getId() == R.id.buttonDulieuy_fragmentNhapbangtay) {
                    maCambienY = maCambien;
                    tencambienY = tencambien;
                    slopeY = hesoDonvi[0];
                    offsetY = hesoDonvi[1];
                }
                btn.setText(tencambien + "(" + donvi + ")");
            }
        });
    }

    public void khoitaodothi() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.WHITE);

        LineData data = new LineData();
//        data.setValueTextColor(Color.BLACK);
        lineChart.setData(data);

        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis x1 = lineChart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawGridLines(false);
        x1.setPosition(XAxis.XAxisPosition.BOTTOM);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);
        x1.setAxisMinimum(-1f);
        x1.setAxisMaximum(1f);
        x1.setSpaceMin(0.1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(-2f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private LineDataSet creatSet(String lable, int color) {
        LineDataSet set = new LineDataSet(null, lable);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(2f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(false);
        set.setCircleColor(color);
        set.setCircleHoleColor(color);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setCircleRadius(3f);

        set.setFillAlpha(65);
        set.setFillColor(Color.BLUE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;

    }

    private void addEntry(float xValue, float yValue) {
        LineData data = lineChart.getLineData();
        ILineDataSet dataSet = (ILineDataSet) data.getDataSetByIndex(0);
        dataSet.addEntry(new Entry(xValue, yValue));
        data.notifyDataChanged();
//            lineChart.setVisibleXRangeMaximum(xValue + 5f);
        float maxX = Math.max(lineChart.getXAxis().getAxisMaximum(), xValue + 1f);
        float minX = Math.max(lineChart.getXAxis().getAxisMinimum(), xValue - 1f);
        lineChart.getXAxis().setAxisMaximum(maxX);
//        lineChart.getXAxis().setAxisMinimum(minX);
        YAxis leftAxis = lineChart.getAxisLeft();
        float maxY = Math.max(leftAxis.getAxisMaximum(), yValue + 5f); // Lấy giá trị lớn nhất của trục Y
//        float minY = Math.min(leftAxis.getAxisMinimum(), yValue - 5f);
//        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        Log.d("life","onstart");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyEvent(DataEvent event) {
        DulieuCB dulieuCB = event.getData();

        if (dulieuCB.getMacambien().equals(maCambienX) && dulieuCB.getTencambien().equals(tencambienX)
                && dulieuCB.getGiatricambien() != null) {
            if (MainActivity.tansoLayMau >= 60) {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                valueSlowX = (float) (slopeX * dulieu.get(dulieu.size() - 1) + offsetX);
                DecimalFormat df;
                if (tencambienX.equals("Dòng điện")) {
                    df = new DecimalFormat("#.000");
                } else {
                    df = new DecimalFormat("#.00");
                }
                df.setMinimumIntegerDigits(1);
                String dfdulieu = df.format(valueSlowX).replace(',', '.');
                nhapbangtayList.get(position).setDulieux(dfdulieu);

            } else {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                for (float x : dulieu
                ) {
                    float value = (float) (slopeX * x + offsetX);
                    mangValueX.add(value);
//                    Log.d("size", String.valueOf(mangValue.size()));
                }
                if (mangValueX.size() >= 200) {
                    rmsFastX = rms(mangValueX);
//                    rmsFastX = (float) dataProcessorX.filter(rmsFastX);
                    DecimalFormat df;
                    if (tencambienX.equals("Dòng điện")) {
                        df = new DecimalFormat("#.000");
                    } else {
                        df = new DecimalFormat("#.00");
                    }
                    df.setMinimumIntegerDigits(1);
                    String dfdulieu = df.format(rmsFastX).replace(',', '.');
//                    Log.d("mangValueX", "Danh sách 200 phần tử: " + mangValueX.toString());

                    nhapbangtayList.get(position).setDulieux(dfdulieu);
                    mangValueX.clear();
                }
            }
        }
        if (dulieuCB.getMacambien().equals(maCambienY) && dulieuCB.getTencambien().equals(tencambienY)
                && dulieuCB.getGiatricambien() != null) {
            if (MainActivity.tansoLayMau >= 60) {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                valueSlowY = (float) (slopeY * dulieu.get(dulieu.size() - 1) + offsetY);
                DecimalFormat df;
                if (tencambienY.equals("Dòng điện")) {
                    df = new DecimalFormat("#.000");
                } else {
                    df = new DecimalFormat("#.00");
                }
                df.setMinimumIntegerDigits(1);
                String dfdulieu = df.format(valueSlowY).replace(',', '.');
                nhapbangtayList.get(position).setDulieuy(dfdulieu);

            } else {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                for (float x : dulieu
                ) {
                    float value = (float) (slopeY * x + offsetY);
                    mangvalueY.add(value);

//                    Log.d("value", String.valueOf(mangvalueY));
                }
                if (mangvalueY.size() >= 200) {

                    rmsFastY = (float) rms(mangvalueY);
//                    rmsFastY = (float) dataProcessorY.filter(rmsFastY);
                    DecimalFormat df;
                    if (tencambienY.equals("Dòng điện")) {
                        df = new DecimalFormat("#.000");
                    } else {
                        df = new DecimalFormat("#.00");
                    }
                    df.setMinimumIntegerDigits(1);
                    String dfdulieu = df.format(rmsFastY).replace(',', '.');
                    nhapbangtayList.get(position).setDulieuy(dfdulieu);
                    mangvalueY.clear();
                }
            }
        }
        nhapBangTayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
//        Log.d("life","onstop");
    }
    private float rms(List<Float> value) {
        float rms=0;
//        float average = 0;
//        for (float val : value) {
//            average += val;
//        }
//        average /= value.size();
//        for (int i = 0; i < value.size(); i++) {
//            value.set(i, value.get(i) - average);
//        }
        for(float val : value){
            rms += val * val;
        }
        rms /= value.size();
        rms = (float) Math.sqrt(rms);
        return rms;
    }
}
