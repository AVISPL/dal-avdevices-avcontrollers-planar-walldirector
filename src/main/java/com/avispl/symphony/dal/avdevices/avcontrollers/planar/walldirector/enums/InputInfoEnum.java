/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums;

/**
 * Enum representing various input information properties and their positions in a data array.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum InputInfoEnum {
    INPUT_PRESENT("Present", 0),
    WIDTH("Width", 1),
    HEIGHT("Height", 2),
    COLOR_SPACE("ColorSpace", 5),
    SUBSAMPLING("ColorSubsampling", 6),
    BITS_PER_PIXEL("ColorDepth(bit)", 7),
    H_FREQ("HorizontalFrequency(kHz)", 10),
    PIXEL_CLOCK("PixelFrequency(MHz)", 11),
    ;

    private final String name;
    private final int position;

    /**
     * Constructor for InputInfoEnum.
     *
     * @param name     the name of the input property
     * @param position the position of the input property in a data array
     */
    InputInfoEnum(String name, int position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Retrieves {@link #position}
     *
     * @return value of {@link #position}
     */
    public int getPosition() {
        return position;
    }

    /**
     * Retrieves {@link #name}
     *
     * @return value of {@link #name}
     */
    public String getName() {
        return name;
    }
}
