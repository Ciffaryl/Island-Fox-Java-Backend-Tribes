package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.AttackRequestDTO;
import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle.BattleHandler;
import com.greenfoxacademy.islandfoxtribes.repositories.battle.BattleRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.message.MessageRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;

import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import com.greenfoxacademy.islandfoxtribes.services.kingdom.KingdomServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BattleHandlerTest extends TestSetup {

    @Autowired
    BattleHandler battleHandler;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    KingdomRepository kingdomRepository;

    @Autowired
    private KingdomServiceImpl kingdomService;

    @Autowired
    private TroopFactory troopFactory;

    @Autowired
    private BuildingFactory buildingFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    @Autowired
    SecurityConfigurer securityConfigurer;

    @MockBean
    PlayerServiceImpl playerServiceImpl;

    @Autowired
    private BattleRepository battleRepository;

    private Player player;
    private Player player2;
    private Kingdom kingdom;
    private Kingdom kingdom2;
    private Building building1;
    private Building building2;
    private Building building3;
    private PlayerJWTDTO jwt;
    private AttackRequestDTO attackRequestDTO;
    private Battle battle1;

    @BeforeEach
    public void setup() {
        player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "islandfoxjava@gmail.com");
        player.setEnabled(true);
        player2 = new Player("TestPlayer2", passwordEncoder.encode("12345678"), "islandfoxjava@gmail.com");
        player2.setEnabled(true);
        kingdom = new Kingdom();
        kingdom2 = new Kingdom();
        kingdom2.setName("Kingdom2");
        kingdom2.setLoyalty(100);
        building1 = new Building();
        building2 = new Building();
        kingdom2.setBuildingList(new ArrayList<>());
        building3 = new Building();
        building3.setKingdom(kingdom2);
        kingdom2.addBuilding(building3);


        kingdom.setResourceList(new ArrayList<>());
        Resource resource = new Resource(ResourceType.GOLD, 10000, kingdom);
        kingdom.addResource(resource);

        kingdom2.setResourceList(new ArrayList<>());
        Resource resource1 = new Resource(ResourceType.GOLD, 50, kingdom2);
        Resource resource2 = new Resource(ResourceType.FOOD, 150, kingdom2);
        kingdom2.addResource(resource1);
        kingdom2.addResource(resource2);

        building1.setBuildingType(BuildingType.TOWN_HALL);
        building1.setLevel(3);
        building1.setKingdom(kingdom);
        building1.setConstructionTime(1000);
        building2.setBuildingType(BuildingType.BARRACKS);
        building2.setLevel(1);
        building2.setKingdom(kingdom);
        List<Building> buildingList = new ArrayList<>();
        buildingList.add(building1);
        buildingList.add(building2);
        kingdom.setBuildingList(buildingList);
        kingdom.setName("something");
        kingdom.setTroopList(new ArrayList<>());
        player.setUserName("someone");
        List<Kingdom> kingdomList = new ArrayList<>();
        kingdomList.add(kingdom);
        player.setKingdomList(kingdomList);

        Location location1 = new Location(0, 0);
        kingdom.setLocation(location1);

        Location location2 = new Location(4, 3);
        kingdom2.setLocation(location2);

        kingdom2.setTroopList(new ArrayList<>());
        kingdom.setPlayer(player);
        playerRepository.save(player);

        List<Kingdom> kingdomList2 = new ArrayList<>();
        kingdomList.add(kingdom2);
        player2.setKingdomList(kingdomList2);
        kingdom2.setPlayer(player2);

        this.playerRepository.save(player2);

        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);


    }

    @Test
    public void countLostTest() {

        //Preparation

        kingdom.setTroopList(new ArrayList<>());
        Troop archer = this.troopFactory.createTroop(building2, TroopType.ARCHER);
        archer.setKingdom(kingdom);
        kingdom.addTroop(archer);
        Troop archer2 = this.troopFactory.createTroop(building2, TroopType.ARCHER);
        archer2.setKingdom(kingdom);
        kingdom.addTroop(archer2);
        Troop archer3 = this.troopFactory.createTroop(building2, TroopType.ARCHER);
        archer3.setKingdom(kingdom);
        kingdom.addTroop(archer3);

        playerRepository.save(player);


        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());


        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);


        this.battleHandler.countLost(this.kingdom.getTroopList(), kingdom, 14, battle1);
        Assertions.assertEquals(1, this.kingdom.getTroopList().size());
        Assertions.assertEquals(1, this.battle1.getTroops().size());
    }

    @Test
    public void attackDefenderWinsTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
        senator.setKingdom(kingdom);
        kingdom.addTroop(senator);

        Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight.setKingdom(kingdom2);
        kingdom2.addTroop(knight);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        boolean result = this.battleHandler.isAttackSuccessful(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertFalse(result);
        Assertions.assertEquals("Your attack on " + kingdom2.getName() +
                " failed. Your army is destroyed.", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(player.getUserName(), this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() +
                ", but you defend yourself! You have no losses!", this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(player2.getUserName(), this.messageRepository.getById(2L).getPlayer().getUserName());
    }

    @Test
    public void attackEqualFightTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        Troop knight2 = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight2.setKingdom(kingdom);
        kingdom.addTroop(knight2);

        Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight.setKingdom(kingdom2);
        kingdom2.addTroop(knight);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        boolean result = this.battleHandler.isAttackSuccessful(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertFalse(result);
        Assertions.assertEquals("Your attack on " + kingdom2.getName() +
                " failed. But your army wasn't destroyed.", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(player.getUserName(), this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() +
                ", but you defend yourself! You may have some losses!", this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(player2.getUserName(), this.messageRepository.getById(2L).getPlayer().getUserName());
    }

    @Test
    public void attackWallEffectTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        Troop knight2 = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight2.setKingdom(kingdom);
        kingdom.addTroop(knight2);

        Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight.setKingdom(kingdom2);
        kingdom2.addTroop(knight);
        Building wall = this.buildingFactory.createBuilding(BuildingType.WALL);
        wall.setKingdom(kingdom2);
        kingdom2.addBuilding(wall);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        boolean result = this.battleHandler.isAttackSuccessful(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertFalse(result);
        Assertions.assertEquals("Your attack on " + kingdom2.getName() +
                " failed. Your army is destroyed.", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(player.getUserName(), this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() +
                ", but you defend yourself! You have no losses!", this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(player2.getUserName(), this.messageRepository.getById(2L).getPlayer().getUserName());
    }

    @Test
    public void attackAttackerWinsTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight.setKingdom(kingdom);
        kingdom.addTroop(knight);

        Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
        senator.setKingdom(kingdom2);
        kingdom2.addTroop(senator);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        boolean result = this.battleHandler.isAttackSuccessful(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertTrue(result);
        Assertions.assertEquals(0, kingdom2.getTroopList().size());
    }

    @Test
    public void spyAttackAttackerWinsTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        for (int i = 0; i < 10; i++) {
            Troop spy = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
            spy.setKingdom(kingdom);
            kingdom.addTroop(spy);
        }

        Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
        senator.setKingdom(kingdom2);
        kingdom2.addTroop(senator);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Spy");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        this.battleHandler.spyAttack(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertEquals("Knights: " + 0 + " Swordsmen: " + 0 + " Archers: " + 0 +
                        " Spies: " + 0 + " Senators: " + 1 + " Barracks level : " + 0 + " Gold: " + 50 +
                        " Food: " + 150, this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(kingdom.getPlayer().getUserName(),
                this.messageRepository.getById(1L).getPlayer().getUserName());
    }

    @Test
    public void spyAttackDefenderWinsTest() throws IOException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        Troop spy = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        spy.setKingdom(kingdom);
        kingdom.addTroop(spy);

        Troop spy1 = this.troopFactory.createTroop(building2, TroopType.SPY);
        spy1.setKingdom(kingdom2);
        kingdom2.addTroop(spy1);
        Troop spy2 = this.troopFactory.createTroop(building2, TroopType.SPY);
        spy2.setKingdom(kingdom2);
        kingdom2.addTroop(spy2);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Spy");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        this.battleHandler.spyAttack(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertEquals("Your spies have been captured and eliminated in kingdom "
                + kingdom2.getName() + ".", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(kingdom.getPlayer().getUserName(),
                this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("" + player.getUserName() + " tried to spy on you. " +
                "You have killed all his spies!", this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(kingdom2.getPlayer().getUserName(),
                this.messageRepository.getById(2L).getPlayer().getUserName());
    }

    @Test
    public void plunderAttackTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
            knight.setKingdom(kingdom);
            kingdom.addTroop(knight);
        }
        Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
        senator.setKingdom(kingdom2);
        kingdom2.addTroop(senator);
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        this.battleHandler.plunderAttack(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertEquals("Your attack on " + kingdom2.getName() + "was successful. You are bringing home "
                + 150 + " of food and " + 50 + " of gold!", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(kingdom.getPlayer().getUserName(),
                this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() + ". " + 150 +
                " food and " + 50 + " gold have been lost.", this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(kingdom2.getPlayer().getUserName(),
                this.messageRepository.getById(2L).getPlayer().getUserName());
        Assertions.assertEquals(0, kingdom2.findGoldResource().getAmount());
        Assertions.assertEquals(0, kingdom2.findFoodResource().getAmount());
    }

    @Test
    public void takeOverAttackWithoutTakeOverTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
            senator.setKingdom(kingdom);
            kingdom.addTroop(senator);
        }
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        this.battleHandler.takeOverAttack(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertEquals("Your attack on " + kingdom2.getName() + " was successful. You destroyed "
                + 20 + " of kingdom's loyalty", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(kingdom.getPlayer().getUserName(),
                this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() + "." +
                        " Your kingdom " + kingdom2.getName() + "have lost " + 20 + "loyalty!",
                this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(kingdom2.getPlayer().getUserName(),
                this.messageRepository.getById(2L).getPlayer().getUserName());
        Assertions.assertEquals(80, kingdom2.getLoyalty());
    }

    @Test
    public void takeOverAttackWithTakeOverTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        for (int i = 0; i < 20; i++) {
            Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
            senator.setKingdom(kingdom);
            kingdom.addTroop(senator);
        }
        playerRepository.save(player2);
        playerRepository.save(player);

        List<Troop> army = new ArrayList<>(this.kingdom.getTroopList());

        this.battle1 = new Battle(2L, 1L, "Plunder");
        this.battle1.setTroops(army);
        this.battleRepository.save(battle1);

        //Act
        this.battleHandler.takeOverAttack(battle1.getId(), 1000L, true);

        //Assert

        Assertions.assertEquals("Your attack on " + kingdom2.getName() + " was successful." +
                " You have captured " + kingdom2.getName() + "!", this.messageRepository.getById(1L).getText());
        Assertions.assertEquals(kingdom.getPlayer().getUserName(),
                this.messageRepository.getById(1L).getPlayer().getUserName());
        Assertions.assertEquals("You have been attacked by " + player.getUserName() + "." +
                        " Your kingdom " + kingdom2.getName() + " is lost.",
                this.messageRepository.getById(2L).getText());
        Assertions.assertEquals(player2.getUserName(),
                this.messageRepository.getById(2L).getPlayer().getUserName());
        Assertions.assertEquals(player.getUserName(), kingdom2.getPlayer().getUserName());
        Assertions.assertEquals(0, player2.getKingdomList().size());

    }
}
