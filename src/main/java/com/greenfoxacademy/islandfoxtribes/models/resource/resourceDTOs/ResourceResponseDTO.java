package com.greenfoxacademy.islandfoxtribes.models.resource.resourceDTOs;


import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import lombok.Data;

@Data
public class ResourceResponseDTO {

    private String type;
    private int amount;
    private int generation;
    private int updatedAt;

    public ResourceResponseDTO(Resource resource) {
        this.type = resource.getResourceType().label;
        this.amount = resource.getAmount();
        this.generation = resource.getGeneration();
        this.updatedAt = resource.getUpdatedAt();
    }
}
