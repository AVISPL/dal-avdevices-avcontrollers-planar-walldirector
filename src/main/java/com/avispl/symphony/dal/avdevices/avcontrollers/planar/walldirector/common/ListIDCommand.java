/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

/**
 * Enum representing commands for various list IDs in the system.
 * Each enum constant is associated with a name and a corresponding command string.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum ListIDCommand {
    POWER_SUPPLY("PowerSupply", "PS.LIST"),
    VIDEO_CONTROLLER("VideoController", "VC.LIST"),
    ZONE("Zone", "ZONE.LIST"),
    PRESET("Preset", "PRESET.LIST"),
    PANEL("Panel", "PANEL.LIST"),
    ;
    private final String name;
    private final String command;

    /**
     * Constructs a {@code ListIDCommand} enum constant with the specified name and command.
     *
     * @param name    the name of the list ID
     * @param command the command associated with the list ID
     */
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
