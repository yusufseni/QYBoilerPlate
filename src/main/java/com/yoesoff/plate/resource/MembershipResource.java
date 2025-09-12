package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.MembershipDTO;
import com.yoesoff.plate.dto.MembershipRequestDTO;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.MembershipStatus;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.MembershipService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/memberships")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MembershipResource {

    @Inject
    MembershipService membershipService;

    @Inject
    AuthService authService;

    // Get user's memberships
    @GET
    @Path("/my")
    public Response getMyMemberships(
            @QueryParam("status") MembershipStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        List<MembershipDTO> memberships = membershipService.getUserMemberships(userOpt.get(), status, page, size);
        return Response.ok(memberships).build();
    }

    // Get organization's members (only for organization users)
    @GET
    @Path("/members")
    public Response getMembers(
            @QueryParam("status") MembershipStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            List<MembershipDTO> members = membershipService.getOrganizationMembers(userOpt.get(), status, page, size);
            return Response.ok(members).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Only organizations can view members").build();
        }
    }

    // Request membership
    @POST
    @Path("/request")
    @Transactional
    public Response requestMembership(
            MembershipRequestDTO request,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            MembershipDTO membership = membershipService.requestMembership(userOpt.get(), request);
            return Response.status(Response.Status.CREATED).entity(membership).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // Approve membership (organization only)
    @PUT
    @Path("/{id}/approve")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response approveMembership(
            @PathParam("id") UUID id,
            @FormParam("notes") String notes,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MembershipDTO membership = membershipService.approveMembership(id, userOpt.get(), notes);
        if (membership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(membership).build();
    }

    // Reject membership (organization only)
    @PUT
    @Path("/{id}/reject")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response rejectMembership(
            @PathParam("id") UUID id,
            @FormParam("reason") String reason,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MembershipDTO membership = membershipService.rejectMembership(id, userOpt.get(), reason);
        if (membership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(membership).build();
    }

    // Cancel membership (either party)
    @PUT
    @Path("/{id}/cancel")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response cancelMembership(
            @PathParam("id") UUID id,
            @FormParam("reason") String reason,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MembershipDTO membership = membershipService.cancelMembership(id, userOpt.get(), reason);
        if (membership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(membership).build();
    }

    // Suspend membership (organization only)
    @PUT
    @Path("/{id}/suspend")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response suspendMembership(
            @PathParam("id") UUID id,
            @FormParam("reason") String reason,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MembershipDTO membership = membershipService.suspendMembership(id, userOpt.get(), reason);
        if (membership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(membership).build();
    }

    // Reactivate membership (organization only)
    @PUT
    @Path("/{id}/reactivate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response reactivateMembership(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        MembershipDTO membership = membershipService.reactivateMembership(id, userOpt.get());
        if (membership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(membership).build();
    }
}