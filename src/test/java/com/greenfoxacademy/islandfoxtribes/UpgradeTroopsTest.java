package com.greenfoxacademy.islandfoxtribes;


import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;

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
import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class UpgradeTroopsTest extends TestSetup {

    @Autowired
    private KingdomRepository kingdomRepository;

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
    private MockMvc mockMvc;

    @Autowired
    private TroopFactory troopFactory;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TroopRepository troopRepository;

    private Player player;
    private Kingdom kingdom;
    private Kingdom kingdom2;
    private Building building1;
    private Building building2;
    private Building building3;
    private Troop troop;
    private Troop troop2;
    private PlayerJWTDTO jwt;

    @BeforeEach
    public void setup() {
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
        player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        kingdom = new Kingdom();
        kingdom2 = new Kingdom();
        building1 = new Building();
        building2 = new Building();
        kingdom2.setBuildingList(new ArrayList<>());
        building3 = new Building();
        building3.setLevel(1);
        building3.setBuildingType(BuildingType.BARRACKS);
        building3.setKingdom(kingdom2);
        kingdom2.addBuilding(building3);
        kingdom.setResourceList(new ArrayList<>());
        kingdom2.setResourceList(new ArrayList<>());
        Resource resource = new Resource(ResourceType.GOLD, 1000, kingdom);
        Resource resource2 = new Resource(ResourceType.GOLD, 10, kingdom2);
        kingdom.addResource(resource);
        kingdom2.addResource(resource2);
        building1.setBuildingType(BuildingType.TOWN_HALL);
        building1.setLevel(3);
        building1.setKingdom(kingdom);
        building1.setConstructionTime(1000);
        building2.setBuildingType(BuildingType.BARRACKS);
        building2.setLevel(1);
        building2.setKingdom(kingdom);
        kingdom.setTroopList(new ArrayList<>());
        kingdom2.setTroopList(new ArrayList<>());
        troop = troopFactory.createTroop(building2, TroopType.KNIGHT);
        building2.setLevel(3);
        kingdom.addTroop(troop);
        troop.setKingdom(kingdom);
        troop2 = troopFactory.createTroop(building3, TroopType.SPY);
        troop2.setKingdom(kingdom2);
        kingdom2.addTroop(troop2);
        troopRepository.save(troop);
        troopRepository.save(troop2);
        building3.setLevel(7);
        List<Building> buildingList = new ArrayList<>();
        buildingList.add(building1);
        buildingList.add(building2);
        kingdom.setBuildingList(buildingList);
        kingdom.setName("something");
        kingdom.setTroopList(new ArrayList<>());
        player.setUserName("someone");
        List<Kingdom> kingdomList = new ArrayList<>();
        kingdomList.add(kingdom);
        kingdomList.add(kingdom2);
        player.setKingdomList(kingdomList);
        kingdom.setPlayer(player);
        kingdom2.setPlayer(player);
        playerRepository.save(player);
        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
    }

    @Test
    public void upgradeTroop() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/kingdoms/1/troops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"troopsId\": [1]\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(900, kingdom.findGoldResource().getAmount());
        Assertions.assertEquals(3, troop.getLevel());
        Assertions.assertEquals(GameConstants.KNIGHT_DEFENSE * 3, troop.getDefense());
        Assertions.assertEquals(GameConstants.KNIGHT_COST * 3, troop.getCost());
        Assertions.assertEquals(GameConstants.KNIGHT_HP * 3, troop.getHp());
        Assertions.assertEquals(GameConstants.KNIGHT_ATTACK * 3, troop.getAttack());
    }

    @Test
    public void upgradeTroopsWrongTroopTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/kingdoms/1/troops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"troopsId\": [1,2]\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"All of upgraded troops need to belong to your kingdom!\"\n" +
                        "}"));
    }

    @Test
    public void upgradeTroopNotEnoughGoldTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(2L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/kingdoms/2/troops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"troopsId\": [2]\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"You don't have enough money for this!\"\n" +
                        "}"));
    }
}
