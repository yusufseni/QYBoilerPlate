package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FlashMessage;
import com.yoesoff.plate.dto.OrganizationSummaryDTO;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.FriendshipService;
import com.yoesoff.plate.service.MembershipService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/social")
public class SocialResource {

    private static final String SESSION_COOKIE = "SESSION";

    @Inject @Location("social/friends.html") Template friendsPage;
    @Inject @Location("social/memberships.html") Template membershipsPage;
    @Inject @Location("social/organizations.html") Template organizationsPage;

    @Inject AuthService authService;
    @Inject FriendshipService friendshipService;
    @Inject MembershipService membershipService;

    private Optional<UserEntity> currentUser(@CookieParam(SESSION_COOKIE) String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return authService.findUserByToken(token);
    }

    private Response redirectToLogin(UriInfo uriInfo, String message) {
        URI uri = uriInfo.getBaseUriBuilder().path("login")
                .queryParam("type", "INFO")
                .queryParam("msg", message).build();
        return Response.seeOther(uri).build();
    }

    // Friends page
    @GET
    @Path("/friends")
    @Produces(MediaType.TEXT_HTML)
    public Response friendsPage(@CookieParam(SESSION_COOKIE) String token,
                                @Context UriInfo uriInfo,
                                @QueryParam("type") String type,
                                @QueryParam("msg") String msg) {
        Optional<UserEntity> userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity user = userOpt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("friends", friendshipService.getFriends(user, 0, 50));
        data.put("pendingReceived", friendshipService.getPendingRequests(user, false, 0, 20));
        data.put("pendingSent", friendshipService.getPendingRequests(user, true, 0, 20));
        data.put("suggestions", friendshipService.suggestFriends(user, 10));
        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(friendsPage.data(data)).build();
    }

    // Memberships page
    @GET
    @Path("/memberships")
    @Produces(MediaType.TEXT_HTML)
    public Response membershipsPage(@CookieParam(SESSION_COOKIE) String token,
                                    @Context UriInfo uriInfo,
                                    @QueryParam("type") String type,
                                    @QueryParam("msg") String msg) {
        Optional<UserEntity> userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        UserEntity user = userOpt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("memberships", membershipService.getUserMemberships(user, null, 0, 50));

        // If user is an organization, show their members
        if (user.organizationType != OrganizationType.PERSONAL) {
            data.put("members", membershipService.getOrganizationMembers(user, null, 0, 50));
        }

        data.put("flash", new FlashMessage(parseType(type), msg));

        return Response.ok(membershipsPage.data(data)).build();
    }

    // Organizations directory
    @GET
    @Path("/organizations")
    @Produces(MediaType.TEXT_HTML)
    public Response organizationsPage(@CookieParam(SESSION_COOKIE) String token,
                                      @Context UriInfo uriInfo,
                                      @QueryParam("type") String organizationType,
                                      @QueryParam("city") String city) {
        Optional<UserEntity> userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            return redirectToLogin(uriInfo, "Please login first");
        }

        // Find organizations
        StringBuilder query = new StringBuilder("organizationType != 'PERSONAL' and status = 'ACTIVE'");
        if (organizationType != null && !organizationType.isBlank()) {
            query.append(" and organizationType = '").append(organizationType).append("'");
        }
        if (city != null && !city.isBlank()) {
            query.append(" and cityEntity.name ilike '%").append(city).append("%'");
        }

        List<UserEntity> organizations = UserEntity.find(query.toString()).page(0, 20).list();

        Map<String, Object> data = new HashMap<>();
        data.put("user", userOpt.get());
        data.put("organizations", organizations);
        data.put("organizationTypes", OrganizationType.values());
        data.put("selectedType", organizationType);
        data.put("selectedCity", city);

        return Response.ok(organizationsPage.data(data)).build();
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