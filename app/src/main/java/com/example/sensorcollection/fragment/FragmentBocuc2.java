package com.example.sensorcollection.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sensorcollection.R;

public class FragmentBocuc2 extends Fragment {
    private LinearLayout linearLayout1, linearLayout2;
    private FragmentBaseMain fragmentBaseMain1, fragmentBaseMain2;
    private FragmentManager fragmentManager;

    public FragmentBocuc2(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bocuc_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout1 = view.findViewById(R.id.linearlayout1_bocuc2);
        LinearLayout linearLayout2 = view.findViewById(R.id.linearlayout2_bocuc2);

//        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
//        fragmentBaseMain1=new FragmentBaseMain();
//        fragmentBaseMain2=new FragmentBaseMain();
//
//        linearLayout1.addView(fragmentBaseMain1.onCreateView(layoutInflater,linearLayout1,savedInstanceState));
//        linearLayout2.addView(fragmentBaseMain2.onCreateView(layoutInflater,linearLayout2,savedInstanceState));
//
//        fragmentBaseMain1.onViewCreated(linearLayout1,savedInstanceState);
//        fragmentBaseMain2.onViewCreated(linearLayout2,savedInstanceState);
//
////        fragmentBaseMain1.khoitaodothi();
////        fragmentBaseMain2.khoitaodothi();
//
//        ImageView imgviewChonHienThi1= fragmentBaseMain1.getImageViewChonKieuHienThi();
//        FrameLayout frmKieuhienthi1=fragmentBaseMain1.getFrameLayoutKieuhienthi();
//        imgviewChonHienThi1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fragmentBaseMain1.taoMenuKieuhienthi(layoutInflater,fragmentManager,frmKieuhienthi1.getId());
//            }
//        });
//        ImageView imgviewChonHienThi2= fragmentBaseMain2.getImageViewChonKieuHienThi();
//        FrameLayout frmKieuhienthi2=fragmentBaseMain2.getFrameLayoutKieuhienthi();
//        imgviewChonHienThi2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                fragmentBaseMain2.taoMenuKieuhienthi(layoutInflater,fragmentManager,frmKieuhienthi2.getId());
//            }
//        });
        fragmentBaseMain1=new FragmentBaseMain();
        FragmentTransaction transaction1=getChildFragmentManager().beginTransaction();
        transaction1.replace(R.id.linearlayout1_bocuc2,fragmentBaseMain1);
        transaction1.commit();

        fragmentBaseMain2=new FragmentBaseMain();
        FragmentTransaction transaction2=getChildFragmentManager().beginTransaction();
        transaction2.replace(R.id.linearlayout2_bocuc2,fragmentBaseMain2);
        transaction2.commit();
    }


}
