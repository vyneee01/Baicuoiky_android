package com.example.nguyenngocvy_2210900086;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adapter.SanPhamAdapter;
import com.example.model.SanPham;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String dbName = "QLSanpham.db";
    String dbPath = "/databases/";
    SQLiteDatabase db = null;
    ListView lvsanpham;
    Button btnThem;
    SanPhamAdapter adapter;
    SanPham sp;
    int posUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        xuLyCopy();
        addView();
        hienThiSanPham();
        addEvent();
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Xóa sản phẩm
        if (item.getItemId() == R.id.mnuXoa) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn thật sự muốn xóa sản phẩm này?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        // Xóa sản phẩm khỏi cơ sở dữ liệu
                        db.delete("SanPham", "Ma=?", new String[]{String.valueOf(sp.getMa())});
                        // Xóa sản phẩm khỏi Adapter
                        adapter.remove(sp);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("Lỗi:", e.toString());
                        Toast.makeText(MainActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Người dùng nhấn "Không", đóng hộp thoại
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            // Hiển thị cửa sổ xác nhận
            dialog.show();
        }

        // Chỉnh sửa sản phẩm
        if (item.getItemId() == R.id.mnuSua) {
            try {
                // Chuyển sang màn hình ThemSuaActivity với trạng thái "SUA"
                Intent intent = new Intent(MainActivity.this, ThemSuaActivity.class);
                intent.putExtra("TRANGTHAI", "SUA");
                intent.putExtra("SANPHAM", sp);
                startActivityForResult(intent, 113);
            } catch (Exception e) {
                Log.e("Lỗi:", e.toString());
                Toast.makeText(MainActivity.this, "Không thể mở màn hình sửa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onContextItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SanPham spNew = (SanPham) data.getSerializableExtra("SANPHAM");

        // Thêm mới sản phẩm
        if (resultCode == 114 && requestCode == 113) {
            adapter.add(spNew);
            try {
                ContentValues values = new ContentValues();
                values.put("Ma", spNew.getMa());
                values.put("Ten", spNew.getTen());
                values.put("SoLuong", spNew.getSoLuong());
                values.put("DonGia", spNew.getDonGia());

                if (db.insert("SanPham", null, values) > 0) {
                    Toast.makeText(MainActivity.this, "Thêm mới sản phẩm thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Thêm mới sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Lỗi:", e.toString());
            }
        }
        // Kết thúc thêm mới

        // Cập nhật sản phẩm
        if (requestCode == 113 && resultCode == 115) {
            try {
                ContentValues values = new ContentValues();
                values.put("Ten", spNew.getTen());
                values.put("SoLuong", spNew.getSoLuong());
                values.put("DonGia", spNew.getDonGia());

                db.update("SanPham", values, "Ma=?", new String[]{String.valueOf(spNew.getMa())});

                adapter.getItem(posUpdate).setTen(spNew.getTen());
                adapter.getItem(posUpdate).setSoLuong(spNew.getSoLuong());
                adapter.getItem(posUpdate).setDonGia(spNew.getDonGia());
                adapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("Lỗi:", e.toString());
            }
        }
        // Kết thúc cập nhật
    }
    private void addView() {
        btnThem=findViewById(R.id.btnThem);
        lvsanpham=findViewById(R.id.lvsanpham);
        adapter = new SanPhamAdapter(MainActivity.this ,R.layout.item_sanpham);
        lvsanpham.setAdapter(adapter);
        registerForContextMenu(lvsanpham);
        lvsanpham.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                sp=adapter.getItem(i);
                posUpdate=i;
                return false;
            }
        });
    }
    private void addEvent() {
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , ThemSuaActivity.class);
                intent.putExtra("TRANGTHAI","THEM");
                startActivityForResult(intent,113);
            }
        });
    }
    private void hienThiSanPham() {
        db = openOrCreateDatabase(dbName , MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM SanPham" , null);
        while (cursor.moveToNext()){
            int maSanPham = cursor.getInt(0);
            String tenSanPham = cursor.getString(1);
            int soLuong = cursor.getInt(2);
            Double donGia = cursor.getDouble(3);
            adapter.add(new SanPham(maSanPham ,tenSanPham, soLuong , donGia));

        }
    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context, menu);
    }
    private void xuLyCopy() {
        try {
            File dbFile = getDatabasePath(dbName);
            if (!dbFile.exists()) {
                copyDataFromAsset();
                Toast.makeText(MainActivity.this, "Copy thành công", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(MainActivity.this, "File đã tồn tại", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.e("Lỗi" , e.toString());
        }
    }
    private void copyDataFromAsset() {
        try{
            InputStream myInput = getAssets().open(dbName);
            String outFileName = getApplicationInfo().dataDir+dbPath+dbName;
            File f = new File(getApplicationInfo().dataDir+dbPath);
            if (!f.exists())
                f.mkdir();
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer , 0 , length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }
        catch (Exception e){
            Log.e("Lỗi" , e.toString());
        }
    }
}