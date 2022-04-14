package com.greenfoxacademy.islandfoxtribes;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.message.Message;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.message.MessageRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MessageControllerTest extends TestSetup {

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
    private MessageRepository messageRepository;

    @MockBean
    private PlayerServiceImpl playerServiceImpl;

    @BeforeEach
    public void setup() {

        securityConfigurer = Mockito.mock(SecurityConfigurer.class);

        Player player1 = new Player();
        player1.setUserName("someone");
        player1.setPassword(passwordEncoder.encode("12345678"));
        player1.setEmail("email");
        player1.setEnabled(true);
        Player savedPlayer1 = playerRepository.save(player1);

        Player player2 = new Player();
        player2.setUserName("somebody");
        player2.setPassword(passwordEncoder.encode("12345678"));
        player2.setEmail("email");
        player2.setEnabled(true);
        Player savedPlayer2 = playerRepository.save(player2);

        Message message1 = new Message(player1, "somebody", "object1", "text1", "13-1-2022 11:00:00");
        Message message2 = new Message(player1, "somebody", "object2", "text2", "13-1-2022 12:00:00");
        List<Message> savedMessages = messageRepository.saveAll(Arrays.asList(message1, message2));
        savedPlayer1.setMessageList(savedMessages);

        Kingdom kingdom1 = new Kingdom();
        kingdom1.setPlayer(player1);
        kingdomRepository.save(kingdom1);
        Kingdom kingdom2 = new Kingdom();
        kingdom2.setPlayer(player2);
        kingdomRepository.save(kingdom2);

        jwt = jwtUtil.generateToken(userDetailsServiceForJwt.loadUserByUsername(player1.getUserName()));
    }

    // tests for @PostMapping("/kingdom/{id}/message" endpoint)
    @Test
    public void sendMessageSuccessfulTest() throws Exception {

        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .content("{\n" +
                                "  \"object\": \"object\",\n" +
                                "  \"text\": \"text\",\n" +
                                "  \"receiverName\": \"somebody\"\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Your message has been sent successfully."));
    }

    @Test
    public void sendMessageEmptyObjectTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .content("{\n" +
                                "  \"text\": \"text\",\n" +
                                "  \"receiverName\": \"somebody\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"The object must be filled.\"\n" +
                        "}"));
    }

    @Test
    public void sendMessageEmptyTextTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .content("{\n" +
                                "  \"object\": \"object\",\n" +
                                "  \"receiverName\": \"somebody\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"Empty message cannot be sent.\"\n" +
                        "}"));
    }

    @Test
    public void sendMessageEmptyReceiverTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .content("{\n" +
                                "  \"object\": \"object\",\n" +
                                "  \"text\": \"text\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"The receiver of the message must be filled in.\"\n" +
                        "}"));
    }

    @Test
    public void sendMessageNonExistingReceiverTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .content("{\n" +
                                "  \"object\": \"object\",\n" +
                                "  \"text\": \"text\",\n" +
                                "  \"receiverName\": \"none\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"The receiver of the message does not exist.\"\n" +
                        "}"));
    }

    @Test
    public void sendMessageUnauthorizedSenderTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/kingdom/1/message")
                        .content("{\n" +
                                "  \"object\": \"object\",\n" +
                                "  \"text\": \"text\",\n" +
                                "  \"receiverName\": \"none\"\n" +
                                "}")
                        .header("Authorization", "Bearer " + jwt.getToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"You have to log in to your account to send a message.\"\n" +
                        "}"));
    }

    // tests for @GetMapping("/kingdom/{id}/messages" endpoint)
    @Test
    public void getAllMessagesAuthorizedTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdom/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"messages\":  [\n" +
                        "             {\n" +
                        "             \"receiver\": \"someone\"," +
                        "             \"sender\": \"somebody\",\n" +
                        "             \"object\": \"object1\",\n" +
                        "             \"text\": \"text1\",\n" +
                        "             \"sentAt\": \"13-1-2022 11:00:00\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "             \"receiver\": \"someone\"," +
                        "             \"sender\": \"somebody\",\n" +
                        "             \"object\": \"object2\",\n" +
                        "             \"text\": \"text2\",\n" +
                        "             \"sentAt\": \"13-1-2022 12:00:00\"\n" +
                        "        }\n" +
                        "     ]\n" +
                        "}"));
    }

    @Test
    public void getAllMessagesUnauthorizedTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdom/1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"You don't have permission to view all messages.\"\n" +
                        "}"));
    }

    // tests for @GetMapping("/kingdom/{id}/message/{id}" endpoint)
    @Test
    public void getMessageByIdAuthorizedTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdom/1/message/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "             \"receiver\": \"someone\"," +
                        "             \"sender\": \"somebody\",\n" +
                        "             \"object\": \"object1\",\n" +
                        "             \"text\": \"text1\",\n" +
                        "             \"sentAt\": \"13-1-2022 11:00:00\"\n" +
                        "        }"));
    }

    @Test
    public void getMessageByIdUnauthorizedTest() throws Exception {
        Mockito.when(playerServiceImpl.validation(1L)).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.get("/kingdom/1/message/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt.getToken()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\n" +
                        "    \"error\": \"You don't have permission to view this message.\"\n" +
                        "}"));
    }
}
