package com.cinemastore.service;

import com.cinemastore.entity.Studio;
import com.cinemastore.repository.StudioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с киностудиями
 */
@Service
@Transactional
public class StudioService {
    
    private final StudioRepository studioRepository;
    
    @Autowired
    public StudioService(StudioRepository studioRepository) {
        this.studioRepository = studioRepository;
    }
    
    /**
     * Создание новой студии
     */
    public Studio createStudio(Studio studio) {
        if (studioRepository.existsByCompanyName(studio.getCompanyName())) {
            throw new RuntimeException("Студия с таким названием уже существует");
        }
        return studioRepository.save(studio);
    }
    
    /**
     * Получение студии по ID
     */
    @Transactional(readOnly = true)
    public Optional<Studio> findById(Long id) {
        return studioRepository.findById(id);
    }
    
    /**
     * Получение студии по названию компании
     */
    @Transactional(readOnly = true)
    public Optional<Studio> findByCompanyName(String companyName) {
        return studioRepository.findByCompanyName(companyName);
    }
    
    /**
     * Получение всех студий
     */
    @Transactional(readOnly = true)
    public List<Studio> findAll() {
        return studioRepository.findAll(Sort.by(Sort.Direction.ASC, "companyName"));
    }
    
    /**
     * Получение студий с пагинацией
     */
    @Transactional(readOnly = true)
    public Page<Studio> findAll(Pageable pageable) {
        return studioRepository.findAll(pageable);
    }
    
    /**
     * Получение только активных студий
     */
    @Transactional(readOnly = true)
    public List<Studio> findAllActive() {
        return studioRepository.findAllActiveOrderByName();
    }
    
    /**
     * Поиск студий
     */
    @Transactional(readOnly = true)
    public Page<Studio> searchStudios(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return studioRepository.findAll(pageable);
        }
        return studioRepository.searchStudios(search.trim(), pageable);
    }
    
    /**
     * Обновление студии
     */
    public Studio updateStudio(Long id, Studio updatedStudio) {
        Studio existingStudio = studioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Студия не найдена"));
        
        // Проверка уникальности названия при изменении
        if (!existingStudio.getCompanyName().equals(updatedStudio.getCompanyName()) 
            && studioRepository.existsByCompanyName(updatedStudio.getCompanyName())) {
            throw new RuntimeException("Студия с таким названием уже существует");
        }
        
        existingStudio.setCompanyName(updatedStudio.getCompanyName());
        existingStudio.setContactPerson(updatedStudio.getContactPerson());
        existingStudio.setPhone(updatedStudio.getPhone());
        existingStudio.setEmail(updatedStudio.getEmail());
        existingStudio.setAddress(updatedStudio.getAddress());
        existingStudio.setDescription(updatedStudio.getDescription());
        
        return studioRepository.save(existingStudio);
    }
    
    /**
     * Активация/деактивация студии
     */
    public Studio toggleActive(Long id) {
        Studio studio = studioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Студия не найдена"));
        
        studio.setActive(!studio.isActive());
        return studioRepository.save(studio);
    }
    
    /**
     * Удаление студии
     */
    public void deleteStudio(Long id) {
        Studio studio = studioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Студия не найдена"));
        
        if (studio.getMovies() != null && !studio.getMovies().isEmpty()) {
            throw new RuntimeException("Невозможно удалить студию с фильмами");
        }
        
        studioRepository.deleteById(id);
    }
    
    /**
     * Получение количества студий
     */
    @Transactional(readOnly = true)
    public long count() {
        return studioRepository.count();
    }
}


