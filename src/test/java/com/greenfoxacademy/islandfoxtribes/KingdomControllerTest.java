package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;

import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.resource.ResourceRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class KingdomControllerTest extends TestSetup {


    @Autowired
    SecurityConfigurer securityConfigurer;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    private PlayerJWTDTO jwt;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private KingdomRepository kingdomRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TroopRepository troopRepository;

    @MockBean
    private PlayerServiceImpl playServiceImpl;

    private Kingdom kingdom;

    @BeforeEach
    public void setup() {

        securityConfigurer = Mockito.mock(SecurityConfigurer.class);

        Player player = new Player("someone", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        Player savedPlayer = playerRepository.save(player);

        this.kingdom = new Kingdom();
        kingdom.setName("something");
        kingdom.setPlayer(savedPlayer);
        Kingdom savedKingdom = kingdomRepository.save(kingdom);
        Location location = new Location(15, 15);
        kingdom.setLocation(location);

        Resource resource1 = new Resource(ResourceType.FOOD, 100, savedKingdom, 0, 0);
        Resource resource2 = new Resource(ResourceType.GOLD, 100, savedKingdom, 0, 0);
        List<Resource> savedResources = resourceRepository.saveAll(Arrays.asList(resource1, resource2));
        BuildingFactory buildingFactory = new BuildingFactory();

        //todo: setKingdom in constructor
        Building building1 = buildingFactory.createBuilding(BuildingType.MINE);
        building1.setLevel(2);
        building1.setKingdom(savedKingdom);
        Building building2 = buildingFactory.createBuilding(BuildingType.FARM);
        building2.setLevel(3);
        building2.setKingdom(savedKingdom);
        List<Building> savedBuildings = buildingRepository.saveAll(Arrays.asList(building1, building2));

        Troop troop1 = new Troop(1, 1, 10, 10, 10, 100, 10, 5, TroopType.ARCHER, savedKingdom, null);
        Troop troop2 = new Troop(2, 2, 20, 20, 20, 200, 200, 3, TroopType.KNIGHT, savedKingdom, null);
        List<Troop> savedTroops = troopRepository.saveAll(Arrays.asList(troop1, troop2));

        savedKingdom.setResourceList(savedResources);
        savedKingdom.setBuildingList(savedBuildings);
        savedKingdom.setTroopList(savedTroops);

        kingdomRepository.save(savedKingdom);

        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));

        Mockito.when(playServiceImpl.validation(1L)).thenReturn(true);
        Mockito.when(playServiceImpl.validation(2L)).thenReturn(false);

    }

    @Test
    public void getAllKingdomsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdoms").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"kingdoms\": [\n" +
                        "        {\n" +
                        "            \"kingdomId\": 1,\n" +
                        "            \"kingdomName\": \"something\",\n" +
                        "            \"ruler\": \"someone\",\n" +
                        "            \"population\": 5,\n" +
                        "            \"location\": {\n" +
                        "                \"coordinateX\": 15,\n" +
                        "                \"coordinateY\": 15\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}"));
    }

    @Test
    public void shouldReturnOkWhenCorrectKingdomId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdoms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("{" +
                        "\"kingdom\":{" +
                        "\"kingdomId\":1," +
                        "\"kingdomName\":\"something\"," +
                        "\"ruler\":\"someone\"," +
                        "\"population\":5," +
                        "\"location\":{\"coordinateX\":15,\"coordinateY\":15}}," +
                        "\"resources\":[{" +
                        "\"type\":\"Food\"," +
                        "\"amount\":100," +
                        "\"generation\":0," +
                        "\"updatedAt\":0" +
                        "}," +
                        "{\"type\":\"Gold\"," +
                        "\"amount\":100," +
                        "\"generation\":0," +
                        "\"updatedAt\":0}]," +
                        "\"buildings\":[{" +
                        "\"id\":1," +
                        "\"type\":\"Mine\"," +
                        "\"level\":2" +
                        "}," +
                        "{\"id\":2," +
                        "\"type\":\"Farm\"," +
                        "\"level\":3}]," +
                        "\"troops\":[{" +
                        "\"id\":1," +
                        "\"level\":1," +
                        "\"hp\":10," +
                        "\"attack\":10," +
                        "\"defence\":10," +
                        "\"type\":\"Archer\"" +
                        "}," +
                        "{\"id\":2," +
                        "\"level\":2," +
                        "\"hp\":20," +
                        "\"attack\":20," +
                        "\"defence\":20," +
                        "\"type\":\"Knight\"}]" +
                        "}"));

    }

    @Test
    public void shouldReturnUnauthorizedWhenWrongKingdomId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdoms/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error",
                        containsString("This kingdom does not belong to authenticated player")));
    }

    @Test
    public void kingdomRegistrationTest() throws Exception {
        Mockito.when(playServiceImpl.validation(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/registration/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                " \"coordinateY\" : 14,\n" +
                                " \"coordinateX\" : 71,\n" +
                                " \"kingdomId\" : 1\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(71, kingdom.getLocation().getCoordinateX());
        Assertions.assertEquals(14, kingdom.getLocation().getCoordinateY());

    }

    @Test
    public void kingdomRegistrationCoordinatesOverTheLimitTest() throws Exception {

        Mockito.when(playServiceImpl.validation(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/registration/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                " \"coordinateY\" : 150,\n" +
                                " \"coordinateX\" : 71,\n" +
                                " \"kingdomId\" : 1\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"One or both coordinates are out of valid range (0-99).\"\n" +
                        "}"));
    }

    @Test
    public void kingdomRegistrationCoordinatesAlreadyTakenTest() throws Exception {
        //Preparation
        Kingdom kingdom2 = new Kingdom();
        Location location2 = new Location();
        location2.setCoordinateY(14);
        location2.setCoordinateX(14);
        kingdom2.setLocation(location2);
        kingdomRepository.save(kingdom2);
        Mockito.when(playServiceImpl.validation(1L)).thenReturn(true);


        // Act
        mockMvc.perform(MockMvcRequestBuilders.put("/registration/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                " \"coordinateY\" : 14,\n" +
                                " \"coordinateX\" : 14,\n" +
                                " \"kingdomId\" : 1\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"Given coordinates are already taken!\"\n" +
                        "}"));
    }

    @Test
    public void getKingdomResourcesAuthorizedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/kingdoms/1/resources").contentType(MediaType.APPLICATION_JSON).header(
                        "Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"kingdom\":  {\n" +
                        "            \"kingdomId\": 1,\n" +
                        "            \"kingdomName\": \"something\",\n" +
                        "            \"ruler\": \"someone\",\n" +
                        "            \"population\": 5,\n" +
                        "                \"location\": {\n" +
                        "                \"coordinateX\": 15,\n" +
                        "                \"coordinateY\": 15\n" +
                        "            }\n" +
                        "        }," +
                        "    \"resources\":  [\n" +
                        "             {\n" +
                        "             \"type\": \"Food\"," +
                        "                  \"amount\": 100,\n" +
                        "                  \"generation\": 0,\n" +
                        "                  \"updatedAt\": 0\n" +
                        "        }," +
                        "        {" +
                        "             \"type\": \"Gold\"," +
                        "                  \"amount\": 100,\n" +
                        "                  \"generation\": 0,\n" +
                        "                  \"updatedAt\": 0\n" +
                        "        }" +
                        "     ]" +
                        "}"));
    }

    @Test
    public void getKingdomResourcesUnauthorizedTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdoms/2/resources")
                        .contentType(MediaType.APPLICATION_JSON).header(
                                "Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"This kingdom does not belong to authenticated player\"\n" +
                        "}"));
    }

}
