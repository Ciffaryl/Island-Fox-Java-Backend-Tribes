package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.building.BuildingCreator;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades.UpgradeCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BuildingServiceTest extends TestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BuildingFactory fakeBuildingFactory;

    private Kingdom fakeKingdom;
    private Building fakeTownHall;
    private Building fakeAcademy;
    private Resource fakeResource;

    @BeforeEach
    void setup() {

        fakeKingdom = new Kingdom();
        fakeKingdom.setBuildingList(new ArrayList<>());
        fakeKingdom.setResourceList(new ArrayList<>());

        fakeTownHall = new Building();
        fakeAcademy = new Building();
        fakeTownHall.setBuildingType(BuildingType.TOWN_HALL);
        fakeTownHall.setLevel(5);
        fakeAcademy.setLevel(3);
        fakeAcademy.setConstructionTime(5000);
        fakeAcademy.setBuildingType(BuildingType.ACADEMY);

        fakeResource = new Resource(ResourceType.GOLD, 10000, fakeKingdom);
        fakeKingdom.addResource(fakeResource);

        fakeKingdom.setBuildingStatus(false);

        fakeKingdom.addBuilding(fakeTownHall);
        fakeKingdom.addBuilding(fakeAcademy);

    }

    @Test
    void buildBuilding() throws IOException, TimeoutException {

        // Preparation phase
        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);
        TroopService fakeTroopService = Mockito.mock(TroopService.class);

        Mockito.when(fakeBuildingRepository.getById(fakeAcademy.getId())).thenReturn(fakeAcademy);
        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        BuildingServiceImpl fakeBuildingServiceImpl = new BuildingServiceImpl(fakeBuildingRepository,
                new BuildingCreator(fakeKingdomRepository,
                        fakeBuildingRepository,
                        fakeBuildingFactory), fakeKingdomRepository, fakeBuildingFactory,
                new UpgradeCreator(fakeTroopService, fakeBuildingRepository));

        // Act
        Errors result1 =
                fakeBuildingServiceImpl.buildBuilding(fakeAcademy.getId(), fakeKingdom, true);
        Errors result2 =
                fakeBuildingServiceImpl.buildBuilding("Mine", fakeKingdom, true);

        // Assert
        Assertions.assertNull(result1);

        Assertions.assertNull(result2);

    }

    @Test
    void buildingBuildingIsAlreadyBuilding() throws IOException, TimeoutException {

        // Preparation phase

        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);
        TroopService fakeTroopService = Mockito.mock(TroopService.class);

        Mockito.when(fakeBuildingRepository.getById(fakeAcademy.getId())).thenReturn(fakeAcademy);
        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeKingdom.setBuildingStatus(true);

        BuildingServiceImpl fakeBuildingServiceImpl = new BuildingServiceImpl(
                fakeBuildingRepository,
                new BuildingCreator(fakeKingdomRepository, fakeBuildingRepository, fakeBuildingFactory),
                fakeKingdomRepository, fakeBuildingFactory,
                new UpgradeCreator(fakeTroopService, fakeBuildingRepository));

        // Act
        Errors result1 =
                fakeBuildingServiceImpl.buildBuilding(fakeAcademy.getId(), fakeKingdom, true);
        Errors result2 =
                fakeBuildingServiceImpl.buildBuilding("Mine", fakeKingdom, true);

        // Assert
        Assertions.assertEquals("You are already building!", result1.getError());
        Assertions.assertEquals("You are already building!", result2.getError());

    }

    @Test
    void buildingBuildingLowLvlTownHall() throws IOException, TimeoutException {

        // Preparation phase

        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);
        TroopService fakeTroopService = Mockito.mock(TroopService.class);

        Mockito.when(fakeBuildingRepository.getById(fakeAcademy.getId())).thenReturn(fakeAcademy);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeTownHall.setLevel(1);

        BuildingServiceImpl fakeBuildingServiceImpl =
                new BuildingServiceImpl(fakeBuildingRepository,
                        new BuildingCreator(fakeKingdomRepository, fakeBuildingRepository, fakeBuildingFactory),
                        fakeKingdomRepository, fakeBuildingFactory,
                        new UpgradeCreator(fakeTroopService, fakeBuildingRepository));

        // Act
        Errors result1 =
                fakeBuildingServiceImpl.buildBuilding(fakeAcademy.getId(), fakeKingdom, true);

        // Assert
        Assertions.assertEquals("This building can't have higher level than Town Hall!", result1.getError());
    }

    @Test
    void buildingBuildingNotEnoughGold() throws IOException, TimeoutException {

        // Preparation phase

        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);
        TroopService fakeTroopService = Mockito.mock(TroopService.class);

        Mockito.when(fakeBuildingRepository.getById(fakeAcademy.getId())).thenReturn(fakeAcademy);
        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeResource.setAmount(10);

        BuildingServiceImpl fakeBuildingServiceImpl =
                new BuildingServiceImpl(fakeBuildingRepository,
                        new BuildingCreator(fakeKingdomRepository, fakeBuildingRepository, fakeBuildingFactory),
                        fakeKingdomRepository, fakeBuildingFactory,
                        new UpgradeCreator(fakeTroopService, fakeBuildingRepository));

        // Act
        Errors result1 =
                fakeBuildingServiceImpl.buildBuilding(fakeAcademy.getId(), fakeKingdom, true);
        Errors result2 =
                fakeBuildingServiceImpl.buildBuilding("Academy", fakeKingdom, true);

        // Assert
        Assertions.assertEquals("You don't have enough gold to build that!", result1.getError());
        Assertions.assertEquals("You don't have enough gold to build that!", result2.getError());

    }

    @Test
    void buildingBuildingCapacityFull() throws IOException, TimeoutException {

        // Preparation phase

        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);
        TroopService fakeTroopService = Mockito.mock(TroopService.class);

        Mockito.when(fakeBuildingRepository.getById(fakeAcademy.getId())).thenReturn(fakeAcademy);
        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        BuildingServiceImpl fakeBuildingServiceImpl =
                new BuildingServiceImpl(fakeBuildingRepository,
                        new BuildingCreator(fakeKingdomRepository, fakeBuildingRepository, fakeBuildingFactory),
                        fakeKingdomRepository, fakeBuildingFactory,
                        new UpgradeCreator(fakeTroopService, fakeBuildingRepository));

        // Act
        Errors result1 =
                fakeBuildingServiceImpl.buildBuilding("Academy",
                        fakeKingdom,
                        true);

        // Assert
        Assertions.assertEquals("You have full capacity of this type of building", result1.getError());
    }

}
