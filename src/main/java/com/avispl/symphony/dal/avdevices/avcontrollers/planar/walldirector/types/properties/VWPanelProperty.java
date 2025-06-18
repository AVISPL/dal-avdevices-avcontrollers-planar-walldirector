/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing properties of a video wall (VW) panel. Each enum value corresponds to a specific command used for monitoring the VW panel.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum VWPanelProperty {
	ID("ID", Command.ID),
	MODEL("Model", Command.MODEL_NAME),
	SERIAL_NUMBER("SerialNumber", Command.SERIAL_NUMBER),
	TEMPERATURE("Temperature(C)", Command.TEMPERATURE),
	SUPPLY_48V("48VSupply(V)", Command.VOLTAGE),
	FIRMWARE_VERSION("FirmwareVersion", Command.FIRMWARE_VERSION),
	CABLE_LENGTH("CableLength", Command.PANEL_RX_LENGTH),
	SIGNAL_QUALITY("SignalQuality", Command.PANEL_SIGNAL_QUALITY),
	PANEL_POSITION("PanelPosition", Command.PANEL_POSITION),
	PANEL_POSITION_COLUMN("PanelPositionColumn", Command.NONE),
	PANEL_POSITION_ROW("PanelPositionRow", Command.NONE),
	VC_OUTPUT("VCOutput", Command.CONNECTION);

	private final String name;
	private final Command command;

	VWPanelProperty(String name, Command command) {
		this.name = name;
		this.command = command;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Retrieves {@link #command}
	 *
	 * @return value of {@link #command}
	 */
	public Command getCommand() {
		return this.command;
	}

	public String getCommandContent(String id) {
		return this.command.getName() + "(PN" + id + ")" + Constant.GET_OPERATOR;
	}
}
