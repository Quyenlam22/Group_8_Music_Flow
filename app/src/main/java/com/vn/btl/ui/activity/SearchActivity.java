package com.vn.btl.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.SearchHistoryAdapter;
import com.vn.btl.ui.adapter.TrackAdapter;
import com.vn.btl.model.Track;
import com.vn.btl.utils.DatabaseHelper;
import com.vn.btl.ui.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private SearchViewModel viewModel;
    private DatabaseHelper databaseHelper;

    private EditText searchEditText;
    private ImageView searchIcon, micIcon, refreshButton;
    private LinearLayout searchHistoryLayout;
    private ListView searchHistoryListView;
    private LinearLayout mainContentLayout;
    private RecyclerView trendingRecyclerView;
    private TextView resultsTitle;
    private ProgressBar loadingProgressBar;

    private TrackAdapter trackAdapter;
    private SearchHistoryAdapter searchHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        searchEditText = findViewById(R.id.searchEditText);
        searchIcon = findViewById(R.id.searchIcon);
        micIcon = findViewById(R.id.micIcon);
        refreshButton = findViewById(R.id.refreshButton);
        searchHistoryLayout = findViewById(R.id.searchHistoryLayout);
        searchHistoryListView = findViewById(R.id.searchHistoryListView);
        mainContentLayout = findViewById(R.id.mainContentLayout);
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        resultsTitle = findViewById(R.id.resultsTitle);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        databaseHelper = new DatabaseHelper(this);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.init(databaseHelper);

        // Observe trending tracks
        viewModel.getTrendingTracks().observe(this, tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                trackAdapter.updateData(tracks);
                showMainContent();
            }
        });

        // Observe search results
        viewModel.getSearchResults().observe(this, tracks -> {
            if (tracks != null && !tracks.isEmpty()) {
                trackAdapter.updateData(tracks);
                resultsTitle.setText("Search Results");
                showMainContent();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            refreshButton.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe current genre
        viewModel.getCurrentGenre().observe(this, genre -> {
            if (genre != null && !genre.isEmpty()) {
                resultsTitle.setText("Trending " + genre.substring(0, 1).toUpperCase() + genre.substring(1) + " Music");
            }
        });
    }

    private void setupRecyclerView() {
        trackAdapter = new TrackAdapter(new ArrayList<>(), track -> {
            // Handle track click - play music or show details
            Toast.makeText(this, "Playing: " + track.getTitle(), Toast.LENGTH_SHORT).show();
        });

        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trendingRecyclerView.setAdapter(trackAdapter);
    }

    private void setupListeners() {
        // Search text change listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    showSearchHistory();
                } else {
                    hideSearchHistory();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Search icon click
        searchIcon.setOnClickListener(v -> performSearch());

        // Refresh button click - load new random trending music
        refreshButton.setOnClickListener(v -> {
            viewModel.loadRandomTrendingTracks();
        });

        // Mic icon click (voice search)
        micIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Voice search feature coming soon", Toast.LENGTH_SHORT).show();
        });


        // Search history list view
        searchHistoryListView.setOnItemClickListener((parent, view, position, id) -> {
            String historyItem = viewModel.getSearchHistory().get(position);
            searchEditText.setText(historyItem);
            performSearch();
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            viewModel.searchTracks(query);
            hideSearchHistory();
        }
    }

    private void showSearchHistory() {
        List<String> history = viewModel.getSearchHistory();
        if (!history.isEmpty()) {
            searchHistoryAdapter = new SearchHistoryAdapter(this, history);
            searchHistoryListView.setAdapter(searchHistoryAdapter);
            searchHistoryLayout.setVisibility(View.VISIBLE);
            mainContentLayout.setVisibility(View.GONE);
        }
    }

    private void hideSearchHistory() {
        searchHistoryLayout.setVisibility(View.GONE);
        mainContentLayout.setVisibility(View.VISIBLE);
    }

    private void showMainContent() {
        searchHistoryLayout.setVisibility(View.GONE);
        mainContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load search history when activity resumes
        if (searchEditText.getText().toString().isEmpty()) {
            showSearchHistory();
        }
    }
}