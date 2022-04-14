package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer;

import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtTokenTest extends TestSetup {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserDetailsServiceForJwt userDetailsServiceForJwt;

    private SecurityConfigurer securityConfigurer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Player player;
    private PlayerJWTDTO jwt;

    @BeforeEach
    void setup() {
        securityConfigurer = Mockito.mock(SecurityConfigurer.class);
        this.player = new Player("TestPlayer", passwordEncoder.encode("12345678"), "email");
        player.setEnabled(true);
        playerRepository.save(player);
        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player.getUserName()));
    }

    @Test
    public void endpointWithNoToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test"))
                .andExpect(status().isFound());
    }

    @Test
    public void loginTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"TestPlayer\",\"password\":\"12345678\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void endpointWithToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    public void endpointWithWrongToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Wrong Token " + jwt.getToken()))
                .andExpect(status().isFound());
    }

}
