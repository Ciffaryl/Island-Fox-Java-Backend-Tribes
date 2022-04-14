package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LeaderboardsTest extends TestSetup {

    @Autowired
    SecurityConfigurer securityConfigurer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private KingdomRepository kingdomRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private TroopRepository troopRepository;

    @BeforeEach
    public void setup() {
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);

        Player player1 = new Player();
        player1.setUserName("fox");
        player1.setPassword(passwordEncoder.encode("12345678"));
//        player1.setEmail("email");
//        player1.setEnabled(true);
        Player savedPlayer1 = playerRepository.save(player1);

        Player player2 = new Player();
        player2.setUserName("wolf");
        player2.setPassword(passwordEncoder.encode("12345678"));
//        player2.setEmail("email");
//        player2.setEnabled(true);
        Player savedPlayer2 = playerRepository.save(player2);

        Player player3 = new Player();
        player3.setUserName("lion");
        Player savedPlayer3 = playerRepository.save(player3);

        Kingdom kingdom1 = new Kingdom();
        kingdom1.setName("fox's kingdom");
        kingdom1.setPlayer(savedPlayer1);
        Kingdom savedKingdom1 = kingdomRepository.save(kingdom1);

        Kingdom kingdom2 = new Kingdom();
        kingdom2.setName("wolf's kingdom");
        kingdom2.setPlayer(savedPlayer2);
        Kingdom savedKingdom2 = kingdomRepository.save(kingdom2);

        Kingdom kingdom3 = new Kingdom();
        kingdom3.setName("lion's kingdom");
        kingdom3.setPlayer(savedPlayer3);
        Kingdom savedKingdom3 = kingdomRepository.save(kingdom3);

        BuildingFactory buildingFactory = new BuildingFactory();

        Building building1 = buildingFactory.createBuilding(BuildingType.MINE);
        building1.setLevel(2);
        building1.setKingdom(savedKingdom1);
        Building building2 = buildingFactory.createBuilding(BuildingType.ACADEMY);
        building2.setLevel(3);
        building2.setKingdom(savedKingdom1);
        Building building3 = buildingFactory.createBuilding(BuildingType.BARRACKS);
        building3.setLevel(2);
        building3.setKingdom(savedKingdom1);
        Building building4 = buildingFactory.createBuilding(BuildingType.ACADEMY);
        building4.setLevel(3);
        building4.setKingdom(savedKingdom2);
        Building building5 = buildingFactory.createBuilding(BuildingType.BARRACKS);
        building5.setLevel(3);
        building5.setKingdom(savedKingdom2);
        Building building6 = buildingFactory.createBuilding(BuildingType.FARM);
        building6.setLevel(2);
        building6.setKingdom(savedKingdom3);

        List<Building> savedBuildings1 = buildingRepository.saveAll(Arrays.asList(building1, building2, building3));
        List<Building> savedBuildings2 = buildingRepository.saveAll(Arrays.asList(building4, building5));
        List<Building> savedBuildings3 = buildingRepository.saveAll(List.of(building6));

        savedKingdom1.setBuildingList(savedBuildings1);
        savedKingdom2.setBuildingList(savedBuildings2);
        savedKingdom3.setBuildingList(savedBuildings3);

        Troop troop1 = new Troop(1, 1, 10, 10, 10, 10, 10,  TroopType.ARCHER, savedKingdom1);
        Troop troop2 = new Troop(2, 2, 20, 20, 20, 200, 200, TroopType.KNIGHT, savedKingdom1);
        Troop troop3 = new Troop(3, 1, 10, 10, 10, 10, 10, TroopType.SWORDSMAN, savedKingdom2);
        Troop troop4 = new Troop(4, 2, 20, 20, 20, 200, 200, TroopType.KNIGHT, savedKingdom2);
        Troop troop5 = new Troop(5, 1, 10, 10, 10, 10, 10, TroopType.SPY, savedKingdom3);
        Troop troop6 = new Troop(6, 2, 20, 20, 20, 200, 200, TroopType.SENATOR, savedKingdom3);
        Troop troop7 = new Troop(7, 2, 20, 20, 20, 200, 200, TroopType.SWORDSMAN, savedKingdom3);

        List<Troop> savedTroops1 = troopRepository.saveAll(Arrays.asList(troop1, troop2));
        List<Troop> savedTroops2 = troopRepository.saveAll(Arrays.asList(troop3, troop4));
        List<Troop> savedTroops3 = troopRepository.saveAll(Arrays.asList(troop5, troop6, troop7));

        savedKingdom1.setTroopList(savedTroops1);
        savedKingdom2.setTroopList(savedTroops2);
        savedKingdom3.setTroopList(savedTroops3);

        kingdomRepository.save(savedKingdom1);
        kingdomRepository.save(savedKingdom2);
        kingdomRepository.save(savedKingdom3);
    }

    @Test
    public void troopsLeaderboardTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                                "/leaderboards/troops")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"results\":  [\n" +
                        "             {\n" +
                        "             \"ruler\": \"lion\"," +
                        "             \"kingdom\": \"lion's kingdom\",\n" +
                        "             \"troops\": 3,\n" +
                        "             \"points\": 43\n" +
                        "           },\n" +
                        "           {\n" +
                        "             \"ruler\": \"fox\"," +
                        "             \"kingdom\": \"fox's kingdom\",\n" +
                        "             \"troops\": 2,\n" +
                        "             \"points\": 25\n" +
                        "           }," +
                        "             {\n" +
                        "             \"ruler\": \"wolf\"," +
                        "             \"kingdom\": \"wolf's kingdom\",\n" +
                        "             \"troops\": 2,\n" +
                        "             \"points\": 23\n" +
                        "        }" +
                        "     ]" +
                        "}"));

    }

    @Test
    public void buildingsLeaderboardTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                                "/leaderboards/buildings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"results\":  [\n" +
                        "           {\n" +
                        "             \"ruler\": \"fox\"," +
                        "             \"kingdom\": \"fox's kingdom\",\n" +
                        "             \"buildings\": 3,\n" +
                        "             \"points\": 370\n" +
                        "             },\n" +
                        "             {\n" +
                        "             \"ruler\": \"wolf\"," +
                        "             \"kingdom\": \"wolf's kingdom\",\n" +
                        "             \"buildings\": 2,\n" +
                        "             \"points\": 360\n" +
                        "             }," +
                        "             {\n" +
                        "             \"ruler\": \"lion\"," +
                        "             \"kingdom\": \"lion's kingdom\",\n" +
                        "             \"buildings\": 1,\n" +
                        "             \"points\": 60\n" +
                        "           }\n" +
                        "     ]" +
                        "}"));
    }

    @Test
    public void kingdomsLeaderboardTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                                "/leaderboards/kingdoms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"results\":  [\n" +
                        "           {\n" +
                        "             \"ruler\": \"fox\"," +
                        "             \"kingdom\": \"fox's kingdom\",\n" +
                        "             \"buildings\": 3,\n" +
                        "             \"troops\": 2,\n" +
                        "             \"points\": 395\n" +
                        "             },\n" +
                        "             {\n" +
                        "             \"ruler\": \"wolf\"," +
                        "             \"kingdom\": \"wolf's kingdom\",\n" +
                        "             \"buildings\": 2,\n" +
                        "             \"troops\": 2,\n" +
                        "             \"points\": 383\n" +
                        "             }," +
                        "             {\n" +
                        "             \"ruler\": \"lion\"," +
                        "             \"kingdom\": \"lion's kingdom\",\n" +
                        "             \"buildings\": 1,\n" +
                        "             \"troops\": 3,\n" +
                        "             \"points\": 103\n" +
                        "           }\n" +
                        "     ]" +
                        "}"));

    }
}
