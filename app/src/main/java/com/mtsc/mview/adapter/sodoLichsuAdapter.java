package com.mtsc.mview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import com.mtsc.mview.R;
import com.mtsc.mview.model.Run;
import com.mtsc.mview.model.SensorData;
import com.mtsc.mview.model.SodoCambien;

import java.util.List;

public class sodoLichsuAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<SensorData> sensorData;

    public sodoLichsuAdapter(Context context, int layout, List<SensorData> sensorData) {
        this.context = context;
        this.layout = layout;
        this.sensorData = sensorData;
    }

    @Override
    public int getCount() {
        return sensorData.size();
    }

    @Override
    public Object getItem(int i) {
        return sensorData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView txtTen, txtMacambien;
        ImageView imgIcon;
        Button btnChondonvi;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.txtTen = (TextView) view.findViewById(R.id.textviewTen_chonsodo);
            viewHolder.txtMacambien = (TextView) view.findViewById(R.id.textviewMa_chonsodo);
            viewHolder.imgIcon = (ImageView) view.findViewById(R.id.imageviewIcon_chonsodo);
            viewHolder.btnChondonvi = (Button) view.findViewById(R.id.buttonDonvi_chonsodo);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SensorData thietbi = sensorData.get(i);
        viewHolder.txtMacambien.setText(thietbi.getSensorName());
        viewHolder.imgIcon.setImageResource(thietbi.getIcon());
        viewHolder.txtTen.setText(thietbi.getSodo().get("tencb")[0]);
        viewHolder.btnChondonvi.setText(thietbi.getSodo().get("donvi")[0]);


        viewHolder.btnChondonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);

                for (String donvi : thietbi.getSodo().get("donvi")
                ) {
                    popupMenu.getMenu().add(donvi);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        viewHolder.btnChondonvi.setText(item.getTitle());
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        return view;
    }
}
