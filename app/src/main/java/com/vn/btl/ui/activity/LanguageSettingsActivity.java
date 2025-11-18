package com.vn.btl.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vn.btl.R;
import com.vn.btl.utils.ThemeManager;

public class LanguageSettingsActivity extends AppCompatActivity {

    public static final String PREFS = "settings_prefs";
    public static final String K_LANG = "k_lang"; // "en" / "vi"

    private static final String[] DISPLAY = {"English", "Tiếng Việt"};
    private static final String[] VALUES  = {"en", "vi"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);

        ListView lv = findViewById(R.id.lv_lang);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, DISPLAY));

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String cur = sp.getString(K_LANG, "en");
        int sel = "vi".equals(cur) ? 1 : 0;
        lv.setItemChecked(sel, true);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            sp.edit().putString(K_LANG, VALUES[position]).apply();
            finish();
        });
    }
}
