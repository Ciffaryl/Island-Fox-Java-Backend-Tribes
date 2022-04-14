package com.greenfoxacademy.islandfoxtribes.repositories.troop;

import com.greenfoxacademy.islandfoxtribes.models.troop.Troop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TroopRepository extends JpaRepository<Troop, Long> {

    void deleteById(Long id);

    @Query("from Troop where kingdom.id = ?1")
    List<Troop> findAllByKingdomId(long id);

}
