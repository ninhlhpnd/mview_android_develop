package com.mtsc.mview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mtsc.mview.R;
import com.mtsc.mview.model.DulieuNhapbangtay;

import java.util.List;

public class NhapBangTayAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<DulieuNhapbangtay> nhapbangtayList;
    private int selectedItem = -1;
    boolean isUpdate=false;
    public NhapBangTayAdapter(Context context, int layout, List<DulieuNhapbangtay> nhapbangtayList) {
        this.context = context;
        this.layout = layout;
        this.nhapbangtayList = nhapbangtayList;
    }

    @Override
    public int getCount() {
        return nhapbangtayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtDulieux, txtDulieuy;
        LinearLayout linearLayout;
    }
    private class OldValues {
        String XValue;
        String YValue;
        int OldPosition;
    }
    OldValues oldValues;
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtDulieux = (TextView) view.findViewById(R.id.textviewDulieux_dongNhapbangtay);
            viewHolder.txtDulieuy = (TextView) view.findViewById(R.id.textviewDulieuy_dongNhapbangtay);
            viewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_dongnhapbangtay);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        oldValues=new OldValues();
        DulieuNhapbangtay dulieuNhapbangtay = nhapbangtayList.get(i);
        viewHolder.txtDulieux.setText(String.valueOf(dulieuNhapbangtay.getDulieux()));
        viewHolder.txtDulieuy.setText(String.valueOf(dulieuNhapbangtay.getDulieuy()));
        if (i == selectedItem) {
            viewHolder.txtDulieux.setBackgroundColor(Color.WHITE);
            viewHolder.txtDulieuy.setBackgroundColor(Color.WHITE);

        } else {
            int xamColor = ContextCompat.getColor(view.getContext(), R.color.xam);
            viewHolder.txtDulieux.setBackgroundColor(xamColor);
            viewHolder.txtDulieuy.setBackgroundColor(xamColor);
        }
        return view;
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
//        if(isUpdate){
//            oldValues.XValue=nhapbangtayList.get(position).getDulieux();
//            oldValues.YValue=nhapbangtayList.get(position).getDulieuy();
//            oldValues.OldPosition=position;
//        }else{
//            nhapbangtayList.get(oldValues.OldPosition).setDulieuy(oldValues.XValue);
//            nhapbangtayList.get(oldValues.OldPosition).setDulieuy(oldValues.YValue);
//        }
        notifyDataSetChanged();
    }
    public void isUpdated(){
        isUpdate=true;
    }
}
