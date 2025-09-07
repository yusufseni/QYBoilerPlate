package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.BookingDTO;
import com.yoesoff.plate.dto.BookingRequestDTO;
import com.yoesoff.plate.entity.BookingEntity;
import com.yoesoff.plate.entity.FighterServiceEntity;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.BookingStatus;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookingService {

    public List<BookingDTO> getUserBookings(UserEntity userEntity, BookingStatus status, int page, int size) {
        StringBuilder query = new StringBuilder();

        if (userEntity.isFighter()) {
            query.append("service.fighter = ?1");
        } else {
            query.append("client = ?1");
        }

        if (status != null) {
            query.append(" and status = ?2");
        }

        List<BookingEntity> bookingEntities = BookingEntity.find(query.toString(),
                        Sort.by("scheduledDateTime").descending(),
                        userEntity, status)
                .page(page, size)
                .list();

        return bookingEntities.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public BookingDTO findBookingById(UUID id, UserEntity userEntity) {
        BookingEntity bookingEntity = BookingEntity.findById(id);
        if (bookingEntity == null) return null;

        // Check if user has access to this booking
        if (!bookingEntity.client.equals(userEntity) && !bookingEntity.getFighter().equals(userEntity)) {
            return null;
        }

        return convertToDTO(bookingEntity);
    }

    @Transactional
    public BookingDTO createBooking(UserEntity client, BookingRequestDTO request) {
        FighterServiceEntity service = FighterServiceEntity.findById(request.serviceId);
        if (service == null || !service.isActive) {
            throw new IllegalArgumentException("Service not available");
        }

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.service = service;
        bookingEntity.client = client;
        bookingEntity.scheduledDateTime = request.scheduledDateTime;
        bookingEntity.durationMinutes = request.durationMinutes != null ?
                request.durationMinutes : service.durationMinutes;
        bookingEntity.clientNotes = request.clientNotes;
        bookingEntity.status = BookingStatus.PENDING;
        bookingEntity.createdAt = LocalDateTime.now();

        // Calculate total price
        BigDecimal hourlyRate = service.pricePerHour;
        BigDecimal hours = BigDecimal.valueOf(bookingEntity.durationMinutes / 60.0);
        bookingEntity.totalPrice = hourlyRate.multiply(hours);

        bookingEntity.persist();
        return convertToDTO(bookingEntity);
    }

    @Transactional
    public BookingDTO confirmBooking(UUID id, UserEntity fighter) {
        BookingEntity bookingEntity = BookingEntity.findById(id);
        if (bookingEntity == null || !bookingEntity.getFighter().equals(fighter)) {
            return null;
        }

        bookingEntity.status = BookingStatus.CONFIRMED;
        bookingEntity.confirmedAt = LocalDateTime.now();
        bookingEntity.persist();

        return convertToDTO(bookingEntity);
    }

    @Transactional
    public BookingDTO cancelBooking(UUID id, UserEntity userEntity, String reason) {
        BookingEntity bookingEntity = BookingEntity.findById(id);
        if (bookingEntity == null) return null;

        // Check if user can cancel this booking
        if (!bookingEntity.client.equals(userEntity) && !bookingEntity.getFighter().equals(userEntity)) {
            return null;
        }

        bookingEntity.status = BookingStatus.CANCELLED;
        bookingEntity.cancelledAt = LocalDateTime.now();

        if (bookingEntity.getFighter().equals(userEntity)) {
            bookingEntity.fighterNotes = reason;
        } else {
            bookingEntity.clientNotes = reason;
        }

        bookingEntity.persist();
        return convertToDTO(bookingEntity);
    }

    @Transactional
    public BookingDTO completeBooking(UUID id, UserEntity fighter, String notes) {
        BookingEntity bookingEntity = BookingEntity.findById(id);
        if (bookingEntity == null || !bookingEntity.getFighter().equals(fighter)) {
            return null;
        }

        bookingEntity.status = BookingStatus.COMPLETED;
        bookingEntity.completedAt = LocalDateTime.now();
        bookingEntity.fighterNotes = notes;
        bookingEntity.persist();

        return convertToDTO(bookingEntity);
    }

    private BookingDTO convertToDTO(BookingEntity bookingEntity) {
        BookingDTO dto = new BookingDTO();
        dto.id = bookingEntity.id;
        dto.scheduledDateTime = bookingEntity.scheduledDateTime;
        dto.durationMinutes = bookingEntity.durationMinutes;
        dto.totalPrice = bookingEntity.totalPrice;
        dto.status = bookingEntity.status;
        dto.clientNotes = bookingEntity.clientNotes;
        dto.fighterNotes = bookingEntity.fighterNotes;
        dto.createdAt = bookingEntity.createdAt;
        dto.confirmedAt = bookingEntity.confirmedAt;
        dto.cancelledAt = bookingEntity.cancelledAt;
        dto.completedAt = bookingEntity.completedAt;

        // Service info
        dto.serviceTitle = bookingEntity.service.title;
        dto.serviceType = bookingEntity.service.serviceType;

        // Client info
        dto.clientName = bookingEntity.client.getFullName();
        dto.clientUsername = bookingEntity.client.username;

        // Fighter info
        dto.fighterName = bookingEntity.getFighter().getDisplayName();
        dto.fighterUsername = bookingEntity.getFighter().username;

        return dto;
    }
}