package com.marttirebane.movieapi.DTO;

import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Past;
import java.util.List;
import java.util.Set;

public class ActorRequest {
   
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Birth date is required.")
    private String birthDate;

    private String description; // Optional field

    private Set<Long> movieIds;
   

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
    public static class ActorMinimalDTO {
        private Long id;
        private String name;
        private String birthDate;

        public ActorMinimalDTO(Long id,String name, String birthDate){
            this.id = id;
            this.name = name;
            this.birthDate = birthDate;

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

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }
    }

    // Detailed Response DTO
    public static class ActorDetailedDTO {
        private Long id;
        private String name;
        private String description;
        private String birthDate;
        private List<MovieRequest.MovieMinimalDTO> featuredMovies;

        public ActorDetailedDTO(Long id,String name, String description, String birthDate, List<MovieRequest.MovieMinimalDTO> featuredMovies) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.birthDate = birthDate;
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

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public List<MovieRequest.MovieMinimalDTO> getFeaturedMovies() {
            return featuredMovies;
        }

        public void setFeaturedMovies(List<MovieRequest.MovieMinimalDTO> featuredMovies) {
            this.featuredMovies = featuredMovies;
        }
    }
}
