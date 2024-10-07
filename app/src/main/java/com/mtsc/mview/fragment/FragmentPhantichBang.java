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

public class FragmentPhantichBang extends Fragment implements FragmentBangsolieu   .PhantichBang {
    private View layout;
    ListView lvToado;
    List<DulieuDothi> dulieuBangList;
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
        txtPhantich=(TextView) view.findViewById(R.id.textview_fragmentPhantich);
        txtPhantich.setText("Min-Max");
        dulieuBangList=new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            dulieuBangList = bundle.getParcelableArrayList("sodo");
            // Sử dụng danh sách myList ở đây
        }
        FragmentBangsolieu fragmentBangsolieu= (FragmentBangsolieu) getParentFragment();
        if(fragmentBangsolieu!=null){
            fragmentBangsolieu.setOnPhanTichBang(this);
        }
        dulieuPhantichAdapter=new DulieuPhantichAdapter(getContext(),R.layout.dong_listviewphantichtoado,dulieuBangList);
        lvToado.setAdapter(dulieuPhantichAdapter);
    }

    @Override
    public void guiGiatriphantich(int position, Float giatri1, Float giatri2, Float giatri3) {
        View view = lvToado.getChildAt(position);
        TextView txtGiatri1 = (TextView) view.findViewById(R.id.txtGiatri1_donglvphantichtoado);
        TextView txtGiatri2 = (TextView) view.findViewById(R.id.txtGiatri2_donglvphantichtoado);
        TextView txtGiatri3 = (TextView) view.findViewById(R.id.txtGiatri3_donglvphantichtoado);
        DecimalFormat df=new DecimalFormat("#.00");
        df.setMinimumIntegerDigits(1);
        txtGiatri1.setText("min=" + df.format(giatri1).replace(',', '.'));
        txtGiatri2.setText("max=" + df.format(giatri2).replace(',', '.'));
        txtGiatri3.setText("aver=" + df.format(giatri3).replace(',', '.'));
    }
}
