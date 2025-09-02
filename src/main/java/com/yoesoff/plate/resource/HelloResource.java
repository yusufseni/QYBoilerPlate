package com.yoesoff.plate.resource;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {
    @Inject
    Template hello; // otomatis mengacu ke templates/hello.html

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance hello(@QueryParam("name") String name) {
        String displayName = (name != null && !name.isEmpty()) ? name : "anonymous";
        return hello.data("name", displayName);
    }
}

