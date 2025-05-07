package com.mtsc.mview.fragment;

import android.content.Context;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.clj.fastble.data.BleDevice;
import com.mtsc.mview.MainActivity;
import com.mtsc.mview.R;
import com.mtsc.mview.adapter.chonsodoAdapter;
import com.mtsc.mview.adapter.chonsodoAdapterUSB;
import com.mtsc.mview.model.CamBien;
import com.mtsc.mview.model.DulieuCB;
import com.mtsc.mview.model.DulieuCacCamBien;
import com.mtsc.mview.ultis.DataEvent;
import com.mtsc.mview.ultis.Uuid;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragmentBaseMain extends Fragment {
    private View layout;
    private ImageView imgChonhienthi, imgChonsodo, imgSolanchay, imgPhantich;
    private FrameLayout frameLayout;
    FragmentDothi fragmentDothi;
    FragmentChuso fragmentChuso;
    FragmentBangsolieu fragmentBangsolieu;
    FragmentGaugeview fragmentGauge;
    Bundle bundleDulieu;
    List<DulieuCacCamBien> listDulieucaccambien;
    List<String> listsolanchay;
    private Float tansolaymau;
    Boolean stateSwtoado = false, stateSwchenhlech = false, stateSwdodoc = false, stateSwtrungbinh = false, stateSwRms = false,
    stateSwBac2 = false;

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
        layout = inflater.inflate(R.layout.fragment_base_main, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        lineChart=(LineChart) view.findViewById(R.id.linechartBaseMain);
        imgChonhienthi = (ImageView) view.findViewById(R.id.imageview_chonkieuhienthi);
        frameLayout = (FrameLayout) view.findViewById(R.id.framelayout_fragmentbasemain);
        imgChonsodo = (ImageView) view.findViewById(R.id.imageview_Chonsodo);
        imgSolanchay = (ImageView) view.findViewById(R.id.imageview_Solanchay);
        imgPhantich = (ImageView) view.findViewById(R.id.imageview_Phantichdulieu);
        imgChonhienthi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuKieuhienthi(view);
            }
        });
        imgChonsodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuChonsodo(view);
            }
        });
        imgSolanchay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuSolanchay(view);
            }
        });
        imgPhantich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taoMenuPhantich(view);
            }
        });
        fragmentChuso = new FragmentChuso();
        fragmentDothi = new FragmentDothi();
        fragmentBangsolieu = new FragmentBangsolieu();
        fragmentGauge = new FragmentGaugeview();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_fragmentbasemain, fragmentDothi);
        transaction.commit();
        imgChonhienthi.setImageResource(R.drawable.icon_chart);
        bundleDulieu = new Bundle();
        listDulieucaccambien = new ArrayList<>();
        listsolanchay = new ArrayList<>();
    }

    public void taoMenuKieuhienthi(View view) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popwindow_chonhienthi, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        LinearLayout dothiLayout = (LinearLayout) popupView.findViewById(R.id.linearlayoutDothi_popupwindow);
        LinearLayout chusoLayout = (LinearLayout) popupView.findViewById(R.id.linearlayoutSo_popupwindow);
        LinearLayout bangLayout = (LinearLayout) popupView.findViewById(R.id.linearlayoutBang_popupwindow);
        LinearLayout halfgauge = (LinearLayout) popupView.findViewById(R.id.linearlayoutGauge_popupwindow);

        dothiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout_fragmentbasemain, fragmentDothi);
                transaction.commit();
                popupWindow.dismiss();
                imgChonhienthi.setImageResource(R.drawable.icon_chart);
            }
        });
        chusoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout_fragmentbasemain, fragmentChuso);
                transaction.commit();
                popupWindow.dismiss();
                imgChonhienthi.setImageResource(R.drawable.icon_number);
            }
        });
        bangLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout_fragmentbasemain, fragmentBangsolieu);
                transaction.commit();
                popupWindow.dismiss();
                imgChonhienthi.setImageResource(R.drawable.icon_table);
            }
        });
        halfgauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout_fragmentbasemain, fragmentGauge);
                transaction.commit();
                popupWindow.dismiss();
                imgChonhienthi.setImageResource(R.drawable.ic_dongho);
            }
        });

    }

    public void taoMenuChonsodo(View v) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_chonsodo, null);
        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v);
        ListView lvSodo = (ListView) popupView.findViewById(R.id.listview_chonsodo);
        TextView txtEmpty = (TextView) popupView.findViewById(R.id.textviewEmpty_chonsodo);
        if (MainActivity.isConnectedUSB) {
            chonsodoAdapterUSB chonsodoAdapterUSB = new chonsodoAdapterUSB(getContext(), R.layout.dong_popupwindowchonsodo, MainActivity.soCambienUSB);
            lvSodo.setAdapter(chonsodoAdapterUSB);
        } else {
            chonsodoAdapter chonsodoAdapter = new chonsodoAdapter(getContext(), R.layout.dong_popupwindowchonsodo, MainActivity.sodoCambienList);
            lvSodo.setAdapter(chonsodoAdapter);
        }
        lvSodo.setEmptyView(txtEmpty);

        lvSodo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String donvi = ((Button) view.findViewById(R.id.buttonDonvi_chonsodo)).getText().toString();
                String tencambien = ((TextView) view.findViewById(R.id.textviewTen_chonsodo)).getText().toString();
                String macambien = ((TextView) view.findViewById(R.id.textviewMa_chonsodo)).getText().toString();
                double[] daido = new double[2];
                for (CamBien cb : Uuid.camBiens
                ) {
                    if (cb.getName().equals(tencambien)) {
                        daido = cb.getDaido();
                        break;
                    }
                }
//                BleDevice bleDevice=MainActivity.tbKetnois.get(i);
                popupWindow.dismiss();
                bundleDulieu.putString("tencambien", tencambien);
                bundleDulieu.putString("donvi", donvi);
                bundleDulieu.putString("macambien", macambien);
                bundleDulieu.putDoubleArray("daido", daido);
//                bundleDulieu.putParcelable("device", (Parcelable) bleDevice);
                if (listener != null) {
                    listener.chonCamBien(bundleDulieu);
                }
//                bundle.putString("donvi",);
            }
        });
    }

    public void taoMenuSolanchay(View v) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_solanchay, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 400, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(5.0f);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int popupWidth = popupView.getMeasuredWidth();
        int x = -popupWidth;

        popupWindow.showAsDropDown(v, x, 0);
        ListView lvSolanchay = (ListView) popupView.findViewById(R.id.listview_solanchay);
        TextView txtEmpty = (TextView) popupView.findViewById(R.id.textviewEmpty_solanchay);
        ArrayAdapter<String> adapterSolanchay = new ArrayAdapter<String>(getContext(), R.layout.dong_caclanchay, R.id.textview_dongcaclanchay, listsolanchay);
        lvSolanchay.setAdapter(adapterSolanchay);
        lvSolanchay.setEmptyView(txtEmpty);

        lvSolanchay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String strlanchay = listsolanchay.get(i);
                listener.xoaDulieucu();
                Map<String, List<Float>> dulieuCu = new HashMap<>();
                Float tanso = Float.valueOf(0);
                int lanchay = Integer.parseInt(strlanchay.substring(4));
                for (int vitri = 0; vitri < listDulieucaccambien.size(); vitri++) {
                    if (listDulieucaccambien.get(vitri).getLanchay() == lanchay) {
                        dulieuCu.computeIfAbsent(listDulieucaccambien.get(vitri).getTencambien(), k -> new ArrayList<>()).add(listDulieucaccambien.get(vitri).getGiatricambien());
                        tanso = listDulieucaccambien.get(vitri).getTansolaymau();
                    }
                }
                listener.xemLaiDulieuCu(dulieuCu, tanso);
            }
        });
    }

    public void taoMenuPhantich(View v) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupwindow_phantich, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 400, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(5.0f);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int popupWidth = popupView.getMeasuredWidth();
        int x = -popupWidth;
        Switch swToado = (Switch) popupView.findViewById(R.id.switchToado_phantich);
        Switch swChenhlech = (Switch) popupView.findViewById(R.id.switchChechlech_phantich);
        Switch swDodoc = (Switch) popupView.findViewById(R.id.switchDodoc_phantich);
        Switch swTrungbinh = (Switch) popupView.findViewById(R.id.switchTrungbinh_phantich);
        Switch swRms = (Switch) popupView.findViewById(R.id.switchRms_phantich);
        Switch swBac2 = (Switch) popupView.findViewById(R.id.switchBac2_phantich);
        swToado.setChecked(stateSwtoado);
        swChenhlech.setChecked(stateSwchenhlech);
        swDodoc.setChecked(stateSwdodoc);
        swTrungbinh.setChecked(stateSwtrungbinh);
        swRms.setChecked(stateSwRms);
        popupWindow.showAsDropDown(v, 0, 0);


        swToado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stateSwtoado = b;
                if (b) {
                    swChenhlech.setChecked(false);
                    swDodoc.setChecked(false);
                    swTrungbinh.setChecked(false);
                    swRms.setChecked(false);
                    swBac2.setChecked(false);
                    listener.phantichDothi(1);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });

        swChenhlech.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stateSwchenhlech = b;
                if (b) {
                    swToado.setChecked(false);
                    swDodoc.setChecked(false);
                    swTrungbinh.setChecked(false);
                    swRms.setChecked(false);
                    swBac2.setChecked(false);
                    listener.phantichDothi(2);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });
        swDodoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stateSwdodoc = b;
                if (b) {
                    swToado.setChecked(false);
                    swChenhlech.setChecked(false);
                    swTrungbinh.setChecked(false);
                    swRms.setChecked(false);
                    swBac2.setChecked(false);
                    listener.phantichDothi(3);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });
        swTrungbinh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stateSwtrungbinh = b;
                if (b) {
                    swToado.setChecked(false);
                    swChenhlech.setChecked(false);
                    swDodoc.setChecked(false);
                    swRms.setChecked(false);
                    swBac2.setChecked(false);
                    listener.phantichDothi(4);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });
        swRms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                stateSwRms = b;
                if (b) {
                    swToado.setChecked(false);
                    swChenhlech.setChecked(false);
                    swDodoc.setChecked(false);
                    swTrungbinh.setChecked(false);
                    swBac2.setChecked(false);
                    listener.phantichDothi(5);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });
        swBac2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                stateSwBac2 = b;
                if (b) {
                    swToado.setChecked(false);
                    swChenhlech.setChecked(false);
                    swDodoc.setChecked(false);
                    swTrungbinh.setChecked(false);
                    swRms.setChecked(false);
                    listener.phantichDothi(6);
                } else {
                    listener.phantichDothi(0);
                }
            }
        });
    }

    private OnDataChangeListener listener;

    // Khai báo interface
    public interface OnDataChangeListener {
        void chonCamBien(Bundle bundle);

        void onDataReceived(DulieuCB dulieu);

        void xoaDulieucu();

        void xemLaiDulieuCu(Map<String, List<Float>> dulieuCu, Float tanso);

        void phantichDothi(int phantich);
    }

    // Cài đặt phương thức của interface để truyền dữ liệu sang Fragment con
    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        Log.d("life","onstart");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyEvent(DataEvent event) {
        DulieuCB dulieucaccambien = event.getData();
        if (dulieucaccambien.getMacambien().equals("lanchay")) {
            listener.xoaDulieucu();
            List<Float> manglanchay = dulieucaccambien.getGiatricambien();
            float lanchay = manglanchay.get(0);
            tansolaymau = manglanchay.get(1);
            listsolanchay.add("Lần " + new DecimalFormat("#").format(lanchay));
        } else {
            listener.onDataReceived(dulieucaccambien);
//            List<Float> mangnewValue = dulieucaccambien.getGiatricambien();
//            for (Float newValue : mangnewValue
//            ) {
//                listDulieucaccambien.add(new DulieuCacCamBien((int) MainActivity.solanchay, tansolaymau, dulieucaccambien.getMacambien(), newValue));
//            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
//        Log.d("life","onstop");
    }


}
