package com.example.myfantasy.character.exceptions;

public class NoCharacterException extends RuntimeException {
    public NoCharacterException() {
    }

    public NoCharacterException(String message) {
        super(message);
    }
}
