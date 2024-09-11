package org.apache.http.header;

import java.util.Objects;

public class HttpHeader {
    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final int KEY_VALUE_COUNT = 2;
    private static final int KEY_ORDER = 0;
    private static final int VALUE_ORDER = 1;

    private final String key;
    private final String value;

    public HttpHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static HttpHeader from(String entry) {
        String[] keyAndValue = entry.split(KEY_VALUE_DELIMITER, KEY_VALUE_COUNT);
        return new HttpHeader(keyAndValue[KEY_ORDER], keyAndValue[VALUE_ORDER]);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + KEY_VALUE_DELIMITER + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeader that = (HttpHeader) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
