package com.greenfoxacademy.islandfoxtribes.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttackRequestDTO {

    private Long target;
    private String battleType;
    private List<Long> troops;
}
