package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.exceptions.NoCharacterException;
import com.example.myfantasy.character.exceptions.ShopKeeperException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Type;
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
    public Character getHeroes(@PathVariable Long id) throws NoCharacterException {
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
    public Character createHero(@RequestBody Character character) {
        if (Type.SHOPKEEPER.equals(character.getType())) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return characterService.createHeroWithDefaults(character);
    }
}