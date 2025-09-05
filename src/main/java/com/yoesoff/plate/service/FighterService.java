// FighterService
package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.FighterProfileDTO;
import com.yoesoff.plate.entity.Booking;
import com.yoesoff.plate.entity.Review;
import com.yoesoff.plate.entity.User;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FighterService {

    public User findFighterByUsername(String username) {
        return User.find("username = ?1 and role = 'FIGHTER' and status = 'ACTIVE'", username).firstResult();
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
        List<User> fighters = User.find(query.toString(),
                        discipline != null ? "%" + discipline + "%" : null,
                        city != null ? "%" + city + "%" : null)
                .page(page, size)
                .list();

        return fighters.stream()
                .map(this::convertToProfileDTO)
                .toList();
    }

    @Transactional
    public void updateProfile(User fighter, String firstName, String lastName, String fightName,
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

    public List<Booking> getFighterBookings(User fighter) {
        return Booking.find("service.fighter = ?1", Sort.by("scheduledDateTime").descending(), fighter).list();
    }

    public Double getAverageRating(User fighter) {
        return Review.getAverageRating(fighter);
    }

    public Long getReviewCount(User fighter) {
        return Review.getReviewCount(fighter);
    }

    private FighterProfileDTO convertToProfileDTO(User fighter) {
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
        dto.cityName = fighter.city != null ? fighter.city.name : null;
        return dto;
    }
}