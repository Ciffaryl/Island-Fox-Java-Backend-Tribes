package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.AttackRequestDTO;
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
import com.greenfoxacademy.islandfoxtribes.repositories.battle.BattleRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
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


import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class KingdomServiceImplTest extends TestSetup {

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
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    @Autowired
    SecurityConfigurer securityConfigurer;

    @MockBean
    PlayerServiceImpl playerServiceImpl;

    @Autowired
    private BattleRepository battleRepository;

    private Player player;
    private Kingdom kingdom;
    private Kingdom kingdom2;
    private Building building1;
    private Building building2;
    private Building building3;
    private PlayerJWTDTO jwt;
    private AttackRequestDTO attackRequestDTO;

    @BeforeEach
    public void setup() {
        player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        kingdom = new Kingdom();
        kingdom2 = new Kingdom();
        building1 = new Building();
        building2 = new Building();
        kingdom2.setBuildingList(new ArrayList<>());
        building3 = new Building();
        building3.setKingdom(kingdom2);
        kingdom2.addBuilding(building3);
        kingdom.setResourceList(new ArrayList<>());
        Resource resource = new Resource(ResourceType.GOLD, 10000, kingdom);
        kingdom.addResource(resource);
        building1.setBuildingType(BuildingType.TOWN_HALL);
        building1.setLevel(3);
        building1.setKingdom(kingdom);
        building1.setConstructionTime(1000);
        building2.setBuildingType(BuildingType.BARRACKS);
        building2.setLevel(3);
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

        kingdom.setTroopList(new ArrayList<>());
        Troop archer = this.troopFactory.createTroop(building2, TroopType.ARCHER);
        archer.setKingdom(kingdom);
        kingdom.addTroop(archer);
        Troop knight = this.troopFactory.createTroop(building2, TroopType.KNIGHT);
        knight.setKingdom(kingdom);
        kingdom.addTroop(knight);
        Troop spy = this.troopFactory.createTroop(building2, TroopType.SPY);
        spy.setKingdom(kingdom);
        kingdom.addTroop(spy);
        Troop swordsman = this.troopFactory.createTroop(building2, TroopType.SWORDSMAN);
        swordsman.setKingdom(kingdom);
        kingdom.addTroop(swordsman);
        Troop senator = this.troopFactory.createTroop(building2, TroopType.SENATOR);
        senator.setKingdom(kingdom);
        kingdom.addTroop(senator);

        Location location1 = new Location(0, 0);
        kingdom.setLocation(location1);

        Location location2 = new Location(4, 3);
        kingdom2.setLocation(location2);

        player.setKingdomList(kingdomList);
        kingdom.setPlayer(player);
        playerRepository.save(player);
        kingdomRepository.save(kingdom2);
        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
        List<Long> armyId = new ArrayList<>();

        for (Troop troop : this.kingdom.getTroopList()) {
            armyId.add(troop.getId());
        }
        attackRequestDTO = new AttackRequestDTO(2L, "Plunder", armyId);
    }

    @Test
    public void findTheSlowestTroopTest() {
        Troop slowest = this.kingdomService.findSlowestTroop(attackRequestDTO.getTroops());

        Assertions.assertEquals(TroopType.SENATOR, slowest.getTroopType());
    }

    @Test
    public void countTravelTimeTest() {
        long result = this.kingdomService.countTravelTime(kingdom, kingdom2, 3);
        Assertions.assertEquals(15000L, result);
    }


}
