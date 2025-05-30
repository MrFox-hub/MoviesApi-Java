package com.marttirebane.movieapi.service;

import com.marttirebane.movieapi.DTO.ActorRequest;
import com.marttirebane.movieapi.DTO.GenreRequest;
import com.marttirebane.movieapi.DTO.MovieRequest;
import com.marttirebane.movieapi.model.Actor;
import com.marttirebane.movieapi.model.Genre;
import com.marttirebane.movieapi.model.Movie;
import com.marttirebane.movieapi.repository.ActorRepository;
import com.marttirebane.movieapi.repository.GenreRepository;
import com.marttirebane.movieapi.repository.MovieRepository;
import com.marttirebane.movieapi.utils.ResourceNotFound;
import com.marttirebane.movieapi.utils.AppUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, ActorRepository actorRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.genreRepository = genreRepository;
    }

    
    public Page<?> getAllMovies(Pageable pageable, boolean detailed) {
        return movieRepository.findAll(pageable)
                .map(movie -> detailed ? mapToDetailedDTO(movie) : mapToMinimalDTO(movie));
    }

    public Optional<MovieRequest.MovieDetailedDTO> getMovieById(Long movieId) {
        return movieRepository.findById(movieId)
            .map(movie -> mapToDetailedDTO(movie));
    }
    

    public Page<?> getMoviesByActor(Long actorId, Pageable pageable, boolean detailed) {
        return movieRepository.findByActorId(actorId, pageable)
                              .orElse(Page.empty())
                              .map(movie -> detailed ? mapToDetailedDTO(movie) : mapToMinimalDTO(movie));
    }
    
    public Page<?> getMoviesByGenre(Long genreId, Pageable pageable, boolean detailed) {
        return movieRepository.findByGenreId(genreId, pageable)
                              .orElse(Page.empty())
                              .map(movie -> detailed ? mapToDetailedDTO(movie) : mapToMinimalDTO(movie));
    }
                           
    public Page<?> getMoviesByYear(int year, Pageable pageable, boolean detailed) {
        return movieRepository.findByReleaseYear(year, pageable)
                              .orElse(Page.empty())
                              .map(movie -> detailed ? mapToDetailedDTO(movie) : mapToMinimalDTO(movie));
    }
    

    public Page<?> searchMoviesByTitle(String title, Pageable pageable, boolean detailed) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable)
                .orElse(Page.empty())
                .map(movie -> detailed ? mapToDetailedDTO(movie) : mapToMinimalDTO(movie));
    }

    public String createMovie(MovieRequest movieRequest) {
        if (movieRequest.getId() != null) {
            throw new RuntimeException("Our system assigns IDs automatically. Please omit the ID field.");
        }

        if (movieRequest.getTitle() == null || movieRequest.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Movie title is required.");
        }

        AppUtils.checkDuplicateMovie(movieRequest.getTitle(), movieRepository);

        if (movieRequest.getReleaseYear() <= 0 || movieRequest.getDuration() <= 0) {
            throw new RuntimeException("Invalid release year or duration.");
        }

        String description = movieRequest.getDescription() != null && !movieRequest.getDescription().trim().isEmpty()
                ? movieRequest.getDescription()
                : "No description provided.";

        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setReleaseYear(movieRequest.getReleaseYear());
        movie.setDuration(movieRequest.getDuration());
        movie.setDescription(description);

        if (movieRequest.getActorIds() != null && !movieRequest.getActorIds().isEmpty()) {
            List<Actor> actors = movieRequest.getActorIds().stream()
                    .map(actorId -> actorRepository.findById(actorId)
                            .orElseThrow(() -> new ResourceNotFound("Actor with ID " + actorId + " not found")))
                    .collect(Collectors.toList());
            movie.setActors(actors);
        }

        if (movieRequest.getGenreIds() != null && !movieRequest.getGenreIds().isEmpty()) {
            List<Genre> genres = movieRequest.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFound("Genre with ID " + genreId + " not found")))
                    .collect(Collectors.toList());
            movie.setGenres(genres);
        }

        movieRepository.save(movie);

        return "Movie created with title: " + movie.getTitle();
    }

    public Optional<Movie> updateMovie(Long id, MovieRequest updatedMovieRequest) {
        return movieRepository.findById(id).map(existingMovie -> {

            if (updatedMovieRequest.getId() != null && !id.equals(updatedMovieRequest.getId())) {
                throw new RuntimeException("The ID field is immutable and cannot be updated.");
            }

            if (updatedMovieRequest.getTitle() != null && !updatedMovieRequest.getTitle().equals(existingMovie.getTitle())) {
                AppUtils.checkDuplicateMovie(updatedMovieRequest.getTitle(), movieRepository);
                existingMovie.setTitle(updatedMovieRequest.getTitle());
            }

            if (updatedMovieRequest.getReleaseYear() > 0) {
                existingMovie.setReleaseYear(updatedMovieRequest.getReleaseYear());
            }

            if (updatedMovieRequest.getDuration() > 0) {
                existingMovie.setDuration(updatedMovieRequest.getDuration());
            }

            if (updatedMovieRequest.getDescription() != null) {
                existingMovie.setDescription(updatedMovieRequest.getDescription());
            }

            return movieRepository.save(existingMovie);
        });
    }

    public Optional<String> deleteMovie(Long id, boolean force) {
        return movieRepository.findById(id).map(movie -> {
            if (!force && (!movie.getActors().isEmpty() || !movie.getGenres().isEmpty())) {
                throw new RuntimeException("Cannot delete movie '" + movie.getTitle() + "' because it is associated with actors or genres. Use force=true to override.");
            }

            if (force) {
                movie.getActors().forEach(actor -> {
                    actor.getMovies().remove(movie);
                    actorRepository.save(actor);
                });

                movie.getGenres().forEach(genre -> {
                    genre.getMovies().remove(movie);
                    genreRepository.save(genre);
                });
            }

            movieRepository.deleteById(id);
            return "Deleted movie with ID " + id + " successfully.";
        });
    }

    private MovieRequest.MovieDetailedDTO mapToDetailedDTO(Movie movie) {
        return new MovieRequest.MovieDetailedDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getDescription() != null ? movie.getDescription() : "No description provided",
            movie.getReleaseYear(),
            movie.getDuration(),
            movie.getActors().stream()
                .map(actor -> new ActorRequest.ActorMinimalDTO(actor.getId(), actor.getName(), actor.getBirthDate().toString()))
                .collect(Collectors.toList()),
            movie.getGenres().stream()
                .map(genre -> new GenreRequest.GenreMinimalDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList())
        );
    }
    
    private MovieRequest.MovieMinimalDTO mapToMinimalDTO(Movie movie) {
        return new MovieRequest.MovieMinimalDTO(
            movie.getId(),
            movie.getTitle(),
            movie.getReleaseYear()
        );
    }

}
