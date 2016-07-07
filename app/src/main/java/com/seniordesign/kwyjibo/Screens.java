package com.seniordesign.kwyjibo;

import java.util.HashMap;
import java.util.Map;

// Sorry for making an annoying looking enum. I added the extra code so that the type names and
// strings used to persist them in SharedPreferences are portable across release builds.
public enum Screens {
    INTRO_TITLE("intro_title"), LOGIN("login"), SIGNUP("signup"), MODE_SELECTION("mode_selection"),
    RECORD_MODE("record_mode"), STATION_SELECTION("station_selection"), STUDIO_MODE("studio_mode"),
    RADIO_STATION("current_station"), CURRENT_SCREEN("current_screen");

    private final String code;
    private static final Map<String,Screens> valuesByCode;

    static {
        valuesByCode = new HashMap<>();
        for(Screens screen : Screens.values()) {
            valuesByCode.put(screen.code, screen);
        }
    }

    private Screens(String code) {
        this.code = code;
    }

    public static Screens lookupByCode(String code) {
        return valuesByCode.get(code);
    }

    public String getCode() {
        return code;
    }
}
