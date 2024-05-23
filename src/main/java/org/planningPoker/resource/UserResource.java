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
    @Path("/all-users")
    public Response getAllUsers(@HeaderParam("Authorization") String jwtToken) {
        return userService.getAllUsers(jwtToken);
    }

    @GET
    @Path("/get-user")
    public Response getUser(@HeaderParam("Authorization") String jwtToken) {
        return userService.getUser(jwtToken);
    }
   
    @GET
    @Path("/tasks")
    public Response getUserTasks(@HeaderParam("Authorization") String jwtToken, @HeaderParam("projectName") String projectName) {      
        return userService.getUserTasks(jwtToken, projectName);
    }

    @PATCH
    @Path("/change-access")
    public Response changeUserAccess(@HeaderParam("Authorization") String jwtToken, @HeaderParam("userEmail") String userEmail) {
        return userService.changeUserAccess(jwtToken, userEmail);
    }

    @GET
    @Path("/number-with-access")
    public Response getNumberWithAccess(@HeaderParam("Authorization") String jwtToken) {
        return userService.getNumberWithAccess(jwtToken);
    }


}
