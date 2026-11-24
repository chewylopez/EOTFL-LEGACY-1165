package com.chewylopez.echoofthefarlands.client;

import com.chewylopez.echoofthefarlands.Config;

import java.util.ArrayList;
import java.util.List;

public class PresetLists {

    public static MenuOptionsGrouping[] buildDistances() {
        int configValue = Config.FARLANDS_LOCATION_CUSTOM.get();
        return new MenuOptionsGrouping[]{
                new MenuOptionsGrouping("1000000", 1000000, "default value"),
                new MenuOptionsGrouping("10000", 10000, ""),
                new MenuOptionsGrouping("50000", 50000, ""),
                new MenuOptionsGrouping("100000", 100000, ""),
                new MenuOptionsGrouping("500000", 500000, ""),
                new MenuOptionsGrouping("12550823", 12550823, "original farlands"),
                new MenuOptionsGrouping("Config Value (" + configValue + ")", configValue,
                        "can be any positive integer, keep in mind that low values will break proportional scalings and high values beyond the world barrier will not be accessible.\n\n-NOTE- value below world height will generate Y-axis Farlands")};
    }

    public static MenuOptionsGrouping[] buildGenTypes() {
        return new MenuOptionsGrouping[]{
                new MenuOptionsGrouping("Default Piecewise (static)", 0,
                        "(default) Piecewise function:\n0-500k (unscaled)\n500k-1m (linear)\n1m-1.5m (exponential)\n1.5m-4m (moat)\n4m+ (wrapped vanilla gen)"),
                new MenuOptionsGrouping("Default Piecewise (proportional)", 1,
                        "Piecewise function:\nscaled to Farlands location\n(1m is same as static)"),
                new MenuOptionsGrouping("Unscaled", 2,
                        "Unscaled noise value with no vanilla wrapping"),
                new MenuOptionsGrouping("Exponential (static)", 3,
                        "Noise value = INT_MAX^factor\nfactor = 1+(2*(distance/1m)\n\n-NOTE- x and z behave differently for this one, not really sure why"),
                new MenuOptionsGrouping("Exponential (proportional)", 4,
                        "Exponential function:\nscaled to Farlands location\n(1m is same as static)"),
                new MenuOptionsGrouping("Linear", 5,
                        "Noise value = value * 10^15\nproportional to wall distance with sheer ending wall at:\ndistance * 1344.45"),
                new MenuOptionsGrouping("Sine Wave", 6,
                        "Noise value = \n2*sin(value)*(value + INT_MAX)")
        };
    }

    public static String[] getTextFromGroupArray(MenuOptionsGrouping[] items) {
        String[] returnable = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            returnable[i] = items[i].getText();
        }
        return returnable;
    }

    public static int getIntsFromButton(String value, MenuOptionsGrouping[] items) {
        int returnable = 0;
        for (MenuOptionsGrouping item : items) {
            if (item.getText().equals(value)) {
                returnable = item.getValue();
            }
        }
        return returnable;
    }

    public static String getTooltipsFromGroupArray(String value, MenuOptionsGrouping[] items) {
        for (MenuOptionsGrouping p : items) {
            if (p.getText().equals(value)) {
                return p.getTooltip();
            }
        }
        return "";
    }

    public static String getTextFromIntValue(int value, MenuOptionsGrouping[] items) {
        for (MenuOptionsGrouping p : items) {
            if (p.getValue() == value) {
                return p.getText();
            }
        }
        return items.length > 0 ? items[0].getText() : "";
    }

    public static int getIndexFromIntValue(int value, MenuOptionsGrouping[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].getValue() == value) {
                return i;
            }
        }
        return 0;
    }

    public static List<String> splitTooltip(String tooltip) {
        List<String> lines = new ArrayList<>();
        if (tooltip == null || tooltip.isEmpty()) {
            return lines;
        }
        for (String line : tooltip.split("\n", -1)) {
            lines.add(line);
        }
        return lines;
    }
}
