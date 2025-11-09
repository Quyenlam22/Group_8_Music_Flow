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
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);
        db = AppDatabase.getInstance(this);
        ArtistDAO artistDao = db.artistDAO();

        recycler = findViewById(R.id.recyclerArtists);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        apiService = RetrofitClient.getApiService();

        loadArtists(); // gọi API lấy danh sách artist từ backend Deezer

        SearchView searchView = findViewById(R.id.searchArtist);
        Button btnDone = findViewById(R.id.btnDone);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(id);

        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
            // Tùy chọn: Đặt màu gợi ý (hint text) thành màu xám nhạt nếu cần
            // searchEditText.setHintTextColor(Color.LTGRAY);
        }

        adapter = new ArtistAdapter(artistList, artist -> {
            artist.setSelected(!artist.isSelected());
            adapter.notifyDataSetChanged();
        });
        recycler.setAdapter(adapter);

        btnDone.setOnClickListener(v -> {
            List<Artist> selectedArtists = new ArrayList<>();
            for (Artist artist : artistList) {
                if (artist.isSelected()) {
                    selectedArtists.add(artist);
                }
            }
            // Lưu selectedArtists vào SQLite trong thread riêng
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                ArtistDAO dao = db.artistDAO();

                for (Artist artist : selectedArtists) {
                    dao.insert(artist);
                }

                // Kiểm tra kết quả
                List<Artist> list = artistDao.getAll();
                for (Artist a : list) {
                    Log.d("DB_ARTIST", "ID: " + a.getArtistId() + " - Name: " + a.getName());
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved " + selectedArtists.size() + " artists", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChooseArtistActivity.this, MainActivity.class));
                    finish();
                });
            }).start();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchArtist(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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

    private void searchArtist(String keyword) {
        // Gọi API /api/deezer/artists/search?query=...
    }
}