package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.UserDTO;
import com.yoesoff.plate.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        List<UserDTO> users = userService.listAllPaged(page, size);
        return Response.ok(users).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        UserDTO user = userService.findById(id);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(user).build();
    }

    @POST
    public Response create(UserDTO dto) {
        UserDTO created = userService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, UserDTO dto) {
        UserDTO updated = userService.update(id, dto);
        if (updated == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = userService.delete(id);
        if (!deleted) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}