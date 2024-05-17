package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.planningPoker.model.User;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.quarkus.runtime.configuration.ProfileManager;
import io.vertx.codegen.doc.Doc;
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
        System.out.println(userList);

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
            System.out.println(userClaim);
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

    public Response getUserTasks(String jwtToken) {
       
        

        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);
            System.out.println("Jag är ett userclaim" + userClaim);
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        if  (userClaim != null) {
            
            try { List<Document> allTasks = getTasks(userClaim);
            
            
            return Response.ok(allTasks).build();
            } catch (Exception e) {
                return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
            }
            //Response userResponse = findUser(userClaim.getPayload().get("upn").toString());
            //Document userDocument = (Document) userResponse.getEntity();
           // User user = new User(userDocument.get("_id").toString(), userDocument.get("email").toString(),
                    //userDocument.get("role").toString(), Integer.parseInt(userDocument.get("authorized").toString()),
                   // userDocument.get("password").toString(), userDocument.get("name").toString());
           // Set<String> userPermission= securityService.getUserPermissions(user);

        } else {

            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
        
    }

    public List<Document> getTasks(Jws<Claims> userClaim) {

        MongoDatabase database;
        if (ProfileManager.getLaunchMode().isDevOrTest()) {
            database = mongoClient.getDatabase("PlanningPokerDev");
        } else {
            database = mongoClient.getDatabase("PlanningPoker");

        }
        MongoCollection<Document> collection = database.getCollection("Tasks");
        FindIterable<Document> documents = collection.find();
        System.out.println(userClaim.getPayload().get("groups").toString());
        List<Document> taskList = new ArrayList<>();
        for (Document document : documents) {
            if (userClaim.getPayload().get("groups").toString().contains("seealltasks")) {
                taskList.add(document);

            } else if (userClaim.getPayload().get("groups").toString().contains("viewactivetasks")) {
                if (!document.get("status").toString().equals("complete") && !document.get("status").toString().equals("needattention") ) {
                    taskList.add(document);
                    System.out.println(document.get("status").toString());
                }
            } 
        }
        System.out.println(taskList);

        return taskList;
    }
}
