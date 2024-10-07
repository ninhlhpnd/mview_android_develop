package com.mtsc.mview.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mtsc.mview.R;

public class FragmentKieuDocDulieu extends Fragment {
    private View layout;
    Button btnAuto, btnManual, btnMachXC, btnSongam;
    private FragmentManager fragmentManager;
    public FragmentKieuDocDulieu(FragmentManager fragmentManager) {
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
        layout = inflater.inflate(R.layout.fragment_kieudocdulieu,container, false);
        return layout;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAuto=(Button) view.findViewById(R.id.buttonTudong_fragmentkieudoc);
        btnManual=(Button) view.findViewById(R.id.buttonBangtay_fragmentkieudoc);
        btnMachXC=(Button) view.findViewById(R.id.buttonMachxoaychieu_fragmentkieudoc);
        btnSongam=(Button) view.findViewById(R.id.buttonSongam_fragmentkieudoc);
        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentBocuc1 fragmentBocuc1=new FragmentBocuc1(fragmentManager);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layoutFragment_main,fragmentBocuc1).commit();
            }
        });
        btnManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNhapbangtay fragmentNhapbangtay=new FragmentNhapbangtay(fragmentManager);
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layoutFragment_main,fragmentNhapbangtay).commit();
            }
        });
        btnMachXC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentMachDienXC fragmentMachDienXC=new FragmentMachDienXC(fragmentManager);
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layoutFragment_main,fragmentMachDienXC).commit();
            }
        });
        btnSongam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentSongam fragmentSongam=new FragmentSongam(fragmentManager);
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.layoutFragment_main,fragmentSongam).commit();
            }
        });
    }


}
