package com.mtsc.mview.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mtsc.mview.R;

public class FragmentBocuc3 extends Fragment {
    private FragmentBaseMain fragmentBaseMain1, fragmentBaseMain2;
    private FragmentManager fragmentManager;
    public FragmentBocuc3(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bocuc_3,container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout linearLayout1 = view.findViewById(R.id.linearlayout1_bocuc3);
        LinearLayout linearLayout2 = view.findViewById(R.id.linearlayout2_bocuc3);

        fragmentBaseMain1=new FragmentBaseMain();
        FragmentTransaction transaction1=getChildFragmentManager().beginTransaction();
        transaction1.replace(R.id.linearlayout1_bocuc3,fragmentBaseMain1);
        transaction1.commit();

        fragmentBaseMain2=new FragmentBaseMain();
        FragmentTransaction transaction2=getChildFragmentManager().beginTransaction();
        transaction2.replace(R.id.linearlayout2_bocuc3,fragmentBaseMain2);
        transaction2.commit();
    }
}
