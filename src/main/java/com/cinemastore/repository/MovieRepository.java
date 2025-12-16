package com.cinemastore.repository;

import com.cinemastore.entity.Genre;
import com.cinemastore.entity.Movie;
import com.cinemastore.entity.Studio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с фильмами
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    List<Movie> findByGenre(Genre genre);
    
    Page<Movie> findByGenre(Genre genre, Pageable pageable);
    
    List<Movie> findByStudio(Studio studio);
    
    Page<Movie> findByStudio(Studio studio, Pageable pageable);
    
    List<Movie> findByAvailableTrue();
    
    Page<Movie> findByAvailableTrue(Pageable pageable);
    
    // Поиск по названию и описанию
    @Query("SELECT m FROM Movie m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Movie> searchMovies(@Param("search") String search, Pageable pageable);
    
    // Поиск в жанре
    @Query("SELECT m FROM Movie m WHERE m.genre = :genre AND (" +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Movie> searchMoviesInGenre(@Param("search") String search, 
                                    @Param("genre") Genre genre, 
                                    Pageable pageable);
    
    // Фильтрация по цене
    @Query("SELECT m FROM Movie m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    Page<Movie> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                 @Param("maxPrice") BigDecimal maxPrice, 
                                 Pageable pageable);
    
    // Комплексный поиск с фильтрами
    @Query("SELECT m FROM Movie m WHERE " +
           "(:search IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:genreId IS NULL OR m.genre.id = :genreId) AND " +
           "(:studioId IS NULL OR m.studio.id = :studioId) AND " +
           "(:minPrice IS NULL OR m.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
           "(:available IS NULL OR m.available = :available)")
    Page<Movie> findWithFilters(@Param("search") String search,
                                @Param("genreId") Long genreId,
                                @Param("studioId") Long studioId,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice,
                                @Param("available") Boolean available,
                                Pageable pageable);
    
    // Статистика
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.genre = :genre")
    long countByGenre(@Param("genre") Genre genre);
    
    @Query("SELECT COUNT(m) FROM Movie m WHERE m.available = true")
    long countAvailable();
}

