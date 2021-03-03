package com.hendrik.http.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class representing HTTP Header
 * This class is used as generic header for both requests and responses
 * 
 * @author Hendrik Tjabben
 */
public class Header {

    /**
     * All header lines
     */
    private Map<HeaderFields.Field, List<String>> headerEntries;

    public Header() {
        this.headerEntries = new HashMap<HeaderFields.Field, List<String>>();
    }

    /**
     * Add an header entry in key value type
     * 
     * @param field The key field type
     * @param value The field type's value
     */
    public void addEntry(final HeaderFields.Field field, final String value) {

        if (field != null) {

            if (this.headerEntries.containsKey(field)) {
                List<String> currentEntries = this.headerEntries.get(field);
                currentEntries.add(value);
                this.headerEntries.put(field, currentEntries);
            } else {
                List<String> newEntryList = new ArrayList<String>();
                newEntryList.add(value);
                this.headerEntries.put(field, newEntryList);
            }

        }
    }

    /**
     * Parses a string line.
     * When the line represents a supported header field, it is added
     * @param line The line to parse
     * @return True if the line was added, false otherwise
     */
    public boolean addEntryWhenSupported(final String line) {

        // String lineNoSpaces = line.replaceAll(" ", "");

        String[] keyValue = line.split(":", 2);

        if (keyValue.length != 2) {
            return false;
        }

        HeaderFields.Field field = HeaderFields.getFieldForString(keyValue[0]);
        if (field == null) {
            return false;
        }

        String valueLine = keyValue[1].trim();

        if (!HeaderFields.isDateField(field)) {
            valueLine = valueLine.replaceAll(" ", "");
        }

        List<String> newEntryList = new ArrayList<String>();
        if (HeaderFields.allowsMultipleValues(field)) {
            String[] valueList = valueLine.split(",");

            for (String value : valueList) {
                newEntryList.add(value);
            }
        } else {
            newEntryList.add(valueLine);
        }

        this.headerEntries.put(field, newEntryList);
        return true;
    }
    
    /**
     * Get the first value for a specific header field
     * 
     * @param field The header entry to get the value for
     * @return An optional containing the first value for the requested field. Empty if field not set in header
     */
    public Optional<String> getValue(final HeaderFields.Field field) {
        if (!this.headerEntries.containsKey(field)) {
            return Optional.empty();
        }

        return Optional.of(this.headerEntries.get(field).get(0));
    }

    /**
     * Get all values for a specific header field
     * 
     * @param field The header entry to get the values for
     * @return An optional containing all values for the requested field. Empty if field not set in header
     */
    public Optional<List<String>> getValues(final HeaderFields.Field field) {
        if (!this.headerEntries.containsKey(field)) {
            return Optional.empty();
        }

        return Optional.of(this.headerEntries.get(field));
    }

    /**
     * Get an entire header line containing of a field key and the value
     * 
     * @param field The field key to get the header line for
     * @return The header line in an optional. Empty if field not set in header
     */
    public Optional<String> getLine(final HeaderFields.Field field) {
        Optional<List<String>> values = getValues(field);

        if (!values.isPresent()) {
            return Optional.empty();
        }

        String flattenedLine = String.join(",", values.get());

        StringBuilder entryBuilder = new StringBuilder()
            .append(HeaderFields.toString(field))
            .append(": ")
            .append(flattenedLine);

        return Optional.of(entryBuilder.toString());
    }

    /**
     * Get all lines for this header
     * 
     * @return All lines that are set in this header
     */
    public List<String> getLines() {
        List<String> result = new ArrayList<String>();

        for (Map.Entry<HeaderFields.Field, List<String>> entry : this.headerEntries.entrySet()) {
            result.add(getLine(entry.getKey()).get());
        }

        return result;
    }

}