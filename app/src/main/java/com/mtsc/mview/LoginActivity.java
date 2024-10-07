package com.mtsc.mview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    Button btnKichhoat;
    static String username="mview";
//    static String password="0103245918Mtsc@";
    static String password="123456";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLoggedIn()) {
            // Nếu đã đăng nhập, chuyển hướng đến MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng LoginActivity để người dùng không quay lại màn hình đăng nhập
        } else {
            // Nếu chưa đăng nhập, hiển thị màn hình đăng nhập
            setContentView(R.layout.activity_login);
            edtUsername = findViewById(R.id.edtTendangnhap);
            edtPassword = findViewById(R.id.edtMatkhau);
            btnKichhoat = findViewById(R.id.buttonKichhoat);

            btnKichhoat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Xử lý khi nút được nhấp (kiểm tra đăng nhập và chuyển hướng)
                    handleLogin();
                }
            });
        }
    }
    private boolean isValidCredentials(String inputUsername, String inputPassword) {
        boolean isValid = inputUsername.equals(username) && inputPassword.equals(password);

        if (isValid) {
            // Lưu trạng thái đăng nhập khi đăng nhập thành công
            saveLoginStatus(true);
        }

        return isValid;
    }

    // Hàm lưu trạng thái đăng nhập vào SharedPreferences
    private void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }
    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }

    // Hàm xử lý đăng nhập
    private void handleLogin() {
        String inputUsername = edtUsername.getText().toString();
        String inputPassword = edtPassword.getText().toString();

        if (isValidCredentials(inputUsername, inputPassword)) {
            // Đăng nhập thành công, chuyển hướng đến MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng LoginActivity để người dùng không quay lại màn hình đăng nhập
        } else {
            // Thông báo khi đăng nhập không thành công
            Toast.makeText(LoginActivity.this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }
    }
}