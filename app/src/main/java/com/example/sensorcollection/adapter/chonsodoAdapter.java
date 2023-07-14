package com.example.sensorcollection.adapter;

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

import com.clj.fastble.data.BleDevice;
import com.example.sensorcollection.R;
import com.example.sensorcollection.ultis.Uuid;

import java.util.List;

public class chonsodoAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<BleDevice> sodoList;

    public chonsodoAdapter(Context context, int layout, List<BleDevice> sodoList) {
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
    private class ViewHolder{
        TextView txtTen,txtMacambien;
        ImageView  imgIcon;
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
        BleDevice thietbi=sodoList.get(i);
        viewHolder.txtMacambien.setText(thietbi.getName());
        int vitridaugach=thietbi.getDevice().getName().indexOf('-');
        String tenCamBien=thietbi.getDevice().getName().substring(0,vitridaugach);
        for (int vitri = 0; vitri< Uuid.camBiens.size(); vitri++){
            if(Uuid.camBiens.get(vitri).getId().equals(tenCamBien)) {
                viewHolder.imgIcon.setImageResource(Uuid.camBiens.get(vitri).getIcon());
                viewHolder.txtTen.setText(Uuid.camBiens.get(vitri).getName());
                viewHolder.btnChondonvi.setText(Uuid.camBiens.get(vitri).getDonvi()[0]);
                break;
            }
        }
        viewHolder.btnChondonvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                for (int vitri = 0; vitri< Uuid.camBiens.size(); vitri++){
                    if(Uuid.camBiens.get(vitri).getId().equals(tenCamBien)) {
                        for (String donvi:Uuid.camBiens.get(vitri).getDonvi()
                             ) {
                            popupMenu.getMenu().add(donvi);
                        }
                        break;
                    }
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
