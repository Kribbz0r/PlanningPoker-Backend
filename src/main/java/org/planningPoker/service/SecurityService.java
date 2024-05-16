package org.planningPoker.service;

import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import org.planningPoker.model.LoginDto;
import org.planningPoker.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class SecurityService {

    @Inject
    UserService userService;

    public Response login(@Valid LoginDto loginDto) {

        Response userResponse = userService.findUser(loginDto.getEmail());
        Document userDocument = (Document) userResponse.getEntity();
        User user;

        if(userDocument != null) {
            user = new User(userDocument.get("_id").toString(), userDocument.get("email").toString(), userDocument.get("role").toString(), Integer.parseInt(userDocument.get("authorized").toString()), 
                            userDocument.get("password").toString(), userDocument.get("name").toString());
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect email or password").build();
        }
        
        if (user != null && checkCredentials(user.getPassword(), loginDto.getPassword())) {
            return Response.ok().entity("You made it!").build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("Incorrect email or password").build();



    }

    private boolean checkCredentials(String encryptedPassword, String rawPassword) {
        return BCrypt.checkpw(rawPassword, encryptedPassword);
    }
    
}
