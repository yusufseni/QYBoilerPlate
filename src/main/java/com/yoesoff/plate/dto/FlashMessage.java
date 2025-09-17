package com.yoesoff.plate.dto;

/**
 * FlashMessage is used to represent temporary messages that can be displayed to users.
 * It contains a type (INFO, WARNING, DANGER, SUCCESS) and the message text.
 * Quarkus Qute does not support Java record properties as bean-style getters (like getType()).
 * Qute expects standard getter methods or public fields
 */
public class FlashMessage {
    private final Type type;
    private final String text;

    public FlashMessage(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public enum Type {INFO, WARNING, DANGER, SUCCESS}
}
