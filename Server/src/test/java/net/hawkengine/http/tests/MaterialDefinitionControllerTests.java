package net.hawkengine.http.tests;


import net.hawkengine.core.ServerConfiguration;
import net.hawkengine.http.MaterialDefinitionController;
import net.hawkengine.model.GitMaterial;
import net.hawkengine.model.JobDefinition;
import net.hawkengine.model.MaterialDefinition;
import net.hawkengine.model.ServiceResult;
import net.hawkengine.model.StageDefinition;
import net.hawkengine.services.MaterialDefinitionService;
import net.hawkengine.services.interfaces.IMaterialDefinitionService;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MaterialDefinitionControllerTests extends JerseyTest {
    private IMaterialDefinitionService materialDefinitionService;
    private MaterialDefinitionController materialDefinitionController;
    private MaterialDefinition materialDefinition;
    private ServiceResult serviceResult;

    public Application configure() {
        ServerConfiguration.configure();
        this.materialDefinitionService = Mockito.mock(MaterialDefinitionService.class);
        this.materialDefinitionController = new MaterialDefinitionController(this.materialDefinitionService);
        this.serviceResult = new ServiceResult();
        return new ResourceConfig().register(this.materialDefinitionController);
    }

    @Test
    public void materialDefinitionController_constructorTest_notNull() {

        MaterialDefinitionController materialDefinitionController = new MaterialDefinitionController();

        assertNotNull(materialDefinitionController);
    }

    @Test
    public void getAllMaterialDefinitions_nonExistingObjects_emptyList() {
        //Arrange
        List<MaterialDefinition> expectedResult = new ArrayList<>();
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.materialDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials").request().get();
        List<JobDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getAllMaterialDefinitions_existingObjects_twoObjects() {
        //Arrange
        List<MaterialDefinition> expectedResult = new ArrayList<>();
        expectedResult.add(this.materialDefinition);
        expectedResult.add(this.materialDefinition);
        this.serviceResult.setObject(expectedResult);
        Mockito.when(this.materialDefinitionService.getAll()).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials").request().get();
        List<StageDefinition> actualResult = response.readEntity(List.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(expectedResult.size(), actualResult.size());
    }

    @Test
    public void getMaterialDefinitionById_existingObject_correctObject() {
        //Arrange
        this.materialDefinition = new GitMaterial();
        this.serviceResult.setObject(this.materialDefinition);
        Mockito.when(this.materialDefinitionService.getById(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials/" + this.materialDefinition.getId()).request().get();
        MaterialDefinition actualResult = response.readEntity(MaterialDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.materialDefinition.getId(), actualResult.getId());
    }

    @Test
    public void getMaterialDefinitionById_nonExistingObject_properErrorMessage() {
        //Arrange
        String expectedResult = "MaterialDefinition not found.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(null);
        Mockito.when(this.materialDefinitionService.getById(Mockito.any())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials/wrongId").request().get();
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addMaterialDefinition_oneObject_successMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        this.serviceResult.setObject(this.materialDefinition);
        Mockito.when(this.materialDefinitionService.addMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().post(entity);
        MaterialDefinition actualResult = response.readEntity(MaterialDefinition.class);

        //Assert
        assertEquals(201, response.getStatus());
        assertEquals(this.materialDefinition.getId(), actualResult.getId());
    }

    @Test
    public void addMaterialDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        this.materialDefinition.setName(null);
        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.serviceResult.setObject(this.materialDefinition);
        Mockito.when(this.materialDefinitionService.addMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void addMaterialDefinition_existingObject_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        String expectedResult = "MaterialDefinition already exists.";
        this.materialDefinitionService.addMaterialDefinition(this.materialDefinition);
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setObject(null);
        Mockito.when(this.materialDefinitionService.addMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }


    @Test
    public void addMaterialDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        this.materialDefinitionService.addMaterialDefinition(this.materialDefinition);
        this.serviceResult.setObject(null);
        this.serviceResult.setError(true);
        String expectedResult = "MaterialDefinition with the same name exists.";
        this.serviceResult.setMessage(expectedResult);
        Mockito.when(this.materialDefinitionService.addMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials/").request().post(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void updateMaterialDefinition_existingMaterialDefinition_updatedMaterialDefinition() {
        //Arrange
        this.prepareMaterialDefinition();
        this.serviceResult.setObject(this.materialDefinition);
        this.materialDefinition.setName("name-updated");
        Mockito.when(this.materialDefinitionService.updateMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().put(entity);
        MaterialDefinition actualResult = response.readEntity(MaterialDefinition.class);

        //Assert
        assertEquals(200, response.getStatus());
        assertEquals(this.materialDefinition.getName(), actualResult.getName());
    }

    @Test
    public void updateMaterialDefinition_nonExistingMaterialDefinition_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        String expectedMessage = "MatarialDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.materialDefinitionService.updateMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");


        //Act
        Response response = target("/materials").request().put(entity);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, response.readEntity(String.class));
    }


    @Test
    public void updateJobDefinition_withSameName_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        this.materialDefinitionService.addMaterialDefinition(this.materialDefinition);
        String expectedMessage = "MaterialDefinition with the same name exists.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.materialDefinitionService.updateMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().put(entity);
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void updateJobDefinition_invalidField_properErrorMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        String expectedResult = "ERROR: MATERIAL DEFINITION NAME IS NULL.";
        this.serviceResult.setMessage(expectedResult);
        this.serviceResult.setError(true);
        this.materialDefinition.setName(null);
        Mockito.when(this.materialDefinitionService.updateMaterialDefinition(Mockito.anyObject())).thenReturn(this.serviceResult);
        Entity entity = Entity.entity(this.materialDefinition, "application/json");

        //Act
        Response response = target("/materials").request().put(entity);
        String actualResult = response.readEntity(String.class);

        //Assert
        assertEquals(400, response.getStatus());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void deleteMaterialDefinition_materialDefinition_successMessage() {
        //Arrange
        this.prepareMaterialDefinition();
        Mockito.when(this.materialDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials/" + this.materialDefinition.getId()).request().delete();

        //Assert
        assertEquals(204, response.getStatus());
    }

    @Test
    public void deleteMaterialDefinition_nonMaterialDefinition_errorMessage() {
        //Arrange
        String expectedMessage = "MaterialDefinition not found.";
        this.serviceResult.setError(true);
        this.serviceResult.setMessage(expectedMessage);
        Mockito.when(this.materialDefinitionService.delete(Mockito.anyString())).thenReturn(this.serviceResult);

        //Act
        Response response = target("/materials/wrongId").request().delete();
        String actualMessage = response.readEntity(String.class);

        //Assert
        assertEquals(404, response.getStatus());
        assertEquals(expectedMessage, actualMessage);
    }


    private void prepareMaterialDefinition() {
        this.materialDefinition = new GitMaterial();
        this.materialDefinition.setName("gitMaterial");
    }
}
