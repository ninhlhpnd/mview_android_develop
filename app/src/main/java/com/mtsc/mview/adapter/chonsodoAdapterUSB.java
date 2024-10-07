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
import com.mtsc.mview.model.CamBienUSB;
import com.mtsc.mview.model.SodoCambien;

import java.util.List;

public class chonsodoAdapterUSB extends BaseAdapter {
    private Context context;
    private int layout;
    private List<CamBienUSB> sodoList;

    public chonsodoAdapterUSB(Context context, int layout, List<CamBienUSB> sodoList) {
        this.context = context;
        this.layout = layout;
        this.sodoList = sodoList;
    }

    @Override
    public int getCount() {
        return sodoList.size();
    }

    @Override
    public Object getItem(int i) {
        return sodoList.get(i);
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
        CamBienUSB thietbi = sodoList.get(i);
        viewHolder.txtMacambien.setText(thietbi.getCamBien().getId() + "-" + thietbi.getId());
        viewHolder.imgIcon.setImageResource(thietbi.getCamBien().getIcon());
        viewHolder.txtTen.setText(thietbi.getCamBien().getName());
        viewHolder.btnChondonvi.setText(thietbi.getCamBien().getDonvi()[0]);


        viewHolder.btnChondonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);

                for (String donvi : thietbi.getCamBien().getDonvi()
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
