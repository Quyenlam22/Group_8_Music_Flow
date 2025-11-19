package com.vn.btl.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class ArtistsFragment extends Fragment {

    private ArtistAdapter adapter;

    // Giữ danh sách artist (kết hợp giữa hai nhánh)
    private final List<Artist> artistList = new ArrayList<>();

    // Các biến từ nhánh develop
    private ArtistDAO artistDAO;
    private ApiService apiService;
    private ImageView btnAdd, btnDelete, btnRefresh, btnSearch;
    private SearchView searchView;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root,
                              @Nullable Bundle savedInstanceState) {

        RecyclerView rv = root.findViewById(R.id.rv_artists);

        btnAdd = root.findViewById(R.id.btnAddArtist);
        btnDelete = root.findViewById(R.id.btnDeleteArtist);
        btnRefresh = root.findViewById(R.id.btnRefreshArtist);
        btnSearch = root.findViewById(R.id.btzSearchArtist);
        searchView = root.findViewById(R.id.svArtist);

        //=========================Edit color in Search view======================
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchEditText = searchView.findViewById(id);

        if (searchEditText != null) {
            searchEditText.setHintTextColor(Color.LTGRAY);
            searchEditText.setTextColor(Color.WHITE);
        }
        //========================================================================

        db = AppDatabase.getInstance(requireContext());
        artistDAO = db.artistDAO();

        rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        adapter = new ArtistAdapter(artistList, requireContext());
        rv.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        // Load random recommend artists
        loadRecommendArtist();

        // Tắt / bật searchView
        btnSearch.setOnClickListener(v -> {
            if (searchView.getVisibility() == View.GONE) {
                searchView.setVisibility(View.VISIBLE);
                searchView.setIconified(false);
                searchView.requestFocus();
            } else {
                searchView.setVisibility(View.GONE);
            }
        });

        //=========================Logic Search View======================
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) searchArtist(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //======================================================================

        // Add artist to favorite (Room)
        btnAdd.setOnClickListener(v -> {
            List<Artist> chooseArt = new ArrayList<>();
            for (Artist artist : artistList) {
                if (artist.isSelected()) chooseArt.add(artist);
            }

            new Thread(() -> {
                for (Artist artist : chooseArt) {
                    artistDAO.insert(artist);
                }

                List<Artist> list = artistDAO.getAll();
                for (Artist a : list) {
                    Log.d("DB_ARTIST", "ID: " + a.getArtistId() + " - Name: " + a.getArtistName());
                }
            }).start();

            Toast.makeText(requireContext(), "Added " + chooseArt.size() + " artist you like", Toast.LENGTH_SHORT).show();
        });

        // Delete artist from favorite
        btnDelete.setOnClickListener(v -> {
            List<Artist> chooseArt = new ArrayList<>();
            for (Artist artist : artistList) {
                if (artist.isSelected()) chooseArt.add(artist);
            }

            new Thread(() -> {
                int deleted = 0;
                for (Artist a : chooseArt) {
                    Artist existing = artistDAO.findByArtistId(a.getArtistId());
                    if (existing != null) {
                        artistDAO.delete(existing);
                        deleted++;
                    }
                }

                List<Artist> list = artistDAO.getAll();
                for (Artist a : list) {
                    Log.d("DB_ARTIST", "ID: " + a.getArtistId() + " - Name: " + a.getArtistName());
                }

                int notFound = chooseArt.size() - deleted;

                Activity activity = getActivity();
                if (activity != null && !activity.isDestroyed()) {
                    int finalDeleted = deleted;
                    activity.runOnUiThread(() -> {
                        if (finalDeleted > 0)
                            Toast.makeText(activity, "Deleted " + finalDeleted + " artist(s)", Toast.LENGTH_SHORT).show();

                        if (notFound > 0)
                            Toast.makeText(activity, "Have " + notFound + " artist(s) not in your favorite list", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // Refresh recommended list
        btnRefresh.setOnClickListener(v -> {
            artistList.clear();
            loadRecommendArtist();
        });
    }

    private void searchArtist(String query) {
        apiService.searchArtists(query).enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    artistList.clear();
                    artistList.addAll(response.body().getArtistSearch());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    private void loadRecommendArtist() {
        apiService.getRandomArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API_ERROR", "Response NULL or failed");
                    return;
                }

                ArtistResponse artistResponse = response.body();

                if (artistResponse.getArtists() != null) {
                    artistList.clear();
                    artistList.addAll(artistResponse.getArtists());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "artistResponse.getArtists() == null");
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Log.e("API_ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}
