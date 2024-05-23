package org.planningPoker.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.planningPoker.model.LoginDto;
import org.planningPoker.service.SecurityService;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/security")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SecurityResource {
    
      @Inject 
      SecurityService securityService;

    @POST
    @Operation(summary = "User Login", description = "Both admin and employees log in here.")
    @APIResponse(responseCode = "403", description = "The user does not have permission to use the application.")
    @APIResponse(responseCode = "401", description = "The user has entered bad credentials.")
    @APIResponse(responseCode = "200", description = "The user has successfully logged in and received their Json Web Token.")
    @Path("/login")
    @PermitAll
    public Response login(@Valid @RequestBody final LoginDto loginDto) throws Exception {
        return securityService.login(loginDto);
    }

}
