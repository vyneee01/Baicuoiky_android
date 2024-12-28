package com.example.nguyenngocvy_2210900086;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.model.SanPham;

public class ThemSuaActivity extends AppCompatActivity {
    Intent intent;//intent nhận dữ liệu
    EditText edtMaSanPham,edtTenSP,edtSoLuong,edtDonGia;
    Button btnThemSP, btnThoatSP;
    String trangthai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_sua);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addView();
        addEvent();
    }
    private void addView() {
        intent = getIntent();
        trangthai = intent.getStringExtra("TRANGTHAI");
        edtMaSanPham = findViewById(R.id.edtMaSanPham);
        edtTenSP = findViewById(R.id.edtTenSP);
        edtSoLuong = findViewById(R.id.edtSoLuong);
        edtDonGia = findViewById(R.id.edtDonGia);
        btnThemSP = findViewById(R.id.btnThemSP);
        btnThoatSP = findViewById(R.id.btnThoatSP);

        if (trangthai.equals("THEM")) {
            // Trạng thái "Thêm": cho phép nhập liệu đầy đủ
            btnThemSP.setText("Thêm");
            edtMaSanPham.setEnabled(true); // Cho phép nhập mã sản phẩm
        } else {
            // Trạng thái "Sửa": chỉ cho phép sửa các thông tin khác ngoài mã sản phẩm
            btnThemSP.setText("Sửa");
            SanPham sp = (SanPham) intent.getSerializableExtra("SANPHAM");

            // Điền thông tin sản phẩm vào các trường
            edtMaSanPham.setText(String.valueOf(sp.getMa()));
            edtMaSanPham.setEnabled(false); // Vô hiệu hóa mã sản phẩm
            edtTenSP.setText(sp.getTen());
            edtSoLuong.setText(String.valueOf(sp.getSoLuong()));
            edtDonGia.setText(String.valueOf(sp.getDonGia()));
        }
    }

    private void addEvent() {
        btnThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String tenSanPham = edtTenSP.getText().toString();
                    int soLuong = Integer.parseInt(edtSoLuong.getText().toString());
                    double donGia = Double.parseDouble(edtDonGia.getText().toString());

                    if (tenSanPham.isEmpty() || soLuong <= 0 || donGia <= 0) {
                        Toast.makeText(ThemSuaActivity.this, "Vui lòng nhập đầy đủ và hợp lệ thông tin sản phẩm!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SanPham sp = new SanPham();
                    sp.setTen(tenSanPham);
                    sp.setSoLuong(soLuong);
                    sp.setDonGia(donGia);

                    if (trangthai.equals("THEM")) {
                        sp.setMa(Integer.parseInt(edtMaSanPham.getText().toString())); // Lấy mã sản phẩm từ EditText
                        setResult(114, intent);
                    } else {
                        sp.setMa(Integer.parseInt(edtMaSanPham.getText().toString())); // Sử dụng mã sản phẩm không thay đổi
                        setResult(115, intent);
                    }

                    intent.putExtra("SANPHAM", sp);
                    finish();
                } catch (NumberFormatException e) {
                    Toast.makeText(ThemSuaActivity.this, "Vui lòng kiểm tra lại dữ liệu nhập vào!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}