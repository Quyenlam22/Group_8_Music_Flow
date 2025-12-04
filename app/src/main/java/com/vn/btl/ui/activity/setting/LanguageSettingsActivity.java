package com.vn.btl.ui.activity.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vn.btl.R;
import com.vn.btl.utils.LanguageManager;
import com.vn.btl.utils.ThemeManager;

public class LanguageSettingsActivity extends AppCompatActivity {

    public static final String PREFS = "settings_prefs";
    public static final String K_LANG = "k_lang"; // "en" / "vi"

    private static final String[] DISPLAY = {"English", "Tiếng Việt"};
    private static final String[] VALUES  = {LanguageManager.LANG_EN, LanguageManager.LANG_VI};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.apply(this); // Áp dụng theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String curLang = sp.getString(K_LANG, LanguageManager.LANG_EN);

        ListView lv = findViewById(R.id.lv_lang);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, DISPLAY));

        int sel = LanguageManager.LANG_VI.equals(curLang) ? 1 : 0;
        lv.setItemChecked(sel, true);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            // Lưu ngôn ngữ mới
            sp.edit().putString(K_LANG, VALUES[position]).apply();

            // Reload SettingsActivity để áp dụng ngôn ngữ
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
