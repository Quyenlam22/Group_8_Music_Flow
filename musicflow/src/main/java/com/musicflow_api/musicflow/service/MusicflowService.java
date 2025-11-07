package com.musicflow_api.musicflow.service;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MusicflowService {
	private final String CLIENT_ID = "d339aca9c64d4bd2b3b285fdb087cfbc";
	private final String CLIENT_SECRET = "23f3e80bb62c4fababdaffd9c603b917";

	private String getAccessToken() {
		String url = "https://accounts.spotify.com/api/token";
		RestTemplate restTemplate = new RestTemplate();

		String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
		
		return (String) response.getBody().get("access_token");
	}
	// lay danh sach cac bai hat trong playlist 
	public Map getFeaturedPlaylists() throws Exception {
        String token = getAccessToken();
		String url = "https://api.spotify.com/v1/playlists/54IZ0BTaEUG0FWnV2IICAc";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		Map<String, Object> body = response.getBody();

	    // Gọi hàm loại bỏ key "available_markets" khỏi toàn bộ Map
	    removeAvailableMarkets(body);

	    return body;
    }
	public Map searchTracks(String keyword) {
		String token = getAccessToken();
		String url = "https://api.spotify.com/v1/search?q=" + keyword + "&type=track&limit=20";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		Map<String, Object> body = response.getBody();

	    removeAvailableMarkets(body);

	    return body;
	}
	public Map<String, Object> getRandomAlbums() throws Exception {
        String token = getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        // random 1 ký tự rồi tìm album theo ký tự đó
        String[] chars = {"a","e","i","o","u","b","c","k","p","g","y","m","w","n","t","s","r"};
        String randomKey = chars[new Random().nextInt(chars.length)];

        String url = "https://api.spotify.com/v1/search"
                + "?q=" + randomKey
                + "&type=album"
                + "&market=VN"
                + "&limit=40";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();

	    removeAvailableMarkets(body);

	    return body;
    }
	public Map<String, Object> getTracksByAlbum(String albumId) throws Exception {
	    String token = getAccessToken(); 

	    String url = "https://api.spotify.com/v1/albums/" + albumId + "/tracks?market=VN&limit=30";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer " + token);

	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    RestTemplate restTemplate = new RestTemplate();

	    ResponseEntity<Map> response =
	            restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

	    Map<String, Object> body = response.getBody();

	    removeAvailableMarkets(body);

	    return body;
	}
	// Hàm đệ quy để xóa "available_markets" ở mọi cấp độ
	@SuppressWarnings("unchecked")
	private void removeAvailableMarkets(Map<String, Object> map) {
	    if (map == null) return;

	    map.remove("available_markets"); // Xoa key available markets

	    for (Object value : map.values()) {
	        if (value instanceof Map) {
	            removeAvailableMarkets((Map<String, Object>) value);
	        } else if (value instanceof List) {
	            for (Object item : (List<?>) value) {
	                if (item instanceof Map) {
	                    removeAvailableMarkets((Map<String, Object>) item);
	                }
	            }
	        }
	    }
	}
}
