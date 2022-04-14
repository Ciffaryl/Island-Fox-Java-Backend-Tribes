package com.greenfoxacademy.islandfoxtribes.controllers;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.AcademyUpgradesDTO;
import com.greenfoxacademy.islandfoxtribes.models.DTOs.FinishedAt;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.building.buildBuildingDtos.RequestNewBuildingDto;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingServiceImpl;
import com.greenfoxacademy.islandfoxtribes.services.player.PlayerServiceImpl;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Import(SecurityConfig.class)

@Controller
public class BuildingController {

    private PlayerServiceImpl playerService;
    private BuildingServiceImpl buildingService;
    private KingdomRepository kingdomRepository;
    private BuildingFactory buildingFactory;
    private BuildingRepository buildingRepository;

    @Autowired
    public BuildingController(PlayerServiceImpl playerService, BuildingServiceImpl buildingService,
                              KingdomRepository kingdomRepository, BuildingFactory buildingFactory,
                              BuildingRepository buildingRepository) {
        this.playerService = playerService;
        this.buildingService = buildingService;
        this.kingdomRepository = kingdomRepository;
        this.buildingFactory = buildingFactory;
        this.buildingRepository = buildingRepository;
    }

    @PostMapping("/kingdoms/{kingdomId}/buildings")
    public ResponseEntity buildNewBuilding(@PathVariable Long kingdomId,
                                           @RequestBody RequestNewBuildingDto requestNewBuildingDto)
            throws IOException, TimeoutException {
        String buildingType = requestNewBuildingDto.getType();
        if (playerService.validation(kingdomId)) {
            // Validation of getting right name of building type
            if (typeValidityCheck(buildingType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Errors("Wrong type of building"));
            }
            Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
            Errors buildNewBuilding = this.buildingService.buildBuilding(buildingType, kingdom);
            if (buildNewBuilding == null) {
                //Calculating of time, when the construction will be finished
                Long currentTime = new Date().getTime();
                Integer constructionTime = this.buildingFactory.createBuilding(
                        BuildingType.fromLabel(buildingType)).getConstructionTime();
                FinishedAt fin = new FinishedAt(new Date(currentTime + constructionTime));
                return ResponseEntity.ok(fin);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildNewBuilding);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                ("This kingdom does not belong to authenticated player"));
    }

    private boolean typeValidityCheck(String type) {
        for (BuildingType buildingType : BuildingType.values()) {
            if (buildingType.label.equals(type)) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("/kingdoms/{kingdomId}/buildings/{buildingId}")
    public ResponseEntity upgradeBuilding(@PathVariable Long kingdomId, @PathVariable Long buildingId)
            throws IOException, TimeoutException {
        if (playerService.validation(kingdomId)) {
            // Validation of belongings to the kingdom of construction
            if (buildingBelongingValidation(kingdomId, buildingId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body
                        (new Errors("This building doesn't belong to this kingdom!"));
            }
            Kingdom kingdom = this.kingdomRepository.getById(kingdomId);
            Errors upgradeBuilding = this.buildingService.buildBuilding(buildingId, kingdom);
            if (upgradeBuilding == null) {
                //Calculating of time, when the construction will be finished
                Building building = this.buildingRepository.getById(buildingId);
                Long currentTime = new Date().getTime();
                Integer constructionTime = building.getConstructionTime();
                FinishedAt fin = new FinishedAt(new Date(currentTime + constructionTime));
                return ResponseEntity.ok(fin);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(upgradeBuilding);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Errors
                ("This kingdom does not belong to authenticated player"));
    }

    private boolean buildingBelongingValidation(Long kingdomId, Long buildingId) {
        Building building = this.buildingRepository.getById(buildingId);
        if (building.getKingdom().getId().equals(kingdomId)) {
            return false;
        } else
            return true;
    }

    @PostMapping("/kingdoms/{kingdomId}/academy/{buildingId}")
    public ResponseEntity academyUpgrade(@PathVariable Long kingdomId, @PathVariable Long buildingId,
                                         @RequestBody AcademyUpgradesDTO academyUpgradesDTO)
        throws IOException, TimeoutException {
        if (!playerService.validation(kingdomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Given kingdom doesn't belong to authenticated player!");
        }
        if (buildingBelongingValidation(kingdomId, buildingId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Given building doesn't belong to given kingdom!");
        }
        if (!buildingRepository.getById(buildingId).getBuildingType().equals(BuildingType.ACADEMY)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Given building is not an Academy!");
        }
        String academyUpgradeName = academyUpgradesDTO.getUpgradeName();
        buildingService.academyUpgrades(kingdomId, buildingId, academyUpgradeName);
        Long currentTime = new Date().getTime();
        FinishedAt fin = new FinishedAt(
                new Date(currentTime + academyUpgradeName.charAt(academyUpgradeName.length() - 1)));
        return ResponseEntity.status(HttpStatus.OK).body(fin);
    }
}

