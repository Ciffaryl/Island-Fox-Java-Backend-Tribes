package com.greenfoxacademy.islandfoxtribes.models.kingdom.kingdomDTO;

import com.greenfoxacademy.islandfoxtribes.models.resource.resourceDTOs.ResourceResponseDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// Response DTO for kingdoms/{id}/resources endpoint

@Data
public class KingdomResourceResponseDTO {

    private KingdomForListDTO kingdom;
    private List<ResourceResponseDTO> resources;

    public KingdomResourceResponseDTO(KingdomForListDTO kingdom) {
        this.kingdom = kingdom;
        this.resources = new ArrayList<>();
    }

    public void addResource(ResourceResponseDTO responseDTO) {
        resources.add(responseDTO);
    }
}
