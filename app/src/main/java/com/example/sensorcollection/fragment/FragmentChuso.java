package com.example.sensorcollection.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sensorcollection.R;
import com.example.sensorcollection.ultis.DataEvent;
import com.example.sensorcollection.ultis.Uuid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class FragmentChuso extends Fragment implements FragmentBaseMain.OnDataChangeListener{
    TextView txtGiatri, txtTencambien;
    String tencambien,donvi,macambien;
    double slope=1, offset=0;
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
    }

    @Override
    public void chonCamBien(Bundle bundle) {
        tencambien=bundle.getString("tencambien");
        donvi=bundle.getString("donvi");
        macambien=bundle.getString("macambien");
        txtGiatri.setText("0.00 " + donvi);
        txtTencambien.setText(tencambien + " (" + donvi + ")");
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
    public void onDataReceived(Map<String, Float[]> mapDulieu) {
        if (mapDulieu.containsKey(macambien) && mapDulieu.get(macambien)!=null) {
            Float[] dulieu = mapDulieu.get(macambien);
            float value= (float) (slope*dulieu[dulieu.length-1] + offset);
            DecimalFormat df = new DecimalFormat("#.00");
            df.setMinimumIntegerDigits(1);
            String dfdulieu = df.format(value).replace(',', '.');
            txtGiatri.setText(dfdulieu + " " + donvi);
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
