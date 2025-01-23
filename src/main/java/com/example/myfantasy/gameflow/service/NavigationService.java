package com.example.myfantasy.gameflow.service;

import com.example.myfantasy.character.exceptions.NoCharacterException;
import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.service.CharacterService;
import com.example.myfantasy.world.model.Direction;
import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationKey;
import com.example.myfantasy.world.model.request.NavigateRequest;
import com.example.myfantasy.world.service.LocationService;
import com.example.myfantasy.world.service.WorldGenerationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class NavigationService {
    private final WorldGenerationService worldGenerationService;

    private final CharacterService characterService;

    private final LocationService locationService;

    private final ConcurrentHashMap<LocationKey, Lock> tileGenerationLockMap;

    public NavigationService(WorldGenerationService worldGenerationService, CharacterService characterService, LocationService locationService) {
        this.worldGenerationService = worldGenerationService;
        this.characterService = characterService;
        this.locationService = locationService;
        this.tileGenerationLockMap = new ConcurrentHashMap<>();
    }

    @Transactional
    public Location move(NavigateRequest navigateRequest) throws NoCharacterException {
        Character character = characterService.getCharacterById(navigateRequest.getHeroId());
        Location destination = revealLocation(character.getCurrentLocation(), navigateRequest.getDirection());
        character.setCurrentLocation(destination);
        characterService.saveHero(character);
        return destination;
    }

    private Location revealLocation(Location currentLocation, Direction direction) {
        LocationKey destinationLocationKey = getDestinationLocationKey(currentLocation, direction);
        Lock tileGenerationLock = tileGenerationLockMap.computeIfAbsent(destinationLocationKey, _ -> new ReentrantLock());
        tileGenerationLock.lock();
        try {
            return locationService.getLocationById(destinationLocationKey)
                    .orElseGet(() -> worldGenerationService.generateLocation(currentLocation, destinationLocationKey));
        } finally {
            tileGenerationLock.unlock();
            tileGenerationLockMap.remove(destinationLocationKey);
        }
    }

    public LocationKey getDestinationLocationKey(Location location, Direction direction) {
        return switch (direction) {
            case NORTH -> new LocationKey(location.getX(), location.getY() + 1);
            case SOUTH -> new LocationKey(location.getX(), location.getY() - 1);
            case EAST -> new LocationKey(location.getX() + 1, location.getY());
            case WEST -> new LocationKey(location.getX() - 1, location.getY());
        };
    }
}
