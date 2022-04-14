package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;

import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional

public class ConstructionBuildingErrorsTest extends TestSetup {

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
    private PlayerRepository playerRepository;

    private Player player;
    private Kingdom kingdom;
    private Kingdom kingdom2;
    private Building building1;
    private Building building2;
    private Building building3;
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
        building3.setKingdom(kingdom2);
        kingdom2.addBuilding(building3);
        kingdom.setResourceList(new ArrayList<>());
        Resource resource = new Resource(ResourceType.GOLD, 10000, kingdom);
        kingdom.addResource(resource);
        building1.setBuildingType(BuildingType.TOWN_HALL);
        building1.setLevel(3);
        building1.setKingdom(kingdom);
        building1.setConstructionTime(1000);
        building2.setBuildingType(BuildingType.FARM);
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
        player.setKingdomList(kingdomList);
        kingdom.setPlayer(player);
        playerRepository.save(player);
        kingdomRepository.save(kingdom2);
        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
    }

    @Test
    public void buildNewBuildingCapacityTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdoms/1/buildings").
                        contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"type\": \"Town hall\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"You have full capacity of this type of building\"\n" +
                        "}"));
    }

    @Test
    public void upgradeBuildingSmallerThanTownHallTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdoms/1/buildings/2").
                        contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"type\": \"Town hall\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"This building can't have higher level than Town Hall!\"\n" +
                        "}"));
    }

    @Test
    public void buildNewBuildingWrongTypeTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdoms/1/buildings").
                        contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"type\": \"town hall\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"Wrong type of building\"\n" +
                        "}"));
    }

    @Test
    public void upgradeBuildingBelongingValidationTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdoms/1/buildings/3").
                        contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"type\": \"Farm\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"This building doesn't belong to this kingdom!\"\n" +
                        "}"));
    }
}
