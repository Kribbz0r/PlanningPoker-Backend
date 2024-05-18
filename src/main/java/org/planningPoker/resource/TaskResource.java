package org.planningPoker.resource;

import org.planningPoker.service.TaskService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    
    @Inject
    TaskService taskService;

    @PATCH
    @Path("/new-project")
    public Response startNewProject(@HeaderParam("projectName") String projectName, @HeaderParam("Authorization") String jwtToken) {
        return taskService.startNewProject(projectName, jwtToken);
    }

    @GET
    @Path("/get-projects")
    public Response getProjects(@HeaderParam("Authorization") String jwtToken) {
        return taskService.getProjects(jwtToken);
    }
    
}
