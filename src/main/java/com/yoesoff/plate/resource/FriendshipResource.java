package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FriendDTO;
import com.yoesoff.plate.dto.FriendRequestDTO;
import com.yoesoff.plate.dto.SocialStatsDTO;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.FriendshipService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/friends")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FriendshipResource {

    @Inject
    FriendshipService friendshipService;

    @Inject
    AuthService authService;

    // Get user's friends
    @GET
    public Response getFriends(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<FriendDTO> friends = friendshipService.getFriends(userOpt.get(), page, size);
        return Response.ok(friends).build();
    }

    // Get pending friend requests
    @GET
    @Path("/requests")
    public Response getPendingRequests(
            @QueryParam("sent") @DefaultValue("false") boolean sent,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<FriendDTO> requests = friendshipService.getPendingRequests(userOpt.get(), sent, page, size);
        return Response.ok(requests).build();
    }

    // Send friend request
    @POST
    @Path("/request")
    @Transactional
    public Response sendFriendRequest(
            FriendRequestDTO request,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            FriendDTO friendship = friendshipService.sendFriendRequest(userOpt.get(), request);
            return Response.status(Response.Status.CREATED).entity(friendship).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Accept friend request
    @PUT
    @Path("/{id}/accept")
    @Transactional
    public Response acceptFriendRequest(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            FriendDTO friendship = friendshipService.acceptFriendRequest(id, userOpt.get());
            if (friendship == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(friendship).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Reject friend request
    @PUT
    @Path("/{id}/reject")
    @Transactional
    public Response rejectFriendRequest(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        FriendDTO friendship = friendshipService.rejectFriendRequest(id, userOpt.get());
        if (friendship == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(friendship).build();
    }

    // Remove friend
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response removeFriend(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean removed = friendshipService.removeFriend(id, userOpt.get());
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    // Block user
    @POST
    @Path("/block/{userId}")
    @Transactional
    public Response blockUser(
            @PathParam("userId") UUID userId,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean blocked = friendshipService.blockUser(userOpt.get(), userId);
        if (!blocked) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot block user").build();
        }
        return Response.ok().build();
    }

    // Get friend suggestions
    @GET
    @Path("/suggestions")
    public Response getFriendSuggestions(
            @QueryParam("limit") @DefaultValue("10") int limit,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<UserEntity> suggestions = friendshipService.suggestFriends(userOpt.get(), limit);
        return Response.ok(suggestions).build();
    }

    // Get social stats
    @GET
    @Path("/stats")
    public Response getSocialStats(@CookieParam("SESSION") String token) {
        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserEntity user = userOpt.get();
        SocialStatsDTO stats = new SocialStatsDTO();
        stats.friendCount = friendshipService.getFriendCount(user);
        stats.pendingRequestsReceived = friendshipService.getPendingRequests(user, false, 0, Integer.MAX_VALUE).size();
        stats.pendingRequestsSent = friendshipService.getPendingRequests(user, true, 0, Integer.MAX_VALUE).size();

        // Add membership stats if needed
        // stats.activeMemberships = membershipService.getUserMemberships(user, MembershipStatus.ACTIVE, 0, Integer.MAX_VALUE).size();

        return Response.ok(stats).build();
    }
}