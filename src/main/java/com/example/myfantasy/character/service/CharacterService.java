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

    private static final int EXPERIENCE_FOR_LEVEL_UP_DEFAULT = 100;
    private static final int MAX_HIT_POINTS_DEFAULT = 300;
    private static final int STATS_DEFAULT = 10;
    private static final int BASE_LEVEL = 1;
    private final CharactersRepository charactersRepository;
    private final LocationService locationService;

    public Character createHeroWithDefaults(Character character) {
        character.setCurrentLocation(locationService.getStartingLocation());
        character.setLevel(BASE_LEVEL);
        character.setMoney(BigDecimal.ZERO);
        character.setAgility(STATS_DEFAULT);
        character.setArmor(STATS_DEFAULT);
        character.setAttack(STATS_DEFAULT);
        character.setHitPoints(MAX_HIT_POINTS_DEFAULT);
        character.setMaxHitPoints(MAX_HIT_POINTS_DEFAULT);
        character.setExperienceForLevelUp(EXPERIENCE_FOR_LEVEL_UP_DEFAULT);
        return saveHero(character);
    }

    public Character saveHero(Character character) {
        return charactersRepository.save(character);
    }

    public Character getCharacterById(Long id) {
        return charactersRepository.findById(id).orElseThrow(() -> new NoCharacterException(STR."Character with id:\{id} does not exist"));
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