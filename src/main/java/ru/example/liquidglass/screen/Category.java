package ru.example.liquidglass.screen;

public enum Category {
    VISUALS("VISUALS"), PLAYER("PLAYER"), MISC("MISC");
    private final String title;
    Category(String title) { this.title = title; }
    public String title() { return title; }
}