package com.mtsc.mview.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
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

public class FragmentSongam extends Fragment {
    private View layout;
    private FragmentManager fragmentManager;
    LineChart lineChart;
    List<Float> mangSongam;
    List<ILineDataSet> dataSets;
    TextView txtBiendo, txtTanso, txtDoto;
    LinearLayout layoutToado;
    DecimalFormat df2, df3, df1;
    Spinner spinner;
    int MAX_VALUES = 1000;
    boolean isTansoSongam = true;
    SeekBar seekBar;

    public FragmentSongam(FragmentManager fragmentManager) {
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
        layout = inflater.inflate(R.layout.fragment_songam, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
        khoitaodothi();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                if (selectedItem.equals("Tần số sóng âm")) {
                    isTansoSongam = true;
                    dataSets.get(0).clear();
                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setAxisMaximum(4f);
                    leftAxis.setAxisMinimum(0f);
                    XAxis x1 = lineChart.getXAxis();
                    x1.setAxisMaximum(0.2f);
                    x1.setSpaceMin(0.001f);
                } else if (selectedItem.equals("Tốc độ truyền âm")) {
                    isTansoSongam = false;
                    dataSets.get(0).clear();
                    YAxis leftAxis = lineChart.getAxisLeft();
                    leftAxis.setAxisMaximum(100f);
                    leftAxis.setAxisMinimum(30f);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float[] range = {0.2f, 0.1f, 0.05f, 0.02f, 0.01f};
                if (isTansoSongam) {
                    lineChart.getXAxis().setAxisMaximum(range[progress - 1]);
                    lineChart.notifyDataSetChanged();
                    lineChart.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void anhXa(View view) {
        lineChart = (LineChart) view.findViewById(R.id.linechart_fragmentsongam);
        txtBiendo = (TextView) view.findViewById(R.id.textviewBiendo_fragmentsongam);
        txtTanso = (TextView) view.findViewById(R.id.textviewTanso_fragmentsongam);
        txtDoto = (TextView) view.findViewById(R.id.textviewDoto_fragmentsongam);
        spinner = view.findViewById(R.id.spinner_fragmentsongam);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.songamLession, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        layoutToado = (LinearLayout) view.findViewById(R.id.layoutToado_fragmentsongam);
        seekBar = (SeekBar) view.findViewById(R.id.seekbarAmthanh_fragmentsongam);
        dataSets = new ArrayList<>();
        mangSongam = new ArrayList<>();
        df2 = new DecimalFormat("#.00");
        df3 = new DecimalFormat("#.000");
        df1 = new DecimalFormat("#.0");

        df2.setMinimumIntegerDigits(1);
        df3.setMinimumIntegerDigits(1);
        df1.setMinimumIntegerDigits(1);

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
        x1.setAxisMaximum(0.2f);
        x1.setSpaceMin(0.001f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(4f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        dataSets.add(creatSet("Sóng âm", Color.RED, YAxis.AxisDependency.LEFT));
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
            lineChart.isAutoScaleMinMaxEnabled();
            float xValueMax = entries.get(entries.size() - 1).getX();
//            lineChart.setVisibleXRangeMaximum(xValueMax + 1f);
            lineChart.moveViewToX(xValueMax);
//            lineChart.getXAxis().setAxisMaximum(xValueMax);
//            lineChart.setVisibleYRangeMaximum(yValue + 5f, YAxis.AxisDependency.LEFT);
            YAxis leftAxis = lineChart.getAxisLeft();
            float maxY = Math.max(leftAxis.getAxisMaximum(), getMaxYValue(entries) + 1f); // Lấy giá trị lớn nhất của trục Y
            float minY = Math.min(leftAxis.getAxisMinimum(), getMinYValue(entries) - 1f);
            leftAxis.setAxisMinimum(minY);
            leftAxis.setAxisMaximum(maxY);
            if (!isTansoSongam) {
                lineChart.getXAxis().setAxisMaximum(xValueMax + 1f);
            }
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
            mangSongam.clear();
            List<Float> value = dulieuCB.getGiatricambien();
            dataSets.get(0).clear();
            if (isTansoSongam) {
//                lineChart.getXAxis().setAxisMaximum(value.get(1) / 1000.0f * MAX_VALUES);
            } else {
                lineChart.getXAxis().setAxisMaximum(10f);
            }

        } else {

            if (dulieuCB.getTencambien().equals("Tần số")) {
                List<Float> value = dulieuCB.getGiatricambien();
                float tanso = value.get(0);
                float maxvalue = value.get(1);
                float minvalue = value.get(2);
                float zerovalue = (minvalue + maxvalue) / 2;
                float biendo = (maxvalue - minvalue) / 2;
                float phase = (float) ((-1 + Math.random() * 2) * 2 * Math.PI);
                double A_min = 0.0;
                double A_max = 1.65;
                double min_dB = 40.0;
                double max_dB = 100.0;
                int decibel = (int) convertAmplitudeToDecibel(biendo, A_min, A_max, min_dB, max_dB);

                float step = lineChart.getXAxis().getAxisMaximum() / MAX_VALUES;
                if (isTansoSongam) {
                    dataSets.get(0).clear();
                    List<Entry> apentry = new ArrayList<>();
                    for (int i = 0; i < MAX_VALUES; i++) {
                        float xvalue = (i * step);
                        float yvalue = (float) (biendo * Math.sin(2 * Math.PI * tanso * xvalue + phase) + zerovalue);
                        apentry.add(new Entry(xvalue, yvalue));
                    }
                    addEntry(dataSets.get(0), apentry);
                }
                else {
                    List<Entry> apentry = new ArrayList<>();
                    float xValue = (float) dataSets.get(0).getEntryCount() * 0.1f;
                    float yValue = (float) decibel;
                    apentry.add(new Entry(xValue, yValue));
                    addEntry(dataSets.get(0), apentry);
                }
                txtTanso.setText("Tần số: " + df2.format(tanso) + "Hz");
                txtBiendo.setText("Biên độ: " + df2.format(biendo) + "V");
                txtDoto.setText("Độ to: " + decibel + "dB");
//                for (float yvalue : value
//                ) {
//                    mangSongam.add(yvalue);
//                }
//                if (mangSongam.size() >= MAX_VALUES) {
//                    double[] sinwave = fft(mangSongam, MainActivity.tansoLayMau / 1000.0, MAX_VALUES);
//                    if (isTansoSongam) {
//                        dataSets.get(0).clear();
//                        List<Entry> apentry = new ArrayList<>();
//                        for (int i = 0; i < MAX_VALUES; i++) {
//                            float xvalue = (float) (i * MainActivity.tansoLayMau / 1000.0);
//                            float yvalue = (float) mangSongam.get(i);
//                            apentry.add(new Entry(xvalue, yvalue));
//                        }
//                        addEntry(dataSets.get(0), apentry);
//                    } else {
//                        float xvalue = (float) dataSets.get(0).getEntryCount() * 0.1f;
//                        float yvalue = (float) sinwave[2];
//                        List<Entry> entry = new ArrayList<>();
//                        entry.add(new Entry(xvalue, yvalue));
//                        addEntry(dataSets.get(0), entry);
//                    }
//
//
//                    mangSongam.clear();
//                }
            }

        }
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
        double mean = 0.0;
        for (int i = 0; i < length; i++) {
            mean += value.get(i);
        }
        mean /= length;

        // Trừ giá trị trung bình để loại bỏ thành phần DC
        for (int i = 0; i < length; i++) {
            y[i] = (double) value.get(i) - mean;
            t[i] = i * sampleTime;
        }
//        for (int i = 0; i < length; i++) {
//            y[i] = (double) value.get(i);
//            t[i] = i * sampleTime;
//        }
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
        double A_min = 0.0;
        double A_max = 1.95;
        double min_dB = 40.0;
        double max_dB = 100.0;
        double decibel = (int) convertAmplitudeToDecibel(A_estimated, A_min, A_max, min_dB, max_dB);
        // Tìm pha bằng cách khớp tín hiệu
//        double phi_estimated = findPhase(t, y, A_estimated, f_estimated);
//        Log.d("songam", "Pha ước tính: " + phi_estimated);
//        Log.d("songam", "Tần số ước tính: " + f_estimated);
//        Log.d("songam", "Biên độ ước tính: " + A_estimated);
        txtTanso.setText("Tần số: " + df2.format(f_estimated) + "Hz");
        txtBiendo.setText("Biên độ: " + df2.format(A_estimated) + "V");
        txtDoto.setText("Độ to: " + (int) decibel + "dB");
        sinewave[0] = A_estimated;
        sinewave[1] = f_estimated;
        sinewave[2] = decibel;
        return sinewave;
    }

    public double convertAmplitudeToDecibel(double amplitude, double minAmplitude, double maxAmplitude, double minDecibel, double maxDecibel) {
        return minDecibel + (amplitude - minAmplitude) / (maxAmplitude - minAmplitude) * (maxDecibel - minDecibel);
    }
}
