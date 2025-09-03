package com.yoesoff.plate.resource;

import com.yoesoff.plate.dto.FlashMessage;
import com.yoesoff.plate.entity.User;
import com.yoesoff.plate.service.AuthService;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import org.jboss.resteasy.reactive.RestForm;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/")
public class AuthResource {

    private static final String SESSION_COOKIE = "SESSION";

    @Inject Template login;         // templates/login.html
    @Inject Template registration;  // templates/registration.html
    @Inject Template dashboard;     // templates/dashboard.html

    @Inject AuthService auth;

    // Utility: ambil user dari cookie token
    private Optional<User> currentUser(@CookieParam(SESSION_COOKIE) String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return auth.findUserByToken(token);
    }

    // --- LOGIN ---
    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance loginPage(@QueryParam("type") String type, @QueryParam("msg") String msg) {
        return login.data("flash", new FlashMessage(
                parseType(type), msg
        ));
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response doLogin(@RestForm String username, @RestForm String password, @Context UriInfo uriInfo) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login")
                    .queryParam("type", "DANGER")
                    .queryParam("msg", "Username/password wajib").build();
            return Response.seeOther(uri).build();
        }

        Optional<User> userOpt = auth.authenticate(username, password);
        if (userOpt.isEmpty()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login")
                    .queryParam("type", "DANGER")
                    .queryParam("msg", "Login gagal").build();
            return Response.seeOther(uri).build();
        }

        var session = auth.createSession(userOpt.get(), 7);
        NewCookie cookie = new NewCookie(
                SESSION_COOKIE, session.token, "/", null,
                "login session", 7 * 24 * 3600, // maxAge 7 hari
                true,  // secure? set true kalau sudah HTTPS
                true   // httpOnly
        );

        return Response.seeOther(uriInfo.getBaseUriBuilder().path("dashboard").queryParam("type", "SUCCESS")
                .queryParam("msg", "Selamat datang " + username).build())
                .cookie(cookie)
                .build();
    }

    // --- REGISTRATION ---
    @GET
    @Path("registration")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance registrationPage(@QueryParam("type") String type, @QueryParam("msg") String msg) {
        return registration.data("flash", new FlashMessage(
                parseType(type), msg
        ));
    }

    @POST
    @Path("registration")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response doRegister(@RestForm String username, @RestForm String email, @RestForm String password, @Context UriInfo uriInfo) {
        if (username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank()) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration").queryParam("type", "INFO").queryParam("msg", "Semua field wajib diisi").build();
            return Response.seeOther(uri).build();
        }
        if (auth.usernameExists(username)) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration").queryParam("type", "WARNING").queryParam("msg", "Username sudah dipakai").build();
            return Response.seeOther(uri).build();
        }
        if (auth.emailExists(email)) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration").queryParam("type", "WARNING").queryParam("msg", "Email sudah dipakai").build();
            return Response.seeOther(uri).build();
        }

        var user = auth.register(username, email, password);
        URI uri = uriInfo.getBaseUriBuilder().path("login").queryParam("type", "SUCCESS").queryParam("msg", "Registrasi berhasil, silakan login").build();
        return Response.seeOther(uri).build();
    }

    // --- DASHBOARD (perlu login) ---
    @GET
    @Path("dashboard")
    @Produces(MediaType.TEXT_HTML)
    public Response dashboardPage(@CookieParam(SESSION_COOKIE) String token, @Context UriInfo uriInfo, @QueryParam("type") String type, @QueryParam("msg") String msg) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login").queryParam("type", "INFO").queryParam("msg", "Silakan login dulu").build();
            return Response.seeOther(uri).build();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", userOpt.get());
        data.put("flash", new FlashMessage(
                parseType(type), msg
        ));
        return Response.ok(dashboard.data(data)).build();
    }

    // --- LOGOUT ---
    @GET
    @Path("logout")
    @Transactional
    public Response logout(@CookieParam(SESSION_COOKIE) String token, @Context UriInfo uriInfo) {
        if (token != null && !token.isBlank()) {
            auth.deleteSession(token);
        }
        // hapus cookie
        NewCookie expired = new NewCookie(SESSION_COOKIE, "", "/", null, "logout", 0, true, true);
        return Response.seeOther(uriInfo.getBaseUriBuilder().path("login").queryParam("type", "SUCCESS").queryParam("msg", "Anda telah logout").build())
                .cookie(expired)
                .build();
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
