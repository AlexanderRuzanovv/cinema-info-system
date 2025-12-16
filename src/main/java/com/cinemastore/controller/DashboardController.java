package com.cinemastore.controller;

import com.cinemastore.entity.TicketStatus;
import com.cinemastore.entity.Role;
import com.cinemastore.security.CustomUserDetails;
import com.cinemastore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

/**
 * Контроллер для главных страниц и дашборда
 */
@Controller
public class DashboardController {
    
    private final MovieService movieService;
    private final GenreService genreService;
    private final TicketService ticketService;
    private final UserService userService;
    private final StudioService studioService;
    
    @Autowired
    public DashboardController(MovieService movieService,
                               GenreService genreService,
                               TicketService ticketService,
                               UserService userService,
                               StudioService studioService) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.studioService = studioService;
    }
    
    /**
     * Главная страница
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
    
    /**
     * О системе
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }

    /**
     * Об авторе
     */
    @GetMapping("/author")
    public String author() {
        return "author";
    }
    
    /**
     * Дашборд (после входа)
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        
        Role role = userDetails.getUser().getRole();
        
        switch (role) {
            case ADMIN:
                return setupAdminDashboard(model);
            case MANAGER:
                return setupManagerDashboard(model);
            case CASHIER:
                return setupCashierDashboard(model);
            case CUSTOMER:
            default:
                return setupCustomerDashboard(userDetails, model);
        }
    }
    
    private String setupAdminDashboard(Model model) {
        // Статистика для администратора
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("totalMovies", movieService.count());
        model.addAttribute("totalGenres", genreService.count());
        model.addAttribute("totalStudios", studioService.count());
        model.addAttribute("totalTickets", ticketService.count());
        
        // Количество пользователей по ролям
        model.addAttribute("customerCount", userService.countByRole(Role.CUSTOMER));
        model.addAttribute("cashierCount", userService.countByRole(Role.CASHIER));
        model.addAttribute("managerCount", userService.countByRole(Role.MANAGER));
        model.addAttribute("adminCount", userService.countByRole(Role.ADMIN));
        
        // Билеты по статусам
        model.addAttribute("reservedTickets", ticketService.countByStatus(TicketStatus.RESERVED));
        model.addAttribute("usedTickets", ticketService.countByStatus(TicketStatus.USED));
        
        // Последние билеты
        model.addAttribute("recentTickets", ticketService.findRecentTickets(5));
        
        return "dashboard/admin";
    }
    
    private String setupManagerDashboard(Model model) {
        // Статистика для менеджера
        model.addAttribute("totalMovies", movieService.count());
        model.addAttribute("totalGenres", genreService.count());
        model.addAttribute("totalStudios", studioService.count());
        model.addAttribute("totalTickets", ticketService.count());
        
        // Доступные фильмы
        model.addAttribute("availableMovies", movieService.countAvailable());
        
        // Статистика билетов
        model.addAttribute("reservedTickets", ticketService.countByStatus(TicketStatus.RESERVED));
        model.addAttribute("paidTickets", ticketService.countByStatus(TicketStatus.PAID));
        
        // Выручка за текущий месяц
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        model.addAttribute("monthlyRevenue", ticketService.calculateRevenue(startOfMonth, LocalDateTime.now()));
        
        // Последние билеты
        model.addAttribute("recentTickets", ticketService.findRecentTickets(10));
        
        return "dashboard/manager";
    }
    
    private String setupCashierDashboard(Model model) {
        // Билеты требующие обработки
        model.addAttribute("ticketsToProcess", ticketService.findTicketsRequiringProcessing());
        
        // Статистика билетов
        model.addAttribute("reservedTickets", ticketService.countByStatus(TicketStatus.RESERVED));
        model.addAttribute("paidTickets", ticketService.countByStatus(TicketStatus.PAID));
        model.addAttribute("activeTickets", ticketService.countByStatus(TicketStatus.ACTIVE));
        model.addAttribute("usedTickets", ticketService.countByStatus(TicketStatus.USED));
        
        // Выручка за сегодня
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        model.addAttribute("todayRevenue", ticketService.calculateRevenue(startOfDay, LocalDateTime.now()));
        
        // Последние билеты
        model.addAttribute("recentTickets", ticketService.findRecentTickets(10));
        
        return "dashboard/cashier";
    }
    
    private String setupCustomerDashboard(CustomUserDetails userDetails, Model model) {
        // Жанры фильмов
        model.addAttribute("genres", genreService.findAll());
        
        // Популярные фильмы (просто последние добавленные)
        model.addAttribute("featuredMovies", movieService.findAll().stream().limit(8).toList());
        
        // Билеты пользователя (последние)
        model.addAttribute("myTickets", ticketService.findRecentTickets(5));
        
        return "dashboard/customer";
    }
}


