package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;

import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtRequestFilter;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RenameKingdomTest extends TestSetup {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    @Autowired
    private SecurityConfigurer securityConfigurer;

    private Player player;
    private PlayerJWTDTO jwt;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @MockBean
    PlayerServiceImpl playerServiceImpl;

    @BeforeEach
    public void setUp() {
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
    }

    @Test
    public void renameKingdomWithBadKingdomId() throws Exception {

        Mockito.when(playerServiceImpl.validation(2L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/kingdoms/2")
                        .content("{\"kingdomName\":\"Test\"}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\": \"This kingdom does not belong to authenticated player\"}"));
    }

    @Test
    public void renameKingdomWithRightKingdomId() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/kingdoms/1")
                        .content("{\"kingdomName\":\"Test\"}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"kingdomId\": 1 , \"kingdomName\" : \"Test\"}"));
    }

    @Test
    public void renameKingdomWithEmptyKingdomName() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/kingdoms/1")
                        .content("{\"kingdomName\":\"\"}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Field kingdomName was empty!\"}"));
    }
}
