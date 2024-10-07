package com.mtsc.mview.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.clj.fastble.data.BleDevice;
import com.github.anastr.speedviewlib.SpeedView;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.MovingAverageFilter;
import com.mtsc.mview.ultis.Uuid;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentGaugeview extends Fragment implements FragmentBaseMain.OnDataChangeListener {
    SpeedView speedView;
    TextView txtTencambien;
    String tencambien, donvi, macambien;
    List<Float> mangValue;
    double slope = 1, offset = 0;
    MovingAverageFilter dataProcessor;
    BleDevice bleDevice;
    float valueSlow = 0;
    float rmsFast = 0;
    double[] daiDo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gaugeview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentBaseMain fragmentBaseMain = (FragmentBaseMain) getParentFragment();
        if (fragmentBaseMain != null) {
            fragmentBaseMain.setOnDataChangeListener(this);
        }
        speedView = (SpeedView) view.findViewById(R.id.halfgauge);
        speedView.setSpeedAt(0);
        daiDo = new double[2];
        txtTencambien = (TextView) view.findViewById(R.id.textviewTenCambien_fragmentGauge);

    }

    @Override
    public void chonCamBien(Bundle bundle) {
        tencambien = bundle.getString("tencambien");
        donvi = bundle.getString("donvi");
        macambien = bundle.getString("macambien");
        txtTencambien.setText(tencambien + " (" + donvi + ")");
        daiDo = bundle.getDoubleArray("daido");
        speedView.setUnit(donvi);
        speedView.setMinSpeed((float) daiDo[0]);
        speedView.setMaxSpeed((float) daiDo[1]);
//        bleDevice=(BleDevice) bundle.getParcelable("device");
//        Log.d("cambien",bleDevice.getName());
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
    }

    @Override
    public void onDataReceived(DulieuCB dulieuCB) {
        if (dulieuCB.getMacambien().equals(macambien) && dulieuCB.getTencambien().equals(tencambien)
                && dulieuCB.getGiatricambien() != null) {
            if (MainActivity.tansoLayMau >= 60) {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                valueSlow = (float) (slope * dulieu.get(dulieu.size() - 1) + offset);
                String[] parts = macambien.split("-");
                if (tencambien.equals("Dòng điện")) {
                    valueSlow = (float) (Math.round(valueSlow * 1000) / 1000.0);
                } else {
                    valueSlow = (float) (Math.round(valueSlow * 100) / 100.0);
                }
                speedView.speedTo(valueSlow);
            } else {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                for (float x : dulieu
                ) {
                    float value = (float) (slope * x + offset);
                    mangValue.add(value);
//                    Log.d("size", String.valueOf(mangValue.size()));
                }
                if (mangValue.size() >= 200) {
                    float tongRms = 0;
                    for (int i = 0; i < mangValue.size(); i++) {
                        tongRms += (mangValue.get(i)) * (mangValue.get(i));
                    }
                    rmsFast = (float) ((float) Math.sqrt(tongRms / mangValue.size()) + offset);
                    rmsFast = (float) dataProcessor.filter(rmsFast);
                    String[] parts = macambien.split("-");
                    if (tencambien.equals("Dòng điện")) {
                        valueSlow = (float) (Math.round(valueSlow * 1000) / 1000.0);
                    } else {
                        valueSlow = (float) (Math.round(valueSlow * 100) / 100.0);
                    }
                    speedView.speedTo(rmsFast);
                    mangValue.clear();
                }
            }
        }
    }

    @Override
    public void xoaDulieucu() {

    }

    @Override
    public void xemLaiDulieuCu(Map<String, List<Float>> dulieuCu, Float tanso) {

    }

    @Override
    public void phantichDothi(int phantich) {

    }
}
