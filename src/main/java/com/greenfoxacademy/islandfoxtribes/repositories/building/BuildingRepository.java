package com.greenfoxacademy.islandfoxtribes.repositories.building;

import com.greenfoxacademy.islandfoxtribes.models.building.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Query("from Building where kingdom.id = ?1")
    List<Building> findAllByKingdomId(long id);

}
