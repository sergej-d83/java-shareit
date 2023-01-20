package ru.practicum.shareit.exception;

public class UserNotOwnItem extends RuntimeException {

    public UserNotOwnItem(String message) {
        super(message);
    }
}
