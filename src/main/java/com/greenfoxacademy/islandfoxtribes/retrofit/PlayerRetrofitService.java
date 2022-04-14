package com.greenfoxacademy.islandfoxtribes.retrofit;

import com.greenfoxacademy.islandfoxtribes.models.player.playerDTOs.PlayerAuthDTO;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.util.List;

public interface PlayerRetrofitService {

    @POST("auth")
    Call<List<PlayerAuthDTO>> getPlayer(@Header("Authorization") String token);

}
