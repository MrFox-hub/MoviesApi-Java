package com.marttirebane.movieapi.repository;

import com.marttirebane.movieapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Boolean> existsByTitleIgnoreCase(String title);

    Optional<Page<Movie>> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    Optional<Page<Movie>> findByGenreId(@Param("genreId") Long genreId, Pageable pageable);
    

    @Query("SELECT m FROM Movie m JOIN m.actors a WHERE a.id = :actorId")
    Optional<Page<Movie>> findByActorId(@Param("actorId") Long actorId, Pageable pageable);
   
    
    @Query("SELECT m FROM Movie m WHERE m.releaseYear = :releaseYear")
    Optional<Page<Movie>> findByReleaseYear(@Param("releaseYear") Integer releaseYear, Pageable pageable);
    
    //Jpa issues Optional<Page<Movie>> findAll(Pageable pageable);
}
