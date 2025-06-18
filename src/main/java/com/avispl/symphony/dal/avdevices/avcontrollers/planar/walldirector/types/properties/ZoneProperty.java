/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing various properties related to a Zone. Each enum value corresponds to a specific command used for monitoring the Zone.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ZoneProperty {
	INPUT("ZoneInput", Command.ZONE_INPUT),
	SOURCE("ZoneSource", Command.ZONE_SOURCE),
	ASPECT("ZoneAspect", Command.ZONE_ASPECT),
	EXPECTED_SOURCE_HEIGHT("ZoneExpectedSourceHeight", Command.ZONE_EXPECTED_SOURCE_HEIGHT),
	EXPECTED_SOURCE_WIDTH("ZoneExpectedSourceWidth", Command.ZONE_EXPECTED_SOURCE_WIDTH),
	ORDER("ZoneOrder", Command.ZONE_ORDER);

	private final String name;
	private final Command command;

	ZoneProperty(String name, Command command) {
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

	public String getCommandContent(String id) {
		return this.command.getName() + "(" + id + ")" + Constant.GET_OPERATOR;
	}
}
