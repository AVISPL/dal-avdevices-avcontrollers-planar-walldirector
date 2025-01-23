/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums;

import java.util.Arrays;

/**
 * Enum representing cabinet input options.
 * Each constant has a corresponding string name.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum CabinetInputEnum {
    AUTO("AUTO"),
    HDMI1("HDMI1"),
    HDMI2("HDMI2"),
            ;

    private final String name;

    /**
     * Constructor for CabinetInputEnum.
     *
     * @param name the string representation of the cabinet input option
     */
    CabinetInputEnum(String name) {
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
                .map(CabinetInputEnum::getName)
                .toArray(String[]::new);
    }
}