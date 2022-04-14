package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import lombok.Data;

@Data
public class ResourceDTO {

    private String type;
    private Integer amount;
    private Integer generation;
    private Integer updatedAt;

    public ResourceDTO(Resource resource) {
        this.type = resource.getResourceType().label;
        this.amount = resource.getAmount();
        this.generation = resource.getGeneration();
        this.updatedAt = resource.getUpdatedAt();
    }
}
