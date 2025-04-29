/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

import java.util.regex.Pattern;

/**
 * Utility class for storing constant values used across the application.
 * <p>
 * This class is not meant to be instantiated.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public class Constant {
	private Constant() {
		// Prevent instantiation
	}

	public static final String PRESET_RECALL_REGEX = "Preset\\d+Recall";
	public static final String COLON_REGEX = "\\:";
	public static final String BETWEEN_CHARS_REGEX = "(?<=.)(?=.)";
	public static final String SOURCE_NAME_FORMAT = "VC%s_IN%s";
	public static final String GROUP_NAME_FORMAT = "%s - %s";
	public static final String PROPERTY_NAME_FORMAT = "%s#%s";
	public static final Pattern INVALID_RESPONSE_PATTERN = Pattern.compile("\\^NAK|ERR|\"\"|empty list");
	public static final String PRESET_RECALL_PATTERN = ".*" + PRESET_RECALL_REGEX;

	public static final String GET_OPERATOR = "?";
	public static final String CR = "\r";
	public static final String DOT = ".";
	public static final String SPACE = " ";
	public static final String COLON = ":";
	public static final String EMPTY = "";
	public static final String COMMA = ",";

	public static final String VW_GROUP = "Video Wall";
	public static final String PS_GROUP = "Power Supplies";
	public static final String VC_GROUP = "Video Controllers";
	public static final String SOURCE_GROUP = "Sources";
	public static final String ZONE_GROUP = "Zones";
	public static final String PRESET_GROUP = "Presets";
	public static final String NETWORK_STATUS_GROUP = "Network Status";
	public static final String PANEL_COMPONENT = "PANEL";
	public static final String PS_COMPONENT = "PS";
	public static final String ZONE_COMPONENT = "ZONE";
	public static final String TEMP_HISTORICAL = "#Temperature(C)";
	public static final String NONE_OR_ERROR = "NoneOrError";

	public static final String DEVICE_DISCONNECTED = "No valid response received after retries. The device may be unreachable or disconnected.";
	public static final String COMMAND_FAILED = "Failed to send command ";
	public static final String INVALID_RESPONSE = "Invalid response for command ";
	public static final String UNKNOWN_CONTROLLER = "Unknown controllable property ";
}
