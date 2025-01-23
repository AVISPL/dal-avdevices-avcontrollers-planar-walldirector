/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums;

import java.util.Arrays;

/**
 * Enum representing various color temperature options.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum ColorTemperatureEnum {
    PARAM_1("3200K"),
    PARAM_2("5500K"),
    PARAM_3("6500K"),
    PARAM_4("8500K"),
    PARAM_5("9300K"),
    NATIVE("NATIVE"),
    CUSTOM("CUSTOM"),
            ;

    private final String name;

    /**
     * Constructor for ColorTemperatureEnum.
     *
     * @param name the string representation of the color temperature
     */
    ColorTemperatureEnum(String name) {
        this.name = name;
    }

    /**
     * Retrieves {@link #name}
     *
     * @return value of {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Get an array of all enum values as strings.
     *
     * @return String[] containing all enum names.
     */
        public static String[] toArray() {
            return Arrays.stream(values())
                .map(ColorTemperatureEnum::getName)
                .toArray(String[]::new);
    }
}