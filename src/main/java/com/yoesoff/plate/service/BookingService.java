package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.BookingDTO;
import com.yoesoff.plate.dto.BookingRequestDTO;
import com.yoesoff.plate.entity.Booking;
import com.yoesoff.plate.entity.FighterServiceEntity;
import com.yoesoff.plate.entity.User;
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

    public List<BookingDTO> getUserBookings(User user, BookingStatus status, int page, int size) {
        StringBuilder query = new StringBuilder();

        if (user.isFighter()) {
            query.append("service.fighter = ?1");
        } else {
            query.append("client = ?1");
        }

        if (status != null) {
            query.append(" and status = ?2");
        }

        List<Booking> bookings = Booking.find(query.toString(),
                        Sort.by("scheduledDateTime").descending(),
                        user, status)
                .page(page, size)
                .list();

        return bookings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public BookingDTO findBookingById(UUID id, User user) {
        Booking booking = Booking.findById(id);
        if (booking == null) return null;

        // Check if user has access to this booking
        if (!booking.client.equals(user) && !booking.getFighter().equals(user)) {
            return null;
        }

        return convertToDTO(booking);
    }

    @Transactional
    public BookingDTO createBooking(User client, BookingRequestDTO request) {
        FighterServiceEntity service = FighterServiceEntity.findById(request.serviceId);
        if (service == null || !service.isActive) {
            throw new IllegalArgumentException("Service not available");
        }

        Booking booking = new Booking();
        booking.service = service;
        booking.client = client;
        booking.scheduledDateTime = request.scheduledDateTime;
        booking.durationMinutes = request.durationMinutes != null ?
                request.durationMinutes : service.durationMinutes;
        booking.clientNotes = request.clientNotes;
        booking.status = BookingStatus.PENDING;
        booking.createdAt = LocalDateTime.now();

        // Calculate total price
        BigDecimal hourlyRate = service.pricePerHour;
        BigDecimal hours = BigDecimal.valueOf(booking.durationMinutes / 60.0);
        booking.totalPrice = hourlyRate.multiply(hours);

        booking.persist();
        return convertToDTO(booking);
    }

    @Transactional
    public BookingDTO confirmBooking(UUID id, User fighter) {
        Booking booking = Booking.findById(id);
        if (booking == null || !booking.getFighter().equals(fighter)) {
            return null;
        }

        booking.status = BookingStatus.CONFIRMED;
        booking.confirmedAt = LocalDateTime.now();
        booking.persist();

        return convertToDTO(booking);
    }

    @Transactional
    public BookingDTO cancelBooking(UUID id, User user, String reason) {
        Booking booking = Booking.findById(id);
        if (booking == null) return null;

        // Check if user can cancel this booking
        if (!booking.client.equals(user) && !booking.getFighter().equals(user)) {
            return null;
        }

        booking.status = BookingStatus.CANCELLED;
        booking.cancelledAt = LocalDateTime.now();

        if (booking.getFighter().equals(user)) {
            booking.fighterNotes = reason;
        } else {
            booking.clientNotes = reason;
        }

        booking.persist();
        return convertToDTO(booking);
    }

    @Transactional
    public BookingDTO completeBooking(UUID id, User fighter, String notes) {
        Booking booking = Booking.findById(id);
        if (booking == null || !booking.getFighter().equals(fighter)) {
            return null;
        }

        booking.status = BookingStatus.COMPLETED;
        booking.completedAt = LocalDateTime.now();
        booking.fighterNotes = notes;
        booking.persist();

        return convertToDTO(booking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.id = booking.id;
        dto.scheduledDateTime = booking.scheduledDateTime;
        dto.durationMinutes = booking.durationMinutes;
        dto.totalPrice = booking.totalPrice;
        dto.status = booking.status;
        dto.clientNotes = booking.clientNotes;
        dto.fighterNotes = booking.fighterNotes;
        dto.createdAt = booking.createdAt;
        dto.confirmedAt = booking.confirmedAt;
        dto.cancelledAt = booking.cancelledAt;
        dto.completedAt = booking.completedAt;

        // Service info
        dto.serviceTitle = booking.service.title;
        dto.serviceType = booking.service.serviceType;

        // Client info
        dto.clientName = booking.client.getFullName();
        dto.clientUsername = booking.client.username;

        // Fighter info
        dto.fighterName = booking.getFighter().getDisplayName();
        dto.fighterUsername = booking.getFighter().username;

        return dto;
    }
}