package com.example.myfantasy.character.service;

import com.example.myfantasy.character.exceptions.NoCharacterException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.repository.CharactersRepository;
import com.example.myfantasy.world.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CharacterService {

    private final CharactersRepository charactersRepository;
    private final LocationService locationService;

    public Character createHero(Character character) {
        character.setCurrentLocation(locationService.getStartingLocation());
        character.setLevel(1);
        character.setMoney(BigDecimal.ZERO);
        character.setAgility(10);
        character.setArmor(10);
        character.setAttack(10);
        character.setHitPoints(300);
        character.setExperience(0);
        character.setExperienceForLevelUp(100);
        return saveHero(character);
    }

    public Character saveHero(Character character) {
        return charactersRepository.save(character);
    }

    public Character getCharacterById(Long id) {
        return charactersRepository.findById(id).orElseThrow(NoCharacterException::new);
    }

    public void removeHeroById(Long id) {
        charactersRepository.deleteById(id);
    }

    public List<Character> getAllHeroes() {
        return charactersRepository.findAll();
    }

    public Character characterLevelUp(Character character, int experience) {
        while (character.getExperienceForLevelUp() - character.getExperience() <= experience) {
            experience = experience - (character.getExperienceForLevelUp() - character.getExperience());
            character.setLevel(character.getLevel() + 1);
            character.setMaxHitPoints(character.getMaxHitPoints() + 20);
            character.setAgility(character.getAgility() + 1);
            character.setAttack(character.getAttack() + 1);
            character.setArmor(character.getArmor() + 1);
            character.setExperience(0);
            character.setExperienceForLevelUp(character.getExperienceForLevelUp() + 50);
            character.setHitPoints(character.getMaxHitPoints());
        }
        character.setExperience(experience);
        return character;
    }
}