package org.planningPoker.resource;

import org.planningPoker.service.UserService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/test")
    public Response testGet() {
        return userService.testGet();
    }

    @GET
    @Path("/get-user")
    public Response getUser(@HeaderParam("Authorization") String jwtToken) {
        System.out.println(jwtToken);
        return userService.getUser(jwtToken);
    }

}
