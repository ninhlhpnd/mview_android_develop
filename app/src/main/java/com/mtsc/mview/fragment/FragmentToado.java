package com.mtsc.mview.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mtsc.mview.R;
import com.mtsc.mview.adapter.DulieuPhantichAdapter;
import com.mtsc.mview.model.DulieuDothi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentToado extends Fragment implements FragmentDothi.PhantichDothi {
    private View layout;
    ListView lvToado;
    List<DulieuDothi> dulieuDothiList;
    TextView txtPhantich;
    DulieuPhantichAdapter dulieuPhantichAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_phantich,container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvToado=(ListView) view.findViewById(R.id.listview_fragmentPhantich);
        dulieuDothiList=new ArrayList<>();
        txtPhantich=(TextView )view.findViewById(R.id.textview_fragmentPhantich);
        txtPhantich.setText("Tọa độ");
        Bundle bundle = getArguments();
        if (bundle != null) {
            dulieuDothiList = bundle.getParcelableArrayList("sodo");
            // Sử dụng danh sách myList ở đây
        }
        FragmentDothi fragmentDothi= (FragmentDothi) getParentFragment();
        if(fragmentDothi!=null){
            fragmentDothi.setOnPhantichDothi(this);
        }
        dulieuPhantichAdapter=new DulieuPhantichAdapter(getContext(),R.layout.dong_listviewphantichtoado,dulieuDothiList);
        lvToado.setAdapter(dulieuPhantichAdapter);

    }

    @Override
    public void guiGiatriphantich(int position,Float giatri1, Float giatri2, Float giatri3) {
        View view = lvToado.getChildAt(position);
        TextView txtGiatri1 = (TextView) view.findViewById(R.id.txtGiatri1_donglvphantichtoado);
        TextView txtGiatri2 = (TextView) view.findViewById(R.id.txtGiatri2_donglvphantichtoado);
        TextView txtGiatri3 = (TextView) view.findViewById(R.id.txtGiatri3_donglvphantichtoado);

        String donvi=dulieuDothiList.get(position).getDonvi();
        DecimalFormat df1=new DecimalFormat("#.0");
        DecimalFormat df2=new DecimalFormat("#.00");
        DecimalFormat df3=new DecimalFormat("#.000");

        df1.setMinimumIntegerDigits(1);
        df2.setMinimumIntegerDigits(1);
        df3.setMinimumIntegerDigits(1);
        if(dulieuDothiList.get(position).getTencambien()=="Vị trí"){
            txtGiatri1.setText("x=" + df2.format(giatri1).replace(',', '.') + " s");
            txtGiatri2.setText("y=" + df1.format(giatri2).replace(',', '.') + " " + donvi);
        }else if(dulieuDothiList.get(position).getTencambien()=="Dòng điện"){
            txtGiatri1.setText("x=" + df3.format(giatri1).replace(',', '.') + " s");
            txtGiatri2.setText("y=" + df3.format(giatri2).replace(',', '.') + " "+donvi);
        }else{
            txtGiatri1.setText("x=" + df3.format(giatri1).replace(',', '.') + " s");
            txtGiatri2.setText("y=" + df2.format(giatri2).replace(',', '.') + " "+ donvi);
        }
        txtGiatri3.setVisibility(View.INVISIBLE);
    }
}
