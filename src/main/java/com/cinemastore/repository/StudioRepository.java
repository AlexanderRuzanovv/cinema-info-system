package com.cinemastore.repository;

import com.cinemastore.entity.Studio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с киностудиями
 */
@Repository
public interface StudioRepository extends JpaRepository<Studio, Long> {
    
    Optional<Studio> findByCompanyName(String companyName);
    
    boolean existsByCompanyName(String companyName);
    
    List<Studio> findByActiveTrue();
    
    Page<Studio> findByActiveTrue(Pageable pageable);
    
    @Query("SELECT s FROM Studio s WHERE " +
           "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Studio> searchStudios(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT s FROM Studio s ORDER BY s.companyName ASC")
    List<Studio> findAllOrderByName();
    
    @Query("SELECT s FROM Studio s WHERE s.active = true ORDER BY s.companyName ASC")
    List<Studio> findAllActiveOrderByName();
}


