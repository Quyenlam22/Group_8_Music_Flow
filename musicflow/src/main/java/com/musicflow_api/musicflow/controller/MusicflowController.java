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
    public Map searchTracks(@RequestParam String q) {
        return musicflowService.searchTracks(q);
    }
    @GetMapping("/featured")
    public Map getFeaturedPlaylists() throws Exception {
        return musicflowService.getFeaturedPlaylists();
    }
    @GetMapping("/albums/random")
    public Map randomAlbums() throws Exception {
    	 return musicflowService.getRandomAlbums();
    }
    @GetMapping("/albums/{id}/tracks")
    public Map getAlbumTracks(@PathVariable String id) throws Exception {
    	 return musicflowService.getTracksByAlbum(id);
    }
}
