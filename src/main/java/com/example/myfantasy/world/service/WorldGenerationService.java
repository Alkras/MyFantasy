package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class WorldGenerationService {

    private final MonsterGenerationService monsterGenerationService;

    private final LocationService locationService;

    private final MonsterService monsterService;

    public Location generateLocation(LocationBiome originalLocationBiome, LocationKey destinationLocationKey) {
        LocationType locationType = generateRandomLocationType();
        LocationBiome locationBiome = generateLocationBiome(originalLocationBiome);
        Location.LocationBuilder locationBuilder = Location.builder()
                .x(destinationLocationKey.getX())
                .y(destinationLocationKey.getY())
                .locationBiome(locationBiome)
                .locationType(locationType);

        int threatLevel = calculateThreatLevel(destinationLocationKey);
        Optional<Monster> optionalMonster = monsterGenerationService.generateMonster(locationType, locationBiome, threatLevel);

        Location newLocation = locationService.saveLocation(locationBuilder.build());
        if (optionalMonster.isPresent()) {
            newLocation.setLocationThreatLevel(threatLevel);
            Monster monster = optionalMonster.get();
            monster.setCurrentLocation(newLocation);
            monsterService.saveMonster(monster);
        }
        return newLocation;
    }

    private static int calculateThreatLevel(LocationKey destinationLocationKey) {
        return Long.valueOf(
                        Math.min(
                                ((Math.abs(destinationLocationKey.getX()) + Math.abs(destinationLocationKey.getY())) / 10) + 1,
                                Integer.MAX_VALUE))
                .intValue();
    }

    private LocationType generateRandomLocationType() {
        return LocationType.getRandomWeightedLocationType();
    }

    private LocationBiome generateLocationBiome(LocationBiome locationBiome) {
        if (new Random().nextInt(2) == 0) {
            return locationBiome;
        }
        LocationBiome[] values = LocationBiome.values();
        int i = new Random().nextInt(values.length);
        return values[i];
    }
}