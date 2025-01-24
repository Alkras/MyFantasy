package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.exceptions.CharacterNotFoundException;
import com.example.myfantasy.character.exceptions.ShopKeeperException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Type;
import com.example.myfantasy.character.model.request.CreateCharacterRequest;
import com.example.myfantasy.character.service.CharacterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/{id}")
    public Character getHero(@PathVariable Long id) throws CharacterNotFoundException {
        if (id.equals(0L)) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return characterService.getCharacterById(id);
    }

    @GetMapping()
    public List<Character> getAllHeroes() {
        return characterService.getAllHeroes();
    }

    @PostMapping()
    public Character createHero(@RequestBody CreateCharacterRequest createCharacterRequest) {
        if (Type.SHOPKEEPER.equals(createCharacterRequest.getType())) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return characterService.createHeroWithDefaults(createCharacterRequest);
    }
}