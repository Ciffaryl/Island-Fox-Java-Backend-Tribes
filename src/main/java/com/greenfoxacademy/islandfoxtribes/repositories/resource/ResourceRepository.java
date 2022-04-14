package com.greenfoxacademy.islandfoxtribes.repositories.resource;

import com.greenfoxacademy.islandfoxtribes.models.resource.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("from Resource where kingdom.id = ?1")
    List<Resource> findAllByKingdomId(long id);

}
