/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.commands.Command;

/**
 * Enum representing power supply properties. Each enum value can be mapped to different commands.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum PowerSupplyProperty {
	ID("ID", Command.ID),
	MODEL("Model", Command.MODEL_NAME),
	SERIAL_NUMBER("SerialNumber", Command.SERIAL_NUMBER),
	MODULE_1_STATUS("Module1Status", Command.PS_STATUS),
	MODULE_2_STATUS("Module2Status", Command.PS_STATUS),
	MODULE_3_STATUS("Module3Status", Command.PS_STATUS),
	MODULE_4_STATUS("Module4Status", Command.PS_STATUS),
	TEMPERATURE("Temperature(C)", Command.TEMPERATURE),
	FIRMWARE_VERSION("FirmwareVersion", Command.FIRMWARE_VERSION);

	private final String name;
	private final Command command;

	PowerSupplyProperty(String name, Command command) {
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
		switch (this) {
			case MODULE_1_STATUS: {
				return this.command.getName() + "(1)?";
			}
			case MODULE_2_STATUS: {
				return this.command.getName() + "(2)?";
			}
			case MODULE_3_STATUS: {
				return this.command.getName() + "(3)?";
			}
			case MODULE_4_STATUS: {
				return this.command.getName() + "(4)?";
			}
			default: {
				return Command.NONE.getName();
			}
		}
	}

	public String getCommandContent(String param) {
		return this.command.getName() + "(PS" + param + ")?";
	}
}
