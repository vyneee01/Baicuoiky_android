package com.example.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.model.SanPham;
import com.example.nguyenngocvy_2210900086.R;

public class SanPhamAdapter extends ArrayAdapter<SanPham> {
    Activity context ;
    int resource ;

    public SanPhamAdapter(@NonNull Activity context, int resource) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View customView = inflater.inflate(resource , null);
        TextView txtMa, txtTen , txtSoLuong, txtDonGia , txtThanhTien;
        txtMa = customView.findViewById(R.id.txtMa);
        txtTen = customView.findViewById(R.id.txtTen);
        txtSoLuong = customView.findViewById(R.id.txtSoLuong);
        txtDonGia = customView.findViewById(R.id.txtDonGia);
        txtThanhTien = customView.findViewById(R.id.txtThanhTien);

        SanPham sp = getItem(position);

        if(sp != null){
            double thanhTien = sp.getSoLuong() * sp.getDonGia();
            if(sp.getSoLuong() >10){
                thanhTien *= 0.9; // thanhTien = thanhTien * 0.9
            }
            txtMa.setText(String.valueOf(sp.getMa()));
            txtTen.setText(sp.getTen());
            txtSoLuong.setText("Số lượng: "+sp.getSoLuong());
            txtDonGia.setText("Đơn giá: "+sp.getDonGia() + "VNĐ");
            txtThanhTien.setText("Thành tiền: "+thanhTien);
        }
        return customView;
    }
}
