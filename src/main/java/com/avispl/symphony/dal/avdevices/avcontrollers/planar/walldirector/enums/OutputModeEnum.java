/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums;

import java.util.Arrays;

/**
 * Enum representing different output modes.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum OutputModeEnum {
    PARAM_4K("4K"),
    PARAM_1080P("1080p"),
    AUTO("AUTO"),
    ;

    private final String name;

    /**
     * Constructor for OutputModeEnum.
     *
     * @param name the name of the output mode
     */
    OutputModeEnum(String name) {
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
                .map(OutputModeEnum::getName)
                .toArray(String[]::new);
    }
}
