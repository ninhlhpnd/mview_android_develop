package com.example.sensorcollection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sensorcollection.R;
import com.example.sensorcollection.model.DulieuDothi;

import java.util.List;

public class DulieuPhantichAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<DulieuDothi> dulieuDothiList;

    public DulieuPhantichAdapter(Context context, int layout, List<DulieuDothi> dulieuDothiList) {
        this.context = context;
        this.layout = layout;
        this.dulieuDothiList = dulieuDothiList;
    }

    @Override
    public int getCount() {
        return dulieuDothiList.size();
    }

    @Override
    public Object getItem(int i) {
        return dulieuDothiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtTen;
        View viewMau;
        TextView txtGiatri1;
        TextView txtGiatri2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtTen = (TextView) view.findViewById(R.id.textview_donglvphantichtoado);
            viewHolder.viewMau = (View) view.findViewById(R.id.view_donglvphantichtoado);
            viewHolder.txtGiatri1=(TextView) view.findViewById(R.id.txtGiatri1_donglvphantichtoado);
            viewHolder.txtGiatri2=(TextView) view.findViewById(R.id.txtGiatri2_donglvphantichtoado);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DulieuDothi dulieuDothi=dulieuDothiList.get(i);
        viewHolder.txtTen.setText(dulieuDothi.getTencambien() + "(" + dulieuDothi.getDonvi() + ")");
        viewHolder.viewMau.setBackgroundColor(dulieuDothi.getColor());

        return view;
    }

}
