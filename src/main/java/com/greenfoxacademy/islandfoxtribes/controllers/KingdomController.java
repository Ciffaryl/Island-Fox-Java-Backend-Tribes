package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.AttackRequestDTO;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.DTOs.KingdomRegistrationRequestDto;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForRenameDTO;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomResourceResponseDTO;
import com.greenfoxacademy.islandfoxtribes.services.kingdom.KingdomServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerService;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Import(SecurityConfig.class)

@RestController

public class KingdomController {

    private final KingdomServiceImpl kingdomService;
    private final PlayerService playerService;

    @Autowired
    public KingdomController(KingdomServiceImpl kingdomService, PlayerServiceImpl playerService) {
        this.kingdomService = kingdomService;
        this.playerService = playerService;

    }

    @PutMapping("/registration/")
    public ResponseEntity kingdomRegistration
            (@RequestBody KingdomRegistrationRequestDto kingdomRegistrationRequestDto) {
        if (playerService.validation(kingdomRegistrationRequestDto.getKingdomId())) {
            Errors error = this.kingdomService.setKingdomLocation(kingdomRegistrationRequestDto);
            if (error == null) {
                return ResponseEntity.ok().build();
            } else if ("One or both coordinates are out of valid range (0-99).".equals(error.getError())) {
                return ResponseEntity.badRequest().body(error);
            } else if ("Given coordinates are already taken!".equals(error.getError())) {
                return ResponseEntity.badRequest().body(error);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                ("This Kingdom does not belongs to authenticated player"));
    }


    @GetMapping("/kingdoms")
    public ResponseEntity getAllKingdoms() {
        return ResponseEntity.ok(kingdomService.getListOfAllKingdomsDtos());
    }

    @GetMapping("/kingdoms/{id}/resources")
    public ResponseEntity getResources(@PathVariable Long id) {

        if (playerService.validation(id)) {
            KingdomResourceResponseDTO kingdom = kingdomService.getKingdomResourceDto(id);
            return ResponseEntity.status(HttpStatus.OK).body(kingdom);
        }
        Errors error = new Errors("This kingdom does not belong to authenticated player");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    @PostMapping("/kingdoms/{id}")
    public ResponseEntity renameKingdom(@PathVariable long id, @RequestBody KingdomForRenameDTO dto) {
        if (playerService.validation(id)) {
            if ("".equals(dto.getKingdomName()) || dto.getKingdomName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Errors
                        ("Field kingdomName was empty!"));
            }
            return ResponseEntity.status(HttpStatus.OK).body(kingdomService.renameKingdom(dto.getKingdomName(), id));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                    ("This kingdom does not belong to authenticated player"));
        }
    }

    @GetMapping("/kingdoms/{id}")
    public ResponseEntity getKingdomDetails(@PathVariable long id) {

        if (!playerService.validation(id)) {
            Errors error = new Errors("This kingdom does not belong to authenticated player");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(kingdomService.getKingdomDetails(id));
    }

    @PostMapping("/kingdoms/{id}/battle")
    public ResponseEntity attackOnPlayer(@PathVariable Long id, @RequestBody AttackRequestDTO attackRequestDTO)
            throws IOException, TimeoutException {
        if (!playerService.validation(id)) {
            Errors error = new Errors("This kingdom does not belong to authenticated player");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        Errors kingdomAttackError = this.kingdomService.attackOnPlayer(id, attackRequestDTO);
        if (kingdomAttackError == null) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(kingdomAttackError);
    }


    @GetMapping("/kingdoms/{id}/buildings")
    public ResponseEntity getKingdomBuildingsDetails(@PathVariable long id) {
        if (!playerService.validation(id)) {
            Errors error = new Errors("This kingdom does not belong to authenticated player");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        return ResponseEntity.status(HttpStatus.OK).body(kingdomService.getKingdomBuildingsDetails(id));
    }

    @GetMapping("kingdoms/{id}/troops")
    public ResponseEntity getKingdomTroopsDetails(@PathVariable long id) {

        if (!playerService.validation(id)) {
            Errors error = new Errors("This kingdom does not belong to authenticated player");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(kingdomService.getKingdomTroopsDetails(id));
    }
}







