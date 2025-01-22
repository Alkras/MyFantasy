package com.example.myfantasy.world.repository;

import com.example.myfantasy.world.model.Location;
import com.example.myfantasy.world.model.LocationKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationsRepository extends JpaRepository<Location, LocationKey> {
}