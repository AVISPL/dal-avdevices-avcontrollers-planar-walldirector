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

	public static final String PRESET_REGEX = "Preset\\d+";
	public static final String PRESET_NAME_REGEX = "Preset\\d+Name";
	public static final String COLON_REGEX = "\\:";
	public static final String BETWEEN_CHARS_REGEX = "(?<=.)(?=.)";
	public static final String SOURCE_NAME_FORMAT = "VC%s_IN%s";
	public static final String GROUP_NAME_FORMAT = "%s-%s";
	public static final String PROPERTY_NAME_FORMAT = "%s#%s";
	public static final Pattern INVALID_RESPONSE_PATTERN = Pattern.compile("\\^NAK|ERR|empty list|time out");
	public static final String PRESET_RECALL_PATTERN = ".*" + PRESET_REGEX;
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
	public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";

	public static final String GET_OPERATOR = "?";
	public static final String CR = "\r";
	public static final String DOT = ".";
	public static final String SPACE = " ";
	public static final String COLON = ":";
	public static final String EMPTY = "";
	public static final String COMMA = ",";

	public static final String ADAPTER_METADATA_GROUP = "AdapterMetadata";
	public static final String VW_GROUP = "VideoWall";
	public static final String PS_GROUP = "PowerSupplies";
	public static final String VC_GROUP = "VideoControllers";
	public static final String SOURCE_GROUP = "Sources";
	public static final String ZONE_GROUP = "Zones";
	public static final String PRESET_GROUP = "Presets";
	public static final String NETWORK_STATUS_GROUP = "NetworkStatus";

	public static final String PANEL_COMPONENT = "PANEL";
	public static final String PS_COMPONENT = "PS";
	public static final String ZONE_COMPONENT = "ZONE";
	public static final String TEMP_HISTORICAL = "#Temperature(C)";
	public static final String UNREACHABLE = "Unreachable";
	public static final String NONE = "None";
	public static final String ACK = "@ACK";

	public static final String UNABLE_TO_READ_PROPERTIES_FILE = "Unable to load properties file: application.properties";
	public static final String DEVICE_DISCONNECTED = "No valid response received. The device may be unreachable or disconnected.";
	public static final String COMMAND_FAILED = "Failed to send command ";
	public static final String FORMAT_DATE_TIME_FAILED = "Failed to formatDatetime with timestamp: ";
	public static final String FORMAT_ELAPSED_TIME_FAILED = "Failed to formatElapsedTime with timestamp: ";
	public static final String COMMAND_UNREACHABLE = "Unreachable command: Failed to connect or execute command ";
	public static final String COMMAND_INVALIDATED = "Invalid command: Invalid or unsupported command ";
	public static final String RESPONSE_INVALIDATED = "Invalid response: Invalid response from command ";
	public static final String UNKNOWN_CONTROLLER = "Unknown controllable property ";
}
