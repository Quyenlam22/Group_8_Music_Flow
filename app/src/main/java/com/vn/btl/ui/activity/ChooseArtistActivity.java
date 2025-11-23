package com.vn.btl.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.database.ArtistDAO;
import com.vn.btl.model.Artist;
import com.vn.btl.repository.ArtistResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.ArtistAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseArtistActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArtistAdapter adapter;
    private List<Artist> artistList = new ArrayList<>();
    private List<Artist> selected = new ArrayList<>();

    private ApiService api;
    private ArtistDAO dao;

    private Button btnDone;
    private SearchView search;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_choose_artist);

        recycler = findViewById(R.id.recyclerArtists);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        api = RetrofitClient.getApiService();
        dao = AppDatabase.getInstance(this).artistDAO();

        adapter = new ArtistAdapter(artistList, artist -> {
            // Toggle selection and enforce max 2
            boolean newState = !artist.isSelected();
            if (newState) {
                // Check current selected count in artistList
                long countSelected = artistList.stream().filter(Artist::isSelected).count();
                if (countSelected >= 2) {
                    Toast.makeText(this, "Chỉ được chọn tối đa 2 nghệ sĩ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            artist.setSelected(newState);

            if (newState) {
                if (!selected.contains(artist)) selected.add(artist);
            } else {
                selected.remove(artist);
            }

            adapter.notifyItemChanged(artistList.indexOf(artist));
        });

        recycler.setAdapter(adapter);

        btnDone = findViewById(R.id.btnDone);
        search = findViewById(R.id.searchArtist);

        setupSearch();
        setupDone();
        clearLocalDBThenLoad();
    }

    private void clearLocalDBThenLoad() {
        new Thread(() -> {
            dao.deleteAll();
            runOnUiThread(this::loadArtists);
        }).start();
    }

    private void setupDone() {
        btnDone.setOnClickListener(v -> {
            // Count selected directly from artistList for accuracy
            long countSelected = artistList.stream().filter(Artist::isSelected).count();
            if (countSelected != 2) {
                Toast.makeText(this, "Bạn phải chọn đúng 2 nghệ sĩ", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                for (Artist a : artistList) {
                    if (a.isSelected()) dao.insert(a);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
            }).start();
        });
    }

    private void setupSearch() {
        int id = search.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText txt = search.findViewById(id);
        if (txt != null) {
            txt.setTextColor(Color.WHITE);
            txt.setHintTextColor(Color.LTGRAY);
        }

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                if (q != null && !q.trim().isEmpty()) searchArtist(q.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String t) {
                if (t == null || t.isEmpty()) loadArtists();
                return true;
            }
        });
    }

    private void loadArtists() {
        api.getRandomArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(ChooseArtistActivity.this, "API error!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Artist> list = res.body().getArtists();
                if (list == null || list.isEmpty()) {
                    Toast.makeText(ChooseArtistActivity.this, "Danh sách rỗng!", Toast.LENGTH_SHORT).show();
                    return;
                }

                artistList.clear();
                artistList.addAll(list);

                // Restore selected state
                for (Artist s : selected) {
                    for (Artist a : artistList) {
                        if (a.getArtistId() == s.getArtistId()) {
                            a.setSelected(true);
                            break;
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Toast.makeText(ChooseArtistActivity.this, "Không thể kết nối API", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    private void searchArtist(String q) {
        api.searchArtists(q).enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> res) {
                if (!res.isSuccessful() || res.body() == null) return;

                List<Artist> list = res.body().getArtistSearch();
                if (list == null) {
                    Toast.makeText(ChooseArtistActivity.this, "Không tìm thấy ca sĩ", Toast.LENGTH_SHORT).show();
                    return;
                }

                artistList.clear();
                artistList.addAll(list);

                for (Artist s : selected) {
                    for (Artist a : artistList) {
                        if (a.getArtistId() == s.getArtistId()) {
                            a.setSelected(true);
                            break;
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Toast.makeText(ChooseArtistActivity.this, "Lỗi search", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
