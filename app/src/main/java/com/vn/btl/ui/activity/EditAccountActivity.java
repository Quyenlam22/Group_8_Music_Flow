package com.vn.btl.ui.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.vn.btl.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditAccountActivity extends AppCompatActivity {

    private EditText etName, etEmail, etDob, etPhone;
    private TextInputEditText etPassword;
    private Button btnSave, btnCancel;

    // Keys cho SharedPreferences
    private static final String PREFS = "user_profile";
    private static final String K_NAME = "name";
    private static final String K_EMAIL = "email";
    private static final String K_PHONE = "phone";
    private static final String K_PASSWORD = "password";
    private static final String K_DOB = "dob";

    // Giá trị gốc để biết có thay đổi hay không
    private String originName, originEmail, originPassword, originDob, originPhone;

    private final SimpleDateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etDob = findViewById(R.id.et_dob);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        // 1) Nạp dữ liệu đang lưu để hiển thị & làm mốc so sánh
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        originName     = sp.getString(K_NAME, "Nguyen Van A");
        originEmail    = sp.getString(K_EMAIL, "abc@gmail.com");
        originPhone    = sp.getString(K_PHONE, "0987654321");
        originPassword = sp.getString(K_PASSWORD, "12345678");
        originDob      = sp.getString(K_DOB, "01/01/1995");

        etName.setText(originName);
        etEmail.setText(originEmail);
        etPhone.setText(originPhone);
        etPassword.setText(originPassword);
        etDob.setText(originDob);

        // 2) DatePicker cho DOB
        etDob.setOnClickListener(v -> showDobPicker());
        etDob.setOnFocusChangeListener((v, hasFocus) -> { if (hasFocus) showDobPicker(); });

        // 3) Theo dõi thay đổi để bật/tắt nút Lưu
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { toggleSaveEnabled(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etDob.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);

        // 4) Lưu
        btnSave.setOnClickListener(v -> {
            if (validateAll()) {
                saveToPrefs();
                Toast.makeText(this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                finish(); // quay về AccountActivity; AccountActivity.onResume() sẽ đọc lại và hiển thị
            }
        });

        // 5) Cancel
        btnCancel.setOnClickListener(v -> finish());

        toggleSaveEnabled(); // lần đầu vào
    }

    private void saveToPrefs() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        sp.edit()
                .putString(K_NAME, safe(etName))
                .putString(K_EMAIL, safe(etEmail))
                .putString(K_PHONE, safe(etPhone))
                .putString(K_PASSWORD, safe(etPassword))
                .putString(K_DOB, safe(etDob))
                .apply();

        // Cập nhật mốc so sánh để lần sau vào màn hình vẫn biết đã lưu
        originName = safe(etName);
        originEmail = safe(etEmail);
        originPhone = safe(etPhone);
        originPassword = safe(etPassword);
        originDob = safe(etDob);
        toggleSaveEnabled();
    }

    private void showDobPicker() {
        Calendar cal = Calendar.getInstance();
        try {
            Date parsed = dobFormat.parse(safe(etDob));
            if (parsed != null) cal.setTime(parsed);
        } catch (Exception ignored) {}

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(year, month, dayOfMonth, 0, 0, 0);
            etDob.setText(dobFormat.format(chosen.getTime()));
        }, y, m, d);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void toggleSaveEnabled() {
        boolean changed =
                !safe(etName).equals(safe(originName)) ||
                        !safe(etEmail).equals(safe(originEmail)) ||
                        !safe(etPassword).equals(safe(originPassword)) ||
                        !safe(etDob).equals(safe(originDob)) ||
                        !safe(etPhone).equals(safe(originPhone));

        btnSave.setEnabled(changed && validateAll(false));
    }

    private String safe(EditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
    private String safe(TextInputEditText e) { return e.getText() == null ? "" : e.getText().toString().trim(); }
    private String safe(String s) { return s == null ? "" : s.trim(); }

    private boolean validateAll() { return validateAll(true); }

    private boolean validateAll(boolean showError) {
        String name = safe(etName);
        String email = safe(etEmail);
        String password = safe(etPassword);
        String dob = safe(etDob);
        String phone = safe(etPhone);

        boolean ok = true;

        if (name.isEmpty()) { if (showError) etName.setError("Không được để trống"); ok = false; }
        if (email.isEmpty()) { if (showError) etEmail.setError("Không được để trống"); ok = false; }
        else {
            boolean pattern = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            boolean gmail = email.endsWith("@gmail.com");
            if (!pattern || !gmail) { if (showError) etEmail.setError("Email phải đúng định dạng và kết thúc @gmail.com"); ok = false; }
        }
        if (phone.isEmpty()) { if (showError) etPhone.setError("Không được để trống"); ok = false; }
        else if (!phone.matches("\\d{10}")) { if (showError) etPhone.setError("Số điện thoại phải là 10 chữ số"); ok = false; }

        if (password.isEmpty()) { if (showError) etPassword.setError("Không được để trống"); ok = false; }

        if (dob.isEmpty()) { if (showError) etDob.setError("Không được để trống"); ok = false; }
        else {
            try {
                Date d = dobFormat.parse(dob);
                if (d == null || d.after(new Date())) {
                    if (showError) etDob.setError("Ngày sinh phải nhỏ hơn hiện tại (dd/MM/yyyy)");
                    ok = false;
                }
            } catch (ParseException e) {
                if (showError) etDob.setError("Sai định dạng dd/MM/yyyy");
                ok = false;
            }
        }
        return ok;
    }
}
