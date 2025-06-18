/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing properties related to Source VC#IN#. Each enum value is associated with specific commands for monitoring.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum SourceVCINProperty {
	INPUT_INFO("InputInfo", Command.INPUT_INFO),
	SOURCE_PRESENT("SourcePresent", Command.NONE),
	RESOLUTION("Resolution", Command.NONE),
	HORIZONTAL_FREQUENCY("HorizontalFrequency(kHz)", Command.NONE),
	PIXEL_FREQUENCY("PixelFrequency(MHz)", Command.NONE),
	COLOR_SPACE("ColorSpace", Command.NONE),
	COLOR_DEPTH("ColorDepth(bit)", Command.NONE),
	COLOR_SUBSAMPLING("ColorSubsampling", Command.NONE);

	private final String name;
	private final Command command;

	SourceVCINProperty(String name, Command command) {
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

	public String getCommandContent(String vcID, String inID) {
		return this.command.getName() + "(VC" + vcID + ".IN" + inID + " ALL)" + Constant.GET_OPERATOR;
	}
}
