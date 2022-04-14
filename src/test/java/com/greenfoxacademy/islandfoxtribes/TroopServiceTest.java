package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs.RequestNewTroopsDTO;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop.TroopCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TroopServiceTest extends TestSetup {


    @Autowired
    private TroopFactory troopFactory;

    private Player fakePlayer;
    private Building fakeBarracks;
    private Building fakeTownHall;
    private Kingdom fakeKingdom;
    private Resource fakeResource;
    private TroopServiceImpl fakeTroopServiceImpl;
    private RequestNewTroopsDTO requestNewTroopsDTO;


    @BeforeEach
    void setup() {


        fakePlayer = new Player("Player1", "password1", "email");
        fakeBarracks = new Building();
        fakeTownHall = new Building();
        fakeKingdom = new Kingdom();
        fakeResource = new Resource(ResourceType.FOOD, 1000, fakeKingdom);
        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(fakeResource);

        fakeKingdom.setResourceList(resourceList);
        fakeKingdom.setPlayer(fakePlayer);
        fakeBarracks.setBuildingType(BuildingType.BARRACKS);
        fakeBarracks.setCreating(false);
        fakeBarracks.setLevel(1);
        fakeTownHall.setBuildingType(BuildingType.TOWN_HALL);
        fakeTownHall.setCreating(false);
        fakeTownHall.setLevel(3);
        requestNewTroopsDTO = new RequestNewTroopsDTO("Knight", 1);

        List<Building> buildingList = new ArrayList<>();
        buildingList.add(fakeBarracks);
        buildingList.add(fakeTownHall);
        fakeKingdom.setBuildingList(buildingList);


    }

    @Test
    @DisplayName("TroopService returns correct DTO FinishedAt object")
    void troopServiceReturnTest() throws IOException, TimeoutException {

        // Preparation phase
        TroopRepository fakeTroopRepository = Mockito.mock(TroopRepository.class);
        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);

        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeBuildingRepository.getById(fakeBarracks.getId())).thenReturn(fakeBarracks);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        Troop myTroop = troopFactory.createTroop(fakeBarracks, TroopType.KNIGHT);

        fakeTroopServiceImpl =
                new TroopServiceImpl(fakeTroopRepository,
                        troopFactory,
                        new TroopCreator(fakeKingdomRepository, fakeBuildingRepository, troopFactory),
                        fakeKingdomRepository);

        // Act
        Errors result =
                fakeTroopServiceImpl.createTroops(requestNewTroopsDTO, fakeKingdom, true);

        // Assert
        Assertions.assertNull(result);
        Assertions.assertEquals(500, fakeResource.getAmount());

    }

    @Test
    void troopServiceWrongBuildingTypeTest() throws IOException, TimeoutException {

        // Preparation phase

        TroopRepository fakeTroopRepository = Mockito.mock(TroopRepository.class);
        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);

        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeBuildingRepository.getById(fakeBarracks.getId())).thenReturn(fakeBarracks);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeTroopServiceImpl =
                new TroopServiceImpl(fakeTroopRepository,
                        troopFactory,
                        new TroopCreator(fakeKingdomRepository, fakeBuildingRepository, troopFactory),
                        fakeKingdomRepository);

        // Setting up academy to MINE to fail the condition.
        fakeBarracks.setBuildingType(BuildingType.MINE);

        // Act
        Errors result =
                fakeTroopServiceImpl.createTroops(requestNewTroopsDTO, fakeKingdom);

        // Assert
        Assertions.assertEquals("You can build only in barracks!", result.getError());
    }

    @Test
    void troopServiceNotEnoughFoodTest() throws IOException, TimeoutException {

        // Preparation phase

        TroopRepository fakeTroopRepository = Mockito.mock(TroopRepository.class);
        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);

        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeBuildingRepository.getById(fakeBarracks.getId())).thenReturn(fakeBarracks);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeTroopServiceImpl =
                new TroopServiceImpl(
                        fakeTroopRepository,
                        troopFactory,
                        new TroopCreator(fakeKingdomRepository, fakeBuildingRepository, troopFactory),
                        fakeKingdomRepository);

        // Setting up resource to 10 to fail the condition.
        fakeResource.setAmount(10);

        // Act
        Errors result = fakeTroopServiceImpl.createTroops(requestNewTroopsDTO, fakeKingdom);

        // Assert
        Assertions.assertEquals("You don't have food for this!", result.getError());

    }

    @Test
    void troopServiceAlreadyCreating() throws IOException, TimeoutException {

        // Preparation phase

        TroopRepository fakeTroopRepository = Mockito.mock(TroopRepository.class);
        KingdomRepository fakeKingdomRepository = Mockito.mock(KingdomRepository.class);
        BuildingRepository fakeBuildingRepository = Mockito.mock(BuildingRepository.class);

        Mockito.when(fakeKingdomRepository.getById(fakeKingdom.getId())).thenReturn(fakeKingdom);
        Mockito.when(fakeBuildingRepository.getById(fakeBarracks.getId())).thenReturn(fakeBarracks);
        Mockito.when(fakeKingdomRepository.save(fakeKingdom)).thenReturn(fakeKingdom);

        fakeTroopServiceImpl =
                new TroopServiceImpl(
                        fakeTroopRepository,
                        troopFactory,
                        new TroopCreator(fakeKingdomRepository, fakeBuildingRepository, troopFactory),
                        fakeKingdomRepository);

        fakeBarracks.setCreating(true);

        // Act
        Errors result = fakeTroopServiceImpl.createTroops(requestNewTroopsDTO, fakeKingdom);

        // Assert
        Assertions.assertEquals("Your barracks are already making new troops!", result.getError());

    }

}
