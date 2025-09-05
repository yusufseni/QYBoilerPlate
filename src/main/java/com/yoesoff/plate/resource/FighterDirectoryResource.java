
package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FighterProfileDTO;
import com.yoesoff.plate.service.FighterService;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/fighters")
public class FighterDirectoryResource {

    @Inject @Location("fighter/directory.html") Template fighterDirectory;
    @Inject @Location("fighter/search.html") Template fighterSearch;// templates/fighter-search.html

    @Inject FighterService fighterService;

    // --- FIGHTER DIRECTORY PAGE ---
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance directoryPage(
            @QueryParam("discipline") String discipline,
            @QueryParam("city") String city,
            @QueryParam("service") String serviceType) {

        List<FighterProfileDTO> fighters = fighterService.searchFighters(discipline, city, serviceType, 0, 12);

        return fighterDirectory.data(Map.of(
                "fighters", fighters,
                "selectedDiscipline", discipline != null ? discipline : "",
                "selectedCity", city != null ? city : "",
                "selectedService", serviceType != null ? serviceType : ""
        ));
    }

    // --- FIGHTER SEARCH API ---
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchFighters(
            @QueryParam("discipline") String discipline,
            @QueryParam("city") String city,
            @QueryParam("service") String serviceType,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("12") int size) {

        List<FighterProfileDTO> fighters = fighterService.searchFighters(discipline, city, serviceType, page, size);
        return Response.ok(fighters).build();
    }
}