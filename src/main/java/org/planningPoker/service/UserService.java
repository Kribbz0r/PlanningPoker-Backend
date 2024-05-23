package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class UserService {

    @Inject
    SecurityService securityService;

    private final MongoClient mongoClient;

    @Inject
    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response getAllUsers(String jwtToken) {

        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        if (userClaim.getPayload().get("groups").toString().contains("handleuser")) {
            MongoDatabase database;
            if (ProfileManager.getLaunchMode().isDevOrTest()) {
                database = mongoClient.getDatabase("PlanningPokerDev");
            } else {
                database = mongoClient.getDatabase("PlanningPoker");
            }
            MongoCollection<Document> collection = database.getCollection("Users");
            
            List<Document> userList = new ArrayList<>();
            for (Document document : collection.find()) {
                if (!"66446a0b97b346b20fd35b73".equals(document.get("role"))) {
                    userList.add(document);
                }
            }
            return Response.ok(userList).build();            
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
        }

    }

    public Response findUser(String email) {

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");

        }
        MongoCollection<Document> collection = database.getCollection("Users");

        Document query = new Document("email", email);
        Document userDocument = collection.find(query).first();

        return Response.ok(userDocument).build();
    }

 
    public Response getUser(String jwtToken) {

        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        if (userClaim != null) {

            Response userResponse = findUser(userClaim.getPayload().get("upn").toString());
            Document userDocument = (Document) userResponse.getEntity();

            userDocument.put("password", "");

            return Response.ok(userDocument).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
        }
    }

    public Response changeUserAccess(String jwtToken, String userEmail) {
        
        Jws<Claims> userClaim = null;
        try {
            userClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are NOT authorized to do this!").build();
        }

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");
        }
        MongoCollection<Document> collection = database.getCollection("Users");
        Document query = new Document("email", userEmail);
        if  (userClaim.getPayload().get("groups").toString().contains("handleuser")) {
            try { 
                Response userResponse = findUser(userEmail);
            Document userDocument = (Document) userResponse.getEntity();
            int currentAuthorizedStatus = userDocument.getInteger("authorized");
            int newAuthorizedStatus = (currentAuthorizedStatus == 1) ? 0 : 1;
            userDocument.put("authorized", newAuthorizedStatus);
            UpdateResult updateResult = collection.updateOne(query, new Document("$set", userDocument));
            
            if (updateResult.getModifiedCount() == 1) {
                return Response.ok().entity(userEmail + (newAuthorizedStatus == 1 ? " now has access." : " no longer has access.")).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("User document not found or unable to update.").build();
            }
            } catch (Exception e) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }

    }

    public Response getNumberWithAccess(String jwtToken) {

        Jws<Claims> userClaim = null;

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");
        }
        MongoCollection<Document> collection = database.getCollection("Users");
        
        try {
            userClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        if  (userClaim.getPayload().get("groups").toString().contains("accesscount")) {
            try {
                long count = collection.countDocuments(new Document("authorized", 1).append("role", "66446bd997b346b20fd35b74"));
                return Response.ok(count).build();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        
    }

}
