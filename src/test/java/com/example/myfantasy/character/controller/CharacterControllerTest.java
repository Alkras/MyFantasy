package com.example.myfantasy.character.controller;

import com.example.myfantasy.character.model.Character;
import com.example.myfantasy.character.model.Type;
import com.example.myfantasy.character.model.request.CreateCharacterRequest;
import com.example.myfantasy.character.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CharacterService characterService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void getHero() throws Exception {
        Character toBeReturned = new Character();
        toBeReturned.setId(2L);
        doReturn(toBeReturned)
                .when(characterService)
                .getCharacterById(1L);

        mockMvc.perform(get("/characters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        verify(characterService).getCharacterById(1L);
    }

    @Test
    void getHeroShouldThrowError() throws Exception {
        mockMvc.perform(get("/characters/0"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("You cannot play as shopkeeper :)"));
    }

    @Test
    void getAllHeroes() throws Exception {
        doReturn(List.of(new Character(), new Character()))
                .when(characterService)
                .getAllHeroes();

        mockMvc.perform(get("/characters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(characterService).getAllHeroes();
    }


    @Test
    void createHero() throws Exception {
        CreateCharacterRequest createCharacterRequest = new CreateCharacterRequest("name", Type.WARRIOR);
        Character toBeReturned = new Character();
        toBeReturned.setId(2L);
        doReturn(toBeReturned)
                .when(characterService)
                .createHeroWithDefaults(createCharacterRequest);

        mockMvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharacterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));

        verify(characterService).createHeroWithDefaults(createCharacterRequest);
    }

    @Test
    void createHeroShouldThrowError() throws Exception {
        CreateCharacterRequest createCharacterRequest = new CreateCharacterRequest("name", Type.SHOPKEEPER);
        mockMvc.perform(post("/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharacterRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").value("You cannot play as shopkeeper :)"));
    }
}