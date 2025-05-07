package com.mtsc.mview.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.MovingAverageFilter;
import com.mtsc.mview.ultis.Uuid;

import org.apache.commons.math3.filter.KalmanFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentChuso extends Fragment implements FragmentBaseMain.OnDataChangeListener{
    TextView txtGiatri, txtTencambien;
    String tencambien,donvi,macambien;
    Button btnTare;
    List<Float> mangValue;
    double slope=1, offset=0;
    MovingAverageFilter dataProcessor;
    BleDevice bleDevice;
    float valueSlow=0;
    float rmsFast=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chuso,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentBaseMain fragmentBaseMain=(FragmentBaseMain)getParentFragment();
        if(fragmentBaseMain!=null){
            fragmentBaseMain.setOnDataChangeListener(this);
        }
        txtGiatri=(TextView) view.findViewById(R.id.textviewGiatri_fragmentchuso);
        txtTencambien=(TextView) view.findViewById(R.id.textviewTenCambien_fragmentChuso);
        btnTare=(Button) view.findViewById(R.id.buttonTare_fragmentchuso);
        mangValue=new ArrayList<>();
        dataProcessor=new MovingAverageFilter(10);
        btnTare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.tansoLayMau>=60){
                    offset=offset-valueSlow;
                }else{
                    offset=offset-rmsFast;
                    Log.d("offset", String.valueOf(offset));
                }

            }
        });
    }

    @Override
    public void chonCamBien(Bundle bundle) {
        tencambien=bundle.getString("tencambien");
        donvi=bundle.getString("donvi");
        macambien=bundle.getString("macambien");
        txtGiatri.setText("0.00 " + donvi);
        txtTencambien.setText(tencambien + " (" + donvi + ")");
//        bleDevice=(BleDevice) bundle.getParcelable("device");
//        Log.d("cambien",bleDevice.getName());
        for(int i=0;i< Uuid.camBiens.size();i++){
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
    }

    @Override
    public void onDataReceived(DulieuCB dulieuCB) {
        if (dulieuCB.getMacambien().equals(macambien) && dulieuCB.getTencambien().equals(tencambien)
                && dulieuCB.getGiatricambien()!=null) {
            if(MainActivity.tansoLayMau>=60) {
                List<Float> dulieu = dulieuCB.getGiatricambien();
                valueSlow = (float) (slope * dulieu.get(dulieu.size() - 1) + offset);
                String[] parts = macambien.split("-");
                DecimalFormat df;
                if (tencambien.equals("Dòng điện") || tencambien.equals("Điện áp") || tencambien.equals("pH")) {
                    df = new DecimalFormat("#.000");
                } else {
                    df = new DecimalFormat("#.00");
                }
                df.setMinimumIntegerDigits(1);
                String dfdulieu = df.format(valueSlow).replace(',', '.');
                txtGiatri.setText(dfdulieu + " " + donvi);
            }else{
                List<Float> dulieu = dulieuCB.getGiatricambien();
                for (float x:dulieu
                     ) {
                    float value=(float) (slope*x+offset);
                    mangValue.add(value);
//                    Log.d("size", String.valueOf(mangValue.size()));
                }
                if(mangValue.size()>=200){
                    float tongRms=0;
                    for(int i=0;i<mangValue.size();i++){
                        tongRms+=(mangValue.get(i))*(mangValue.get(i));
                    }
                    rmsFast= (float) ((float) Math.sqrt(tongRms/mangValue.size()) + offset);
                    rmsFast= (float) dataProcessor.filter(rmsFast);
                    String[] parts = macambien.split("-");
                    DecimalFormat df;
                    if (parts[0].equals("Current")) {
                        df = new DecimalFormat("#.000");
                    } else {
                        df = new DecimalFormat("#.00");
                    }
                    df.setMinimumIntegerDigits(1);
                    String dfdulieu = df.format(rmsFast).replace(',', '.');
                    txtGiatri.setText(dfdulieu + " " + donvi);
                    mangValue.clear();
                }
            }
        }
    }

    @Override
    public void xoaDulieucu() {

    }

    @Override
    public void xemLaiDulieuCu(Map<String, List<Float>> dulieuCu,Float tanso) {

    }

    @Override
    public void phantichDothi(int phantich) {

    }

}
