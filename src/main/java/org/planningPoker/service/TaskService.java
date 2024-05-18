package org.planningPoker.service;

import org.bson.Document;
import org.planningPoker.config.AppConfig;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class TaskService {
    
    @Inject
    AppConfig appConfig;

    @Inject
    SecurityService securityService;

    private final MongoClient mongoClient;

    @Inject
    public TaskService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response startNewProject(String projectName, String jwtToken) {

         Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);

            if (userClaim.getPayload().get("groups").toString().contains("startproject")) {
                MongoDatabase database;
                if (ProfileManager.getLaunchMode().isDevOrTest()) {
                    database = mongoClient.getDatabase("PlanningPokerDev");
                } else {
                    database = mongoClient.getDatabase("PlanningPoker");
                }
                MongoCollection<Document> collection = database.getCollection("Projects");
                Document query = new Document();
                Document update = new Document("$push", new Document("projects", projectName));
                collection.updateOne(query, update);
                database.createCollection(projectName);
                return Response.ok().entity(projectName + " project has been created").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }

    }
}
