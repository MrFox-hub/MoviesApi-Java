package com.marttirebane.movieapi.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;

public class MovieRequest {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Release year is required")
    private Integer releaseYear;

    @Positive(message = "Duration must be positive")
    private Integer duration;

    private String description; // Optional field

    private Set<Long> actorIds; // IDs of actors to associate
    private Set<Long> genreIds; // IDs of genres to associate

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getActorIds() {
        return actorIds;
    }

    public void setActorIds(Set<Long> actorIds) {
        this.actorIds = actorIds;
    }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Minimal Response DTO
    public static class MovieMinimalDTO {
        private Long id;
        private String title;
        private int releaseYear;

        // Constructor
        public MovieMinimalDTO(Long id, String title, int releaseYear) {
            this.id = id;
            this.title = title;
            this.releaseYear = releaseYear;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
        }
    }

    // Detailed Response DTO
public static class MovieDetailedDTO {
    private Long id;
    private String title;
    private String description;
    private int releaseYear;
    private int duration;
    private List<ActorRequest.ActorMinimalDTO> actors; // Fixed to use ActorMinimalDTO
    private List<GenreRequest.GenreMinimalDTO> genres; // Fixed to use GenreMinimalDTO

    // Constructor
    public MovieDetailedDTO(Long id, String title, String description, int releaseYear, int duration,
                            List<ActorRequest.ActorMinimalDTO> actors, // Fixed type
                            List<GenreRequest.GenreMinimalDTO> genres) { // Fixed type
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.actors = actors;
        this.genres = genres;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<ActorRequest.ActorMinimalDTO> getActors() { // Fixed return type
        return actors;
    }

    public void setActors(List<ActorRequest.ActorMinimalDTO> actors) { // Fixed parameter type
        this.actors = actors;
    }

    public List<GenreRequest.GenreMinimalDTO> getGenres() { // Fixed return type
        return genres;
    }

    public void setGenres(List<GenreRequest.GenreMinimalDTO> genres) { // Fixed parameter type
        this.genres = genres;
    }
}

}
