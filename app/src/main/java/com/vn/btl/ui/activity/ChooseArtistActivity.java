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
    private List<Artist> selectedArtists = new ArrayList<>();
    private AppDatabase db;
    private ArtistDAO artistDao;
    private ApiService apiService;
    private Button btnDone;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_artist);

        db = AppDatabase.getInstance(this);
        artistDao = db.artistDAO();
        apiService = RetrofitClient.getApiService();

        recycler = findViewById(R.id.recyclerArtists);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        searchView = findViewById(R.id.searchArtist);
        btnDone = findViewById(R.id.btnDone);

        adapter = new ArtistAdapter(artistList, artist -> {
            artist.setSelected(!artist.isSelected());
            if (artist.isSelected()) {
                if (!selectedArtists.contains(artist)) selectedArtists.add(artist);
            } else {
                selectedArtists.remove(artist);
            }
            adapter.notifyItemChanged(artistList.indexOf(artist));
        });

        recycler.setAdapter(adapter);

        // ====================== Clear old data ======================
        new Thread(() -> {
            artistDao.deleteAll();
            Log.d("DB_ARTIST", "Deleted old artist data");
            runOnUiThread(this::loadArtists); // load artist after delete
        }).start();
        // ============================================================

        setupSearchView();
        setupDoneButton();
    }

    private void setupSearchView() {
        // Customize search text color
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(id);
        if (searchEditText != null) {
            searchEditText.setHintTextColor(Color.LTGRAY);
            searchEditText.setTextColor(Color.WHITE);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) searchArtist(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) loadArtists(); // reload random artists
                return false;
            }
        });
    }

    private void setupDoneButton() {
        btnDone.setOnClickListener(v -> {
            List<Artist> chosen = new ArrayList<>();
            for (Artist artist : artistList) {
                if (artist.isSelected()) chosen.add(artist);
            }

            if (chosen.size() < 2) {
                Toast.makeText(this, "Please select at least 2 artists", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save selected artists in SQLite
            new Thread(() -> {
                for (Artist artist : chosen) {
                    artistDao.insert(artist);
                }

                Log.d("DB_ARTIST", "Saved " + chosen.size() + " artists");
                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved " + chosen.size() + " artists", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChooseArtistActivity.this, MainActivity.class));
                    finish();
                });
            }).start();
        });
    }

    private void loadArtists() {
        apiService.getRandomArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        artistList.clear();
                        artistList.addAll(response.body().getData());
                        // Keep previously selected artists
                        for (Artist selected : selectedArtists) {
                            for (Artist a : artistList) {
                                if (a.getArtistId() == selected.getArtistId()) {
                                    a.setSelected(true);
                                    break;
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(ChooseArtistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void searchArtist(String query) {
        apiService.searchArtists(query).enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        artistList.clear();
                        if (response.body().getArtistSearch() != null)
                            artistList.addAll(response.body().getArtistSearch());

                        // Keep previously selected
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
                                selected.setSelected(true);
                                artistList.add(0, selected);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChooseArtistActivity.this, "No artist found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(ChooseArtistActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
