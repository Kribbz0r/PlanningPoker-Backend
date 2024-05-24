package org.planningPoker.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
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
    @Operation(summary = "Get all users", description = "Admin retrieves a list of all users from the database.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user(admin) was not found")
    @APIResponse(responseCode = "200", description = "List retrieved and returned.")
    @Path("/all-users")
    public Response getAllUsers(@HeaderParam("Authorization") String jwtToken) {
        return userService.getAllUsers(jwtToken);
    }

    @GET
    @Operation(summary = "Get a single user", description = "After log in the JWT is sent here to retrieve a users details.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "200", description = "User details returned successfully.")
    @Path("/get-user")
    public Response getUser(@HeaderParam("Authorization") String jwtToken) {
        return userService.getUser(jwtToken);
    }

    @PATCH
    @Operation(summary = "Change a users access", description = "Give or remove permisson for an employee to use the application.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "200", description = "The employees access was successfully updated")
    @Path("/change-access")
    public Response changeUserAccess(@HeaderParam("Authorization") String jwtToken, @HeaderParam("userEmail") String userEmail) {
        return userService.changeUserAccess(jwtToken, userEmail);
    }

    @GET
    @Operation(summary = "Get number of employees currently with access", description = "Returns a number representing the number of employees who have authorization to use the application")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "500", description = "Server error when performing the count.")
    @APIResponse(responseCode = "200", description = "The number was successfully returned.")
    @Path("/number-with-access")
    public Response getNumberWithAccess(@HeaderParam("Authorization") String jwtToken) {
        return userService.getNumberWithAccess(jwtToken);
    }


}
