package com.cinemastore.service;

import com.cinemastore.entity.*;
import com.cinemastore.repository.MovieRepository;
import com.cinemastore.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с билетами
 */
@Service
@Transactional
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;
    
    @Autowired
    public TicketService(TicketRepository ticketRepository, 
                        MovieRepository movieRepository) {
        this.ticketRepository = ticketRepository;
        this.movieRepository = movieRepository;
    }
    
    /**
     * Создание нового билета
     */
    public Ticket createTicket(User customer, Movie movie, LocalDateTime showtime, String seat) {
        if (!movie.isAvailable()) {
            throw new RuntimeException("Фильм недоступен для продажи");
        }
        
        Ticket ticket = new Ticket(customer, movie, showtime);
        ticket.setSeat(seat);
        ticket.setPrice(movie.getPrice());
        ticket.setStatus(TicketStatus.RESERVED);
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Получение билета по ID
     */
    @Transactional(readOnly = true)
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }
    
    /**
     * Получение билета по номеру
     */
    @Transactional(readOnly = true)
    public Optional<Ticket> findByTicketNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber);
    }
    
    /**
     * Получение всех билетов
     */
    @Transactional(readOnly = true)
    public List<Ticket> findAll() {
        return ticketRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    
    /**
     * Получение билетов с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }
    
    /**
     * Получение билетов клиента
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findByCustomer(User customer, Pageable pageable) {
        return ticketRepository.findByCustomer(customer, pageable);
    }
    
    /**
     * Получение билетов по статусу
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findByStatus(TicketStatus status, Pageable pageable) {
        return ticketRepository.findByStatus(status, pageable);
    }
    
    /**
     * Поиск билетов
     */
    @Transactional(readOnly = true)
    public Page<Ticket> searchTickets(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return ticketRepository.findAll(pageable);
        }
        return ticketRepository.searchTickets(search.trim(), pageable);
    }
    
    /**
     * Изменение статуса билета
     */
    public Ticket updateStatus(Long ticketId, TicketStatus newStatus, User cashier) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Билет не найден"));
        
        validateStatusTransition(ticket.getStatus(), newStatus);
        
        ticket.setStatus(newStatus);
        
        if (cashier != null && ticket.getCashier() == null) {
            ticket.setCashier(cashier);
        }
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Проверка допустимости перехода между статусами
     */
    private void validateStatusTransition(TicketStatus current, TicketStatus next) {
        // Определяем допустимые переходы
        switch (current) {
            case RESERVED:
                if (next != TicketStatus.PAID && next != TicketStatus.CANCELLED) {
                    throw new RuntimeException("Недопустимый переход статуса");
                }
                break;
            case PAID:
                if (next != TicketStatus.ACTIVE && next != TicketStatus.CANCELLED) {
                    throw new RuntimeException("Недопустимый переход статуса");
                }
                break;
            case ACTIVE:
                if (next != TicketStatus.USED && next != TicketStatus.CANCELLED) {
                    throw new RuntimeException("Недопустимый переход статуса");
                }
                break;
            case USED:
            case COMPLETED:
            case CANCELLED:
                throw new RuntimeException("Билет уже завершен или отменен");
        }
    }
    
    /**
     * Обновление места в билете
     */
    public Ticket updateSeat(Long ticketId, String seat) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Билет не найден"));
        
        if (ticket.getStatus() != TicketStatus.RESERVED && ticket.getStatus() != TicketStatus.PAID) {
            throw new RuntimeException("Невозможно изменить место в текущем статусе");
        }
        
        ticket.setSeat(seat);
        return ticketRepository.save(ticket);
    }
    
    /**
     * Добавление примечания к билету
     */
    public Ticket updateNotes(Long ticketId, String notes) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Билет не найден"));
        
        ticket.setNotes(notes);
        return ticketRepository.save(ticket);
    }
    
    /**
     * Удаление билета
     */
    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Билет не найден"));
        
        if (ticket.getStatus() != TicketStatus.RESERVED && ticket.getStatus() != TicketStatus.CANCELLED) {
            throw new RuntimeException("Невозможно удалить билет в текущем статусе");
        }
        
        ticketRepository.deleteById(id);
    }
    
    /**
     * Получение последних билетов
     */
    @Transactional(readOnly = true)
    public List<Ticket> findRecentTickets(int limit) {
        return ticketRepository.findRecentTickets(PageRequest.of(0, limit));
    }
    
    /**
     * Получение билетов требующих обработки
     */
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsRequiringProcessing() {
        return ticketRepository.findTicketsRequiringProcessing();
    }
    
    /**
     * Получение билетов за период
     */
    @Transactional(readOnly = true)
    public Page<Ticket> findTicketsBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return ticketRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }
    
    /**
     * Расчет выручки за период
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = ticketRepository.calculateRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
    
    /**
     * Получение количества билетов по статусу
     */
    @Transactional(readOnly = true)
    public long countByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }
    
    /**
     * Получение общего количества билетов
     */
    @Transactional(readOnly = true)
    public long count() {
        return ticketRepository.count();
    }
}

