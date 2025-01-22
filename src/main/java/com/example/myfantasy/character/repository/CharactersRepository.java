package com.example.myfantasy.character.repository;

import com.example.myfantasy.character.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharactersRepository extends JpaRepository<Character, Long> {
}