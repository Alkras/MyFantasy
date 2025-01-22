package com.example.myfantasy.world.controller;


import com.example.myfantasy.character.Constants;
import com.example.myfantasy.character.exceptions.NoCharacterException;
import com.example.myfantasy.character.exceptions.ShopKeeperException;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.request.NavigateRequest;
import com.example.myfantasy.world.service.NavigationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/navigate")
public class NavigationController {

    private final NavigationService navigationService;

    public NavigationController(NavigationService navigationService) {
        this.navigationService = navigationService;
    }

    @PostMapping()
    public Location navigate(@RequestBody NavigateRequest navigateRequest) throws NoCharacterException {
        if (Constants.SHOPKEEPER_ID.equals(navigateRequest.getHeroId())) {
            throw new ShopKeeperException("You cannot play as shopkeeper :)");
        }
        return navigationService.move(navigateRequest);
    }
}