package com.example.myfantasy.character.repository;

import com.example.myfantasy.character.model.ItemTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemTemplatesRepository extends JpaRepository<ItemTemplate, String> {
}
