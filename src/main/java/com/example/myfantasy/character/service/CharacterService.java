package com.example.myfantasy.character.service;

import com.example.myfantasy.character.exceptions.NoCharacterException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.repository.CharactersRepository;
import com.example.myfantasy.world.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService {

    private final CharactersRepository charactersRepository;
    private final LocationService locationService;

    public CharacterService(CharactersRepository charactersRepository, LocationService locationService) {
        this.charactersRepository = charactersRepository;
        this.locationService = locationService;
    }

    public Character createHero(Character character) {
        character.setCurrentLocation(locationService.getStartingLocation());
        character.setLevel(1);
        return saveHero(character);
    }

    public Character saveHero(Character character) {
        return charactersRepository.save(character);
    }

    public Character getCharacterById(Long id) {
            return charactersRepository.findById(id).orElseThrow(NoCharacterException::new);
    }

    public List<Character> getAllHeroes() {
        return charactersRepository.findAll();
    }
}