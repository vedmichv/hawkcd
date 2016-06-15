package net.hawkengine.model;

public class EnvironmentVariable {
    private String name;
    private String value;
    private boolean isSecured;

    public EnvironmentVariable(String name, String value, boolean isSecured) {
        this(name, value);
        this.setSecured(isSecured);
    }

    public EnvironmentVariable(String name, String value) {
        this.setName(name);
        this.setValue(value);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSecured() {
        return this.isSecured;
    }

    public void setSecured(boolean value) {
        this.isSecured = value;
    }
}
