package com.example.sensorcollection.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sensorcollection.MainActivity;
import com.example.sensorcollection.R;
import com.example.sensorcollection.adapter.DulieuDothiAdapter;
import com.example.sensorcollection.model.DulieuDothi;
import com.example.sensorcollection.ultis.Uuid;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
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
import com.github.mikephil.charting.utils.Transformer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentDothi extends Fragment implements FragmentBaseMain.OnDataChangeListener {
    LineChart lineChart;
    ListView listviewCacsodo;
    List<DulieuDothi> dulieuDothis;
    DulieuDothiAdapter dulieuDothiAdapter;
    List<ILineDataSet> dataSets;
    private int kieuPhantich=0;
    FragmentToado fragmentToado;
    FragmentChenhlech fragmentChenhlech;
    FragmentDodoc fragmentDodoc;
    Fragment currentFragment;
    FragmentManager fragmentManager;
    int soDothi=0;
    double slope=1, offset=0;
    float dulieucbcu=0, tgiancu=0;

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
        return inflater.inflate(R.layout.fragment_dothi,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart=(LineChart)view.findViewById(R.id.linechart_fragmentdothi);
        listviewCacsodo=(ListView) view.findViewById(R.id.listview_fragmentdothi);
        dulieuDothis=new ArrayList<>();
        dulieuDothiAdapter=new DulieuDothiAdapter(getContext(),R.layout.dong_listviewcacsodo,dulieuDothis);
        listviewCacsodo.setAdapter(dulieuDothiAdapter);
        dataSets=new ArrayList<>();
        fragmentToado = new FragmentToado();
        fragmentChenhlech = new FragmentChenhlech();
        fragmentDodoc=new FragmentDodoc();
        fragmentManager = getChildFragmentManager();

        FragmentBaseMain fragmentBaseMain=(FragmentBaseMain)getParentFragment();
        if(fragmentBaseMain!=null){
            fragmentBaseMain.setOnDataChangeListener(this);
        }
        khoitaodothi();
        listviewCacsodo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DulieuDothi dulieuDothi= dulieuDothis.get(i);
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
                positiveButton.setTextColor(ContextCompat.getColor(getContext(),R.color.black)); // Thay thế your_color bằng mã màu tùy chọn
                negativeButton.setTextColor(ContextCompat.getColor(getContext(),R.color.black));

                return false;
            }
        });
        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lineChart.onTouchEvent(motionEvent);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if(kieuPhantich==1){
                            float xTouch = motionEvent.getX();
                            float yTouch = motionEvent.getY();
                            MPPointD point = lineChart.getValuesByTouchPoint(xTouch, yTouch, YAxis.AxisDependency.LEFT);
                            float xValue = (float) point.x;
                            LineData data = lineChart.getLineData();
                            List<Entry> toadodiem=new ArrayList<>();
                            for(int i=0;i<dataSets.size();i++){
                                Entry closestEntry = data.getDataSetByIndex(i).getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST);
                                toadodiem.add(new Entry(closestEntry.getX(),closestEntry.getY()));
                                listener.guiGiatriphantich(i,closestEntry.getX(),closestEntry.getY(),null);
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
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();

                            LimitLine gioihan1 = lineChart.getXAxis().getLimitLines().get(0);
                            LimitLine gioihan2 = lineChart.getXAxis().getLimitLines().get(1);

                            for(int i=0;i<dataSets.size();i++){
                                Entry entry1=data.getDataSetByIndex(i).getEntryForXValue(gioihan1.getLimit(), Float.NaN,DataSet.Rounding.CLOSEST);
                                Entry entry2=data.getDataSetByIndex(i).getEntryForXValue(gioihan2.getLimit(),Float.NaN,DataSet.Rounding.CLOSEST);
                                float deltax=Math.abs(entry1.getX()-entry2.getX());
                                float deltay=Math.abs(entry1.getY()-entry2.getY());
                                listener.guiGiatriphantich(i,deltax,deltay,null);
                            }

                        }
                        if(kieuPhantich==3){
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
                            float xmax=Math.max(gioihan1.getLimit(),gioihan2.getLimit());
                            float xmin=Math.min(gioihan1.getLimit(),gioihan2.getLimit());

                            for(int line=0;line<soDothi;line++) {
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
                                double rSquare=regression.getRSquare();
                                listener.guiGiatriphantich(line, (float) slope, (float) intercept, (float)rSquare);
                                float ymin = (float) (xmin * slope + intercept);
                                float ymax = (float) (xmax * slope + intercept);

                                List<Entry> entries = new ArrayList<>();
                                entries.add(new Entry(xmin, ymin)); // Điểm A
                                entries.add(new Entry(xmax, ymax)); // Điểm B

                                LineDataSet bestfitLine = (LineDataSet) data.getDataSetByIndex(soDothi+line);
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
                        break;
                }
                return true;
            }
        });
    }

    public void khoitaodothi(){
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setScaleYEnabled(true);
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
        rightAxis.setEnabled(false);
    }
    private LineDataSet creatSet(String lable,int color){
        LineDataSet set = new LineDataSet(null,lable);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
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
        String tencambien=bundle.getString("tencambien");
        String donvi=bundle.getString("donvi");
        String macambien=bundle.getString("macambien");
        for(int i = 0; i< Uuid.camBiens.size(); i++){
            if(Uuid.camBiens.get(i).getName().equals(tencambien)){
                for(int j=0;j<Uuid.camBiens.get(i).getDonvi().length;j++){
                    if(Uuid.camBiens.get(i).getDonvi()[j].equals(donvi)){
                        slope=Uuid.camBiens.get(i).getHeso()[j][0];
                        offset=Uuid.camBiens.get(i).getHeso()[j][1];
                        break;
                    }
                }
                break;
            }
        }
        for (DulieuDothi dulieudothi:dulieuDothis
             ) {
            if(dulieudothi.getMacambien().equals(macambien)){
                return;
            }
        }
        dataSets.add(creatSet(tencambien,mamaudothi[dulieuDothis.size()]));
        soDothi++;
        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        dulieuDothis.add(new DulieuDothi(mamaudothi[dulieuDothis.size()],tencambien,macambien,donvi));
        dulieuDothiAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDataReceived(Map<String, Float[]> hashmap) {
        for(int i=0;i<dulieuDothis.size();i++){
            String cambien=dulieuDothis.get(i).getMacambien();
            if(hashmap.containsKey(cambien) && hashmap.get(cambien)!=null){
                Float[] dulieu=hashmap.get(cambien);
                for (float x: dulieu
                     ) {
                    addEntry(dataSets.get(i), (float) (slope*x+offset), dataSets.get(i).getEntryCount() * (float)(MainActivity.tansoLayMau /1000.0));
                }
                break;
            }
        }
    }

    @Override
    public void xoaDulieucu() {

        LineData data = lineChart.getLineData();
        data.clearValues(); // Xóa tất cả các DataSet
        lineChart.setData(data);
        for(int i=0;i<dulieuDothis.size();i++){
            dataSets.add(creatSet(dulieuDothis.get(i).getTencambien(),dulieuDothis.get(i).getColor()));
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
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    @Override
    public void xemLaiDulieuCu(Map<String, List<Float>> dulieuCu, Float tanso) {
        for(int i=0;i<dulieuDothis.size();i++){
            String cambien=dulieuDothis.get(i).getMacambien();
            if(dulieuCu.containsKey(cambien) && dulieuCu.get(cambien)!=null){
                List<Float> dulieu=dulieuCu.get(cambien);
                for (Float value:dulieu
                     ) {
                    addEntry(dataSets.get(i), (float) (slope* value+offset),dataSets.get(i).getEntryCount() * (float)(tanso/1000.0) );
                }
                break;
            }
        }
    }

    @Override
    public void phantichDothi(int phantich) {
        kieuPhantich=phantich;
        lineChart.getXAxis().removeAllLimitLines();
        if (phantich==0){
            if (currentFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(currentFragment);
                transaction.commit();
            }

            lineChart.getXAxis().removeAllLimitLines();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
        if(phantich==1) {
            LimitLine limitLine = new LimitLine(2, "");
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
        if (phantich==2){
            LimitLine limitLine1 = new LimitLine(2, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(5, "");
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
        if(phantich==3){
            LimitLine limitLine1 = new LimitLine(2, "");
            limitLine1.setLineColor(Color.BLACK);
            limitLine1.setLineWidth(1f);
            LimitLine limitLine2 = new LimitLine(5, "");
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
    }


    private void addEntry(ILineDataSet dataSet, float yValue, float xValue) {
        LineData data = lineChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(dataSets.indexOf(dataSet));
            if (set == null) {
                set = dataSet;
                data.addDataSet(set);
            }
            data.addEntry(new Entry(xValue, yValue), dataSets.indexOf(dataSet));
            data.notifyDataChanged();
//            lineChart.setVisibleXRangeMaximum(xValue + 5f);
            lineChart.isAutoScaleMinMaxEnabled();
            lineChart.moveViewToX(xValue + 5f);
//            lineChart.setVisibleYRangeMaximum(yValue + 5f, YAxis.AxisDependency.LEFT);
            lineChart.getXAxis().setAxisMaximum(xValue + 5f);
            YAxis leftAxis = lineChart.getAxisLeft();
            float maxY = Math.max(leftAxis.getAxisMaximum(), yValue + 5f); // Lấy giá trị lớn nhất của trục Y
            float minY = Math.min(leftAxis.getAxisMinimum(), yValue - 5f);
            leftAxis.setAxisMinimum(minY);
            leftAxis.setAxisMaximum(maxY);
            lineChart.notifyDataSetChanged();
        }
    }

    private PhantichDothi listener;
    // Khai báo interface
    public interface PhantichDothi {
        void guiGiatriphantich(int position,Float giatri1, Float giatri2, Float giatri3);
    }

    // Cài đặt phương thức của interface để truyền dữ liệu sang Fragment con
    public void setOnPhantichDothi(PhantichDothi listener) {
        this.listener = listener;
    }
}
