package org.planningPoker.resource;

import org.planningPoker.service.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/user")
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
        return userService.getUser(jwtToken);
    }
   
    @GET
    @Path("/tasks")
    public Response getUserTasks(@HeaderParam("Authorization") String jwtToken) {      
        return userService.getUserTasks(jwtToken);
    }

    @PATCH
    @Path("/change-access")
    public Response changeUserAccess(@HeaderParam("Authorization") String jwtToken, @HeaderParam("userEmail") String userEmail) {
        return userService.changeUserAccess(jwtToken, userEmail);
    }


}
