package com.marttirebane.movieapi.repository;

import com.marttirebane.movieapi.model.Actor;
import com.marttirebane.movieapi.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    // Check if an actor exists by name (case-insensitive)
    Optional<Boolean> existsByNameIgnoreCase(String name);

    // Find actors by name containing a search string, with pagination
    Page<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find actors associated with a specific movie, with pagination
    Page<Actor> findByMoviesContaining(Movie movie, Pageable pageable);

    // Custom query for finding actors by multiple IDs with pagination
    @Query("SELECT a FROM Actor a WHERE a.id IN :ids")
    Page<Actor> findByIds(@Param("ids") List<Long> ids, Pageable pageable);

    //Jpa messing it up : Optional<Page<Actor>> findAll(Pageable pageable);
}


    
