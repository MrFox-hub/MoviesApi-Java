package com.marttirebane.movieapi.utils;

import com.marttirebane.movieapi.repository.ActorRepository;
import com.marttirebane.movieapi.repository.GenreRepository;
import com.marttirebane.movieapi.repository.MovieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
import java.util.*;

public class AppUtils {

    // Pagination Utilities
    public static <T> Page<T> createPagedStream(List<T> items, Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
    
        // Validate page and size inputs
        if (page < 0 || size <= 0) {
            throw new RuntimeException("Page number must be >= 0 and size must be > 0.");
        }
    
        // Calculate total pages dynamically
        int totalElements = items.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
    
        if (page >= totalPages) {
            throw new RuntimeException(String.format(
                "Page %d is out of bounds. Total pages: %d, Total elements: %d, Page size: %d.", page, totalPages, totalElements, size));
        }
    
        // Calculate the start and end indices for the sublist
        int start = page * size;
        int end = Math.min(start + size, totalElements);
    
        return new PageImpl<>(items.subList(start, end), pageable, totalElements);
    }
    

    public static <T> Map<String, Object> simplifyPageResponse(Page<T> page) {
        Map<String, Object> response = new LinkedHashMap<>(); 
        response.put("content", page.getContent()); 
    
        
        response.put("totalPages", page.getTotalPages());
        response.put("currentPage", page.getNumber() + 1); 
        response.put("pageSize", page.getSize());
    
        return response;
    }
    

    public static void validatePagination(int page, int size) {
        if (size >= 9001) {
            throw new RuntimeException("IT'S OVER 9000!!! The size is too large.");
        }

        if (page < 0) {
            throw new RuntimeException("Page number cannot be negative.");
        }

        if (size < 1 || size > 100) {
            if (size > 100) {
                throw new RuntimeException("Currently, we don't support more than 100 items per page.");
            }
            throw new RuntimeException("Page size must be between 1 and 100.");
        }
    }

    public static boolean validateBirthDate(String birthDate, List<String> errors) {
        if (!birthDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            errors.add("Birth date must be in the format YYYY-MM-DD.");
            return false;
        }
    
        try {
            String[] parts = birthDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
    
            if (month < 1 || month > 12) {
                errors.add("Month must be between 1 and 12.");
                return false;
            }
    
            if (day < 1 || day > getDaysInMonth(month, year)) {
                errors.add("Day is not valid for the given month and year.");
                return false;
            }

            LocalDate inputDate = LocalDate.of(year, month, day);
            LocalDate currentDate = LocalDate.now();

            if (inputDate.isAfter(currentDate)) {
            errors.add("Birth date cannot be in the future.");
            return false;
            }
    
            if (year < 1700) {
                errors.add("Birth year is unreasonably in the past.");
                return false;
            }

        } catch (NumberFormatException e) {
            errors.add("Birth date contains invalid numeric values.");
            return false;
        }
    
        return true;
    }
    
    private static int getDaysInMonth(int month, int year) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> (isLeapYear(year) ? 29 : 28);
            default -> 0;
        };
    }
    
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    
    

    public static void validateReleaseYear(int releaseYear) {
        int currentYear = LocalDate.now().getYear();
        if (releaseYear < 1800 || releaseYear > currentYear + 3) {
            throw new RuntimeException("Release year must be between 1800 and " + (currentYear + 3) + ".");
        }
    }

    public static void validateDuration(int duration) {
        if (duration <= 0 || duration > 999) {
            throw new RuntimeException("Duration must be between 1 and 999 minutes.");
        }
    }

   // Duplicate Checks
public static void checkDuplicateActor(String name, ActorRepository actorRepository) {
    boolean exists = actorRepository.existsByNameIgnoreCase(name).orElse(false);
    if (exists) {
        throw new RuntimeException("Actor with name '" + name + "' already exists.");
    }
}

public static void checkDuplicateGenre(String name, GenreRepository genreRepository) {
    boolean exists = genreRepository.existsByNameIgnoreCase(name).orElse(false);
    if (exists) {
        throw new RuntimeException("Genre with name '" + name + "' already exists.");
    }
}

public static void checkDuplicateMovie(String title, MovieRepository movieRepository) {
    boolean exists = movieRepository.existsByTitleIgnoreCase(title).orElse(false);
    if (exists) {
        throw new RuntimeException("Movie with title '" + title + "' already exists.");
    }
}

}
