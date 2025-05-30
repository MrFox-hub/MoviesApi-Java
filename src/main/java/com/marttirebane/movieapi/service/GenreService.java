package com.marttirebane.movieapi.service;

import com.marttirebane.movieapi.DTO.GenreRequest;
import com.marttirebane.movieapi.DTO.MovieRequest;
import com.marttirebane.movieapi.model.Genre;
import com.marttirebane.movieapi.model.Movie;
import com.marttirebane.movieapi.repository.GenreRepository;
import com.marttirebane.movieapi.repository.MovieRepository;
import com.marttirebane.movieapi.utils.ResourceNotFound;
import com.marttirebane.movieapi.utils.AppUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    // --- Get Methods ---

    public Page<?> getAllGenres(Pageable pageable, boolean detailed) {
        return genreRepository.findAll(pageable).map(genre -> mapGenreToDTO(genre, detailed));
    }

    public Page<GenreRequest.GenreMinimalDTO> getAllGenresMinimal(Pageable pageable) {
        return genreRepository.findAll(pageable).map(genre -> new GenreRequest.GenreMinimalDTO(genre.getId(), genre.getName()));
    }

    public Page<GenreRequest.GenreDetailedDTO> getAllGenresDetailed(Pageable pageable) {
        return genreRepository.findAll(pageable).map(genre ->
                new GenreRequest.GenreDetailedDTO(
                        genre.getId(),
                        genre.getName(),
                        genre.getDescription(),
                        genre.getMovies().stream()
                                .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(),movie.getTitle(), movie.getReleaseYear()))
                                .collect(Collectors.toList())
                ));
    }
    public GenreRequest.GenreDetailedDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Genre with ID " + id + " not found."));
    
        return new GenreRequest.GenreDetailedDTO(
                genre.getId(),
                genre.getName(),
                genre.getDescription(),
                genre.getMovies().stream()
                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(), movie.getTitle(), movie.getReleaseYear()))
                        .collect(Collectors.toList())
        );
    }
    


    public Page<?> searchGenresByName(String name, Pageable pageable, boolean detailed) {
        return genreRepository.findByNameContainingIgnoreCase(name, pageable).orElse(Page.empty())
                .map(genre -> {
                    if (detailed) {
                        return new GenreRequest.GenreDetailedDTO(
                                genre.getId(),
                                genre.getName(),
                                genre.getDescription() != null ? genre.getDescription() : "No description provided",
                                genre.getMovies().stream()
                                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(), movie.getTitle(), movie.getReleaseYear()))
                                        .collect(Collectors.toList())
                        );
                    } else {
                        return new GenreRequest.GenreMinimalDTO(genre.getId(),genre.getName());
                    }
                });
    }
    

    public String createGenre(GenreRequest genreRequest) {
        if (genreRequest.getId() != null) {
            throw new RuntimeException("Our system assigns IDs automatically. Please omit the ID field.");
        }

        if (genreRequest.getName() == null || genreRequest.getName().trim().isEmpty()) {
            throw new RuntimeException("Genre name is required.");
        }

        AppUtils.checkDuplicateGenre(genreRequest.getName(), genreRepository);

        String description = genreRequest.getDescription() != null && !genreRequest.getDescription().trim().isEmpty()
                ? genreRequest.getDescription()
                : "No description provided.";

        Genre genre = new Genre();
        genre.setName(genreRequest.getName());
        genre.setDescription(description);

        if (genreRequest.getMovieIds() != null && !genreRequest.getMovieIds().isEmpty()) {
            Set<Movie> movies = genreRequest.getMovieIds().stream()
                    .map(movieId -> movieRepository.findById(movieId)
                            .orElseThrow(() -> new ResourceNotFound("Movie with ID " + movieId + " not found")))
                    .collect(Collectors.toSet());
            genre.setMovies(new ArrayList<>(movies));
        }

        genreRepository.save(genre);

        if (genreRequest.getMovieIds() != null && !genreRequest.getMovieIds().isEmpty()) {
            return "Added genre with the following details: Name - " + genre.getName() + ", Description - " + genre.getDescription()
                    + ", Associated Movies - " + genreRequest.getMovieIds();
        } else {
            return "Added genre with the following details: Name - " + genre.getName() + ", Description - " + genre.getDescription();
        }
    }

    public Optional<Genre> updateGenre(Long id, GenreRequest updatedGenre) {
        return genreRepository.findById(id).map(existingGenre -> {

            if (updatedGenre.getId() != null && !id.equals(updatedGenre.getId())) {
                throw new RuntimeException("The ID field is immutable and cannot be updated.");
            }

            if (updatedGenre.getName() != null && !updatedGenre.getName().equals(existingGenre.getName())) {
                AppUtils.checkDuplicateGenre(updatedGenre.getName(), genreRepository);
                existingGenre.setName(updatedGenre.getName());
            }

            if (updatedGenre.getDescription() != null) {
                existingGenre.setDescription(updatedGenre.getDescription());
            }

            return genreRepository.save(existingGenre);
        });
    }

    public Optional<String> deleteGenre(Long id, boolean force) {
        return genreRepository.findById(id).map(genre -> {
            if (!force && !genre.getMovies().isEmpty()) {
                throw new RuntimeException("Cannot delete genre '" + genre.getName() + "' because it is associated with "
                        + genre.getMovies().size() + " movie(s). Use force=true to override.");
            }

            if (force) {
                genre.getMovies().forEach(movie -> {
                    movie.getGenres().remove(genre);
                    movieRepository.save(movie);
                });
            }

            genreRepository.deleteById(id);
            return "Deleted genre with ID " + id + " successfully.";
        });
    }

    // Helper method to map Genre to DTO dynamically based on 'detailed' flag
private Object mapGenreToDTO(Genre genre, boolean detailed) {
    if (detailed) {
        return new GenreRequest.GenreDetailedDTO(
                genre.getId(),
                genre.getName(),
                genre.getDescription() != null ? genre.getDescription() : "No description provided",
                genre.getMovies().stream()
                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(), movie.getTitle(), movie.getReleaseYear()))
                        .collect(Collectors.toList())
        );
    } else {
        return new GenreRequest.GenreMinimalDTO(genre.getId(), genre.getName());
    }
}

}
