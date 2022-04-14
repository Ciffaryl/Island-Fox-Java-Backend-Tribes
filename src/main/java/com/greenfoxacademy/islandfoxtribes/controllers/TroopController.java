package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.FinishedAt;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs.RequestNewTroopsDTO;
import com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs.UpgradeTroopsDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopFactory;
import com.greenfoxacademy.islandfoxtribes.services.troop.TroopServiceImpl;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Import(SecurityConfig.class)

@Controller
public class TroopController {

    private PlayerServiceImpl playerService;
    private TroopServiceImpl troopService;
    private KingdomRepository kingdomRepository;
    private TroopFactory troopFactory;
    private BuildingRepository buildingRepository;

    @Autowired
    public TroopController(PlayerServiceImpl playerService, TroopServiceImpl troopService,
                           KingdomRepository kingdomRepository, TroopFactory troopFactory,
                           BuildingRepository buildingRepository) {
        this.playerService = playerService;
        this.troopService = troopService;
        this.kingdomRepository = kingdomRepository;
        this.troopFactory = troopFactory;
        this.buildingRepository = buildingRepository;
    }

    @PostMapping("/kingdoms/{kingdomId}/troops")
    public ResponseEntity createTroops(@PathVariable Long kingdomId,
                                       @RequestBody RequestNewTroopsDTO requestNewTroopsDTO)
            throws IOException, TimeoutException {
        if (!(playerService.validation(kingdomId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                    ("This kingdom does not belong to authenticated player"));
        }
        Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
        Building barracks = kingdom.findBarracks();
        int quantity = requestNewTroopsDTO.getQuantity();
        String troopTypeLabel = requestNewTroopsDTO.getType();
        if (barracks == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Errors("You don't have barracks yet!"));
        }
        //Method .createTroops make returns exact errors, if ther's is no, it just return null a make all the logic.
        // So if createNewTroopsError is null everything is ok.
        Errors createNewTroopsError = this.troopService.createTroops(requestNewTroopsDTO, kingdom);
        if (createNewTroopsError == null) {
            Long currentTime = new Date().getTime();
            Long troopConstructionTime = (long) troopFactory.createTroop(barracks,
                    TroopType.fromLabel(troopTypeLabel)).getConstructionTime();
            Long allTroopConstructionTime = troopConstructionTime * quantity;
            return ResponseEntity.ok().body(new FinishedAt(new Date(currentTime + allTroopConstructionTime)));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createNewTroopsError);
        }
    }

    @PutMapping("/kingdoms/{kingdomId}/troops")
    public ResponseEntity upgradeTroops(@PathVariable Long kingdomId, @RequestBody UpgradeTroopsDTO upgradeTroopsDTO) {
        if (playerService.validation(kingdomId)) {
            Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
            Errors upgradedTroops = this.troopService.upgradeTroops(kingdom, upgradeTroopsDTO.getTroopsId());

            if (upgradedTroops == null) {
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(upgradedTroops);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                ("This kingdom does not belong to authenticated player"));
    }

}
