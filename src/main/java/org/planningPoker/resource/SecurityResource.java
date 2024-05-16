package org.planningPoker.resource;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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
    @Path("/login")
    @PermitAll
    public Response login(@Valid @RequestBody final LoginDto loginDto) {
        return securityService.login(loginDto);
    }

}
