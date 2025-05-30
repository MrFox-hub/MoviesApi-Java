package com.marttirebane.movieapi.DTO;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;


public class GenreRequest {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description; // Optional field

    private Set<Long> movieIds;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(Set<Long> movieIds) {
        this.movieIds = movieIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Minimal Response DTO
    public static class GenreMinimalDTO {
        private Long id;
        private String name;

        public GenreMinimalDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Detailed Response DTO
    public static class GenreDetailedDTO {
        private Long id;
        private String name;
        private String description;
        private List<MovieRequest.MovieMinimalDTO> featuredMovies;

        public GenreDetailedDTO(Long id, String name, String description, List<MovieRequest.MovieMinimalDTO> featuredMovies) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.featuredMovies = featuredMovies;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<MovieRequest.MovieMinimalDTO> getFeaturedMovies() {
            return featuredMovies;
        }

        public void setFeaturedMovies(List<MovieRequest.MovieMinimalDTO> featuredMovies) {
            this.featuredMovies = featuredMovies;
        }
    }
}
