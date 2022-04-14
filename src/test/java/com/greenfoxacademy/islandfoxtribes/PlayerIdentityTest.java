package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.controllers.RegistrationController;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtRequestFilter;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;

import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlayerIdentityTest extends TestSetup {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private MockMvc mvc;

    private RegistrationController registrationController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    @Autowired
    private PlayerServiceImpl playerService;
    private SecurityConfigurer securityConfigurer;


    private Player player;
    private PlayerJWTDTO jwt;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void authenticationTest() throws Exception {

        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
        this.player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        Kingdom kingdom = new Kingdom();
        kingdom.setName("BestKingdom");
        List<Kingdom> kingdomList = new ArrayList<>();
        kingdomList.add(kingdom);
        player.setKingdomList(kingdomList);
        playerRepository.save(player);
        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));

        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .content("{\"username\":\"TestPlayer\",\"password\":\"12345678\"}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"ruler\": \"TestPlayer\",\n" +
                        "        \"kingdomId\": 1,\n" +
                        "        \"kingdomName\": \"BestKingdom\"}]"));
    }

    @Test
    public void authenticationTestWithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/auth")
                        .header("Authorization", "Wrong Token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}
