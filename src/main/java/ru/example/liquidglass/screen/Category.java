package ru.example.liquidglass.screen;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), VISUALS("Visuals"), PLAYER("Player"), MISC("Miscellaneous");
    private final String title;
    Category(String title) { this.title = title; }
    public String title() { return title; }
}
