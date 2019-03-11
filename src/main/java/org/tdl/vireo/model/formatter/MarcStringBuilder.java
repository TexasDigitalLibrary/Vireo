package org.tdl.vireo.model.formatter;

public class MarcStringBuilder {
    private StringBuilder stringBuilder;

    public MarcStringBuilder() {
        stringBuilder = new StringBuilder();
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public void append(String string) {
        stringBuilder.append(string);
    }

    public int length() {
        return stringBuilder.length();
    }

}