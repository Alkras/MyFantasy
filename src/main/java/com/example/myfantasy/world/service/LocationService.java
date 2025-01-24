package com.example.myfantasy.world.service;

import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationBiome;
import com.example.myfantasy.world.model.LocationKey;
import com.example.myfantasy.world.model.LocationType;
import com.example.myfantasy.world.repository.LocationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LocationService {

    private final LocationsRepository locationsRepository;

    public Location getStartingLocation() {
        return getLocationById(new LocationKey(0L, 0L)).orElseGet(this::createStartingLocation);
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
