package com.example.myfantasy.character.repository;

import com.example.myfantasy.character.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByCharacterId(Long characterId);

}
