package com.marttirebane.movieapi.controller;

import com.marttirebane.movieapi.DTO.ActorRequest;
import com.marttirebane.movieapi.model.Actor;
import com.marttirebane.movieapi.service.ActorService;
import com.marttirebane.movieapi.utils.ResourceNotFound;

import jakarta.validation.Valid;

import com.marttirebane.movieapi.utils.AppUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actors")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    public Map<String, Object> getAllActors(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size,
                                            @RequestParam(defaultValue = "false") boolean detailed) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> actors = detailed 
                ? actorService.getAllActorsDetailed(pageable) 
                : actorService.getAllActorsMinimal(pageable);

        return AppUtils.simplifyPageResponse(actors);
    }

    @GetMapping("/{id}")
    public ActorRequest.ActorDetailedDTO getActorById(@PathVariable Long id) {
    return actorService.getActorById(id);
    }


    @GetMapping("/{id}/movie-titles")
    public Map<String, Object> getMovieTitlesAndYears(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "50") int size) {
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<String> movieTitlesAndYears = actorService.getMovieTitlesAndYears(id, pageable)
                .orElseThrow(() -> new ResourceNotFound("Actor with ID " + id + " not found."));

        return AppUtils.simplifyPageResponse(movieTitlesAndYears);
    }

    @GetMapping("/search")
    public Map<String, Object> searchActors(@RequestParam String name,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "50") int size,
                                            @RequestParam(defaultValue = "false") boolean detailed) {
        if (name.trim().isEmpty()) {
            throw new RuntimeException("The search query parameter 'name' cannot be empty.");
        }
        AppUtils.validatePagination(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<?> actors = detailed 
                ? actorService.searchActorsByNameDetailed(name, pageable) 
                : actorService.searchActorsByNameMinimal(name, pageable);

        return AppUtils.simplifyPageResponse(actors);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createActor(@RequestBody @Valid ActorRequest actorRequest) {
        AppUtils.validateBirthDate(actorRequest.getBirthDate(), null);
        String resultMessage = actorService.createActor(actorRequest);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", resultMessage);
        return response;
    }

    @PatchMapping("/{id}")
    public Map<String, Object> updateActor(@PathVariable Long id, @RequestBody @Valid ActorRequest updatedActor) {
        Actor updated = actorService.updateActor(id, updatedActor)
                .orElseThrow(() -> new ResourceNotFound("Actor with ID " + id + " not found."));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Actor updated successfully.");
        response.put("actor", updated);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Map<String, Object> deleteActor(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean force) {
        String resultMessage = actorService.deleteActor(id, force)
                .orElseThrow(() -> new ResourceNotFound("Actor with ID " + id + " not found."));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", resultMessage);
        return response;
    }

}
