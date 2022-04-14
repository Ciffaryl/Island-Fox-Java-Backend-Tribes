package com.greenfoxacademy.islandfoxtribes.services.kingdom;

import com.greenfoxacademy.islandfoxtribes.models.DTOs.*;

import com.greenfoxacademy.islandfoxtribes.models.battle.Battle;
import com.greenfoxacademy.islandfoxtribes.models.constants.GameConstants;
import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.DTOs.KingdomRegistrationRequestDto;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import com.greenfoxacademy.islandfoxtribes.models.building.Building;


import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForListDTO;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomForRenameDTO;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomListDTO;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO.KingdomResourceResponseDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.resourceDTOs.ResourceResponseDTO;
import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import com.greenfoxacademy.islandfoxtribes.models.troop.TroopType;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.QueueSender;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle.BattleHandler;
import com.greenfoxacademy.islandfoxtribes.rabbitMQ.battle.QueueReceiverForBattle;
import com.greenfoxacademy.islandfoxtribes.repositories.battle.BattleRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.building.BuildingRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.kingdom.KingdomRepository;
import com.greenfoxacademy.islandfoxtribes.services.resource.ResourceService;
import com.greenfoxacademy.islandfoxtribes.repositories.resource.ResourceRepository;
import com.greenfoxacademy.islandfoxtribes.repositories.troop.TroopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;


@Service
public class KingdomServiceImpl implements KingdomService {

    private final KingdomRepository kingdomRepository;
    private final ResourceRepository resourceRepository;
    private final BuildingRepository buildingRepository;
    private final TroopRepository troopRepository;
    private final BattleRepository battleRepository;
    private final BattleHandler battleHandler;
    private final ResourceService resourceService;

    @Autowired

    public KingdomServiceImpl(KingdomRepository kingdomRepository, ResourceRepository resourceRepository,
                              BuildingRepository buildingRepository, TroopRepository troopRepository,
                              BattleRepository battleRepository, BattleHandler battleHandler,
                              ResourceService resourceService) {
        this.kingdomRepository = kingdomRepository;
        this.resourceRepository = resourceRepository;
        this.buildingRepository = buildingRepository;
        this.troopRepository = troopRepository;
        this.battleRepository = battleRepository;
        this.battleHandler = battleHandler;
        this.resourceService = resourceService;
    }

    @Override
    public Errors setKingdomLocation(KingdomRegistrationRequestDto kingdomRegistrationRequestDto) {
        int coordinatesX = kingdomRegistrationRequestDto.getCoordinateX();
        int coordinatesY = kingdomRegistrationRequestDto.getCoordinateY();
        if (coordinatesX > 99 || coordinatesY > 99) {
            return new Errors("One or both coordinates are out of valid range (0-99).");
        }
        if (isLocationAvailable(kingdomRegistrationRequestDto)) {
            Kingdom kingdom = this.kingdomRepository.getById
                    (kingdomRegistrationRequestDto.getKingdomId());
            kingdom.setLocation(new Location(coordinatesX, coordinatesY));
            this.kingdomRepository.save(kingdom);
            return null;
        } else
            return new Errors("Given coordinates are already taken!");
    }

    @Override
    public boolean isLocationAvailable(KingdomRegistrationRequestDto kingdomRegistrationRequestDto) {
        List<Kingdom> kingdomList = this.kingdomRepository.findAll();
        //For every kingdom in database this loop checking if exist some with same
        // coordinates as in kingdomRegistrationRequestDto
        for (Kingdom kingdom : kingdomList) {
            if (kingdom.getLocation().getCoordinateX().equals(kingdomRegistrationRequestDto.getCoordinateX()) &&
                    kingdom.getLocation().getCoordinateY().equals(kingdomRegistrationRequestDto.getCoordinateY())) {
                return false;
            }
        }
        return true;
    }

    public KingdomListDTO getListOfAllKingdomsDtos() {
        List<Kingdom> listOfKingdoms = this.kingdomRepository.findAll();
        List<KingdomForListDTO> listOfKingdomsDtos = new ArrayList<>();
        for (Kingdom kingdom : listOfKingdoms) {
            listOfKingdomsDtos.add(new KingdomForListDTO(kingdom));
        }
        return new KingdomListDTO(listOfKingdomsDtos);
    }

    @Override
    public KingdomResourceResponseDTO getKingdomResourceDto(Long id) {

        Kingdom kingdom = kingdomRepository.getById(id);

        KingdomForListDTO kingdomDTO = new KingdomForListDTO(kingdom);

        KingdomResourceResponseDTO kingdomResourceResponseDTO = new KingdomResourceResponseDTO(kingdomDTO);

        for (Resource resource : kingdom.getResourceList()) {
            ResourceResponseDTO responseDTO = new ResourceResponseDTO(resource);
            kingdomResourceResponseDTO.addResource(responseDTO);
        }

        return kingdomResourceResponseDTO;
    }

    public KingdomForRenameDTO renameKingdom(String name, Long id) {
        Kingdom kingdom = kingdomRepository.findKingdomById(id);
        kingdom.setName(name);
        kingdomRepository.save(kingdom);
        KingdomForRenameDTO kingdomForRenameDTO = new KingdomForRenameDTO(kingdom);
        return kingdomForRenameDTO;

    }

    @Override
    public List<Kingdom> getAll() {
        return kingdomRepository.findAll();
    }

    @Override
    public Optional<Kingdom> getKingdomByName(String kingdomName) {
        return kingdomRepository.findKingdomByName(kingdomName);
    }

    // This is what happens when the game is being refreshed.
    @Override
    public void scheduledRefreshing() {
        // This method calculates farmed food and increases the foodResource amount.
        resourceService.foodFarmed();

        // This method calculates mined gold and increases the goldResource amount.
        resourceService.goldMined();

        // This method calculates how much food has been eaten by the army and decreases the foodResource amount.
        // If there is not enough food to feed the army, one unit will die.
        resourceService.foodEaten();
    }

    public KingdomDetailsDTO getKingdomDetails(long id) {

        KingdomDetailsDTO kingdomDetailsDTO = new KingdomDetailsDTO();

        //kingdom
        Kingdom kingdom = kingdomRepository.getById(id);
        KingdomForListDTO kingdomForListDTO = new KingdomForListDTO(kingdom);
        kingdomDetailsDTO.setKingdom(kingdomForListDTO);

        //resources
        List<Resource> kingdomResources = resourceRepository.findAllByKingdomId(id);
        List<ResourceDTO> resourceDTOS = new ArrayList<>();
        for (Resource resource : kingdomResources) {
            ResourceDTO resourceDTO = new ResourceDTO(resource);
            resourceDTOS.add(resourceDTO);
        }
        kingdomDetailsDTO.setResources(resourceDTOS);

        //buildings
        List<Building> kingdomBuildings = buildingRepository.findAllByKingdomId(id);
        List<BuildingDTO> buildingDTOS = new ArrayList<>();
        for (Building building : kingdomBuildings) {
            BuildingDTO buildingDTO = new BuildingDTO(building);
            buildingDTOS.add(buildingDTO);
        }
        kingdomDetailsDTO.setBuildings(buildingDTOS);

        //troops
        List<Troop> kingdomTroops = troopRepository.findAllByKingdomId(id);
        List<TroopDTO> troopDTOS = new ArrayList<>();
        for (Troop troop : kingdomTroops) {
            TroopDTO troopDTO = new TroopDTO(troop);
            troopDTOS.add(troopDTO);
        }
        kingdomDetailsDTO.setTroops(troopDTOS);

        return kingdomDetailsDTO;

    }

    @Override
    public KingdomBuildingsDetailsDTO getKingdomBuildingsDetails(long id) {

        KingdomBuildingsDetailsDTO kingdomBuildingsDetailsDTO = new KingdomBuildingsDetailsDTO();

        //kingdom
        Kingdom kingdom = kingdomRepository.getById(id);
        KingdomForListDTO kingdomForListDTO = new KingdomForListDTO(kingdom);
        kingdomBuildingsDetailsDTO.setKingdom(kingdomForListDTO);

        //buildings
        List<Building> kingdomBuildings = buildingRepository.findAllByKingdomId(id);
        List<BuildingDTO> buildingDTOS = new ArrayList<>();
        for (Building building : kingdomBuildings) {
            BuildingDTO buildingDTO = new BuildingDTO(building);
            buildingDTOS.add(buildingDTO);
        }
        kingdomBuildingsDetailsDTO.setBuildings(buildingDTOS);

        return kingdomBuildingsDetailsDTO;
    }

    @Override
    public KingdomTroopsDetailsDTO getKingdomTroopsDetails(long id) {

        KingdomTroopsDetailsDTO kingdomTroopsDetailsDTO = new KingdomTroopsDetailsDTO();

        //kingdom
        Kingdom kingdom = kingdomRepository.getById(id);
        KingdomForListDTO kingdomForListDTO = new KingdomForListDTO(kingdom);
        kingdomTroopsDetailsDTO.setKingdom(kingdomForListDTO);

        //troops
        List<Troop> kingdomTroops = troopRepository.findAllByKingdomId(id);
        List<TroopDTO> troopDTOS = new ArrayList<>();
        for (Troop troop : kingdomTroops) {
            TroopDTO troopDTO = new TroopDTO(troop);
            troopDTOS.add(troopDTO);
        }
        kingdomTroopsDetailsDTO.setTroops(troopDTOS);

        return kingdomTroopsDetailsDTO;
    }

    @Override
    public void scheduledLoyaltyIncrease() {
        List<Kingdom> kingdomList = this.kingdomRepository.findAll();

        for (Kingdom kingdom : kingdomList) {
            int newLoyalty = Math.min(100, kingdom.getLoyalty() + 10);
            kingdom.setLoyalty(newLoyalty);
        }
    }


    @Override
    public Errors attackOnPlayer(Long id, AttackRequestDTO attackRequestDTO) throws IOException, TimeoutException {
        String typeOfAttack = attackRequestDTO.getBattleType();
        if ("Spy".equals(typeOfAttack)) {
            return spyAttack(id, attackRequestDTO, false);
        } else if ("Plunder".equals(typeOfAttack) || "TakeOver".equals(typeOfAttack)) {
            return plunderAndTakeOverAttack(id, attackRequestDTO, false);
        }
        return new Errors("Wrong type of attack!");

    }


    private Errors plunderAndTakeOverAttack(Long id, AttackRequestDTO attackRequestDTO, boolean skip)
            throws IOException, TimeoutException {
        Kingdom target = this.kingdomRepository.getById(attackRequestDTO.getTarget());
        List<Long> troops = attackRequestDTO.getTroops();

        Errors errors = errorHandling(target, troops, id);
        if (!(errors == null)) {
            return errors;
        }

        Troop slowestTroop = findSlowestTroop(troops);
        Kingdom attacker = this.kingdomRepository.getById(id);
        Long travelTime = countTravelTime(attacker, target, slowestTroop.getSpeed());
        Battle battle = createBattle(attacker, target, attackRequestDTO);
        String messageKey = attackRequestDTO.getBattleType() + "/" + battle.getId() + "/" + travelTime;

        if (!skip) {
            rabbitMQ(messageKey);
        }
        return null;
    }

    private Errors errorHandling(Kingdom target, List<Long> troops, Long id) {
        if (troops == null || troops.size() == 0) {
            return new Errors("You need to send something!");
        }
        if (!(troopsBelongingsValidation(id, troops))) {
            return new Errors("All your troops must be yours and must be in the city!");
        }
        if (!(validateTarget(target))) {
            return new Errors("This player doesn't exist");
        }
        if (Objects.equals(target.getId(), id)) {
            return new Errors("You can't attack on yourself!");
        }
        return null;
    }


    private boolean troopsBelongingsValidation(Long id, List<Long> troops) {
        for (Long troopId : troops) {
            Optional<Troop> currentTroop = this.troopRepository.findById(troopId);
            if (currentTroop.isEmpty()) {
                return false;
            }
            if (!(Objects.equals(currentTroop.get().getKingdom().getId(), id))) {
                return false;
            }
            if (!(currentTroop.get().getBattle() == null)) {
                return false;
            }
        }
        return true;
    }


    public Troop findSlowestTroop(List<Long> troops) {
        Troop slowest = this.troopRepository.getById(troops.get(0));
        for (Long troopId : troops) {
            Troop currentTroop = this.troopRepository.getById(troopId);
            if (currentTroop.getSpeed() > slowest.getSpeed()) {
                slowest = currentTroop;
            }
        }
        return slowest;
    }


    private Errors spyAttack(Long id, AttackRequestDTO attackRequestDTO, boolean skip)
            throws IOException, TimeoutException {
        List<Long> troops = attackRequestDTO.getTroops();
        Kingdom target = this.kingdomRepository.getById(attackRequestDTO.getTarget());

        Errors errors = errorHandling(target, troops, id);

        if (!(errors == null)) {
            return errors;
        }

        if (!(validateTroops(troops))) {
            return new Errors("All troops must be Spies!");
        }
        Kingdom attacker = this.kingdomRepository.getById(id);
        Long travelTime = countTravelTime(attacker, target, GameConstants.SPY_SPEED);
        Battle battle = createBattle(attacker, target, attackRequestDTO);

        String messageKey = attackRequestDTO.getBattleType() + "/" + battle.getId() + "/" + travelTime;

        if (!skip) {
            rabbitMQ(messageKey);
        }
        return null;
    }

    private void rabbitMQ(String messageKey) throws IOException, TimeoutException {
        QueueSender battleSender = new QueueSender(messageKey);
        QueueReceiverForBattle queueReceiverForBattle = new QueueReceiverForBattle(messageKey, battleHandler);
        battleSender.sendMessage(messageKey);
        queueReceiverForBattle.receiveMessage();

    }

    private Battle createBattle(Kingdom attacker, Kingdom target, AttackRequestDTO attackRequestDTO) {
        Battle battle = new Battle(target.getId(), attacker.getId(), attackRequestDTO.getBattleType());

        for (Long troop : attackRequestDTO.getTroops()) {
            Troop actualTroop = this.troopRepository.getById(troop);
            battle.addTroopToBattle(actualTroop);
            actualTroop.setBattle(battle);
        }

        return this.battleRepository.save(battle);
    }

    public long countTravelTime(Kingdom attacker, Kingdom target, int unitSpeed) {
        Location attackerLocation = attacker.getLocation();
        Location targetLocation = target.getLocation();
        int attackerX = attackerLocation.getCoordinateX();
        int attackerY = attackerLocation.getCoordinateY();
        int targetX = targetLocation.getCoordinateX();
        int targetY = targetLocation.getCoordinateY();

        int a = attackerX - targetX;
        int b = attackerY - targetY;
        int c = (a * a) + (b * b);

        double result = Math.sqrt(c) * (unitSpeed * 1000);
        return Math.round(result);
    }

    private boolean validateTarget(Kingdom target) {
        for (Kingdom kingdom : this.kingdomRepository.findAll()) {
            if (Objects.equals(target.getId(), kingdom.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean validateTroops(List<Long> troops) {
        for (Long troop : troops) {
            Troop currentTroop = this.troopRepository.getById(troop);
            if (!(currentTroop.getTroopType().equals(TroopType.SPY))) {
                return false;
            }
        }
        return true;
    }

}
