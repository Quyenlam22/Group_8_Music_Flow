package com.musicflow_api.musicflow.service;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MusicflowService {
	private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

	
    public Map<String, Object> searchTracks(String keyword) {
        String url = "https://api.deezer.com/search?q=" + keyword;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        return response.getBody();
    }

    public Map<String, Object> getRandomArtists() {
        // Random chữ cái từ a-z
    	String[] chars = {"a","e","i","o","u","b","c","k","p","g","y","m","w","n","t","s","r","j","l"};
        String randomChar = chars[new Random().nextInt(chars.length)];
        //char randomChar = (char) ('a' + random.nextInt(26));

        String url = "https://api.deezer.com/search/artist?q=" + randomChar;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        // Lấy danh sách artist
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("data");

        // Random 18 nghệ sĩ để show
        Collections.shuffle(items);
        List<Map<String, Object>> randomArtists = items.stream().limit(18).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("artists", randomArtists);

        return result;
    }

    public Map<String, Object> getArtistTopTracks(String artistId) {
        String url = "https://api.deezer.com/artist/" + artistId + "/top?limit=10";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }
    // Lấy danh sách album nổi bật (để random hoặc hiển thị)
    public Map<String, Object> getRandomAlbums() {
        String url = "https://api.deezer.com/chart/0/albums";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return response;
    }

    // Lấy chi tiết album + danh sách track trong album
    public Map<String, Object> getAlbumTracks(Long albumId) {
        String url = "https://api.deezer.com/album/" + albumId;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return response;
    }
}
