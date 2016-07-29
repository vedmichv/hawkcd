package net.hawkengine.http.tests;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.db.redis.RedisManager;
import net.hawkengine.http.PipelineController;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.Pipeline;
import net.hawkengine.model.PipelineDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.PipelineDefinitionService;
import net.hawkengine.services.PipelineService;
import net.hawkengine.services.Service;
import net.hawkengine.services.interfaces.IPipelineGroupService;
import net.hawkengine.services.interfaces.IPipelineService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PipelineControllerTests extends JerseyTest {
    private IPipelineService pipelineService;
    private PipelineController pipelineController;
    private Pipeline pipeline;
    private ServiceResult serviceResult;



    public Application configure() {
        ServerConfiguration.configure();
        this.pipelineService = Mockito.mock(PipelineService.class);
        this.pipelineController = new PipelineController(this.pipelineService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.pipelineController);
    }

    @Test
    public void PipelineController_constructorTest_notNull(){

        PipelineController pipelineController = new PipelineController();

        assertNotNull(pipelineController);
    }

    @Test
    public void getAllPipelines_request_emptyList(){
        //Arrange
        List<Pipeline> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.pipelineService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipelines").request().get();
        List<Pipeline> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200,response.getStatus());
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void getAllPipelines_existingObjects_twoObjects() {
        //Arrange
        List<Pipeline> expectedResult = new ArrayList<>();
        this.prepearePipeline();
        expectedResult.add(this.pipeline);
        expectedResult.add(this.pipeline);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.pipelineService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipelines").request().get();
        List<Pipeline> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getPipelineById_existingObject_correctObject() {
        //Arrange
        this.prepearePipeline();
        this.serviceResult.setObject(this.pipeline);
        Mockito.when(this.pipelineService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipelines/" + this.pipeline.getId()).request().get();
        Pipeline actualResult = response.readEntity(Pipeline.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.pipeline.getId(), actualResult.getId());
    }

    @Test
    public void getPipelineById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "Pipeline not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.pipelineService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/pipelines/wrongId").request().get();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualMessage);
    }

    @Test
    public void addPipeline_oneObject_successMessage() {
        //Arrange
        this.prepearePipeline();
        this.serviceResult.setObject(this.pipeline);
        Mockito.when(this.pipelineService.add(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.pipeline, "application/json");

        //Act
        Response response = target("/pipelines").request().post(entity);
        Pipeline actualResult = response.readEntity(Pipeline.class);


        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.pipeline.getId(), actualResult.getId());

    }


    private void prepearePipeline(){
        PipelineDefinition pipelineDefinition = new PipelineDefinition();
        pipelineDefinition.setName("pipelineDefinition");
        this.pipeline = new Pipeline();
        this.pipeline.setPipelineDefinitionName(pipelineDefinition.getName());
        this.pipeline.setPipelineDefinitionId(pipelineDefinition.getId());

    }
}
