package com.cinemastore.repository;

import com.cinemastore.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с жанрами фильмов
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    
    Optional<Genre> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT g FROM Genre g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Genre> searchByName(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT g FROM Genre g LEFT JOIN FETCH g.movies WHERE g.id = :id")
    Optional<Genre> findByIdWithMovies(@Param("id") Long id);
    
    @Query("SELECT g FROM Genre g ORDER BY g.name ASC")
    List<Genre> findAllOrderByName();
}


