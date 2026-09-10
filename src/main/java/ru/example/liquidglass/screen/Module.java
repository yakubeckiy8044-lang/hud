package ru.example.liquidglass.screen;

import java.util.function.Consumer;

public final class Module {
    private final String name;
    private final Category category;
    private final Consumer<Boolean> stateListener;
    private final String marker;
    private boolean enabled;

    public Module(String name, Category category) { this(name, category, null, "..."); }
    public Module(String name, Category category, String marker) { this(name, category, null, marker); }
    public Module(String name, Category category, Consumer<Boolean> stateListener) {
        this(name, category, stateListener, "...");
    }
    public Module(String name, Category category, Consumer<Boolean> stateListener, String marker) {
        this.name = name;
        this.category = category;
        this.stateListener = stateListener;
        this.marker = marker;
    }
    public String name() { return name; }
    public Category category() { return category; }
    public String marker() { return marker; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (stateListener != null) stateListener.accept(enabled);
    }
    public void toggle() { setEnabled(!enabled); }
}
