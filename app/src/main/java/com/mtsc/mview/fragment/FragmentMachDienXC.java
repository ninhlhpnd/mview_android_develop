package com.mtsc.mview.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.ultis.DataEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jtransforms.fft.DoubleFFT_1D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentMachDienXC extends Fragment {
    private View layout;
    private FragmentManager fragmentManager;
    LineChart lineChart;
    List<Float> entryListAp, entryListDong;
    List<ILineDataSet> dataSets;
    TextView txtUtoado, txtUrms, txtUchenhlech, txtItoado, txtIrms, txtIchenhlech;
    LinearLayout layoutUToado, layoutUChenhlech, layoutURms, layoutIToado, layoutIChenhlech, layoutIRms;
    DecimalFormat df2, df3;
    SeekBar seekbarU, seekbarI;
    boolean isToado = false, isChenhlech = false, isRms = false;
    int kieuPhantich = 0;
    int MAX_VALUES=200;
    public FragmentMachDienXC(FragmentManager fragmentManager) {
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
        layout = inflater.inflate(R.layout.fragment_machdienxc, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
        khoitaodothi();
//        List<Float> listTest=new ArrayList<>();
//        for(int i=0;i<400;i++){
//            double t=i*0.001;
//            float x= (float) (5 * Math.sin(2*Math.PI*50*t + Math.PI/4));
//            listTest.add(x);
//        }
//        hoiquytuyentinh(listTest,0.001,400);
        layoutUToado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToadoFunction();
            }
        });
        layoutUChenhlech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChenhLechFunction();
            }
        });
        layoutURms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RMSFunction();
            }
        });
        layoutIToado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToadoFunction();
            }
        });
        layoutIChenhlech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChenhLechFunction();
            }
        });
        layoutIRms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RMSFunction();
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
                    case MotionEvent.ACTION_MOVE: {
                        if (kieuPhantich == 1) {
                            float xTouch = motionEvent.getX();
                            float yTouch = motionEvent.getY();
                            MPPointD point = lineChart.getValuesByTouchPoint(xTouch, yTouch, YAxis.AxisDependency.LEFT);
                            float xValue = (float) point.x;
                            LineData data = lineChart.getLineData();
                            List<Entry> toadodiem = new ArrayList<>();
                            Entry closestEntry = data.getDataSetByIndex(0).getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST);
                            Entry closestEntryI = data.getDataSetByIndex(1).getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST);

                            txtUtoado.setText("x:" + df3.format(closestEntry.getX()).replace(',', '.') + "s"
                                    + " y:" + df2.format(closestEntry.getY()).replace(',', '.') + "V");

                            txtItoado.setText("x:" + df3.format(closestEntryI.getX()).replace(',', '.') + "s"
                                    + " y:" + df3.format(closestEntryI.getY()).replace(',', '.') + "A");

                            lineChart.getXAxis().removeAllLimitLines();
                            // Tính toán lại vị trí của đường line
                            LimitLine limitLine = new LimitLine(closestEntry.getX(), "");
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
                            Entry entry1 = data.getDataSetByIndex(0).getEntryForXValue(gioihan1.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                            Entry entry2 = data.getDataSetByIndex(0).getEntryForXValue(gioihan2.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                            float deltax = Math.abs(entry1.getX() - entry2.getX());
                            float deltay = Math.abs(entry1.getY() - entry2.getY());
                            txtUchenhlech.setText("Δx:" + df3.format(deltax).replace(',', '.') + "s"
                                    + " Δy=" + df2.format(deltay).replace(',', '.') + "V");

                            Entry entry1I = data.getDataSetByIndex(1).getEntryForXValue(gioihan1.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                            Entry entry2I = data.getDataSetByIndex(1).getEntryForXValue(gioihan2.getLimit(), Float.NaN, DataSet.Rounding.CLOSEST);
                            float deltaxI = Math.abs(entry1I.getX() - entry2I.getX());
                            float deltayI = Math.abs(entry1I.getY() - entry2I.getY());
                            txtIchenhlech.setText("Δx:" + df3.format(deltaxI).replace(',', '.') + "s"
                                    + " Δy=" + df3.format(deltayI).replace(',', '.') + "A");
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

                            List<Entry> dataPointU = new ArrayList<>();
                            for (int i = 0; i < dataSets.get(0).getEntryCount(); i++) {
                                Entry entry = data.getDataSetByIndex(0).getEntryForIndex(i);
                                if (entry.getX() >= xmin && entry.getX() <= xmax) {
                                    dataPointU.add(entry);
                                }
                            }
                            float Urms = RMSCalculate(dataPointU);
                            txtUrms.setText("Urms(V):" + df2.format(Urms).replace(',', '.') + "V");

                            List<Entry> dataPointI = new ArrayList<>();
                            for (int i = 0; i < dataSets.get(1).getEntryCount(); i++) {
                                Entry entry = data.getDataSetByIndex(1).getEntryForIndex(i);
                                if (entry.getX() >= xmin && entry.getX() <= xmax) {
                                    dataPointI.add(entry);
                                }
                            }
                            float Irms = RMSCalculate(dataPointI);
                            txtIrms.setText("Irms(A):" + df3.format(Irms).replace(',', '.') + "A");

                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                        }
                        break;
                    }

                }
                return true;
            }
        });
        seekbarU.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float maxU = (float) (12.0 / (seekBar.getProgress() * 1.0));
                lineChart.getAxisLeft().setAxisMaximum(maxU);
                lineChart.getAxisLeft().setAxisMinimum(-maxU);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarI.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float maxI = (float) (1.0 / (seekBar.getProgress() * 1.0));
                lineChart.getAxisRight().setAxisMaximum(maxI);
                lineChart.getAxisRight().setAxisMinimum(-maxI);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void RMSFunction() {
        isRms = !isRms;
        int color = ContextCompat.getColor(getContext(), R.color.xam);
        if (isRms) {
            layoutUToado.setBackgroundColor(color);
            layoutUChenhlech.setBackgroundColor(color);
            layoutURms.setBackgroundColor(Color.WHITE);

            layoutIToado.setBackgroundColor(color);
            layoutIChenhlech.setBackgroundColor(color);
            layoutIRms.setBackgroundColor(Color.WHITE);

            kieuPhantich = 3;

            lineChart.getXAxis().removeAllLimitLines();
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
        } else {
            kieuPhantich = 0;
            layoutURms.setBackgroundColor(color);
            layoutIRms.setBackgroundColor(color);
            lineChart.getXAxis().removeAllLimitLines();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private void ChenhLechFunction() {
        isChenhlech = !isChenhlech;
        int color = ContextCompat.getColor(getContext(), R.color.xam);
        if (isChenhlech) {

            layoutUToado.setBackgroundColor(color);
            layoutUChenhlech.setBackgroundColor(Color.WHITE);
            layoutURms.setBackgroundColor(color);

            layoutIToado.setBackgroundColor(color);
            layoutIChenhlech.setBackgroundColor(Color.WHITE);
            layoutIRms.setBackgroundColor(color);

            kieuPhantich = 2;

            lineChart.getXAxis().removeAllLimitLines();

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
        } else {
            kieuPhantich = 0;
            layoutUChenhlech.setBackgroundColor(color);
            layoutIChenhlech.setBackgroundColor(color);
            lineChart.getXAxis().removeAllLimitLines();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private void ToadoFunction() {
        isToado = !isToado;
        int color = ContextCompat.getColor(getContext(), R.color.xam);
        if (isToado) {
            isChenhlech = false;
            isRms = false;

            layoutUToado.setBackgroundColor(Color.WHITE);
            layoutUChenhlech.setBackgroundColor(color);
            layoutURms.setBackgroundColor(color);

            layoutIToado.setBackgroundColor(Color.WHITE);
            layoutIChenhlech.setBackgroundColor(color);
            layoutIRms.setBackgroundColor(color);

            kieuPhantich = 1;
            lineChart.getXAxis().removeAllLimitLines();

            float xmin = lineChart.getLowestVisibleX();
            float xmax = lineChart.getHighestVisibleX();
            LimitLine limitLine = new LimitLine((xmin + xmax) / 2, "");
            limitLine.setLineColor(Color.BLACK);
            limitLine.setLineWidth(1f);
            lineChart.getXAxis().addLimitLine(limitLine);
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        } else {
            layoutUToado.setBackgroundColor(color);
            layoutIToado.setBackgroundColor(color);
            kieuPhantich = 0;
            lineChart.getXAxis().removeAllLimitLines();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private void anhXa(View view) {
        lineChart = (LineChart) view.findViewById(R.id.linechart_fragmentMachxc);
        txtUtoado = (TextView) view.findViewById(R.id.textviewUtoado_fragmentmachxc);
        txtUchenhlech = (TextView) view.findViewById(R.id.textviewUchenhlech_fragmentmachxc);
        txtUrms = (TextView) view.findViewById(R.id.textviewUrms_fragmentmachxc);

        txtItoado = (TextView) view.findViewById(R.id.textviewItoado_fragmentmachxc);
        txtIchenhlech = (TextView) view.findViewById(R.id.textviewIchenhlech_fragmentmachxc);
        txtIrms = (TextView) view.findViewById(R.id.textviewIrms_fragmentmachxc);

        layoutUToado = (LinearLayout) view.findViewById(R.id.layoutUToado_fragmentmachxc);
        layoutUChenhlech = (LinearLayout) view.findViewById(R.id.layoutUChenhlech_fragmentmachxc);
        layoutURms = (LinearLayout) view.findViewById(R.id.layoutURms_fragmentmachxc);

        layoutIToado = (LinearLayout) view.findViewById(R.id.layoutIToado_fragmentmachxc);
        layoutIChenhlech = (LinearLayout) view.findViewById(R.id.layoutIChenhlech_fragmentmachxc);
        layoutIRms = (LinearLayout) view.findViewById(R.id.layoutIRms_fragmentmachxc);

        seekbarU = (SeekBar) view.findViewById(R.id.seekbarU_fragmentmachxc);
        seekbarI = (SeekBar) view.findViewById(R.id.seekbarI_fragmentmachxc);
        dataSets = new ArrayList<>();
        entryListAp = new ArrayList<>();
        entryListDong = new ArrayList<>();
        df2 = new DecimalFormat("#.00");
        df3 = new DecimalFormat("#.000");
        df2.setMinimumIntegerDigits(1);
        df3.setMinimumIntegerDigits(1);

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
        x1.setAxisMaximum((float) (MAX_VALUES*0.001));
        x1.setSpaceMin(0.05f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(12f);
        leftAxis.setAxisMinimum(-12f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setAxisMaximum(1f);
        rightAxis.setAxisMinimum(-1f);
        rightAxis.setDrawGridLines(true);
        dataSets.add(creatSet("Điện áp", Color.RED, YAxis.AxisDependency.LEFT));
        dataSets.add(creatSet("Dòng điện", Color.BLACK, YAxis.AxisDependency.RIGHT));
    }

    private LineDataSet creatSet(String lable, int color, YAxis.AxisDependency axisDependency) {
        LineDataSet set = new LineDataSet(null, lable);
        set.setAxisDependency(axisDependency);
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

            lineChart.moveViewToX(xValueMax);
//            lineChart.setVisibleYRangeMaximum(yValue + 5f, YAxis.AxisDependency.LEFT);
            YAxis leftAxis = lineChart.getAxisLeft();
            float maxY = Math.max(leftAxis.getAxisMaximum(), getMaxYValue(entries) + 1f); // Lấy giá trị lớn nhất của trục Y
            float minY = Math.min(leftAxis.getAxisMinimum(), getMinYValue(entries) - 1f);
            leftAxis.setAxisMinimum(minY);
            leftAxis.setAxisMaximum(maxY);
//            lineChart.getAxisRight().setAxisMaximum(maxY);
//            lineChart.getAxisRight().setAxisMinimum(minY);
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

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
//        Log.d("life","onstop");
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
        if (dulieuCB.getMacambien().equals("lanchay")) {
            entryListAp.clear();
            entryListDong.clear();
        } else {
            if (dulieuCB.getTencambien().equals("Dòng điện")) {
                List<Float> value = dulieuCB.getGiatricambien();
                for (float yvalue : value
                ) {
                    entryListDong.add(yvalue);
                }
                if (entryListDong.size() >= MAX_VALUES) {
                    double[] sinwave = fft(entryListDong, (MainActivity.tansoLayMau / 1000.0), MAX_VALUES);
                    dataSets.get(1).clear();
                    List<Entry> dongentry = new ArrayList<>();
                    for (int i = 0; i < MAX_VALUES; i++) {
                        float xvalue = (float) (i * (MainActivity.tansoLayMau / 1000.0));
                        float yvalue = (float) (sinwave[0] * Math.sin(2 * Math.PI * sinwave[1] * xvalue + sinwave[2] - 0.26));
                        dongentry.add(new Entry(xvalue, yvalue));
                    }
                    addEntry(dataSets.get(1), dongentry);
                    entryListDong.clear();
                }
            } else if (dulieuCB.getTencambien().equals("Điện áp")) {
                List<Float> value = dulieuCB.getGiatricambien();
                for (float yvalue : value
                ) {
                    entryListAp.add(yvalue);
                }
                if (entryListAp.size() >= MAX_VALUES) {
                    double[] sinwave = fft(entryListAp, (MainActivity.tansoLayMau / 1000.0), MAX_VALUES);
                    dataSets.get(0).clear();
                    List<Entry> apentry = new ArrayList<>();
                    for (int i = 0; i < MAX_VALUES; i++) {
                        float xvalue = (float) (i * (MainActivity.tansoLayMau / 1000.0));
                        float yvalue = (float) (sinwave[0] * Math.sin(2 * Math.PI * sinwave[1] * xvalue + sinwave[2]));
                        apentry.add(new Entry(xvalue, yvalue));
                    }
                    addEntry(dataSets.get(0), apentry);
                    entryListAp.clear();

                }
            }
        }
    }

    private float RMSCalculate(List<Entry> dataPoint) {
        float yrms = 0;
        double[] xValues = new double[dataPoint.size()];
        double[] yValues = new double[dataPoint.size()];
        for (int i = 0; i < dataPoint.size(); i++) {
            Entry entry = dataPoint.get(i);
            xValues[i] = entry.getX();
            yValues[i] = entry.getY();
        }
        float ymin = (float) yValues[0], ymax = (float) yValues[0];
        for (int i = 0; i < yValues.length; i++) {
            if (ymax < yValues[i]) ymax = (float) yValues[i];
            if (ymin > yValues[i]) ymin = (float) yValues[i];
            yrms += yValues[i] * yValues[i];
        }
        float yPeak = ymax - ymin;
        yrms = (float) Math.sqrt(yrms / yValues.length);
        return yrms;
    }

    private static double findPhase(double[] t, double[] y, double A, double f) {
        double phi = 0;
        double minError = Double.MAX_VALUE;

        for (double testPhi = 0; testPhi < 2 * Math.PI; testPhi += 0.01) {
            double error = 0;
            for (int i = 0; i < t.length; i++) {
                double y_estimated = A * Math.sin(2 * Math.PI * f * t[i] + testPhi);
                error += Math.pow(y[i] - y_estimated, 2);
            }
            if (error < minError) {
                minError = error;
                phi = testPhi;
            }
        }

        return phi;
    }

    private double[] fft(List<Float> value, double sampleTime, int length) {
        double[] sinewave = new double[3];
        DoubleFFT_1D fft = new DoubleFFT_1D(length);
        double[] fftData = new double[2 * length];  // FFT sử dụng mảng phức
        double[] y = new double[length];
        double[] t = new double[length];
        for (int i = 0; i < length; i++) {
            y[i] = (double) value.get(i);
            t[i] = i * sampleTime;
        }
        System.arraycopy(y, 0, fftData, 0, length);
        fft.realForwardFull(fftData);
        double[] freqs = new double[length / 2];
        double[] magnitudes = new double[length / 2];
        for (int i = 0; i < length / 2; i++) {
            double real = fftData[2 * i];
            double imag = fftData[2 * i + 1];
            magnitudes[i] = Math.sqrt(real * real + imag * imag) * 2 / length;
            freqs[i] = i / (length * sampleTime);
        }

        // Xác định tần số và biên độ ước tính
        int maxIndex = 0;
        for (int i = 1; i < magnitudes.length; i++) {
            if (magnitudes[i] > magnitudes[maxIndex]) {
                maxIndex = i;
            }
        }
        double f_estimated = freqs[maxIndex];
        double A_estimated = magnitudes[maxIndex];

        Log.d("data", "Tần số ước tính: " + f_estimated);
        Log.d("data", "Biên độ ước tính: " + A_estimated);

        // Tìm pha bằng cách khớp tín hiệu
        double phi_estimated = findPhase(t, y, A_estimated, f_estimated);
        Log.d("data", "Pha ước tính: " + phi_estimated);
        sinewave[0] = A_estimated;
        sinewave[1] = f_estimated;
        sinewave[2] = phi_estimated;
        return sinewave;
    }


}
