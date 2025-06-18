/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing properties related to Video Controller (VC). Each enum value is associated with specific commands for monitoring the video controller.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum VideoControllerProperty {
	ID("ID", Command.ID),
	MODEL("Model", Command.MODEL_NAME),
	SERIAL_NUMBER("SerialNumber", Command.SERIAL_NUMBER),
	TEMPERATURE_AND_FAN_STATUS("Temperature&FanStatus", Command.FAN_STATUS),
	FIRMWARE_VERSION("FirmwareVersion", Command.FIRMWARE_VERSION),
	INLET_AIR_TEMPERATURE("InletAirTemperature(C)", Command.TEMPERATURE);

	private final String name;
	private final Command command;

	VideoControllerProperty(String name, Command command) {
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
		return this.command.getName() + "(VC" + id + ")" + Constant.GET_OPERATOR;
	}
}
