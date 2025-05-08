/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing preset properties. Each enum value is associated with specific commands for monitoring.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum PresetProperty {
	ACTIVE_PRESET("ActivePreset", Command.PRESET_ACTIVE),
	ACTIVE_PRESET_NAME("ActivePresetName", Command.PRESET_CURRENT),
	PRESET("Preset%s", Command.NONE),
	PRESET_NAME("Preset%sName", Command.PRESET_NAME);

	private final String name;
	private final Command command;

	PresetProperty(String name, Command command) {
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

	public String getCommandContent(String presetID) {
		return this.command.getName() + "(" + presetID + ")" + Constant.GET_OPERATOR;
	}
}
