package com.marttirebane.movieapi.controller;

import com.marttirebane.movieapi.DTO.GenreRequest;
import com.marttirebane.movieapi.service.GenreService;
import com.marttirebane.movieapi.utils.AppUtils;
import com.marttirebane.movieapi.utils.ResourceNotFound;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // GET Endpoints
    @GetMapping
    public Map<String, Object> getAllGenres(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size,
                                            @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> genres = genreService.getAllGenres(pageable, detailed); // Ensure service returns Page<>
        return AppUtils.simplifyPageResponse(genres);
    }

    @GetMapping("/{id}")
    public GenreRequest.GenreDetailedDTO getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }


    @GetMapping("/search")
    public Map<String, Object> searchGenresByName(@RequestParam String name,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size,
                                                   @RequestParam(defaultValue = "false") boolean detailed) {
        if (name.trim().isEmpty()) {
            throw new RuntimeException("The search query parameter 'name' cannot be empty.");
        }
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> genres = genreService.searchGenresByName(name, pageable, detailed); // Ensure service returns Page<>
        return AppUtils.simplifyPageResponse(genres);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createGenre(@RequestBody @Valid GenreRequest genreRequest) {
        String resultMessage = genreService.createGenre(genreRequest);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", resultMessage);
        return response;
    }

    

    // PATCH Endpoint
    @PatchMapping("/{id}")
    public String updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest updatedGenre) {
        return genreService.updateGenre(id, updatedGenre)
                .map(genre -> "Genre updated successfully: " + genre.getName())
                .orElseThrow(() -> new ResourceNotFound("Genre with ID " + id + " not found."));
    }

    // DELETE Endpoint
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        genreService.deleteGenre(id, force)
                .orElseThrow(() -> new ResourceNotFound("Genre with ID " + id + " not found."));
    }
}
