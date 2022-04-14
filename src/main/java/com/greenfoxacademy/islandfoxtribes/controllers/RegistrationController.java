package com.greenfoxacademy.islandfoxtribes.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerAuthDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerJWTDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerLoginDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterRequestDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtUtil;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerService;
import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Import(SecurityConfig.class)

@RestController
public class RegistrationController {

    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final KingdomRepository kingdomRepository;
    private final UserDetailsServiceForJwt userDetailsServiceForJwt;

    Logger logger = Logger.getLogger(
            RegistrationController.class.getName());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;


    @Autowired

    public RegistrationController(PlayerRepository playerRepository,
                                  PlayerService playerService,
                                  KingdomRepository kingdomRepository,
                                  UserDetailsServiceForJwt userDetailsServiceForJwt) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.kingdomRepository = kingdomRepository;
        this.userDetailsServiceForJwt = userDetailsServiceForJwt;
    }

    @PostMapping("/registration")
    public ResponseEntity registration(@RequestBody PlayerRegisterRequestDTO player) {
        if (playerRepository.findByUserName(player.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exists!");
        } else if (playerRepository.findByEmail(player.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User with this email already exists!");
        } else if (player.getPassword() == null || player.getPassword().toCharArray().length < 8) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Password has to be at least 8 characters long!");
        } else if (player.getUsername().isEmpty() ||
                kingdomRepository.findKingdomByName(player.getKingdomName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Kingdom Name have to be unique!");
        } else {
            if (player.getKingdomName() == null) {
                player.setKingdomName(player.getUsername() + "'s Kingdom");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(playerService.registration(player));
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody PlayerLoginDTO dto, HttpServletResponse response) {
        // I need Error as a response
        Errors errors = new Errors();

        if (playerRepository.findByUserName(dto.getUsername()).isPresent()) {
            // this will determine if the user with this password is correct

            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            } catch (BadCredentialsException e) {
                logger.log(Level.WARNING, "Incorrect username or password.");
                errors.setError("Username and/or password was incorrect!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
            }

            //  this will set  current user data with "user" as authority role
            final UserDetails userDetails = userDetailsServiceForJwt.loadUserByUsername(dto.getUsername());
            PlayerJWTDTO jwt = jwtTokenUtil.generateToken(userDetails);
            response.setHeader("Authorization", "Bearer " + jwt.getToken());
            // this will generate JWT token and return DTO as a response
            return ResponseEntity.status(HttpStatus.OK).body(jwt);
        } else if (dto.getPassword().isEmpty() || dto.getUsername().isEmpty()) {
            errors.setError("Field username and/or field password was empty!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } else {
            errors.setError("Player not found!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }

    @GetMapping("/test")
    String test() {
        return "hura";
    }

    @PostMapping("/test2")
    public String test2() {
        Boolean a = playerService.validation(1L);
        System.out.println(a);
        return "hura";
    }

    @PostMapping("/auth")
    public ResponseEntity<List<PlayerAuthDTO>> authentication(Authentication authentication) {
        // I did auth endpoint as a List, because later, if we have more kingdoms, we need to know all ids

        Player player = playerRepository.findPlayerByUserName(authentication.getName());
        PlayerAuthDTO dto = new PlayerAuthDTO();

        List<PlayerAuthDTO> playerAuthDTOList = new ArrayList<>();

        for (Kingdom k : player.getKingdomList()) {
            dto.setRuler(player.getUserName());
            dto.setKingdomId(k.getId());
            dto.setKingdomName(k.getName());
            playerAuthDTOList.add(dto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(playerAuthDTOList);
    }
}
