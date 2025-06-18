/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing different types of ID properties.
 * Each ID property corresponds to a specific list of commands in the device.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum IDProperty {
	PN_IDS(Command.PANEL_LIST),
	PS_IDS(Command.PS_LIST),
	VC_IDS(Command.VC_LIST),
	ZONE_IDS(Command.ZONE_LIST),
	PRESET_IDS(Command.PRESET_LIST);

	private final Command command;

	IDProperty(Command command) {
		this.command = command;
	}

	/**
	 * Retrieves {@link #command}
	 *
	 * @return value of {@link #command}
	 */
	public Command getCommand() {
		return this.command;
	}

	public String getCommandContent() {
		return this.command.getName() + Constant.GET_OPERATOR;
	}
}
