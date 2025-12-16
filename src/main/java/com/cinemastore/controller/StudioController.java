package com.cinemastore.controller;

import com.cinemastore.entity.Studio;
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

/**
 * Контроллер для работы с киностудиями
 */
@Controller
@RequestMapping("/studios")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class StudioController {
    
    private final StudioService studioService;
    
    @Autowired
    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }
    
    /**
     * Список всех студий
     */
    @GetMapping
    public String listStudios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "companyName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Studio> studios;
        if (search != null && !search.trim().isEmpty()) {
            studios = studioService.searchStudios(search, pageable);
        } else {
            studios = studioService.findAll(pageable);
        }
        
        model.addAttribute("studios", studios);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "studios/list";
    }
    
    /**
     * Просмотр студии
     */
    @GetMapping("/view/{id}")
    public String viewStudio(@PathVariable Long id, Model model) {
        Studio studio = studioService.findById(id)
            .orElseThrow(() -> new RuntimeException("Студия не найдена"));
        model.addAttribute("studio", studio);
        return "studios/view";
    }
    
    /**
     * Форма создания студии
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("studio", new Studio());
        return "studios/form";
    }
    
    /**
     * Создание студии
     */
    @PostMapping("/new")
    public String createStudio(@Valid @ModelAttribute("studio") Studio studio,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            return "studios/form";
        }
        
        try {
            studioService.createStudio(studio);
            redirectAttributes.addFlashAttribute("success", "Студия успешно создана");
            return "redirect:/studios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "studios/form";
        }
    }
    
    /**
     * Форма редактирования студии
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Studio studio = studioService.findById(id)
            .orElseThrow(() -> new RuntimeException("Студия не найдена"));
        model.addAttribute("studio", studio);
        return "studios/form";
    }
    
    /**
     * Обновление студии
     */
    @PostMapping("/edit/{id}")
    public String updateStudio(@PathVariable Long id,
                                @Valid @ModelAttribute("studio") Studio studio,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            return "studios/form";
        }
        
        try {
            studioService.updateStudio(id, studio);
            redirectAttributes.addFlashAttribute("success", "Студия успешно обновлена");
            return "redirect:/studios";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "studios/form";
        }
    }
    
    /**
     * Удаление студии
     */
    @PostMapping("/delete/{id}")
    public String deleteStudio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studioService.deleteStudio(id);
            redirectAttributes.addFlashAttribute("success", "Студия успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/studios";
    }
    
    /**
     * Переключение активности студии
     */
    @PostMapping("/toggle-active/{id}")
    public String toggleActive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Studio studio = studioService.toggleActive(id);
            String status = studio.isActive() ? "активна" : "неактивна";
            redirectAttributes.addFlashAttribute("success", "Студия теперь " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/studios";
    }
}


