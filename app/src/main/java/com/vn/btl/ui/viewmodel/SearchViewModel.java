package com.vn.btl.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.vn.btl.model.SearchResponse;
import com.vn.btl.model.Track;
import com.vn.btl.repository.SearchRepository;
import com.vn.btl.utils.DatabaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private SearchRepository repository;
    private DatabaseHelper databaseHelper;
    private MutableLiveData<List<Track>> trendingTracks = new MutableLiveData<>();
    private MutableLiveData<List<Track>> searchResults = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> currentGenre = new MutableLiveData<>();

    // Danh sách các thể loại nhạc phổ biến
    private List<String> genres = Arrays.asList(
            "pop", "rock", "hip hop", "jazz", "classical", "electronic",
            "r&b", "country", "reggae", "metal", "blues", "k-pop",
            "latin", "indie", "rap", "dance", "folk", "soul"
    );

    public void init(DatabaseHelper dbHelper) {
        this.repository = new SearchRepository();
        this.databaseHelper = dbHelper;
        loadRandomTrendingTracks();
    }

    public void loadRandomTrendingTracks() {
        isLoading.setValue(true);
        String randomGenre = getRandomGenre();
        currentGenre.setValue(randomGenre);

        repository.getTrendingTracks(randomGenre).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Track> tracks = response.body().getData();
                    if (tracks != null && !tracks.isEmpty()) {
                        trendingTracks.setValue(tracks);
                    } else {
                        errorMessage.setValue("No tracks found for genre: " + randomGenre);
                        // Thử load lại với thể loại khác
                        loadRandomTrendingTracks();
                    }
                } else {
                    errorMessage.setValue("Failed to load trending tracks");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public void searchTracks(String query) {
        if (query.isEmpty()) return;

        isLoading.setValue(true);
        // Save to search history
        databaseHelper.addSearchQuery(query);

        repository.searchTracks(query).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Search failed");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Search error: " + t.getMessage());
            }
        });
    }

    public List<String> getSearchHistory() {
        return databaseHelper.getAllSearchHistory();
    }

    public void clearSearchHistory() {
        databaseHelper.clearSearchHistory();
    }

    public void deleteSearchQuery(String query) {
        databaseHelper.deleteSearchQuery(query);
    }

    private String getRandomGenre() {
        Random random = new Random();
        return genres.get(random.nextInt(genres.size()));
    }

    // LiveData getters
    public LiveData<List<Track>> getTrendingTracks() { return trendingTracks; }
    public LiveData<List<Track>> getSearchResults() { return searchResults; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getCurrentGenre() { return currentGenre; }
}