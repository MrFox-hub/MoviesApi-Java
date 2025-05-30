package com.marttirebane.movieapi.repository;

import com.marttirebane.movieapi.model.Genre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Boolean> existsByNameIgnoreCase(String name);

    Optional<Page<Genre>> findByNameContainingIgnoreCase(String name, Pageable pageable);

    //Cant Build due to Iterable<Long> Optional<Page<Genre>> findAllById(Iterable<Long> ids, Pageable pageable);
    @Query("SELECT g FROM Genre g WHERE g.id IN :ids")
    Page<Genre> findByIds(@Param("ids") List<Long> ids, Pageable pageable);

    //Jpa Messing with this Optional<Page<Genre>> findAll(Pageable pageable);
}
