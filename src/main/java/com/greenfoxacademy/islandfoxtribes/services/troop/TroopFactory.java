package com.greenfoxacademy.islandfoxtribes.services.troop;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import org.springframework.stereotype.Service;

@Service
public class TroopFactory {

    public Troop createTroop(Building building, TroopType troopType) {

        if (TroopType.SWORDSMAN.equals(troopType)) {
            return new Troop(
                    building.getLevel(),
                    GameConstants.SWORDSMAN_HP,
                    GameConstants.SWORDSMAN_ATTACK,
                    GameConstants.SWORDSMAN_DEFENSE,
                    GameConstants.SWORDSMAN_COST,
                    GameConstants.SWORDSMAN_CONSTRUCTION_TIME,
                    GameConstants.SWORDSMAN_SPEED,
                    TroopType.SWORDSMAN);

        } else if (TroopType.ARCHER.equals(troopType)) {
            return new Troop(
                    building.getLevel(),
                    GameConstants.ARCHER_HP,
                    GameConstants.ARCHER_ATTACK,
                    GameConstants.ARCHER_DEFENSE,
                    GameConstants.ARCHER_COST,
                    GameConstants.ARCHER_CONSTRUCTION_TIME,
                    GameConstants.ARCHER_SPEED,
                    TroopType.ARCHER);

        } else if (TroopType.KNIGHT.equals(troopType)) {
            return new Troop(
                    building.getLevel(),
                    GameConstants.KNIGHT_HP,
                    GameConstants.KNIGHT_ATTACK,
                    GameConstants.KNIGHT_DEFENSE,
                    GameConstants.KNIGHT_COST,
                    GameConstants.KNIGHT_CONSTRUCTION_TIME,
                    GameConstants.KNIGHT_SPEED,
                    TroopType.KNIGHT);

        } else if (TroopType.SPY.equals(troopType)) {
            return new Troop(
                    building.getLevel(),
                    GameConstants.SPY_HP,
                    GameConstants.SPY_ATTACK,
                    GameConstants.SPY_DEFENSE,
                    GameConstants.SPY_COST,
                    GameConstants.SPY_CONSTRUCTION_TIME,
                    GameConstants.SPY_SPEED,
                    TroopType.SPY);

        } else if (TroopType.SENATOR.equals(troopType)) {
            return new Troop(
                    building.getLevel(),
                    GameConstants.SENATOR_HP,
                    GameConstants.SENATOR_ATTACK,
                    GameConstants.SENATOR_DEFENSE,
                    GameConstants.SENATOR_COST,
                    GameConstants.SENATOR_CONSTRUCTION_TIME,
                    GameConstants.SENATOR_SPEED,
                    TroopType.SENATOR);

        }
        return null;

    }

}
