package com.cinemastore.controller;

import com.cinemastore.entity.*;
import com.cinemastore.security.CustomUserDetails;
import com.cinemastore.service.MovieService;
import com.cinemastore.service.TicketService;
import com.cinemastore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Контроллер для работы с билетами
 */
@Controller
@RequestMapping("/tickets")
public class TicketController {
    
    private final TicketService ticketService;
    private final MovieService movieService;
    private final UserService userService;
    
    @Autowired
    public TicketController(TicketService ticketService, 
                            MovieService movieService,
                            UserService userService) {
        this.ticketService = ticketService;
        this.movieService = movieService;
        this.userService = userService;
    }
    
    /**
     * Список всех билетов (для кассиров, менеджеров и админов)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CASHIER', 'MANAGER', 'ADMIN')")
    public String listTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Ticket> tickets;
        if (status != null && !status.isEmpty()) {
            tickets = ticketService.findByStatus(TicketStatus.valueOf(status), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            tickets = ticketService.searchTickets(search, pageable);
        } else {
            tickets = ticketService.findAll(pageable);
        }
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "tickets/list";
    }
    
    /**
     * Мои билеты (для зрителей)
     */
    @GetMapping("/my")
    public String myTickets(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        User customer = userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> tickets = ticketService.findByCustomer(customer, pageable);
        
        model.addAttribute("tickets", tickets);
        
        return "tickets/my-tickets";
    }
    
    /**
     * Просмотр билета
     */
    @GetMapping("/view/{id}")
    public String viewTicket(@PathVariable Long id, 
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        Ticket ticket = ticketService.findById(id)
            .orElseThrow(() -> new RuntimeException("Билет не найден"));
        
        // Проверка доступа - зритель видит только свои билеты
        User currentUser = userDetails.getUser();
        if (currentUser.getRole().name().equals("CUSTOMER") && 
            !ticket.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        model.addAttribute("ticket", ticket);
        model.addAttribute("statuses", TicketStatus.values());
        
        return "tickets/view";
    }
    
    /**
     * Форма создания билета
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("movies", movieService.findAvailable(PageRequest.of(0, 100)));
        return "tickets/create";
    }
    
    /**
     * Создание билета
     */
    @PostMapping("/new")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String createTicket(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam Long movieId,
                              @RequestParam String showtime,
                              @RequestParam(required = false) String seat,
                              RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Фильм не найден"));
            
            LocalDateTime showtimeDateTime = LocalDateTime.parse(showtime);
            Ticket ticket = ticketService.createTicket(userDetails.getUser(), movie, showtimeDateTime, seat);
            
            redirectAttributes.addFlashAttribute("success", "Билет успешно создан");
            return "redirect:/tickets/view/" + ticket.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tickets/my";
        }
    }
    
    /**
     * Изменение статуса билета (для кассиров/менеджеров)
     */
    @PostMapping("/{ticketId}/status")
    @PreAuthorize("hasAnyRole('CASHIER', 'MANAGER', 'ADMIN')")
    public String updateTicketStatus(@PathVariable Long ticketId,
                                    @RequestParam String status,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            ticketService.updateStatus(ticketId, TicketStatus.valueOf(status), userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Статус билета обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/view/" + ticketId;
    }
    
    /**
     * Отмена билета
     */
    @PostMapping("/{ticketId}/cancel")
    public String cancelTicket(@PathVariable Long ticketId,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            ticketService.updateStatus(ticketId, TicketStatus.CANCELLED, userDetails.getUser());
            redirectAttributes.addFlashAttribute("success", "Билет отменен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/view/" + ticketId;
    }
    
    /**
     * Удаление билета
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.deleteTicket(id);
            redirectAttributes.addFlashAttribute("success", "Билет удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets";
    }
}

