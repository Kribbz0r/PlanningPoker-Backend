package org.planningPoker.service;

import org.planningPoker.model.LoginDto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class SecurityService {

    public Response login(@Valid LoginDto loginDto) {
        return Response.ok().entity("You made it!").build();
    }
    
}
