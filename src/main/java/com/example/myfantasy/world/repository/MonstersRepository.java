package com.example.myfantasy.world.repository;

import com.example.myfantasy.world.model.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonstersRepository extends JpaRepository<Monster, Long> {

    Optional<Monster> findByCurrentLocationXAndCurrentLocationY(Long x, Long y);
}
