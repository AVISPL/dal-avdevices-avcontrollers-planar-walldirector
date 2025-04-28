/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.properties;

import java.util.EnumSet;
import java.util.Set;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.commands.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing general properties for a video wall (VW). Each enum value is associated with specific commands for monitoring or controlling the video wall.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum VWGeneralProperty {
	PANEL_MODEL("PanelModel", Command.WALL_MODEL),
	COLUMNS("Columns", Command.MATRIX_COLUMNS),
	ROWS("Rows", Command.MATRIX_ROWS),
	BACKLIGHT_CONTROL_MODE("BacklightControlMode", Command.BACKLIGHT_MODE),
	BACKLIGHT_INTENSITY("BacklightIntensity", Command.BACKLIGHT_INTENSITY),
	STANDBY_MODE("StandbyMode", Command.STANDBY_MODE),
	SYSTEM_POWER("SystemPower", Command.SYSTEM_POWER),
	SYSTEM_REBOOT("SystemReboot", Command.SYSTEM_REBOOT);

	private final String name;
	private final Command command;

	VWGeneralProperty(String name, Command command) {
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
	public Command getCommand() {
		return command;
	}

	public String getCommandContent() {
		return this.command.getName() + Constant.GET_OPERATOR;
	}

	public static Set<VWGeneralProperty> getOnlyControllableProperties() {
		return EnumSet.of(SYSTEM_REBOOT);
	}
}
