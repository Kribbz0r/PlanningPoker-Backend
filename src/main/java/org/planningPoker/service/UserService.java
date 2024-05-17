package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HeaderParam;
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

}
