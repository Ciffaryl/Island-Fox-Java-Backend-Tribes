package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.upgrades.UpgradeCreator;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopService;
import org.junit.Assert;
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
public class AcademyUpgradesTest extends TestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TroopService troopService;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private KingdomRepository kingdomRepository;

    @Autowired
    private TroopFactory troopFactory;

    private UpgradeCreator fakeUpgradeCreator;
    private Kingdom fakeKingdom;
    private Building fakeBuilding;

    @BeforeEach
    void setup() {

        fakeKingdom = new Kingdom();
        fakeBuilding = new Building();
        fakeKingdom.setTroopList(new ArrayList<>());

        fakeBuilding.setLevel(2);
        Troop fakeTroop = troopFactory.createTroop(fakeBuilding, TroopType.ARCHER);
        fakeKingdom.addTroop(fakeTroop);

        kingdomRepository.save(fakeKingdom);
        buildingRepository.save(fakeBuilding);

        fakeUpgradeCreator = new UpgradeCreator(troopService, buildingRepository);

    }

    @Test
    void upgradeCreatorHp1Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Hp/Upgrade/1");
        Assertions.assertEquals(30, fakeKingdom.getTroopList().get(0).getHp().intValue());
    }

    @Test
    void upgradeCreatorHp2Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Hp/Upgrade/2");
        Assertions.assertEquals(50, fakeKingdom.getTroopList().get(0).getHp().intValue());
    }

    @Test
    void upgradeCreatorHp3Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Hp/Upgrade/3");
        Assertions.assertEquals(70, fakeKingdom.getTroopList().get(0).getHp().intValue());
    }

    @Test
    void upgradeCreatorAttack1Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Attack/Upgrade/1");
        Assertions.assertEquals(50, fakeKingdom.getTroopList().get(0).getAttack().intValue());
    }

    @Test
    void upgradeCreatorAttack2Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Attack/Upgrade/2");
        Assertions.assertEquals(60, fakeKingdom.getTroopList().get(0).getAttack().intValue());
    }

    @Test
    void upgradeCreatorAttack3Test() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Attack/Upgrade/3");
        Assertions.assertEquals(70, fakeKingdom.getTroopList().get(0).getAttack().intValue());
    }

    @Test
    void upgradeCreatorTestDefense1() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Defense/Upgrade/1");
        Assertions.assertEquals(20, fakeKingdom.getTroopList().get(0).getDefense().intValue());
    }

    @Test
    void upgradeCreatorTestDefense2() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Defense/Upgrade/2");
        Assertions.assertEquals(30, fakeKingdom.getTroopList().get(0).getDefense().intValue());
    }

    @Test
    void upgradeCreatorTestDefense3() {
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Defense/Upgrade/3");
        Assertions.assertEquals(40, fakeKingdom.getTroopList().get(0).getDefense().intValue());
    }

    @Test
    void upgradeCreatorIsCreatingTest() {
        fakeBuilding.setCreating(true);
        fakeUpgradeCreator.upgradingTroops(fakeKingdom.getId(), fakeBuilding.getId(), "Archer/Defense/Upgrade/3");
        Assert.assertFalse(fakeBuilding.isCreating());
    }

}
