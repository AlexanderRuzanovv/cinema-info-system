package com.cinemastore.controller;

import com.cinemastore.entity.Movie;
import com.cinemastore.service.GenreService;
import com.cinemastore.service.MovieService;
import com.cinemastore.service.StudioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Контроллер для работы с фильмами
 */
@Controller
@RequestMapping("/movies")
public class MovieController {
    
    private final MovieService movieService;
    private final GenreService genreService;
    private final StudioService studioService;
    
    @Autowired
    public MovieController(MovieService movieService,
                          GenreService genreService,
                          StudioService studioService) {
        this.movieService = movieService;
        this.genreService = genreService;
        this.studioService = studioService;
    }
    
    /**
     * Список всех фильмов с фильтрацией, поиском и сортировкой
     */
    @GetMapping
    public String listMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Long studioId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Movie> movies;
        
        // Если есть фильтры - используем комплексный поиск
        if (search != null || genreId != null || studioId != null || 
            minPrice != null || maxPrice != null || available != null) {
            movies = movieService.findWithFilters(search, genreId, studioId, 
                                                  minPrice, maxPrice, available, pageable);
        } else {
            movies = movieService.findAll(pageable);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("studios", studioService.findAllActive());
        
        // Параметры для сохранения состояния фильтров
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentGenreId", genreId);
        model.addAttribute("currentStudioId", studioId);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentAvailable", available);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "movies/list";
    }
    
    /**
     * Просмотр фильма
     */
    @GetMapping("/view/{id}")
    public String viewMovie(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id)
            .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        model.addAttribute("movie", movie);
        return "movies/view";
    }
    
    /**
     * Форма создания фильма
     */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("studios", studioService.findAllActive());
        return "movies/form";
    }
    
    /**
     * Создание фильма
     */
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String createMovie(@Valid @ModelAttribute("movie") Movie movie,
                             BindingResult result,
                             @RequestParam(required = false) Long genreId,
                             @RequestParam(required = false) Long studioId,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("studios", studioService.findAllActive());
            return "movies/form";
        }
        
        try {
            if (genreId != null) {
                movie.setGenre(genreService.findById(genreId).orElse(null));
            }
            if (studioId != null) {
                movie.setStudio(studioService.findById(studioId).orElse(null));
            }
            
            movieService.createMovie(movie);
            redirectAttributes.addFlashAttribute("success", "Фильм успешно создан");
            return "redirect:/movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("studios", studioService.findAllActive());
            return "movies/form";
        }
    }
    
    /**
     * Форма редактирования фильма
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id)
            .orElseThrow(() -> new RuntimeException("Фильм не найден"));
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("studios", studioService.findAllActive());
        return "movies/form";
    }
    
    /**
     * Обновление фильма
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String updateMovie(@PathVariable Long id,
                             @Valid @ModelAttribute("movie") Movie movie,
                             BindingResult result,
                             @RequestParam(required = false) Long genreId,
                             @RequestParam(required = false) Long studioId,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("studios", studioService.findAllActive());
            return "movies/form";
        }
        
        try {
            if (genreId != null) {
                movie.setGenre(genreService.findById(genreId).orElse(null));
            }
            if (studioId != null) {
                movie.setStudio(studioService.findById(studioId).orElse(null));
            }
            
            movieService.updateMovie(id, movie);
            redirectAttributes.addFlashAttribute("success", "Фильм успешно обновлен");
            return "redirect:/movies";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("studios", studioService.findAllActive());
            return "movies/form";
        }
    }
    
    /**
     * Удаление фильма
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Фильм успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movies";
    }
    
    /**
     * Переключение доступности фильма
     */
    @PostMapping("/toggle-available/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String toggleAvailable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.toggleAvailable(id);
            String status = movie.isAvailable() ? "доступен" : "недоступен";
            redirectAttributes.addFlashAttribute("success", "Фильм теперь " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movies";
    }
}

