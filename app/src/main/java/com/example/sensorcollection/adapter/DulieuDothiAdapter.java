package com.example.sensorcollection.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensorcollection.R;
import com.example.sensorcollection.model.DulieuDothi;

import java.util.List;

public class DulieuDothiAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<DulieuDothi> dulieuDothiList;

    public DulieuDothiAdapter(Context context, int layout, List<DulieuDothi> dulieuDothiList) {
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
        TextView txtTen,txtMa;
        View viewMau;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtTen = (TextView) view.findViewById(R.id.textview_dongCacsodo);
            viewHolder.viewMau = (View) view.findViewById(R.id.view_donglistviewcacsodo);
            viewHolder.txtMa=(TextView) view.findViewById(R.id.textviewMa_dongCacsodo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DulieuDothi dulieuDothi=dulieuDothiList.get(i);
        viewHolder.txtTen.setText(dulieuDothi.getTencambien() + "(" + dulieuDothi.getDonvi() + ")");
        viewHolder.txtMa.setText(dulieuDothi.getMacambien());
        viewHolder.viewMau.setBackgroundColor(dulieuDothi.getColor());
        return view;
    }
}
