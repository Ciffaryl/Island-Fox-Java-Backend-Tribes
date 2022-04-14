package com.greenfoxacademy.islandfoxtribes.services.player;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerRegisterRequestDTO;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserDetailsServiceForJwt implements UserDetailsService {

    private final PlayerRepository playerRepository;
    private final PlayerServiceImpl playerService;

    @Autowired
    public UserDetailsServiceForJwt(PlayerRepository playerRepository, PlayerServiceImpl playerService) {
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findPlayerByUserName(username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        return new User(player.getUserName(), player.getPassword(), authorities);
    }

    public void processOAuthPostLogin(String username) {
        Optional<Player> existUser = playerRepository.findByUserName(username);
        if (existUser.isEmpty()) {
            PlayerRegisterRequestDTO playerForRegistration = new PlayerRegisterRequestDTO(
                    username, "temporal", username, "NameNeedsToBeChanged");
            playerService.registration(playerForRegistration);
        }
    }
}
