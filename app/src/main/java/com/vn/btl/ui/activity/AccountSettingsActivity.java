package com.vn.btl.ui.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vn.btl.R;
import com.vn.btl.utils.ThemeManager;

import java.util.Calendar;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String PREFS_PROFILE = "user_profile";
    private static final String K_NAME = "name";
    private static final String K_EMAIL = "email";
    private static final String K_PHONE = "phone";
    private static final String K_PASSWORD = "password";
    private static final String K_DOB = "dob";

    private EditText etName, etEmail, etPhone;
    private TextInputEditText etPassword, etDob;
    private Button btnSave, btnCancel;

    private String origName, origEmail, origPhone, origPass, origDob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.vn.btl.utils.ThemeManager.apply(this);
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        bindViews();
        loadFromPrefs();
        setupInteractions();
    }

    private void bindViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etDob = findViewById(R.id.et_dob);

        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);

        TextInputLayout tilDob = findViewById(R.id.til_dob);
        tilDob.setEndIconOnClickListener(v -> openDatePicker());
        etDob.setOnClickListener(v -> openDatePicker());
        etDob.setInputType(InputType.TYPE_NULL); // không mở bàn phím
    }

    private void loadFromPrefs() {
        SharedPreferences sp = getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE);
        origName  = sp.getString(K_NAME,  "Mee Hà");
        origEmail = sp.getString(K_EMAIL, "example@gmail.com");
        origPhone = sp.getString(K_PHONE, "0900000000");
        origPass  = sp.getString(K_PASSWORD, "");
        origDob   = sp.getString(K_DOB, "");

        etName.setText(origName);
        etEmail.setText(origEmail);
        etPhone.setText(origPhone);
        etPassword.setText(origPass);
        etDob.setText(origDob);

        btnSave.setEnabled(false);
    }

    private void setupInteractions() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { checkEnableSave(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etDob.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> {
            if (!validate()) return;
            saveToPrefs();
            Toast.makeText(this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
            finish(); // quay lại Settings
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String dd = (day < 10 ? "0" : "") + day;
                    String mm = (month + 1 < 10 ? "0" : "") + (month + 1);
                    etDob.setText(dd + "/" + mm + "/" + year);
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private boolean validate() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String dob   = etDob.getText() == null ? "" : etDob.getText().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email phải có dạng @gmail.com", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Số điện thoại phải gồm 10 chữ số", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isDobBeforeToday(dob)) {
            Toast.makeText(this, "Ngày sinh phải nhỏ hơn ngày hiện tại", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isDobBeforeToday(String dob) {
        try {
            String[] p = dob.split("/");
            int d = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]) - 1;
            int y = Integer.parseInt(p[2]);
            Calendar dobCal = Calendar.getInstance();
            dobCal.set(y, m, d, 0, 0, 0);
            dobCal.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return dobCal.before(today);
        } catch (Exception e) {
            return false;
        }
    }

    private void saveToPrefs() {
        SharedPreferences sp = getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE);
        sp.edit()
                .putString(K_NAME,  etName.getText().toString().trim())
                .putString(K_EMAIL, etEmail.getText().toString().trim())
                .putString(K_PHONE, etPhone.getText().toString().trim())
                .putString(K_PASSWORD, etPassword.getText() == null ? "" : etPassword.getText().toString())
                .putString(K_DOB, etDob.getText() == null ? "" : etDob.getText().toString())
                .apply();
    }

    private void checkEnableSave() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String dob   = etDob.getText() == null ? "" : etDob.getText().toString();

        boolean changed =
                !TextUtils.equals(name,  origName)  ||
                        !TextUtils.equals(email, origEmail) ||
                        !TextUtils.equals(phone, origPhone) ||
                        !TextUtils.equals(pass,  origPass)  ||
                        !TextUtils.equals(dob,   origDob);

        btnSave.setEnabled(changed);
    }
}
