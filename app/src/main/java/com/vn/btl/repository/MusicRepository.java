package com.vn.btl.repository;

import com.vn.btl.model.PlaylistResponse;
import com.vn.btl.model.Track;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MusicRepository {

    public List<Track> getTracks() {
        List<Track> tracks = new ArrayList<>();

        try {
            // Gọi API Deezer thật
            String apiUrl = "https://api.deezer.com/search?q=Divisive";
            String jsonResponse = makeApiCall(apiUrl);

            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                // Parse JSON response
                Gson gson = new Gson();
                PlaylistResponse response = gson.fromJson(jsonResponse, PlaylistResponse.class);

                if (response != null && response.getData() != null) {
                    tracks = response.getData();
                    System.out.println("✅ API loaded " + tracks.size() + " tracks");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ API Error: " + e.getMessage());
            e.printStackTrace();
        }

        return tracks;
    }

    private String makeApiCall(String apiUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            System.out.println("🔗 API Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String result = scanner.hasNext() ? scanner.next() : "";
                System.out.println("📥 Received " + result.length() + " characters from API");
                return result;
            }

        } catch (Exception e) {
            System.out.println("🔗 Connection Error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}