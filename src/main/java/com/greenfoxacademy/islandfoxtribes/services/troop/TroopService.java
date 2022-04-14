package com.greenfoxacademy.islandfoxtribes.services.troop;

import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.troop.troopDTOs.RequestNewTroopsDTO;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface TroopService {

    Errors createTroops(RequestNewTroopsDTO requestNewTroopsDTO, Kingdom kingdom)
            throws IOException, TimeoutException;

    Errors createTroops(RequestNewTroopsDTO requestNewTroopsDTO, Kingdom kingdom, Boolean skipQueue)
            throws IOException, TimeoutException;

    Errors upgradeTroops(Kingdom kingdom, List<Long> troopsToUpgrade);

    void delete(Troop troop);

    void upgradeTroops(Long kingdomId, String troopType, String stat, int level);

}
