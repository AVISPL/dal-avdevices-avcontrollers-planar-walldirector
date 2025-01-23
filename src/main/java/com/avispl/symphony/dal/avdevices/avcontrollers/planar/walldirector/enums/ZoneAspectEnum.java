/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums;

import java.util.Arrays;

/**
 * Enum representing different zone aspect modes.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum ZoneAspectEnum {
    FILL("FILL"),
    CROP("CROP"),
    PARAM_16X9("16X9"),
    PARAM_4X3("4X3"),
    NATIVE("NATIVE"),
    AUTO("AUTO"),
            ;

    private final String name;

    /**
     * Constructor for ZoneAspectEnum.
     *
     * @param name the name of the zone aspect mode
     */
    ZoneAspectEnum(String name) {
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
                .map(ZoneAspectEnum::getName)
                .toArray(String[]::new);
    }
}