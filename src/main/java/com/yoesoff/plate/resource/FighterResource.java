package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FlashMessage;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.FighterService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/fighter")
public class FighterResource {

    private static final String SESSION_COOKIE = "SESSION";

    @Inject @Location("fighter/profile.html") Template fighterProfile;
    @Inject @Location("fighter/edit.html") Template fighterEdit;
    @Inject @Location("fighter/services.html") Template fighterServices;
    @Inject @Location("fighter/bookings.html") Template fighterBookings;

    @Inject AuthService auth;
    @Inject FighterService fighterService;

    private Optional<UserEntity> currentUser(@CookieParam(SESSION_COOKIE) String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return auth.findUserByToken(token);
    }

    private Response redirectToLogin(UriInfo uriInfo, String message) {
        URI uri = uriInfo.getBaseUriBuilder().path("login")
                .queryParam("type", "INFO")
                .queryParam("msg", message).build();
        return Response.seeOther(uri).build();
    }

    // --- FIGHTER PROFILE ---
    @GET
    @Path("profile")
    @Produces(MediaType.TEXT_HTML)
    public Response profilePage(@CookieParam(SESSION_COOKIE) String token,
                                @Context UriInfo uriInfo,
                                @QueryParam("type") String type,
                                @QueryParam("msg") String msg) {
        Optional<UserEntity> userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity userEntity = userOpt.get();
        if (!userEntity.isFighter()) {
            URI uri = uriInfo.getBaseUriBuilder().path("dashboard").build();
            return Response.seeOther(uri).build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fighter", userEntity);
        data.put("fightRecords", userEntity.fightRecordEntities != null ? userEntity.fightRecordEntities : java.util.List.of());
        data.put("services", userEntity.services != null ? userEntity.services : java.util.List.of());
        data.put("reviews", userEntity.receivedReviews != null ? userEntity.receivedReviews : java.util.List.of());
        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(fighterProfile.data(data)).build();
    }


    // --- PUBLIC FIGHTER PROFILE ---
    @GET
    @Path("profile/{username}")
    @Produces(MediaType.TEXT_HTML)
    public Response publicProfile(@PathParam("username") String username) {
        UserEntity fighter = fighterService.findFighterByUsername(username);
        if (fighter == null || !fighter.isFighter()) {
            throw new NotFoundException("Fighter not found");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fighter", fighter);
        data.put("fightRecords", fighter.fightRecordEntities);
        data.put("services", fighter.services.stream().filter(s -> s.isActive).toList());
        data.put("reviews", fighter.receivedReviews);
        data.put("averageRating", fighterService.getAverageRating(fighter));
        data.put("reviewCount", fighterService.getReviewCount(fighter));

        return Response.ok(fighterProfile.data(data)).build();
    }

    // --- EDIT PROFILE ---
    @GET
    @Path("edit")
    @Produces(MediaType.TEXT_HTML)
    public Response editProfilePage(@CookieParam(SESSION_COOKIE) String token,
                                    @Context UriInfo uriInfo,
                                    @QueryParam("type") String type,
                                    @QueryParam("msg") String msg) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity userEntity = userOpt.get();
        if (!userEntity.isFighter()) {
            URI uri = uriInfo.getBaseUriBuilder().path("dashboard").build();
            return Response.seeOther(uri).build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fighter", userEntity);
        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(fighterEdit.data(data)).build();
    }

    @POST
    @Path("edit")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response updateProfile(@CookieParam(SESSION_COOKIE) String token,
                                  @Context UriInfo uriInfo,
                                  @FormParam("firstName") String firstName,
                                  @FormParam("lastName") String lastName,
                                  @FormParam("fightName") String fightName,
                                  @FormParam("bio") String bio,
                                  @FormParam("primaryDiscipline") String primaryDiscipline,
                                  @FormParam("weightClass") String weightClass,
                                  @FormParam("gym") String gym,
                                  @FormParam("trainer") String trainer,
                                  @FormParam("achievements") String achievements) {

        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity fighter = userOpt.get();
        if (!fighter.isFighter()) {
            URI uri = uriInfo.getBaseUriBuilder().path("dashboard").build();
            return Response.seeOther(uri).build();
        }

        // Update fighter profile
        fighterService.updateProfile(fighter, firstName, lastName, fightName, bio,
                primaryDiscipline, weightClass, gym, trainer, achievements);

        URI uri = uriInfo.getBaseUriBuilder().path("fighter/profile")
                .queryParam("type", "SUCCESS")
                .queryParam("msg", "Profile updated successfully").build();
        return Response.seeOther(uri).build();
    }

    // --- SERVICES MANAGEMENT ---
    @GET
    @Path("services")
    @Produces(MediaType.TEXT_HTML)
    public Response servicesPage(@CookieParam(SESSION_COOKIE) String token,
                                 @Context UriInfo uriInfo,
                                 @QueryParam("type") String type,
                                 @QueryParam("msg") String msg) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity fighter = userOpt.get();
        if (!fighter.isFighter()) {
            URI uri = uriInfo.getBaseUriBuilder().path("dashboard").build();
            return Response.seeOther(uri).build();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("fighter", fighter);
        data.put("services", fighter.services);
        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(fighterServices.data(data)).build();
    }

    // --- BOOKINGS MANAGEMENT ---
    @GET
    @Path("bookings")
    @Produces(MediaType.TEXT_HTML)
    public Response bookingsPage(@CookieParam(SESSION_COOKIE) String token,
                                 @Context UriInfo uriInfo,
                                 @QueryParam("type") String type,
                                 @QueryParam("msg") String msg) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity fighter = userOpt.get();
        if (!fighter.isFighter()) {
            URI uri = uriInfo.getBaseUriBuilder().path("dashboard").build();
            return Response.seeOther(uri).build();
        }

        var bookings = fighterService.getFighterBookings(fighter);

        Map<String, Object> data = new HashMap<>();
        data.put("fighter", fighter);
        data.put("bookings", bookings);
        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(fighterBookings.data(data)).build();
    }

    private FlashMessage.Type parseType(String type) {
        if (type == null) return FlashMessage.Type.INFO;
        try {
            return FlashMessage.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FlashMessage.Type.INFO;
        }
    }
}