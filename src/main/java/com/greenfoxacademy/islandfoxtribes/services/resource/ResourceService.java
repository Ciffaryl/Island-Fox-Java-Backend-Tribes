package com.greenfoxacademy.islandfoxtribes.services.resource;

import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;

import java.util.List;

public interface ResourceService {

    List<Resource> findAll();

    void foodEaten();

    void foodFarmed();

    void goldMined();

}
