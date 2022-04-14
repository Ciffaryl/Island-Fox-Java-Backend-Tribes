package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.troop.TroopCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import org.junit.Assert;
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
public class TroopCreatorTest extends TestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KingdomRepository kingdomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TroopFactory troopFactory;

    @Test
    void troopCreatorTest() {

        // Preparation phase
        Kingdom fakeKingdom = new Kingdom();
        Building fakeBuilding = new Building();

        fakeKingdom.setTroopList(new ArrayList<>());

        fakeBuilding.setLevel(2);

        kingdomRepository.save(fakeKingdom);
        buildingRepository.save(fakeBuilding);

        TroopCreator fakeTroopCreator = new TroopCreator(kingdomRepository, buildingRepository, troopFactory);

        // Act
        fakeTroopCreator.createTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Knight", "create");

        // Assert
        Assert.assertEquals(TroopType.KNIGHT, fakeKingdom.getTroopList().get(0).getTroopType());
        Assert.assertFalse(fakeBuilding.isCreating());

    }

}
