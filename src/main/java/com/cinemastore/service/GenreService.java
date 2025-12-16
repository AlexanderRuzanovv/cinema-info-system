package com.cinemastore.service;

import com.cinemastore.entity.Genre;
import com.cinemastore.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с жанрами фильмов
 */
@Service
@Transactional
public class GenreService {
    
    private final GenreRepository genreRepository;
    
    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }
    
    /**
     * Создание нового жанра
     */
    public Genre createGenre(Genre genre) {
        if (genreRepository.existsByName(genre.getName())) {
            throw new RuntimeException("Жанр с таким названием уже существует");
        }
        return genreRepository.save(genre);
    }
    
    /**
     * Получение жанра по ID
     */
    @Transactional(readOnly = true)
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }
    
    /**
     * Получение жанра по названию
     */
    @Transactional(readOnly = true)
    public Optional<Genre> findByName(String name) {
        return genreRepository.findByName(name);
    }
    
    /**
     * Получение жанра с фильмами
     */
    @Transactional(readOnly = true)
    public Optional<Genre> findByIdWithMovies(Long id) {
        return genreRepository.findByIdWithMovies(id);
    }
    
    /**
     * Получение всех жанров
     */
    @Transactional(readOnly = true)
    public List<Genre> findAll() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }
    
    /**
     * Получение жанров с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<Genre> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }
    
    /**
     * Поиск жанров по названию
     */
    @Transactional(readOnly = true)
    public Page<Genre> searchByName(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return genreRepository.findAll(pageable);
        }
        return genreRepository.searchByName(search.trim(), pageable);
    }
    
    /**
     * Обновление жанра
     */
    public Genre updateGenre(Long id, Genre updatedGenre) {
        Genre existingGenre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        
        // Проверка уникальности имени при изменении
        if (!existingGenre.getName().equals(updatedGenre.getName()) 
            && genreRepository.existsByName(updatedGenre.getName())) {
            throw new RuntimeException("Жанр с таким названием уже существует");
        }
        
        existingGenre.setName(updatedGenre.getName());
        existingGenre.setDescription(updatedGenre.getDescription());
        
        return genreRepository.save(existingGenre);
    }
    
    /**
     * Удаление жанра
     */
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        
        if (genre.getMovies() != null && !genre.getMovies().isEmpty()) {
            throw new RuntimeException("Невозможно удалить жанр с фильмами");
        }
        
        genreRepository.deleteById(id);
    }
    
    /**
     * Проверка существования жанра
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return genreRepository.existsByName(name);
    }
    
    /**
     * Получение количества жанров
     */
    @Transactional(readOnly = true)
    public long count() {
        return genreRepository.count();
    }
}


