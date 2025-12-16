package com.cinemastore.service;

import com.cinemastore.entity.Genre;
import com.cinemastore.entity.Movie;
import com.cinemastore.entity.Studio;
import com.cinemastore.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с фильмами
 */
@Service
@Transactional
public class MovieService {
    
    private final MovieRepository movieRepository;
    
    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }
    
    /**
     * Создание нового фильма
     */
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    
    /**
     * Получение фильма по ID
     */
    @Transactional(readOnly = true)
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }
    
    /**
     * Получение всех фильмов
     */
    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        return movieRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }
    
    /**
     * Получение фильмов с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<Movie> findAll(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }
    
    /**
     * Получение доступных фильмов
     */
    @Transactional(readOnly = true)
    public Page<Movie> findAvailable(Pageable pageable) {
        return movieRepository.findByAvailableTrue(pageable);
    }
    
    /**
     * Получение фильмов по жанру
     */
    @Transactional(readOnly = true)
    public Page<Movie> findByGenre(Genre genre, Pageable pageable) {
        return movieRepository.findByGenre(genre, pageable);
    }
    
    /**
     * Получение фильмов по студии
     */
    @Transactional(readOnly = true)
    public Page<Movie> findByStudio(Studio studio, Pageable pageable) {
        return movieRepository.findByStudio(studio, pageable);
    }
    
    /**
     * Поиск фильмов
     */
    @Transactional(readOnly = true)
    public Page<Movie> searchMovies(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return movieRepository.findAll(pageable);
        }
        return movieRepository.searchMovies(search.trim(), pageable);
    }
    
    /**
     * Поиск фильмов в жанре
     */
    @Transactional(readOnly = true)
    public Page<Movie> searchMoviesInGenre(String search, Genre genre, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return movieRepository.findByGenre(genre, pageable);
        }
        return movieRepository.searchMoviesInGenre(search.trim(), genre, pageable);
    }
    
    /**
     * Фильтрация фильмов по цене
     */
    @Transactional(readOnly = true)
    public Page<Movie> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return movieRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
    
    /**
     * Комплексный поиск с фильтрами
     */
    @Transactional(readOnly = true)
    public Page<Movie> findWithFilters(String search, Long genreId, Long studioId,
                                       BigDecimal minPrice, BigDecimal maxPrice, 
                                       Boolean available, Pageable pageable) {
        return movieRepository.findWithFilters(search, genreId, studioId, 
                                               minPrice, maxPrice, available, pageable);
    }
    
    /**
     * Обновление фильма
     */
    public Movie updateMovie(Long id, Movie updatedMovie) {
        Movie existingMovie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        
        existingMovie.setName(updatedMovie.getName());
        existingMovie.setDescription(updatedMovie.getDescription());
        existingMovie.setPrice(updatedMovie.getPrice());
        existingMovie.setDuration(updatedMovie.getDuration());
        existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
        existingMovie.setRating(updatedMovie.getRating());
        existingMovie.setGenre(updatedMovie.getGenre());
        existingMovie.setStudio(updatedMovie.getStudio());
        existingMovie.setAvailable(updatedMovie.isAvailable());
        
        return movieRepository.save(existingMovie);
    }
    
    /**
     * Активация/деактивация фильма
     */
    public Movie toggleAvailable(Long id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        
        movie.setAvailable(!movie.isAvailable());
        return movieRepository.save(movie);
    }
    
    /**
     * Удаление фильма
     */
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Фильм не найден");
        }
        movieRepository.deleteById(id);
    }
    
    /**
     * Получение количества фильмов
     */
    @Transactional(readOnly = true)
    public long count() {
        return movieRepository.count();
    }
    
    /**
     * Получение количества доступных фильмов
     */
    @Transactional(readOnly = true)
    public long countAvailable() {
        return movieRepository.countAvailable();
    }
}

