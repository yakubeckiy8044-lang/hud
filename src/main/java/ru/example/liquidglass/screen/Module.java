package ru.example.liquidglass.screen;

import java.util.function.Consumer;

public final class Module {
    private final String name;
    private final Category category;
    private final Consumer<Boolean> stateListener;
    private boolean enabled;

    public Module(String name, Category category) { this(name, category, null); }
    public Module(String name, Category category, Consumer<Boolean> stateListener) {
        this.name = name;
        this.category = category;
        this.stateListener = stateListener;
    }
    public String name() { return name; }
    public Category category() { return category; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (stateListener != null) stateListener.accept(enabled);
    }
    public void toggle() { setEnabled(!enabled); }
}
