package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.FighterServiceDTO;
import com.yoesoff.plate.entity.FighterServiceEntity;
import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.enums.ServiceType;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class FighterServiceService {

    public List<FighterServiceDTO> searchServices(ServiceType serviceType, String discipline,
                                                  String city, Double minPrice, Double maxPrice,
                                                  int page, int size) {
        StringBuilder query = new StringBuilder("isActive = true");

        if (serviceType != null) {
            query.append(" and serviceType = ?1");
        }
        if (discipline != null && !discipline.isBlank()) {
            query.append(" and fighter.primaryDiscipline ilike ?2");
        }
        if (city != null && !city.isBlank()) {
            query.append(" and fighter.city.name ilike ?3");
        }
        if (minPrice != null) {
            query.append(" and pricePerHour >= ?4");
        }
        if (maxPrice != null) {
            query.append(" and pricePerHour <= ?5");
        }

        List<FighterServiceEntity> services = FighterServiceEntity.find(query.toString(),
                        Sort.by("createdAt").descending(),
                        serviceType,
                        discipline != null ? "%" + discipline + "%" : null,
                        city != null ? "%" + city + "%" : null,
                        minPrice,
                        maxPrice)
                .page(page, size)
                .list();

        return services.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public FighterServiceDTO findById(UUID id) {
        FighterServiceEntity service = FighterServiceEntity.findById(id);
        return service != null ? convertToDTO(service) : null;
    }

    @Transactional
    public FighterServiceDTO createService(User fighter, FighterServiceDTO dto) {
        FighterServiceEntity service = new FighterServiceEntity();
        service.fighter = fighter;
        service.serviceType = dto.serviceType;
        service.title = dto.title;
        service.description = dto.description;
        service.pricePerHour = dto.pricePerHour;
        service.durationMinutes = dto.durationMinutes;
        service.isOnline = dto.isOnline;
        service.createdAt = LocalDateTime.now();
        service.persist();

        return convertToDTO(service);
    }

    @Transactional
    public FighterServiceDTO updateService(UUID id, FighterServiceDTO dto, User user) {
        FighterServiceEntity service = FighterServiceEntity.findById(id);
        if (service == null || !service.fighter.equals(user)) {
            return null;
        }

        service.serviceType = dto.serviceType;
        service.title = dto.title;
        service.description = dto.description;
        service.pricePerHour = dto.pricePerHour;
        service.durationMinutes = dto.durationMinutes;
        service.isOnline = dto.isOnline;
        service.isActive = dto.isActive;
        service.persist();

        return convertToDTO(service);
    }

    @Transactional
    public boolean deleteService(UUID id, User user) {
        FighterServiceEntity service = FighterServiceEntity.findById(id);
        if (service == null || !service.fighter.equals(user)) {
            return false;
        }

        service.isActive = false;
        service.persist();
        return true;
    }

    private FighterServiceDTO convertToDTO(FighterServiceEntity service) {
        FighterServiceDTO dto = new FighterServiceDTO();
        dto.id = service.id;
        dto.serviceType = service.serviceType;
        dto.title = service.title;
        dto.description = service.description;
        dto.pricePerHour = service.pricePerHour;
        dto.durationMinutes = service.durationMinutes;
        dto.isActive = service.isActive;
        dto.isOnline = service.isOnline;
        dto.fighterName = service.fighter.getDisplayName();
        dto.fighterUsername = service.fighter.username;
        dto.fighterDiscipline = service.fighter.primaryDiscipline;
        dto.fighterCity = service.fighter.city != null ? service.fighter.city.name : null;
        dto.createdAt = service.createdAt;
        return dto;
    }
}