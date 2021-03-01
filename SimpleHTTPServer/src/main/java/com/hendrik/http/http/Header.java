package com.hendrik.http.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Header {

    /**
     * All header lines
     */
    private Map<HeaderFields.Field, String> headerEntries;

    public Header() {
        this.headerEntries = new HashMap<HeaderFields.Field, String>();
    }

    /**
     * Add an header entry in key value type
     * 
     * @param field The key field type
     * @param value The field type's value
     */
    public void addEntry(final HeaderFields.Field field, final String value) {

        if (field != null) {

            this.headerEntries.put(field, value);
        }
    }

    /**
     * Parses a string line.
     * When the line represents a supported header field, it is added
     * @param line The line to parse
     * @return True if the line was added, false otherwise
     */
    public boolean addEntryWhenSupported(final String line) {

        String lineNoSpaces = line.replaceAll(" ", "");

        String[] keyValue = lineNoSpaces.split(":");

        if (keyValue.length != 2) {
            return false;
        }

        HeaderFields.Field field = HeaderFields.getFieldForString(keyValue[0]);

        if (field == null) {
            return false;
        }

        this.headerEntries.put(field, keyValue[1]);
        return true;
    }
    
    public Optional<String> getValue(final HeaderFields.Field field) {
        if (!this.headerEntries.containsKey(field)) {
            return Optional.empty();
        }

        return Optional.of(this.headerEntries.get(field));
    }

    public Optional<String> getLine(final HeaderFields.Field field) {
        Optional<String> value = getValue(field);

        if (!value.isPresent()) {
            return Optional.empty();
        }

        StringBuilder entryBuilder = new StringBuilder()
            .append(HeaderFields.toString(field))
            .append(": ")
            .append(value.get());

        return Optional.of(entryBuilder.toString());
    }

    public List<String> getLines() {
        List<String> result = new ArrayList<String>();

        for (Map.Entry<HeaderFields.Field, String> entry : this.headerEntries.entrySet()) {
            result.add(getLine(entry.getKey()).get());
        }

        return result;
    }

}