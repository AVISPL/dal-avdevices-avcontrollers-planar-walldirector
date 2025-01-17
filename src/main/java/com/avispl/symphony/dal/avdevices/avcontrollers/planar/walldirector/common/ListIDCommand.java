package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

public enum ListIDCommand {
    POWER_SUPPLY("PowerSupply", "PS.LIST"),
    VIDEO_CONTROLLER("VideoController", "VC.LIST"),
    ZONE("Zone", "ZONE.LIST"),
    PRESET("Preset", "PRESET.LIST"),
    PANEL("Panel", "PANEL.LIST"),
    ;
    private final String name;
    private final String command;

    ListIDCommand(String name, String command) {
        this.name = name;
        this.command = command;
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
     * Retrieves {@link #command}
     *
     * @return value of {@link #command}
     */
    public String getCommand() {
        return command;
    }
}
