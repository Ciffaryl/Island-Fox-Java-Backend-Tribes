package com.greenfoxacademy.islandfoxtribes.services.player;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import com.greenfoxacademy.islandfoxtribes.models.building.BuildingType;
import com.greenfoxacademy.islandfoxtribes.models.kingdom.Kingdom;
import com.greenfoxacademy.islandfoxtribes.models.location.Location;
import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerAuthDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterRequestDTO;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterResponseDTO;
import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import com.greenfoxacademy.islandfoxtribes.models.resource.ResourceType;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.retrofit.PlayerRetrofitService;
import com.greenfoxacademy.islandfoxtribes.retrofit.RetrofitInstance;
import com.greenfoxacademy.islandfoxtribes.services.building.BuildingFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.greenfoxacademy.islandfoxtribes.security.Jwt.JwtRequestFilter.CURRENT_TOKEN;


import java.util.ArrayList;
import java.util.List;


@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final BuildingFactory buildingFactory;
    private final EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, BuildingFactory buildingFactory,
                             EmailService emailService) {
        this.playerRepository = playerRepository;
        this.buildingFactory = buildingFactory;
        this.emailService = emailService;
    }

    @Override
    public PlayerRegisterResponseDTO registration(PlayerRegisterRequestDTO playerDTO) {
        Player player = new Player(playerDTO.getUsername(), passwordEncoder.encode(playerDTO.getPassword()),
                playerDTO.getEmail());
        Kingdom kingdom = new Kingdom(playerDTO.getKingdomName(), player);

        // because of CascadeType in player model, here we don't need to save all items particularly

        // ******** Create building for Kingdom ***********

        //Coordinates set behind the map
        Location location = new Location(-1, -1);
        kingdom.setLocation(location);

        //   !!!!   Here we need to add price

        Building townHall = buildingFactory.createBuilding(BuildingType.TOWN_HALL);
        Building treasury = buildingFactory.createBuilding(BuildingType.TREASURY);
        Building granary = buildingFactory.createBuilding(BuildingType.GRANARY);
        Building farm = buildingFactory.createBuilding(BuildingType.FARM);
        Building mine = buildingFactory.createBuilding(BuildingType.MINE);
        kingdom.addBuilding(townHall);
        townHall.setKingdom(kingdom);
        kingdom.addBuilding(treasury);
        treasury.setKingdom(kingdom);
        kingdom.addBuilding(granary);
        granary.setKingdom(kingdom);
        kingdom.addBuilding(farm);
        farm.setKingdom(kingdom);
        kingdom.addBuilding(mine);
        mine.setKingdom(kingdom);

        // ******** Create resources for Kingdom ***********

        List<Resource> resourceList = new ArrayList<>();

        //Here we have to decide amount of resources

        Resource food = new Resource(ResourceType.FOOD, 5000, kingdom, 0, 0);
        Resource gold = new Resource(ResourceType.GOLD, 5000, kingdom, 0, 0);


        resourceList.add(food);
        resourceList.add(gold);
        kingdom.setResourceList(resourceList);

        // And finally here we  create Kingdom and player and save player to database
        player.addKingdom(kingdom);
        playerRepository.save(player);

        // ************ for the end I created DTO as a response for endpoint **********

        //this will send an email with a verification code
        emailService.sendVerificationEmail(player);

        PlayerRegisterResponseDTO playerRegistrationDTO =
                new PlayerRegisterResponseDTO(player.getUserName(), kingdom.getId());
        return playerRegistrationDTO;
    }

    @Override
    public Boolean validation(Long kingdomId) {

        //I decided to use the simplest way to get JWT token. when you call the authentication method,
        // JwtFilter automatically sets the JWT token as a string to CURRENT_TOKEN
        // which we will use for authentication via retrofit... not really safe, I know

        String token = "Bearer " + CURRENT_TOKEN;
        List<PlayerAuthDTO> playerAuthDTOList = new ArrayList<>();
        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        PlayerRetrofitService playerService = retrofit.create(PlayerRetrofitService.class);
        Call<List<PlayerAuthDTO>> call = playerService.getPlayer(token);

        try {
            Response<List<PlayerAuthDTO>> a = call.execute();
            playerAuthDTOList = new ArrayList<>(a.body());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // here is the point of validation.
        // When you use validation, you set kingdom id of which kingdom do you want to check
        // and this will tell you, if your player is kingdom id owner or not as true or false.
        // So if validation is true, you can add whole logic on it and if not, you simply deny access.

        for (PlayerAuthDTO a : playerAuthDTOList) {
            if (a.getKingdomId().equals(kingdomId)) {
                return true;
            }
        }
        return false;
    }

}


