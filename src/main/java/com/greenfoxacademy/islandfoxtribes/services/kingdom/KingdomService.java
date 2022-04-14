package com.greenfoxacademy.islandfoxtribes.services.kingdom;
import com.greenfoxacademy.islandfoxtribes.models.DTOs.AttackRequestDTO;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.KingdomBuildingsDetailsDTO;
import com.greenfoxacademy.islandfoxtribes.models.DTOs.KingdomDetailsDTO;
import com.greenfoxacademy.islandfoxtribes.models.DTOs.KingdomTroopsDetailsDTO;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.DTOs.KingdomRegistrationRequestDto;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForRenameDTO;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomListDTO;

import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomResourceResponseDTO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;



import java.util.Optional;


public interface KingdomService {

    Errors setKingdomLocation(KingdomRegistrationRequestDto kingdomRegistrationRequestDto);

    boolean isLocationAvailable(KingdomRegistrationRequestDto kingdomRegistrationRequestDto);

    List<Kingdom> getAll();

    Optional<Kingdom> getKingdomByName(String kingdomName);

    KingdomListDTO getListOfAllKingdomsDtos();

    void scheduledRefreshing();

    KingdomResourceResponseDTO getKingdomResourceDto(Long id);

    KingdomForRenameDTO renameKingdom(String name, Long id);

    KingdomDetailsDTO getKingdomDetails(long id);

    Errors attackOnPlayer(Long id, AttackRequestDTO attackRequestDTO) throws IOException, TimeoutException;

    KingdomBuildingsDetailsDTO getKingdomBuildingsDetails(long id);

    KingdomTroopsDetailsDTO getKingdomTroopsDetails(long id);

    void scheduledLoyaltyIncrease();
}

