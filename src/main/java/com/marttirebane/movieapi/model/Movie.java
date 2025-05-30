package com.marttirebane.movieapi.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int duration;
    private int releaseYear;
    private String description;

    @ManyToMany
    @JoinTable(
        name = "movie_actor",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "movie_genre",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    public Movie() {}

    public Movie(String title, int duration, int releaseYear, String description) {
        this.title = title;
        this.duration = duration;
        this.releaseYear = releaseYear;
        this.description = description;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @JsonProperty("actors")
    public List<Map<String, String>> getActorDetails() {
        if (actors == null) return List.of();
        return actors.stream()
                .map(actor -> Map.of(
                        "name", actor.getName(),
                        "description", actor.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @JsonProperty("genres")
    public List<Map<String, String>> getGenreDetails() {
        if (genres == null) return List.of();
        return genres.stream()
                .map(genre -> Map.of(
                        "name", genre.getName(),
                        "description", genre.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
