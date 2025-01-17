package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

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

    InputInfoEnum(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
