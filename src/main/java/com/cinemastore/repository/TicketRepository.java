package com.cinemastore.repository;

import com.cinemastore.entity.Ticket;
import com.cinemastore.entity.TicketStatus;
import com.cinemastore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с билетами
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    
    List<Ticket> findByCustomer(User customer);
    
    Page<Ticket> findByCustomer(User customer, Pageable pageable);
    
    List<Ticket> findByCashier(User cashier);
    
    Page<Ticket> findByCashier(User cashier, Pageable pageable);
    
    List<Ticket> findByStatus(TicketStatus status);
    
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
    
    // Билеты клиента по статусу
    List<Ticket> findByCustomerAndStatus(User customer, TicketStatus status);
    
    Page<Ticket> findByCustomerAndStatus(User customer, TicketStatus status, Pageable pageable);
    
    // Поиск билетов
    @Query("SELECT t FROM Ticket t WHERE " +
           "LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.customer.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.customer.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ticket> searchTickets(@Param("search") String search, Pageable pageable);
    
    // Билеты за период
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Ticket> findTicketsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    Page<Ticket> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Статистика
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(@Param("status") TicketStatus status);
    
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") TicketStatus status);
    
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'USED'")
    BigDecimal calculateRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    // Последние билеты
    @Query("SELECT t FROM Ticket t ORDER BY t.createdAt DESC")
    List<Ticket> findRecentTickets(Pageable pageable);
    
    // Билеты требующие обработки
    @Query("SELECT t FROM Ticket t WHERE t.status IN ('RESERVED', 'PAID') ORDER BY t.createdAt ASC")
    List<Ticket> findTicketsRequiringProcessing();
}


