package com.yoesoff.plate.dto;

public class FlashMessage {
    public enum Type { INFO, WARNING, DANGER, SUCCESS }

    public final Type type;
    public final String text;

    public FlashMessage(Type type, String text) {
        this.type = type;
        this.text = text;
    }
}