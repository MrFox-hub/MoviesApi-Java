package com.marttirebane.movieapi.controller;

import com.marttirebane.movieapi.DTO.MovieRequest;
import com.marttirebane.movieapi.service.MovieService;
import com.marttirebane.movieapi.utils.AppUtils;
import com.marttirebane.movieapi.utils.ResourceNotFound;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // --- GET Endpoints ---
    @GetMapping
    public Map<String, Object> getAllMovies(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size,
                                            @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> movies = movieService.getAllMovies(pageable, detailed);
        return AppUtils.simplifyPageResponse(movies);
    }

    @GetMapping("/{id}")
public MovieRequest.MovieDetailedDTO getMovieById(@PathVariable Long id) {
    return movieService.getMovieById(id)
        .orElseThrow(() -> new ResourceNotFound("Movie with ID " + id + " not found."));
}


    @GetMapping("/search")
public Map<String, Object> searchMovies(@RequestParam String title,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size,
                                         @RequestParam(defaultValue = "false") boolean detailed) {
    if (title.trim().isEmpty()) {
        throw new RuntimeException("The search query parameter 'title' cannot be empty.");
    }
    AppUtils.validatePagination(page, size);
    Pageable pageable = PageRequest.of(page, size);

    Page<?> movies = movieService.searchMoviesByTitle(title, pageable, detailed);
    if (movies.isEmpty()) {
        throw new ResourceNotFound("No movies found matching the title.");
    }
    
    //Do not wrap Page<?> in Optional for methods that use JPA's Pageable. 
    //These methods naturally handle the "null or empty" scenario by providing an empty Page.

    return AppUtils.simplifyPageResponse(movies);
}


    @GetMapping(params = "genre")
    public Map<String, Object> filterMoviesByGenre(@RequestParam Long genre,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size,
                                                   @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> movies = movieService.getMoviesByGenre(genre, pageable, detailed);
        return AppUtils.simplifyPageResponse(movies);
    }

    @GetMapping(params = "actor")
    public Map<String, Object> filterMoviesByActor(@RequestParam Long actor,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size,
                                                   @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> movies = movieService.getMoviesByActor(actor, pageable, detailed);
        return AppUtils.simplifyPageResponse(movies);
    }

    @GetMapping(params = "year")
    public Map<String, Object> filterMoviesByYear(@RequestParam int year,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "50") int size,
                                                  @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> movies = movieService.getMoviesByYear(year, pageable, detailed);
        return AppUtils.simplifyPageResponse(movies);
    }

    // --- POST Endpoint ---
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createMovie(@RequestBody @Valid MovieRequest movieRequest) {
        String resultMessage = movieService.createMovie(movieRequest);
        return Map.of("message", resultMessage);
    }

    // --- PATCH Endpoint ---
    @PatchMapping("/{id}")
    public String updateMovie(@PathVariable Long id, @RequestBody @Valid MovieRequest updatedMovie) {
        return movieService.updateMovie(id, updatedMovie)
                .map(movie -> "Movie updated successfully: " + movie.getTitle())
                .orElseThrow(() -> new ResourceNotFound("Movie with ID " + id + " not found."));
    }

    // --- DELETE Endpoint ---
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        movieService.deleteMovie(id, force)
                .orElseThrow(() -> new ResourceNotFound("Movie with ID " + id + " not found."));
    }
}
