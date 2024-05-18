package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
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

    public Response testGet() {

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");
        }
        MongoCollection<Document> collection = database.getCollection("Users");
        
        List<Document> userList = new ArrayList<>();
        for (Document document : collection.find()) {
            userList.add(document);
        }
        return Response.ok(userList).build();
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

    public Response getUserTasks(String jwtToken, String projectName) {
       
        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        if  (userClaim != null) {
            try { 
                List<Document> allTasks = getTasks(userClaim, projectName);
                return Response.ok(allTasks).build();
            } catch (Exception e) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        
    }

    public List<Document> getTasks(Jws<Claims> userClaim, String projectName) {

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");

        }
        MongoCollection<Document> collection = database.getCollection(projectName);
        FindIterable<Document> documents = collection.find();
        List<Document> taskList = new ArrayList<>();
        for (Document document : documents) {
            if (userClaim.getPayload().get("groups").toString().contains("seealltasks")) {
                taskList.add(document);

            } else if (!document.get("status").toString().equals("complete") && !document.get("status").toString().equals("needattention") && 
                userClaim.getPayload().get("groups").toString().contains("viewactivetasks")) {
                    taskList.add(document);
            } 
        }

        return taskList;
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

}
