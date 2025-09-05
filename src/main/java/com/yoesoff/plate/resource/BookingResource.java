package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.BookingDTO;
import com.yoesoff.plate.dto.BookingRequestDTO;
import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.enums.BookingStatus;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.BookingService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    BookingService bookingService;

    @Inject
    AuthService authService;

    @GET
    public Response getBookings(
            @QueryParam("status") BookingStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<BookingDTO> bookings = bookingService.getUserBookings(userOpt.get(), status, page, size);
        return Response.ok(bookings).build();
    }

    @GET
    @Path("/{id}")
    public Response getBooking(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO booking = bookingService.findBookingById(id, userOpt.get());
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }

    @POST
    @Transactional
    public Response createBooking(
            BookingRequestDTO request,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO booking = bookingService.createBooking(userOpt.get(), request);
        return Response.status(Response.Status.CREATED).entity(booking).build();
    }

    @PUT
    @Path("/{id}/confirm")
    @Transactional
    public Response confirmBooking(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO booking = bookingService.confirmBooking(id, userOpt.get());
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }

    @PUT
    @Path("/{id}/cancel")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response cancelBooking(
            @PathParam("id") UUID id,
            @FormParam("reason") String reason,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO booking = bookingService.cancelBooking(id, userOpt.get(), reason);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }

    @PUT
    @Path("/{id}/complete")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response completeBooking(
            @PathParam("id") UUID id,
            @FormParam("notes") String notes,
            @CookieParam("SESSION") String token) {

        Optional<User> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        BookingDTO booking = bookingService.completeBooking(id, userOpt.get(), notes);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(booking).build();
    }
}