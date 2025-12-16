package com.cinemastore.controller;

import com.cinemastore.entity.Genre;
import com.cinemastore.service.GenreService;
import com.cinemastore.service.MovieService;
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

/**
 * Контроллер для работы с жанрами фильмов
 */
@Controller
@RequestMapping("/genres")
public class GenreController {
    
    private final GenreService genreService;
    private final MovieService movieService;
    
    @Autowired
    public GenreController(GenreService genreService, MovieService movieService) {
        this.genreService = genreService;
        this.movieService = movieService;
    }
    
    /**
     * Список всех жанров
     */
    @GetMapping
    public String listGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Genre> genres;
        if (search != null && !search.trim().isEmpty()) {
            genres = genreService.searchByName(search, pageable);
        } else {
            genres = genreService.findAll(pageable);
        }
        
        model.addAttribute("genres", genres);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "genres/list";
    }
    
    /**
     * Просмотр жанра с фильмами
     */
    @GetMapping("/view/{id}")
    public String viewGenre(@PathVariable Long id,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "12") int size,
                              Model model) {
        Genre genre = genreService.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        model.addAttribute("genre", genre);
        model.addAttribute("movies", movieService.findByGenre(genre, pageable));
        
        return "genres/view";
    }
    
    /**
     * Форма создания жанра
     */
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "genres/form";
    }
    
    /**
     * Создание жанра
     */
    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String createGenre(@Valid @ModelAttribute("genre") Genre genre,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            return "genres/form";
        }
        
        try {
            genreService.createGenre(genre);
            redirectAttributes.addFlashAttribute("success", "Жанр успешно создан");
            return "redirect:/genres";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "genres/form";
        }
    }
    
    /**
     * Форма редактирования жанра
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Genre genre = genreService.findById(id)
            .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        model.addAttribute("genre", genre);
        return "genres/form";
    }
    
    /**
     * Обновление жанра
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String updateGenre(@PathVariable Long id,
                                @Valid @ModelAttribute("genre") Genre genre,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            return "genres/form";
        }
        
        try {
            genreService.updateGenre(id, genre);
            redirectAttributes.addFlashAttribute("success", "Жанр успешно обновлен");
            return "redirect:/genres";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "genres/form";
        }
    }
    
    /**
     * Удаление жанра
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.deleteGenre(id);
            redirectAttributes.addFlashAttribute("success", "Жанр успешно удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/genres";
    }
}


