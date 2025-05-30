package com.marttirebane.movieapi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFound extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(ResourceNotFound.class);

    public ResourceNotFound(String message) {
        super(message);
        logger.error(message); // Log the error
    }

    public ResourceNotFound(String resourceName, Long id) {
        super(resourceName + " with ID " + id + " was not found.");
        logger.error(resourceName + " with ID " + id + " was not found."); // Log the error
    }
}
