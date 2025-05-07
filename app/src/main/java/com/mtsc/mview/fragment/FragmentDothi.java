package com.mtsc.mview.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.adapter.DulieuDothiAdapter;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuDothi;
import com.mtsc.mview.ultis.Uuid;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentDothi extends Fragment implements FragmentBaseMain.OnDataChangeListener {
    LineChart lineChart;
    ListView listviewCacsodo;
    List<DulieuDothi> dulieuDothis;
    DulieuDothiAdapter dulieuDothiAdapter;
    List<ILineDataSet> dataSets;
    ImageView imgZoomout, imgGoptruc;
    private int kieuPhantich = 0;
    FragmentToado fragmentToado;
    FragmentChenhlech fragmentChenhlech;
    FragmentDodoc fragmentDodoc;
    FragmentTrungbinh fragmentTrungbinh;
    FragmentRms fragmentRms;
    FragmentHambac2 fragmentBac2;
    Fragment currentFragment;
    FragmentManager fragmentManager;
    int soDothi = 0;
    float dulieucbcu = 0, tgiancu = 0;
    boolean isLimitLineDragging = false, isZooming = false;
    private float initialDistance = 0;
    private float initialDistanceX = 0;
    private float initialDistanceY = 0;
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
        return inflater.inflate(R.layout.fragment_dothi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = (LineChart) view.findViewById(R.id.linechart_fragmentdothi);
        listviewCacsodo = (ListView) view.findViewById(R.id.listview_fragmentdothi);
        imgZoomout = (ImageView) view.findViewById(R.id.imageviewZoomout_fragmentdothi);
        imgGoptruc = (ImageView) view.findViewById(R.id.imageviewGoptruc_fragmentdothi);
        dulieuDothis = new ArrayList<>();
        dulieuDothiAdapter = new DulieuDothiAdapter(getContext(), R.layout.dong_listviewcacsodo, dulieuDothis);
        listviewCacsodo.setAdapter(dulieuDothiAdapter);
        dataSets = new ArrayList<>();
        fragmentToado = new FragmentToado();
        fragmentChenhlech = new FragmentChenhlech();
        fragmentDodoc = new FragmentDodoc();
        fragmentTrungbinh = new FragmentTrungbinh();
        fragmentRms = new FragmentRms();
        fragmentBac2 = new FragmentHambac2();
        fragmentManager = getChildFragmentManager();

        FragmentBaseMain fragmentBaseMain = (FragmentBaseMain) getParentFragment();
        if (fragmentBaseMain != null) {
            fragmentBaseMain.setOnDataChangeListener(this);
        }
        khoitaodothi();
        listviewCacsodo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DulieuDothi dulieuDothi = dulieuDothis.get(i);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setTitle("Xác nhận xóa dữ liệu");
                builder.setMessage("Bạn có chắc chắn muốn xóa " + dulieuDothi.getTencambien() + "-" + dulieuDothi.getMacambien() + "?");
                builder.setBackground(getResources().getDrawable(R.drawable.dialog_background)); // Thiết lập background tùy chỉnh cho dialog
                builder.setIcon(R.drawable.ic_delete);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dulieuDothis.remove(i);
                        dulieuDothiAdapter.notifyDataSetChanged();
                        dataSets.remove(i);
                        soDothi--;

                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog
                        dialog.dismiss();
                    }
                });

// Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

// Thiết lập màu chữ cho các nút
                positiveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Thay thế your_color bằng mã màu tùy chọn
                negativeButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                return false;
            }
        });
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (kieuPhantich == 0) {
                    lineChart.setDragXEnabled(true);
                    lineChart.onTouchEvent(motionEvent);
                } else {
                    lineChart.setDragXEnabled(false);
                }
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_MOVE:
                        if (kieuPhantich == 1) {
                            float xTouch = motionEvent.getX();
                            float yTouch = motionEvent.getY();
                            MPPointD point = lineChart.getValuesByTouchPoint(xTouch, yTouch, YAxis.AxisDependency.LEFT);
                            float xValue = (float) point.x;
                            LineData data = lineChart.getLineData();
                            List<Entry> toadodiem = new ArrayList<>();
                            for (int i = 0; i < dataSets.size(); i++) {
                                Entry closestEntry = data.getDataSetByIndex(i).getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST);
                                toadodiem.add(new Entry(closestEntry.getX(), closestEntry.getY()));
                                listener.guiGiatriphantich(i, closestEntry.getX(), closestEntry.getY(), null);
                            }

                            lineChart.getXAxis().removeAllLimitLines();
                            // Tính toán lại vị trí của đường line
                            LimitLine limitLine = new LimitLine(toadodiem.get(0).getX(), "");
                            limitLine.setLineColor(Color.BLACK);
                            limitLine.setLineWidth(1f);
                            lineChart.getXAxis().addLimitLine(limitLine);
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();

                        }
                        if (kieuPhantich == 2) {
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
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();

                            for (int i = 0; i < dataSets.size(); i++) {
                                Entry entry1 = data.getDataSetByIndex(i).getEntryForXValue(gioihan1.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                                Entry entry2 = data.getDataSetByIndex(i).getEntryForXValue(gioihan2.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                                float deltax = Math.abs(entry1.getX() - entry2.getX());
                                float deltay = Math.abs(entry1.getY() - entry2.getY());
                                listener.guiGiatriphantich(i, deltax, deltay, null);
                            }
                            return false;
                        }
                        if (kieuPhantich == 3) {
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

                            for (int line = 0; line < soDothi; line++) {
                                List<Entry> dataPoint = new ArrayList<>();
                                for (int i = 0; i < dataSets.get(line).getEntryCount(); i++) {
                                    Entry entry = data.getDataSetByIndex(line).getEntryForIndex(i);
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
                                double slope = regression.getSlope();
                                double intercept = regression.getIntercept();
                                double rSquare = regression.getRSquare();
                                listener.guiGiatriphantich(line, (float) slope, (float) intercept, (float) rSquare);
                                float ymin = (float) (xmin * slope + intercept);
                                float ymax = (float) (xmax * slope + intercept);


                                List<Entry> entries = new ArrayList<>();
                                entries.add(new Entry(xmin, ymin)); // Điểm A
                                entries.add(new Entry(xmax, ymax)); // Điểm B

                                LineDataSet bestfitLine = (LineDataSet) data.getDataSetByIndex(soDothi + line);
                                if (bestfitLine == null) {
                                    // Nếu chưa tồn tại ILineDataSet với label "bestfit", tạo mới và thêm vào biểu đồ
                                    LineDataSet bestfit = new LineDataSet(entries, "");
                                    bestfit.setColor(dataSets.get(line).getColor()); // Thiết lập màu đỏ cho đường thẳng
                                    bestfit.setLineWidth(2f); // Thiết lập độ rộng của đường thẳng
                                    bestfit.setDrawCircles(false); // Không vẽ điểm tròn tại các điểm dữ liệu
                                    bestfit.enableDashedLine(15f, 10f, 0f);
                                    data.addDataSet(bestfit);
                                } else {
                                    // Nếu đã tồn tại ILineDataSet với label "bestfit", cập nhật lại dữ liệu
                                    bestfitLine.setValues(entries);
                                }

                            }
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                        if (kieuPhantich == 4) {
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

                            for (int line = 0; line < soDothi; line++) {
                                List<Entry> dataPoint = new ArrayList<>();
                                for (int i = 0; i < dataSets.get(line).getEntryCount(); i++) {
                                    Entry entry = data.getDataSetByIndex(line).getEntryForIndex(i);
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
                                float ymin = (float) yValues[0], ymax = (float) yValues[0], ytb = 0;
                                for (int i = 0; i < yValues.length; i++) {
                                    if (ymax < yValues[i]) ymax = (float) yValues[i];
                                    if (ymin > yValues[i]) ymin = (float) yValues[i];
                                    ytb += yValues[i];
                                }
                                ytb = ytb / yValues.length;
                                listener.guiGiatriphantich(line, ymin, ymax, ytb);

                            }
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                        if (kieuPhantich == 5) {
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

                            for (int line = 0; line < soDothi; line++) {
                                List<Entry> dataPoint = new ArrayList<>();
                                for (int i = 0; i < dataSets.get(line).getEntryCount(); i++) {
                                    Entry entry = data.getDataSetByIndex(line).getEntryForIndex(i);
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
                                float ymin = (float) yValues[0], ymax = (float) yValues[0], yRms = 0;
                                for (int i = 0; i < yValues.length; i++) {
                                    if (ymax < yValues[i]) ymax = (float) yValues[i];
                                    if (ymin > yValues[i]) ymin = (float) yValues[i];
                                    yRms += yValues[i] * yValues[i];
                                }
                                float yPeak = ymax - ymin;
                                yRms = (float) Math.sqrt(yRms / yValues.length);
                                listener.guiGiatriphantich(line, yRms, yPeak, null);

                            }
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                        if (kieuPhantich == 6) {
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

                            for (int line = 0; line < soDothi; line++) {
                                List<Entry> dataPoint = new ArrayList<>();
                                for (int i = 0; i < dataSets.get(line).getEntryCount(); i++) {
                                    Entry entry = data.getDataSetByIndex(line).getEntryForIndex(i);
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
//                                float ymin = (float) yValues[0], ymax = (float) yValues[0], yRms = 0;
//                                for (int i = 0; i < yValues.length; i++) {
//                                    if (ymax < yValues[i]) ymax = (float) yValues[i];
//                                    if (ymin > yValues[i]) ymin = (float) yValues[i];
//                                    yRms += yValues[i] * yValues[i];
//                                }
//                                float yPeak = ymax - ymin;
//                                yRms = (float) Math.sqrt(yRms / yValues.length);
//                                listener.guiGiatriphantich(line, yRms, yPeak, null);
                                double[] coefficients = quadraticRegressionExact(xValues, yValues);
                                double a = coefficients[0];
                                double b = coefficients[1];
                                double c = coefficients[2];
                                listener.guiGiatriphantich(line, (float) a, (float) b, (float)c);
                            }
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                }
                return true;
            }
        });
        imgZoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < dataSets.size(); i++) {
                    ILineDataSet dataSet = dataSets.get(i);
                    YAxis.AxisDependency dependency = dataSet.getAxisDependency();
                    float ymax = dataSet.getYMax();
                    float ymin = dataSet.getYMin();
//                    Log.d("data",dataSet.getLabel()+ "," + ymax + "," + ymin +","+dependency);
                    lineChart.getAxis(dependency).setAxisMaximum((float) (ymax + 0.5f));
                    lineChart.getAxis(dependency).setAxisMinimum((float) (ymin - 0.5f));
                    lineChart.setVisibleYRange(ymin, ymax, dependency);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                }
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
        x1.setAxisMinimum(0f);
        x1.setAxisMaximum(10f);
        x1.setSpaceMin(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(-2f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setAxisMaximum(10f);
        rightAxis.setAxisMinimum(-2f);
        rightAxis.setDrawGridLines(true);

    }

    private LineDataSet creatSet(String lable, int color) {
        LineDataSet set = new LineDataSet(null, lable);
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            // Nếu đã có ít nhất một dòng đồ thị được thêm vào
            set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        } else {
            // Nếu chưa có dòng đồ thị nào được thêm vào
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
        }
        set.setColor(color);
        set.setLineWidth(2f);
        set.setDrawCircles(true);
        set.setDrawCircleHole(false);
        set.setCircleColor(color);
        set.setCircleHoleColor(color);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setCircleRadius(1.5f);

        set.setFillAlpha(65);
        set.setFillColor(Color.BLUE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;

    }

    @Override
    public void chonCamBien(Bundle bundle) {
        String tencambien = bundle.getString("tencambien");
        String donvi = bundle.getString("donvi");
        String macambien = bundle.getString("macambien");
        double slope = 1, offset = 0;
        for (int i = 0; i < MainActivity.sodoCambienList.size(); i++) {
            if (MainActivity.sodoCambienList.get(i).getTencambien().equals(tencambien)) {
                for (int j = 0; j < MainActivity.sodoCambienList.get(i).getDonvi().length; j++) {
                    if (MainActivity.sodoCambienList.get(i).getDonvi()[j].equals(donvi)) {
                        slope = MainActivity.sodoCambienList.get(i).getHeso()[j][0];
                        offset = MainActivity.sodoCambienList.get(i).getHeso()[j][1];
                        break;
                    }
                }
                break;
            }
        }
        for (DulieuDothi dulieudothi : dulieuDothis
        ) {
            if (dulieudothi.getMacambien().equals(macambien) && dulieudothi.getTencambien().equals(tencambien)) {
                return;
            }
        }
        dataSets.add(creatSet(tencambien, mamaudothi[dulieuDothis.size()]));
        soDothi++;
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        dulieuDothis.add(new DulieuDothi(mamaudothi[dulieuDothis.size()], tencambien, macambien, donvi, new double[]{slope, offset}));
        dulieuDothiAdapter.notifyDataSetChanged();
        if (macambien.substring(0, macambien.indexOf('-')).equals("V&A")) {
            lineChart.setPinchZoom(false);
            lineChart.setDragYEnabled(false);
            lineChart.invalidate();
        }
    }

    @Override
    public void onDataReceived(DulieuCB dulieuCB) {
        for (int i = 0; i < dulieuDothis.size(); i++) {
            String macambien = dulieuDothis.get(i).getMacambien();
            String tencambien = dulieuDothis.get(i).getTencambien();
            double slope = dulieuDothis.get(i).getHeso()[0];
            double offset = dulieuDothis.get(i).getHeso()[1];
            if (dulieuCB.getMacambien().equals(macambien) && dulieuCB.getTencambien().equals(tencambien)
                    && dulieuCB.getGiatricambien() != null) {
                List<Float> listDulieu = dulieuCB.getGiatricambien();

                List<Entry> entries = new ArrayList<>();
                int xValue = 0;
                for (float y : listDulieu
                ) {
                    entries.add(new Entry((xValue + dataSets.get(i).getEntryCount()) * (float) (MainActivity.tansoLayMau / 1000.0), (float) (slope * y + offset)));
                    xValue++;
//                    Log.d("data", String.valueOf(y));
                }
                addEntry(dataSets.get(i), entries);
                break;
            }
        }
    }

    @Override
    public void xoaDulieucu() {

        LineData data = lineChart.getLineData();
        data.clearValues(); // Xóa tất cả các DataSet
        lineChart.setData(data);
        for (int i = 0; i < dulieuDothis.size(); i++) {
            dataSets.add(creatSet(dulieuDothis.get(i).getTencambien(), dulieuDothis.get(i).getColor()));
            LineData lineData = new LineData(dataSets);
            lineChart.setData(lineData);
        }
        XAxis x1 = lineChart.getXAxis();
        x1.setAxisMinimum(0f);
        x1.setAxisMaximum(10f);
        x1.setSpaceMin(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(-2f);


        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setAxisMaximum(10f);
        rightAxis.setAxisMinimum(-2f);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    public void xemLaiDulieuCu(Map<String, List<Float>> dulieuCu, Float tanso) {
        for (int i = 0; i < dulieuDothis.size(); i++) {
            String cambien = dulieuDothis.get(i).getMacambien();
            if (dulieuCu.containsKey(cambien) && dulieuCu.get(cambien) != null) {
                List<Float> dulieu = dulieuCu.get(cambien);
                for (Float value : dulieu
                ) {
//                    addEntry(dataSets.get(i), (float) (slope * value + offset), dataSets.get(i).getEntryCount() * (float) (tanso / 1000.0));
                }
                break;
            }
        }
    }

    @Override
    public void phantichDothi(int phantich) {
        kieuPhantich = phantich;
        lineChart.getXAxis().removeAllLimitLines();
        if (phantich == 0) {
            if (currentFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(currentFragment);
                transaction.commit();
            }

            lineChart.getXAxis().removeAllLimitLines();
            if (dataSets.size() > soDothi) {
                for (int i = soDothi; i < dataSets.size(); i++) {
                    dataSets.remove(i);
                }
            }
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
        if (phantich == 1) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine = new LimitLine((xmin + xmax) / 2, "");
            limitLine.setLineColor(Color.BLACK);
            limitLine.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentToado.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentToado);
            transaction.commit();
            currentFragment = fragmentToado;
        }
        if (phantich == 2) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine1 = new LimitLine(xmin, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(xmax, "");
            limitLine2.setLineColor(Color.BLACK);
            limitLine2.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine1);
            lineChart.getXAxis().addLimitLine(limitLine2);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentChenhlech.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentChenhlech);
            transaction.commit();
            currentFragment = fragmentChenhlech;
        }
        if (phantich == 3) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine1 = new LimitLine(xmin, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(xmax, "");
            limitLine2.setLineColor(Color.BLACK);
            limitLine2.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine1);
            lineChart.getXAxis().addLimitLine(limitLine2);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentDodoc.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentDodoc);
            transaction.commit();
            currentFragment = fragmentDodoc;
        }
        if (phantich == 4) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine1 = new LimitLine(xmin, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(xmax, "");
            limitLine2.setLineColor(Color.BLACK);
            limitLine2.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine1);
            lineChart.getXAxis().addLimitLine(limitLine2);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentTrungbinh.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentTrungbinh);
            transaction.commit();
            currentFragment = fragmentTrungbinh;
        }
        if (phantich == 5) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine1 = new LimitLine(xmin, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(xmax, "");
            limitLine2.setLineColor(Color.BLACK);
            limitLine2.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine1);
            lineChart.getXAxis().addLimitLine(limitLine2);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentRms.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentRms);
            transaction.commit();
            currentFragment = fragmentRms;
        }
        if (phantich == 6) {
            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine1 = new LimitLine(xmin, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(xmax, "");
            limitLine2.setLineColor(Color.BLACK);
            limitLine2.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine1);
            lineChart.getXAxis().addLimitLine(limitLine2);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("sodo", (ArrayList<? extends Parcelable>) dulieuDothis);
            fragmentBac2.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.layoutPhantich_fragmentdothi, fragmentBac2);
            transaction.commit();
            currentFragment = fragmentBac2;
        }
    }


    private void addEntry(ILineDataSet dataSet, List<Entry> entries) {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(dataSets.indexOf(dataSet));
            if (set == null) {
                set = dataSet;
                data.addDataSet(set);
            }
            for (Entry entry : entries
            ) {
                set.addEntry(entry);

            }
//            set.addEntry(new Entry(xValue, yValue));
            data.notifyDataChanged();
//            lineChart.setVisibleXRangeMaximum(xValue + 5f);
            lineChart.isAutoScaleMinMaxEnabled();
            float xValueMax = entries.get(entries.size() - 1).getX();

            lineChart.moveViewToX(xValueMax + 1f);
//            lineChart.setVisibleYRangeMaximum(yValue + 5f, YAxis.AxisDependency.LEFT);
            lineChart.getXAxis().setAxisMaximum(xValueMax + 5f);
            YAxis leftAxis = lineChart.getAxisLeft();
            float maxY = Math.max(leftAxis.getAxisMaximum(), getMaxYValue(entries) + 5f); // Lấy giá trị lớn nhất của trục Y
            float minY = Math.min(leftAxis.getAxisMinimum(), getMinYValue(entries) - 5f);
            leftAxis.setAxisMinimum(minY);
            leftAxis.setAxisMaximum(maxY);
            lineChart.getAxisRight().setAxisMaximum(maxY);
            lineChart.getAxisRight().setAxisMinimum(minY);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private float getMaxYValue(List<Entry> entries) {
        float max = Float.MIN_VALUE;
        for (Entry entry : entries) {
            if (entry.getY() > max) {
                max = entry.getY();
            }
        }
        return max;
    }

    private float getMinYValue(List<Entry> entries) {
        float min = Float.MAX_VALUE;
        for (Entry entry : entries) {
            if (entry.getY() < min) {
                min = entry.getY();
            }
        }
        return min;
    }

    private PhantichDothi listener;

    // Khai báo interface
    public interface PhantichDothi {
        void guiGiatriphantich(int position, Float giatri1, Float giatri2, Float giatri3);
    }

    // Cài đặt phương thức của interface để truyền dữ liệu sang Fragment con
    public void setOnPhantichDothi(PhantichDothi listener) {
        this.listener = listener;
    }
    public static double[] quadraticRegressionExact(double[] x, double[] y) {
        int n = x.length;

        // Tính các tổng
        double sumX = 0, sumY = 0, sumX2 = 0, sumX3 = 0, sumX4 = 0;
        double sumXY = 0, sumX2Y = 0;

        for (int i = 0; i < n; i++) {
            double xi = x[i];
            double yi = y[i];
            double xi2 = xi * xi;
            double xi3 = xi2 * xi;
            double xi4 = xi3 * xi;

            sumX += xi;
            sumY += yi;
            sumX2 += xi2;
            sumX3 += xi3;
            sumX4 += xi4;
            sumXY += xi * yi;
            sumX2Y += xi2 * yi;
        }

        // Hệ phương trình
        double[][] coefficients = {
                {sumX4, sumX3, sumX2},
                {sumX3, sumX2, sumX},
                {sumX2, sumX, n}
        };

        double[] constants = {sumX2Y, sumXY, sumY};

        // Giải hệ phương trình
        return solveLinearSystem(coefficients, constants);
    }

    private static double[] solveLinearSystem(double[][] A, double[] B) {
        int n = B.length;
        double[] result = new double[n];

        // Gauss Elimination
        for (int i = 0; i < n; i++) {
            // Tìm hàng lớn nhất
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) {
                    max = j;
                }
            }

            // Hoán đổi hàng
            double[] temp = A[i];
            A[i] = A[max];
            A[max] = temp;

            double tmp = B[i];
            B[i] = B[max];
            B[max] = tmp;

            // Khử Gauss
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }

        // Back Substitution
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * result[j];
            }
            result[i] = (B[i] - sum) / A[i][i];
        }

        return result;
    }
}
