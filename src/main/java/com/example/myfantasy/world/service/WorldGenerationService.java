package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.*;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class WorldGenerationService {

    public Location generateLocation(Location location, LocationKey destinationLocationKey) {
        return Location.builder()
                .x(destinationLocationKey.getX())
                .y(destinationLocationKey.getY())
                .locationBiome(generateLocationBiome(location.getLocationBiome()))
                .locationType(generateRandomLocationType())
                .build();
    }

    private LocationType generateRandomLocationType() {
        LocationType[] values = LocationType.values();
        int i = new Random().nextInt(values.length);
        return values[i];
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