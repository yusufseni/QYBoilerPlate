package com.yoesoff.plate.resource.web;

import com.yoesoff.plate.dto.FlashMessage;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.service.AuthService;
import com.yoesoff.plate.util.FlashMessageUtil;
import io.quarkus.qute.Location;
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
public class AuthWebResource {

    private static final String SESSION_COOKIE = "SESSION";

    @Inject Template login;
    @Inject Template registration;
    @Inject Template dashboard;
    @Inject Template profile;


    @Inject AuthService auth;

    private Optional<UserEntity> currentUser(@CookieParam(SESSION_COOKIE) String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return auth.findUserByToken(token);
    }

    @GET
    @Path("login")
    @Produces(MediaType.TEXT_HTML)
    public Response loginPage(@CookieParam(FlashMessageUtil.FLASH_COOKIE) Cookie flashCookie,
                              @Context HttpHeaders headers) {
        String type = null, msg = null;
        if (flashCookie != null) {
            String[] parts = FlashMessageUtil.parseFlashCookie(flashCookie);
            if (parts != null && parts.length == 2) {
                type = parts[0];
                msg = parts[1];
            }
        }
        // Clear the flash cookie after reading
        TemplateInstance page = login.data("flash", new FlashMessage(parseType(type), msg));
        return Response.ok(page)
                .cookie(FlashMessageUtil.clearFlashCookie())
                .build();
    }


    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response doLogin(@RestForm String username, @RestForm String password, @Context UriInfo uriInfo) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login").build();
            return Response.seeOther(uri)
                    .cookie(FlashMessageUtil.createFlashCookie("DANGER", "Username/password required"))
                    .build();
        }

        Optional<UserEntity> userOpt = auth.authenticate(username, password);
        if (userOpt.isEmpty()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login").build();
            return Response.seeOther(uri)
                    .cookie(FlashMessageUtil.createFlashCookie("DANGER", "Login failed"))
                    .build();
        }

        var session = auth.createSession(userOpt.get(), 7);
        NewCookie sessionCookie = new NewCookie(
                SESSION_COOKIE, session.token, "/", null,
                "login session", 7 * 24 * 3600,
                true, true
        );

        // Redirect based on user role
        String redirectPath = userOpt.get().isFighter() ? "fighter/profile" : "dashboard";

        return Response.seeOther(uriInfo.getBaseUriBuilder().path(redirectPath).build())
                .cookie(sessionCookie)
                .cookie(FlashMessageUtil.createFlashCookie("SUCCESS", "Welcome " + username))
                .build();
    }


    // --- ENHANCED REGISTRATION FOR CLIENTS ---
    @GET
    @Path("registration")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance registrationPage(@QueryParam("type") String type, @QueryParam("msg") String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("flash", new FlashMessage(parseType(type), msg));
        data.put("organizationTypes", OrganizationType.values());
        return registration.data(data);
    }

    @POST
    @Path("registration")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response doRegister(
            @RestForm OrganizationType organizationType,
            @RestForm String username,
            @RestForm String email,
            @RestForm String password,
            @RestForm String firstName,
            @RestForm String lastName,
            @RestForm String phoneNumber,
            @Context UriInfo uriInfo) {

        if (username == null || email == null || password == null ||
                username.isBlank() || email.isBlank() || password.isBlank()) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration")
                    .queryParam("type", "INFO")
                    .queryParam("msg", "Username, email, and password are required").build();
            return Response.seeOther(uri).build();
        }

        if (auth.usernameExists(username)) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration")
                    .queryParam("type", "WARNING")
                    .queryParam("msg", "Username already exists").build();
            return Response.seeOther(uri).build();
        }

        if (auth.emailExists(email)) {
            URI uri = uriInfo.getBaseUriBuilder().path("registration")
                    .queryParam("type", "WARNING")
                    .queryParam("msg", "Email already exists").build();
            return Response.seeOther(uri).build();
        }

        var user = auth.registerClient(organizationType, username, email, password, firstName, lastName, phoneNumber);
        URI uri = uriInfo.getBaseUriBuilder().path("login")
                .queryParam("type", "SUCCESS")
                .queryParam("msg", "Registration successful, please login").build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("profile")
    @Produces(MediaType.TEXT_HTML)
    public Response userProfilePage(@CookieParam(SESSION_COOKIE) String token,
                                    @Context UriInfo uriInfo,
                                    @QueryParam("type") String type,
                                    @QueryParam("msg") String msg) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            URI uri = uriInfo.getBaseUriBuilder().path("login")
                    .queryParam("type", "INFO")
                    .queryParam("msg", "Please login first").build();
            return Response.seeOther(uri).build();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user", userOpt.get());
        data.put("flash", new FlashMessage(parseType(type), msg));
        return Response.ok(profile.data(data)).build();
    }

    // --- DASHBOARD (role-based redirect) ---
    @GET
    @Path("dashboard")
    @Produces(MediaType.TEXT_HTML)
    public Response dashboardPage(@CookieParam(SESSION_COOKIE) String token,
                                  @CookieParam(FlashMessageUtil.FLASH_COOKIE) Cookie flashCookie) {
        var userOpt = currentUser(token);
        if (userOpt.isEmpty()) {
            // No flash here, just redirect
            return Response.seeOther(UriBuilder.fromPath("login").build()).build();
        }

        String type = null, msg = null;
        if (flashCookie != null) {
            String[] parts = FlashMessageUtil.parseFlashCookie(flashCookie);
            if (parts != null && parts.length == 2) {
                type = parts[0];
                msg = parts[1];
            }
        }
        TemplateInstance page = dashboard.data("user", userOpt.get())
                .data("flash", new FlashMessage(parseType(type), msg));
        return Response.ok(page)
                .cookie(FlashMessageUtil.clearFlashCookie())
                .build();
    }

    // --- LOGOUT (same as your current code) ---
    @GET
    @Path("logout")
    @Transactional
    public Response logout(@CookieParam(SESSION_COOKIE) String token, @Context UriInfo uriInfo) {
        if (token != null && !token.isBlank()) {
            auth.deleteSession(token);
        }
        NewCookie expired = new NewCookie(SESSION_COOKIE, "", "/", null, "logout", 0, true, true);
        return Response.seeOther(uriInfo.getBaseUriBuilder().path("login").build())
                .cookie(expired)
                .cookie(FlashMessageUtil.createFlashCookie("SUCCESS", "You have been logged out"))
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