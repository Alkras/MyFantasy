package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.*;
import com.example.myfantasy.world.repository.LocationsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {

    private final LocationsRepository locationsRepository;

    public LocationService(LocationsRepository locationsRepository) {
        this.locationsRepository = locationsRepository;
    }

    public Location getStartingLocation() {
        return locationsRepository.findById(new LocationKey(0L, 0L)).orElseGet(this::createStartingLocation);
    }

    public Optional<Location> getLocationById(LocationKey locationKey) {
        return locationsRepository.findById(locationKey);
    }

    public Location saveLocation(Location location) {
        return locationsRepository.save(location);
    }

    private Location createStartingLocation() {
        Location initialLocation = Location.builder()
                .x(0L)
                .y(0L)
                .locationType(LocationType.EMPTY)
                .locationBiome(LocationBiome.VILLAGE)
                .locationThreatLevel(0)
                .build();
        return saveLocation(initialLocation);
    }
}
