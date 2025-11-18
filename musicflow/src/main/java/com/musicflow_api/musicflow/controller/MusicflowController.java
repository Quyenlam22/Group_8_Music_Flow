package com.musicflow_api.musicflow.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.musicflow_api.musicflow.service.MusicflowService;

@RestController
@RequestMapping("/api/musicflow")
public class MusicflowController {
	private final MusicflowService musicflowService;

	public MusicflowController(MusicflowService musicflowService) {
		super();
		this.musicflowService = musicflowService;
	}
	
    @GetMapping("/search")
    public Map<String, Object> searchTracks(@RequestParam String q) {
        return musicflowService.searchTracks(q);
    }
    @GetMapping("/artists/random")
    public Map<String, Object> getRandomArtists() {
        return musicflowService.getRandomArtists();
    }

    @GetMapping("/artist/{id}/top")
    public Map<String, Object> getArtistTop(@PathVariable String id) {
        return musicflowService.getArtistTopTracks(id);
    }

    @GetMapping("albums/random")
    public  Map<String, Object> getRandomAlbums() {
        return musicflowService.getRandomAlbums();
    }

    @GetMapping("albums/{albumId}")
    public Map<String, Object> getAlbumTracks(@PathVariable Long albumId) {
        return musicflowService.getAlbumTracks(albumId);
    }
}
