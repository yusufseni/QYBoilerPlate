// FighterService
package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.FighterProfileDTO;
import com.yoesoff.plate.entity.BookingEntity;
import com.yoesoff.plate.entity.ReviewEntity;
import com.yoesoff.plate.entity.UserEntity;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FighterService {

    public UserEntity findFighterByUsername(String username) {
        return UserEntity.find("username = ?1 and role = 'FIGHTER' and status = 'ACTIVE'", username).firstResult();
    }

    public List<FighterProfileDTO> searchFighters(String discipline, String city, String serviceType, int page, int size) {
        StringBuilder query = new StringBuilder("role = 'FIGHTER' and status = 'ACTIVE'");

        if (discipline != null && !discipline.isBlank()) {
            query.append(" and primaryDiscipline ilike ?1");
        }
        if (city != null && !city.isBlank()) {
            query.append(" and city.name ilike ?2");
        }

        // Execute query and convert to DTOs
        List<UserEntity> fighters = UserEntity.find(query.toString(),
                        discipline != null ? "%" + discipline + "%" : null,
                        city != null ? "%" + city + "%" : null)
                .page(page, size)
                .list();

        return fighters.stream()
                .map(this::convertToProfileDTO)
                .toList();
    }

    @Transactional
    public void updateProfile(UserEntity fighter, String firstName, String lastName, String fightName,
                              String bio, String primaryDiscipline, String weightClass,
                              String gym, String trainer, String achievements) {
        fighter.firstName = firstName;
        fighter.lastName = lastName;
        fighter.fightName = fightName;
        fighter.bio = bio;
        fighter.primaryDiscipline = primaryDiscipline;
        fighter.weightClass = weightClass;
        fighter.gym = gym;
        fighter.trainer = trainer;
        fighter.achievements = achievements;
        fighter.updatedAt = LocalDateTime.now();
        fighter.persist();
    }

    public List<BookingEntity> getFighterBookings(UserEntity fighter) {
        return BookingEntity.find("service.fighter = ?1", Sort.by("scheduledDateTime").descending(), fighter).list();
    }

    public Double getAverageRating(UserEntity fighter) {
        return ReviewEntity.getAverageRating(fighter);
    }

    public Long getReviewCount(UserEntity fighter) {
        return ReviewEntity.getReviewCount(fighter);
    }

    private FighterProfileDTO convertToProfileDTO(UserEntity fighter) {
        FighterProfileDTO dto = new FighterProfileDTO();
        dto.id = fighter.id;
        dto.username = fighter.username;
        dto.fightName = fighter.fightName;
        dto.firstName = fighter.firstName;
        dto.lastName = fighter.lastName;
        dto.bio = fighter.bio;
        dto.primaryDiscipline = fighter.primaryDiscipline;
        dto.weightClass = fighter.weightClass;
        dto.gym = fighter.gym;
        dto.profileImageUrl = fighter.profileImageUrl;
        dto.averageRating = getAverageRating(fighter);
        dto.reviewCount = getReviewCount(fighter);
        dto.servicesCount = (long) fighter.services.size();
        dto.cityName = fighter.cityEntity != null ? fighter.cityEntity.name : null;
        return dto;
    }
}