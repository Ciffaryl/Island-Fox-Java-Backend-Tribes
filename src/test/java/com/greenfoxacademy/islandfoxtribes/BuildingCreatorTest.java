package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.building.BuildingCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BuildingCreatorTest extends TestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    KingdomRepository kingdomRepository;

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    BuildingFactory buildingFactory;

    private Kingdom fakeKingdom;
    private Building fakeAcademy;


    @BeforeEach
    void setup() {

        fakeKingdom = new Kingdom();
        fakeAcademy = new Building();

        fakeAcademy.setLevel(3);
        fakeAcademy.setBuildingType(BuildingType.ACADEMY);

        fakeKingdom.setBuildingList(new ArrayList<>());
        fakeKingdom.addBuilding(fakeAcademy);

        kingdomRepository.save(fakeKingdom);

    }

    @Test
    void buildingCreatorCreateTest() {

        // Preparation phase
        BuildingCreator buildingCreator = new BuildingCreator(kingdomRepository, buildingRepository, buildingFactory);

        // Act
        buildingCreator.createOrUpgradeBuilding(fakeKingdom.getId(), fakeAcademy.getId(), "Academy", "create");

        // Assert
        Assertions.assertEquals(2, fakeKingdom.getBuildingList().size());
    }

    @Test
    void buildingCreatorUpgradeTest() {

        // Preparation phase
        BuildingCreator buildingCreator = new BuildingCreator(kingdomRepository, buildingRepository, buildingFactory);

        // Act
        buildingCreator.createOrUpgradeBuilding(fakeKingdom.getId(), fakeAcademy.getId(), "Academy", "upgrade");

        // Assert
        Assertions.assertEquals(4, fakeAcademy.getLevel());
    }

}
