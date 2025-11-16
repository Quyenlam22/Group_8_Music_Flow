package com.vn.btl.ui.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.vn.btl.R;
import com.vn.btl.utils.ThemeManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String PREFS_PROFILE = "user_profile";
    private static final String K_PASSWORD = "password";

    private EditText etUsername, etName, etEmail, etPhone;
    private TextInputEditText etPassword, etDob;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnSave, btnCancel;
    private ImageView ivBack; // Nút Back

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String origUsername, origName, origEmail, origPhone, origPass, origDob, origGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Khởi tạo Activity và kiểm tra trạng thái đăng nhập
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để xem cài đặt tài khoản.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bindViews();
        loadUserDataFromFirebase();
    }

    private void bindViews() {
        // Ánh xạ các trường
        etUsername = findViewById(R.id.et_username);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        rbOther = findViewById(R.id.rb_other);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etDob = findViewById(R.id.et_dob);

        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave.setEnabled(false);

        // Nút Back
        ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Thiết lập Date Picker và Email
        TextInputLayout tilDob = findViewById(R.id.til_dob);
        if (tilDob != null) {
            tilDob.setEndIconOnClickListener(v -> openDatePicker());
        }
        etDob.setOnClickListener(v -> openDatePicker());
        etDob.setInputType(InputType.TYPE_NULL);

        // BẬT SỬA EMAIL VÀ USERNAME
        etEmail.setEnabled(true);
        etUsername.setEnabled(true);
    }

    /**
     * Tải dữ liệu hồ sơ từ Firebase Auth (Email) và Firestore (Username, Name, Phone, DOB, Gender).
     */
    private void loadUserDataFromFirebase() {
        if (currentUser == null) return;

        origEmail = currentUser.getEmail();
        etEmail.setText(origEmail);

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        origUsername = documentSnapshot.getString("username");
                        origName  = documentSnapshot.getString("name");
                        origPhone = documentSnapshot.getString("phone");
                        origDob   = documentSnapshot.getString("dob");
                        origGender = documentSnapshot.getString("gender");

                        etUsername.setText(origUsername != null ? origUsername : "");
                        etName.setText(origName != null ? origName : "");
                        etPhone.setText(origPhone != null ? origPhone : "");
                        etDob.setText(origDob != null ? origDob : "");

                        setGenderRadioButton(origGender);

                    } else {
                        Toast.makeText(this, "Hồ sơ chưa được tạo, vui lòng nhập thông tin.", Toast.LENGTH_LONG).show();
                        origUsername = origName = origPhone = origDob = origGender = "";
                    }

                    // THAY ĐỔI: Reset mật khẩu gốc về rỗng và xóa trường nhập
                    // Mật khẩu không thể đọc được từ Firebase.
                    origPass = "";
                    etPassword.setText("");

                    btnSave.setEnabled(false);
                    setupInteractions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu hồ sơ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    /**
     * Thiết lập lắng nghe sự kiện TextWatcher và OnClickListener.
     */
    private void setupInteractions() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { checkEnableSave(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        etUsername.addTextChangedListener(watcher);
        etName.addTextChangedListener(watcher);
        etEmail.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
        etDob.addTextChangedListener(watcher);

        // Lắng nghe thay đổi của RadioGroup
        rgGender.setOnCheckedChangeListener((group, checkedId) -> checkEnableSave());

        btnSave.setOnClickListener(v -> {
            if (!validate()) return;
            checkUniquenessAndUpdate();
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Mở DatePickerDialog để chọn Ngày sinh.
     */
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

    /**
     * Kiểm tra tính hợp lệ của các trường nhập liệu.
     * Mật khẩu là tùy chọn, nhưng nếu nhập thì phải >= 6 ký tự.
     */
    private boolean validate() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String dob   = etDob.getText() == null ? "" : etDob.getText().toString();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền các thông tin cần thiết (username, name, email).", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Địa chỉ Email không hợp lệ.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            Toast.makeText(this, "Số điện thoại phải gồm 10 chữ số.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!dob.isEmpty() && !isDobBeforeToday(dob)) {
            Toast.makeText(this, "Ngày sinh phải nhỏ hơn ngày hiện tại.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // THAY ĐỔI: Mật khẩu là tùy chọn, chỉ kiểm tra khi nó không rỗng
        if (!pass.isEmpty() && pass.length() < 6) {
            Toast.makeText(this, "Mật khẩu (nếu thay đổi) phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra Ngày sinh có trước Ngày hiện tại hay không.
     */
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

    /**
     * Kiểm tra xem có bất kỳ trường nào đã thay đổi so với giá trị gốc để bật nút Lưu.
     * Mật khẩu chỉ kích hoạt Save nếu người dùng nhập MỚI (khác origPass = "").
     */
    private void checkEnableSave() {
        String username = etUsername.getText().toString().trim();
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass  = etPassword.getText() == null ? "" : etPassword.getText().toString(); // Mật khẩu mới
        String dob   = etDob.getText() == null ? "" : etDob.getText().toString();
        String gender = getSelectedGender();

        // Kiểm tra sự thay đổi. Mật khẩu sẽ luôn khác origPass nếu người dùng nhập bất cứ thứ gì.
        boolean changed =
                !TextUtils.equals(username, origUsername) ||
                        !TextUtils.equals(name,  origName)  ||
                        !TextUtils.equals(email, origEmail) ||
                        !TextUtils.equals(phone, origPhone) ||
                        !TextUtils.equals(pass,  origPass)  || // pass != origPass (luôn đúng nếu nhập)
                        !TextUtils.equals(dob,   origDob)   ||
                        !TextUtils.equals(gender, origGender);

        btnSave.setEnabled(changed);
    }

    /**
     * Lấy giá trị Giới tính đang được chọn.
     */
    private String getSelectedGender() {
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == rbMale.getId()) return "Nam";
        if (checkedId == rbFemale.getId()) return "Nữ";
        if (checkedId == rbOther.getId()) return "Khác";
        return "";
    }

    /**
     * Thiết lập RadioButton dựa trên giá trị Gender từ Firebase.
     */
    private void setGenderRadioButton(String gender) {
        if (gender == null) return;
        if (gender.equals("Nam")) {
            rbMale.setChecked(true);
        } else if (gender.equals("Nữ")) {
            rbFemale.setChecked(true);
        } else if (gender.equals("Khác")) {
            rbOther.setChecked(true);
        }
    }

    /**
     * Kiểm tra tính duy nhất (không trùng lặp) của Email và Username mới (nếu có thay đổi)
     * trước khi tiến hành cập nhật.
     */
    private void checkUniquenessAndUpdate() {
        final String newUsername = etUsername.getText().toString().trim();
        final String newEmail = etEmail.getText().toString().trim();

        final boolean isUsernameChanged = !TextUtils.equals(newUsername, origUsername);
        final boolean isEmailChanged = !TextUtils.equals(newEmail, origEmail);

        if (isUsernameChanged) {
            // Bước 1: Kiểm tra trùng lặp Username trong Firestore
            db.collection("users")
                    .whereEqualTo("username", newUsername)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            Toast.makeText(this, "Username đã được sử dụng bởi tài khoản khác.", Toast.LENGTH_LONG).show();
                        } else {
                            startUpdateProcess(isEmailChanged, newEmail, newUsername);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi kiểm tra Username: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // Không cần kiểm tra Username, tiếp tục cập nhật
            startUpdateProcess(isEmailChanged, newEmail, newUsername);
        }
    }

    /**
     * Bắt đầu quy trình cập nhật sau khi kiểm tra Username.
     */
    private void startUpdateProcess(boolean isEmailChanged, String newEmail, String newUsername) {
        if (isEmailChanged) {
            // Bước 2: Cập nhật Email trong Auth (sẽ kiểm tra trùng lặp tự động)
            updateEmailInAuth(newEmail, newUsername);
        } else {
            // Email không đổi, bỏ qua Auth, cập nhật Firestore
            updateFirestoreProfile(newUsername, origEmail);
        }
    }

    /**
     * Cập nhật Email trong Firebase Auth.
     */
    private void updateEmailInAuth(String newEmail, String newUsername) {
        currentUser.updateEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateFirestoreProfile(newUsername, newEmail);
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định.";
                        if (errorMessage.contains("already in use")) {
                            Toast.makeText(this, "Email đã được sử dụng bởi tài khoản khác.", Toast.LENGTH_LONG).show();
                        } else if (errorMessage.contains("requires recent authentication")) {
                            Toast.makeText(this, "Vui lòng đăng nhập lại để cập nhật Email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Email đã được sử dụng bởi tài khoản khác.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * Cập nhật thông tin hồ sơ (bao gồm Username) lên Firestore.
     */
    private void updateFirestoreProfile(String username, String currentEmail) {
        String name  = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String newPass = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String dob   = etDob.getText() == null ? "" : etDob.getText().toString();
        String gender = getSelectedGender();

        // 1. Cập nhật Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("dob", dob);
        updates.put("gender", gender);
        updates.put("email", currentEmail);

        db.collection("users").document(currentUser.getUid())
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // CHỈ GỌI CẬP NHẬT MẬT KHẨU NẾU TRƯỜNG NHẬP KHÔNG RỖNG
                    if (!newPass.isEmpty()) {
                        updatePasswordInAuth(newPass, username, name, phone, dob, gender, currentEmail);
                    } else {
                        // Mật khẩu không đổi, hoàn thành
                        handleUpdateSuccess(username, name, phone, dob, origPass, gender, currentEmail);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Cập nhật Mật khẩu trong Firebase Auth.
     */
    private void updatePasswordInAuth(String newPass, String username, String name, String phone, String dob, String gender, String currentEmail) {
        currentUser.updatePassword(newPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleUpdateSuccess(username, name, phone, dob, newPass, gender, currentEmail);
                    } else {
                        Toast.makeText(this, "Lỗi cập nhật mật khẩu. Vui lòng đăng nhập lại để xác thực và thử lại.", Toast.LENGTH_LONG).show();
                        // Giữ lại mật khẩu cũ ("") nếu cập nhật thất bại
                        handleUpdateSuccess(username, name, phone, dob, origPass, gender, currentEmail);
                    }
                });
    }

    /**
     * Xử lý khi cập nhật thành công (Firestore & Auth).
     */
    private void handleUpdateSuccess(String username, String name, String phone, String dob, String newPass, String gender, String currentEmail) {
        // Cập nhật lại các giá trị gốc
        origUsername = username;
        origName = name;
        origPhone = phone;
        origDob = dob;
        // Đặt lại origPass về "" (dù là mật khẩu mới hay không)
        origPass = "";
        origGender = gender;
        origEmail = currentEmail;

        // CHỈ LƯU MẬT KHẨU MỚI VÀO SHAREDPREFS NẾU NÓ THỰC SỰ ĐƯỢC CẬP NHẬT
        if (!newPass.isEmpty()) {
            savePasswordToPrefs(newPass);
        }

        // Xóa trường mật khẩu sau khi thành công
        etPassword.setText("");

        Toast.makeText(this, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(false);
    }

    /**
     * Chỉ lưu Mật khẩu vào SharedPreferences để kiểm tra thay đổi.
     */
    private void savePasswordToPrefs(String password) {
        getSharedPreferences(PREFS_PROFILE, MODE_PRIVATE)
                .edit()
                .putString(K_PASSWORD, password)
                .apply();
    }
}
