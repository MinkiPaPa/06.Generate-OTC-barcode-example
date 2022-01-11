package kr.co.nicevan.genotcbarcode.util;

import java.util.HashMap;
import java.util.Map;

public enum  PreferenceKeys {

    serverIP("Server IP"),
    serverPort("Server Port"),
    isCert("Cert Yes or No"),
    serverCert("Server Cert"),
    sessionKey("Session Key"),
    cardId("Card ID");

    private final String value;
    PreferenceKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, PreferenceKeys> map = new HashMap<>();
    static {
        for (PreferenceKeys val : values()) {
            map.put(val.value, val);
        }
    }

    public static PreferenceKeys findKey(String value) {
        return map.get(value);
    }
}
