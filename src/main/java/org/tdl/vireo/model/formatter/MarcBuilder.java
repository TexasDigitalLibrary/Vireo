package org.tdl.vireo.model.formatter;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class MarcBuilder {
    private String leader;
    private StringBuilder directoryBuilder;
    private StringBuilder fieldBuilder;

    public MarcBuilder() {
        directoryBuilder = new StringBuilder();
        fieldBuilder = new StringBuilder();
    }

    public void addField(String tag, String field) {
        Pattern unicodePattern = Pattern.compile("\\\\u([0-9A-Z]{4})");
        Matcher matches = unicodePattern.matcher(field);
        StringBuffer buffer = new StringBuffer();
        while (matches.find()) {
            matches.appendReplacement(buffer, new String(Character.toChars(Integer.parseInt(matches.group(1), 16))));
        }

        String lString = pad(String.valueOf(buffer.length()), 4);
        String rString = pad(String.valueOf(fieldBuilder.length()), 5);
        directoryBuilder.append(tag + lString + rString);
        fieldBuilder.append(buffer.toString());
    }

    public String toString() {
        String text = leader + directoryBuilder.toString() + fieldBuilder.toString() + "\u001E";
        text = text.replaceAll("RLXXX", pad(String.valueOf(text.length()), 5));
        text = text.replaceAll("BADXX", pad(String.valueOf((leader + directoryBuilder.toString()).length()), 5));
        return text;
    }

    private String pad(String string, int length) {
        return StringUtils.leftPad(string, length, "0");
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }
}