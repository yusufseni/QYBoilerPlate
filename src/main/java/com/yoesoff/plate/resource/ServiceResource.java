package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FighterServiceDTO;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.ServiceType;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.service.FighterServiceService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/api/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceResource {

    @Inject
    FighterServiceService serviceService;

    @Inject
    AuthService authService;

    @GET
    public Response searchServices(
            @QueryParam("type") ServiceType serviceType,
            @QueryParam("discipline") String discipline,
            @QueryParam("city") String city,
            @QueryParam("minPrice") Double minPrice,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        List<FighterServiceDTO> services = serviceService.searchServices(
                serviceType, discipline, city, minPrice, maxPrice, page, size);
        return Response.ok(services).build();
    }

    @GET
    @Path("/{id}")
    public Response getService(@PathParam("id") UUID id) {
        FighterServiceDTO service = serviceService.findById(id);
        if (service == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(service).build();
    }

    @POST
    @Transactional
    public Response createService(
            FighterServiceDTO serviceDTO,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty() || !userOpt.get().isFighter()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        FighterServiceDTO created = serviceService.createService(userOpt.get(), serviceDTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateService(
            @PathParam("id") UUID id,
            FighterServiceDTO serviceDTO,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        FighterServiceDTO updated = serviceService.updateService(id, serviceDTO, userOpt.get());
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteService(
            @PathParam("id") UUID id,
            @CookieParam("SESSION") String token) {

        Optional<UserEntity> userOpt = authService.findUserByToken(token);
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean deleted = serviceService.deleteService(id, userOpt.get());
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}