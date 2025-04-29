/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.commands.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing preset properties. Each enum value is associated with specific commands for monitoring.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum PresetProperty {
	ACTIVE_PRESET("ActivePreset", Command.PRESET_ACTIVE),
	PRESET_RECALL("Preset%sRecall", Command.NONE);

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
}
