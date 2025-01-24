package com.example.myfantasy.gameflow.controller;


import com.example.myfantasy.character.Constants;
import com.example.myfantasy.character.exceptions.ShopKeeperException;
import com.example.myfantasy.gameflow.model.FightInfo;
import com.example.myfantasy.gameflow.model.request.FightRequest;
import com.example.myfantasy.gameflow.service.FightService;
import com.example.myfantasy.gameflow.service.NavigationService;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.request.NavigateRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actions")
@AllArgsConstructor
public class ActionController {

    private final NavigationService navigationService;

    private final FightService fightService;

    @PostMapping("/navigate")
    public Location navigate(@RequestBody NavigateRequest navigateRequest) {
        if (Constants.SHOPKEEPER_ID.equals(navigateRequest.getHeroId())) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return navigationService.move(navigateRequest);
    }

    @PostMapping("/fight")
    public FightInfo fight(@RequestParam Long characterId){
        if (Constants.SHOPKEEPER_ID.equals(characterId)) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return fightService.fight(characterId);
    }
}