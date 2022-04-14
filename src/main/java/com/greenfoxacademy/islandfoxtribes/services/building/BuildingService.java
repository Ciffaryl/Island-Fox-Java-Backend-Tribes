package com.greenfoxacademy.islandfoxtribes.services.building;

import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface BuildingService {

    Errors buildBuilding(Long buildingId, Kingdom kingdom)
            throws IOException, TimeoutException;

    Errors buildBuilding(Long buildingId, Kingdom kingdom, boolean skipQueue)
            throws IOException, TimeoutException;

    Errors buildBuilding(String buildingTypeLabel, Kingdom kingdom)
            throws IOException, TimeoutException;

    Errors buildBuilding(String buildingTypeLabel, Kingdom kingdom, boolean skipQueue)
            throws IOException, TimeoutException;

    void rabbitMQ(boolean skipQueue, String messageKey, Kingdom kingdom)
            throws IOException, TimeoutException;

    void academyUpgrades(Long kingdomId, Long buildingId, String upgradeName)
            throws IOException, TimeoutException;

    void academyUpgrades(Long kingdomId, Long buildingId, String upgradeName, boolean skipQueue)
            throws IOException, TimeoutException;

}
