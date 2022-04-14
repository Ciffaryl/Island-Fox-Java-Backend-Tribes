package com.greenfoxacademy.islandfoxtribes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest extends TestSetup {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PlayerRepository playerRepository;

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void registerNewUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\":\"test\",\"password\":\"12345678\", " +
                                "\"email\":\"islandfoxjava@gmail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"username\":\"test\",\"kingdomId\":1}"));
    }

    @Test
    void get_registrationWithTakenUsername() throws Exception {
        mockMvc.perform(post("/registration")
                        .content("{\"username\":\"bbbb\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Test
    void get_registrationWithTakenEmail() throws Exception {
        Player player = new Player("TestPlayer", passwordEncoder.encode("12345678"),
                "islandfoxjava@gmail.com");
        playerRepository.save(player);

        mockMvc.perform(post("/registration")
                        .content("{\"username\":\"test\",\"password\":\"12345678\", " +
                                "\"email\":\"islandfoxjava@gmail.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("User with this email already exists!"));
    }
}
