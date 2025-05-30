package com.marttirebane.movieapi.service;

import com.marttirebane.movieapi.DTO.ActorRequest;
import com.marttirebane.movieapi.DTO.GenreRequest;
import com.marttirebane.movieapi.DTO.MovieRequest;
import com.marttirebane.movieapi.model.Actor;
import com.marttirebane.movieapi.model.Movie;
import com.marttirebane.movieapi.repository.ActorRepository;
import com.marttirebane.movieapi.repository.MovieRepository;
import com.marttirebane.movieapi.utils.ResourceNotFound;
import com.marttirebane.movieapi.utils.AppUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    // Retrieves all actors with support for minimal and detailed views
    public Page<?> getAllActors(Pageable pageable, boolean detailed) {
        return actorRepository.findAll(pageable).map(actor -> mapActorToDTO(actor, detailed));
    }

    public Page<ActorRequest.ActorMinimalDTO> getAllActorsMinimal(Pageable pageable) {
        return actorRepository.findAll(pageable).map(actor ->
                new ActorRequest.ActorMinimalDTO(actor.getId(),actor.getName(), actor.getBirthDate().toString()));
    }

    public Page<ActorRequest.ActorDetailedDTO> getAllActorsDetailed(Pageable pageable) {
        return actorRepository.findAll(pageable).map(actor -> new ActorRequest.ActorDetailedDTO(
                actor.getId(),
                actor.getName(),
                actor.getDescription(),
                actor.getBirthDate().toString(),
                actor.getMovies().stream()
                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(),movie.getTitle(), movie.getReleaseYear()))
                        .collect(Collectors.toList())
        ));
    }
    public ActorRequest.ActorDetailedDTO getActorById(Long id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Actor with ID " + id + " not found."));
    
        return new ActorRequest.ActorDetailedDTO(
                actor.getId(),
                actor.getName(),
                actor.getDescription(),
                actor.getBirthDate().toString(),
                actor.getMovies().stream()
                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(),movie.getTitle(), movie.getReleaseYear()))
                        .collect(Collectors.toList())
        );
    }
    
    

    public Page<?> getMoviesByActor(Long actorId, Pageable pageable, boolean detailed) {
        return movieRepository.findByActorId(actorId, pageable)
                .orElseThrow(() -> new ResourceNotFound("No movies found for actor with ID " + actorId))
                .map(movie -> detailed
                        ? new MovieRequest.MovieDetailedDTO(
                            movie.getId(),
                            movie.getTitle(),
                            movie.getDescription() != null ? movie.getDescription() : "No description provided",
                            movie.getReleaseYear(),
                            movie.getDuration(),
                            movie.getActors().stream()
                                    .map(actor -> new ActorRequest.ActorMinimalDTO(actor.getId(),actor.getName(), actor.getBirthDate().toString()))
                                    .collect(Collectors.toList()),
                            movie.getGenres().stream()
                                    .map(genre -> new GenreRequest.GenreMinimalDTO(genre.getId(),genre.getName()))
                                    .collect(Collectors.toList())
                        )
                        : new MovieRequest.MovieMinimalDTO(movie.getId(),movie.getTitle(), movie.getReleaseYear())
                );
    }
   
    public Optional<Page<String>> getMovieTitlesAndYears(Long actorId, Pageable pageable) {
        return actorRepository.findById(actorId).map(actor -> actor.getMovieTitlesAndYears(pageable));
    }

    public Page<?> searchActorsByName(String name, Pageable pageable, boolean detailed) {
        return actorRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(actor -> mapActorToDTO(actor, detailed));
    }

    public Page<ActorRequest.ActorMinimalDTO> searchActorsByNameMinimal(String name, Pageable pageable) {
        Page<Actor> actors = actorRepository.findByNameContainingIgnoreCase(name, pageable);
        return actors.map(actor -> new ActorRequest.ActorMinimalDTO(actor.getId(), actor.getName(), actor.getBirthDate().toString()));
    }

    public Page<ActorRequest.ActorDetailedDTO> searchActorsByNameDetailed(String name, Pageable pageable) {
        Page<Actor> actors = actorRepository.findByNameContainingIgnoreCase(name, pageable);
        return actors.map(actor -> new ActorRequest.ActorDetailedDTO(
                actor.getId(),
                actor.getName(),
                actor.getDescription(),
                actor.getBirthDate().toString(),
                actor.getMovies().stream()
                        .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(), movie.getTitle(), movie.getReleaseYear()))
                        .collect(Collectors.toList())
        ));
    }

    public String createActor(ActorRequest actorRequest) {
        // Validate name
        if (actorRequest.getName() == null || actorRequest.getName().trim().isEmpty()) {
            throw new RuntimeException("Actor name is required.");
        }
    
        // Prevent manual ID assignment
        if (actorRequest.getId() != null) {
            throw new RuntimeException("Our system assigns IDs automatically. Please omit the ID field.");
        }
    
        // Check for duplicate actor
        AppUtils.checkDuplicateActor(actorRequest.getName(), actorRepository);
    
        // Validate birth date
        if (actorRequest.getBirthDate() == null) {
            throw new RuntimeException("Birth date is required.");
        }
    
        List<String> validationErrors = new ArrayList<>();
        if (!AppUtils.validateBirthDate(actorRequest.getBirthDate(), validationErrors)) {
            throw new RuntimeException(String.join(", ", validationErrors));
        }
    
        // Set default description if not provided
        String description = actorRequest.getDescription() != null && !actorRequest.getDescription().trim().isEmpty()
                ? actorRequest.getDescription()
                : "No description provided.";
    
        // Create the actor
        Actor actor = new Actor();
        actor.setName(actorRequest.getName());
        actor.setBirthDate(actorRequest.getBirthDate()); // Keep it as a String in the model
        actor.setDescription(description);
    
        // Associate with movies if provided
        if (actorRequest.getMovieIds() != null && !actorRequest.getMovieIds().isEmpty()) {
            Set<Movie> movies = actorRequest.getMovieIds().stream()
                    .map(movieId -> movieRepository.findById(movieId)
                            .orElseThrow(() -> new ResourceNotFound("Movie with ID " + movieId + " not found")))
                    .collect(Collectors.toSet());
            actor.setMovies(new ArrayList<>(movies));
        }
    
        actorRepository.save(actor);
    
        if (actorRequest.getMovieIds() != null && !actorRequest.getMovieIds().isEmpty()) {
            return "Added actor with the following details: Name - " + actor.getName() + ", Birthdate - " + actor.getBirthDate() + ", Description - " + actor.getDescription()
                    + ", Associated Movies - " + actorRequest.getMovieIds();
        } else {
            return "Added actor with the following details: Name - " + actor.getName() + ", Birthdate - " + actor.getBirthDate() + ", Description - " + actor.getDescription();
        }
    }
    

    public Optional<Actor> updateActor(Long id, ActorRequest updatedActorRequest) {
        return actorRepository.findById(id).map(existingActor -> {
    
            // Validate and update fields from ActorRequest
            if (updatedActorRequest.getName() != null && !updatedActorRequest.getName().equals(existingActor.getName())) {
                AppUtils.checkDuplicateActor(updatedActorRequest.getName(), actorRepository);
                existingActor.setName(updatedActorRequest.getName());
            }
    
            // Validate and update birthDate
            if (updatedActorRequest.getBirthDate() != null) {
                List<String> validationErrors = new ArrayList<>();
                if (!AppUtils.validateBirthDate(updatedActorRequest.getBirthDate(), validationErrors)) {
                    throw new RuntimeException("Birth date validation failed: " + String.join("; ", validationErrors));
                }
                // If valid, set the birth date as a String
                existingActor.setBirthDate(updatedActorRequest.getBirthDate());
            }
    
            if (updatedActorRequest.getDescription() != null) {
                existingActor.setDescription(updatedActorRequest.getDescription());
            }
    
            // Update associated movies if provided
            if (updatedActorRequest.getMovieIds() != null && !updatedActorRequest.getMovieIds().isEmpty()) {
                Set<Movie> movies = updatedActorRequest.getMovieIds().stream()
                        .map(movieId -> movieRepository.findById(movieId)
                                .orElseThrow(() -> new ResourceNotFound("Movie with ID " + movieId + " not found")))
                        .collect(Collectors.toSet());
                existingActor.setMovies(new ArrayList<>(movies));
            }
    
            return actorRepository.save(existingActor);
        });
    }
    
    public Optional<String> deleteActor(Long id, boolean force) {
        return actorRepository.findById(id).map(actor -> {
            if (!force && !actor.getMovies().isEmpty()) {
                throw new RuntimeException("Cannot delete actor '" + actor.getName() + "' because they are associated with "
                        + actor.getMovies().size() + " movie(s). Use force=true to override.");
            }
    
            if (force) {
                actor.getMovies().forEach(movie -> {
                    movie.getActors().remove(actor);
                    movieRepository.save(movie);
                });
                System.out.println("Decapitated the head of Hydra: Severed all relationships between actor '" + actor.getName() + "' and associated movies.");
            }
    
            actorRepository.deleteById(id);
            return "Deleted actor with ID " + id + " successfully.";
        });
    }

    // Helper method to map Actor to DTO dynamically based on 'detailed' flag
    private Object mapActorToDTO(Actor actor, boolean detailed) {
        if (detailed) {
            return new ActorRequest.ActorDetailedDTO(
                    actor.getId(),
                    actor.getName(),
                    actor.getDescription() != null ? actor.getDescription() : "No description provided",
                    actor.getBirthDate(),
                    actor.getMovies().stream()
                            .map(movie -> new MovieRequest.MovieMinimalDTO(movie.getId(), movie.getTitle(), movie.getReleaseYear()))
                            .collect(Collectors.toList())
            );
        } else {
            return new ActorRequest.ActorMinimalDTO(actor.getId(), actor.getName(), actor.getBirthDate());
        }
    }
    
}

