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
    private AppDatabase db;
    private List<Artist> artistList = new ArrayList<>();
    private List<Artist> selectedArtists = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);
        db = AppDatabase.getInstance(this);
        ArtistDAO artistDao = db.artistDAO();

        // XÓA TOÀN BỘ DỮ LIỆU NGHỆ SĨ CŨ
        new Thread(() -> {
            artistDao.deleteAll();
            Log.d("DB_ARTIST", "Đã xóa toàn bộ dữ liệu nghệ sĩ cũ");
        }).start();

        recycler = findViewById(R.id.recyclerArtists);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        apiService = RetrofitClient.getApiService();
        loadArtists(); // gọi API lấy danh sách artist từ backend Deezer

        SearchView searchView = findViewById(R.id.searchArtist);
        Button btnDone = findViewById(R.id.btnDone);

        //=========================Edit color in Search view======================
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(id);
        searchEditText.setHintTextColor(Color.LTGRAY);

        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
        }
        //========================================================================
        //========================================================================
        adapter = new ArtistAdapter(artistList, artist -> {
            artist.setSelected(!artist.isSelected());
            if (artist.isSelected()) {
                if (!selectedArtists.contains(artist))
                    selectedArtists.add(artist);
            } else {
                selectedArtists.remove(artist);
            }
            adapter.notifyDataSetChanged();
        });
        recycler.setAdapter(adapter);

        //=========================Logic Nut button Done======================
        btnDone.setOnClickListener(v -> {
            List<Artist> chooseArt = new ArrayList<>();
            for (Artist artist : artistList) {
                if (artist.isSelected()) {
                    chooseArt.add(artist);
                }
            }
            // Kiểm tra phải chọn ít nhất 2 artist
            if (chooseArt.size() < 2) {
                Toast.makeText(this, "Please select at least 2 artists", Toast.LENGTH_SHORT).show();
                return;
            }
            // Lưu selectedArtists vào SQLite trong thread riêng
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                ArtistDAO dao = db.artistDAO();

                for (Artist artist : chooseArt) {
                    dao.insert(artist);
                }

                // Kiểm tra kết quả
                List<Artist> list = artistDao.getAll();
                for (Artist a : list) {
                    Log.d("DB_ARTIST", "ID: " + a.getArtistId() + " - Name: " + a.getArtistName());
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved " + chooseArt.size() + " artists", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChooseArtistActivity.this, MainActivity.class));
                    finish();
                });
            }).start();
        });
        //======================================================================
        //=========================Logic Search View======================
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                for (Artist artist : artistList) {
                    if (artist.isSelected()) {
                        selectedArtists.add(artist);
                    }
                }
                if (!query.isEmpty()) searchArtist(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadArtists(); // load lại danh sách random ban đầu
                }
                return false;
            }
        });
        //======================================================================
    }
    private void loadArtists() {

        apiService.getRandomArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArtistResponse artistResponse = response.body();
                    if (artistResponse.getData() != null) {
                        artistList.addAll(artistResponse.getData());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("API_ERROR", "artistResponse.getData() == null");
                    }
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Toast.makeText(ChooseArtistActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });

    }

    private void searchArtist(String query) {
        // Gọi API /api/deezer/artists/search?query=..
        apiService.searchArtists(query).enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                ArtistResponse artistResponse = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    artistList.clear();
                    artistList.addAll(artistResponse.getArtistSearch());

                    // Giữ lại các artist đã chọn, tránh trùng ID
                    for (Artist selected : selectedArtists) {
                        boolean exists = false;
                        for (Artist a : artistList) {
                            if (a.getArtistId() == selected.getArtistId()) {
                                a.setSelected(true);
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            artistList.add(0, selected);
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChooseArtistActivity.this, "Không tìm thấy nghệ sĩ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Toast.makeText(ChooseArtistActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}