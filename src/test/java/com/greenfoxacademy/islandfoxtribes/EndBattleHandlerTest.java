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
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle.EndBattleHandler;
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
public class EndBattleHandlerTest extends TestSetup {

    @Autowired
    EndBattleHandler endBattleHandler;

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
        player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        player2 = new Player("TestPlayer2", passwordEncoder.encode("12345678"), "email");
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

        Building granary = buildingFactory.createBuilding(BuildingType.GRANARY);
        Building treasury = buildingFactory.createBuilding(BuildingType.TREASURY);



        kingdom.setResourceList(new ArrayList<>());
        Resource resource = new Resource(ResourceType.GOLD, 100, kingdom);
        Resource food = new Resource(ResourceType.FOOD, 100, kingdom);
        kingdom.addResource(resource);
        kingdom.addResource(food);

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
        kingdom.addBuilding(granary);
        kingdom.addBuilding(treasury);
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
    public void endBattleTest() throws IOException, InterruptedException, TimeoutException {
        // Preparation
        kingdom.setTroopList(new ArrayList<>());
        for (int i = 0; i < 10; i++) {
            Troop knight2 = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
            knight2.setHp(1);
            knight2.setKingdom(kingdom);
            kingdom.addTroop(knight2);
        }


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
        this.endBattleHandler.endBattle(1L, 150, 150, 1L);

        //Assert
        for (int i = 0; i < 10; i++) {
            Assertions.assertNull(kingdom.getTroopList().get(i).getBattle());
            Assertions.assertEquals(500, kingdom.getTroopList().get(i).getHp());
        }
        Assertions.assertEquals(250, kingdom.findFoodResource().getAmount());
        Assertions.assertEquals(250, kingdom.findGoldResource().getAmount());


    }


}
