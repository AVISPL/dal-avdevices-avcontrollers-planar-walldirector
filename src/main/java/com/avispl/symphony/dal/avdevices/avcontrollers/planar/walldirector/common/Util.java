/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products.ProductFamily;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.NetworkStatusProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.PowerSupplyProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.PresetProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.SourceVCINProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VWGeneralProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VWPanelProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VideoControllerProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.ZoneProperty;

/**
 * Utility class providing helper methods for response validation and parsing.
 * <p>
 * This class is not meant to be instantiated.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public class Util {
	private Util() {
		// Prevent instantiation
	}

	/**
	 * Checks if all values in the map are "unreachable" (either as "unreachable" strings, empty strings
	 * which are controllable, or lists of "unreachable" strings).
	 *
	 * @param data the map to check
	 * @param <K> the type of keys in the map
	 * @param <V> the type of values in the map
	 * @return {@code true} if all values are unreachable; {@code false} otherwise
	 */
	public static <K, V> boolean areValuesUnreachable(Map<K, V> data) {
		return data.values().stream().allMatch(v -> {
			if (v instanceof String) {
				return ((String) v).isEmpty() || Constant.UNREACHABLE.equals(v);
			}
			if (v instanceof List<?>) {
				return ((List<?>) v).stream().allMatch(Constant.UNREACHABLE::equals);
			}
			return false;
		});
	}

	/**
	 * Checks if the given string value is considered invalid.
	 *
	 * @param value the string value to be checked
	 * @return {@code true} if the value is either {@link Constant#NONE} or {@link Constant#UNREACHABLE}, otherwise {@code false}
	 */
	public static boolean invalidValue(String value) {
		return value.equals(Constant.NONE) || value.equals(Constant.UNREACHABLE);
	}

	/**
	 * Maps a given {@link VWGeneralProperty} and its associated response string to a formatted value
	 * based on property-specific logic.
	 *
	 * @param properties a map of {@link VWGeneralProperty} to their response string values
	 * @param property the specific property to map
	 * @return the formatted value for the property, or {@code null} if the property is not applicable or input is invalid
	 */
	public static String mapToVWGeneralProperty(Map<VWGeneralProperty, String> properties, VWGeneralProperty property) {
		if (MapUtils.isEmpty(properties) || property == null) {
			return null;
		}
		String response = properties.get(property);
		switch (property) {
			case PRODUCT: {
				return ProductFamily.getByFamily(response).getName();
			}
			case PANEL_MODEL: {
				return ProductFamily.hasModel(properties.get(VWGeneralProperty.PRODUCT), response) ? response : Constant.NONE;
			}
			case WIDTH:
			case HEIGHT:
			case COLUMNS:
			case ROWS:
			case BACKLIGHT_INTENSITY:
			case BACKLIGHT_CONTROL_MODE: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			case SYSTEM_POWER: {
				return response.equals("ON") ? "1" : "0";
			}
			case STANDBY_MODE: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : toCapitalizationCase(response);
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Maps a response string to a formatted VW panel property value.
	 *
	 * @param property the property name
	 * @param response the response string
	 * @return the formatted panel property value, or null if not applicable
	 */
	public static String mapToVWPanelProperty(VWPanelProperty property, String response) {
		if (property == null || response == null) {
			return null;
		}
		switch (property) {
			case ID:
			case MODEL:
			case SERIAL_NUMBER:
			case TEMPERATURE:
			case SUPPLY_48V:
			case FIRMWARE_VERSION:
			case CABLE_LENGTH:
			case SIGNAL_QUALITY:
			case VC_OUTPUT: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Maps a response string to panel position properties if the property is PANEL_POSITION.
	 *
	 * @param property the property name
	 * @param response the response string containing position data
	 * @return a map with PANEL_POSITION_COLUMN and PANEL_POSITION_ROW, or empty map if not applicable
	 */
	public static Map<VWPanelProperty, String> mapToVWPanelProperties(VWPanelProperty property, String response) {
		if (property != VWPanelProperty.PANEL_POSITION || response == null) {
			return Collections.emptyMap();
		}
		String[] values = response.split(Constant.SPACE, 2);
		if (values.length <= 1) {
			return Collections.emptyMap();
		}
		Map<VWPanelProperty, String> panelPositions = new EnumMap<>(VWPanelProperty.class);
		panelPositions.put(VWPanelProperty.PANEL_POSITION_COLUMN, values[0]);
		panelPositions.put(VWPanelProperty.PANEL_POSITION_ROW, values[1]);

		return panelPositions;
	}

	/**
	 * Maps a response string to a formatted power supply property value.
	 *
	 * @param property the property name
	 * @param response the response string
	 * @return the formatted power supply property value, or null if not applicable
	 */
	public static String mapToPowerSupplyProperty(PowerSupplyProperty property, String response) {
		if (property == null || response == null) {
			return null;
		}
		switch (property) {
			case ID:
			case MODEL:
			case SERIAL_NUMBER:
			case MODULE_1_STATUS:
			case MODULE_2_STATUS:
			case MODULE_3_STATUS:
			case MODULE_4_STATUS:
			case TEMPERATURE:
			case FIRMWARE_VERSION: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Maps a response string to a formatted video controller property value.
	 *
	 * @param property the property name
	 * @param response the response string
	 * @return the formatted video controller property value, or null if not applicable
	 */
	public static String mapToVideoControllerProperty(VideoControllerProperty property, String response) {
		if (property == null || response == null) {
			return null;
		}
		switch (property) {
			case ID:
			case MODEL:
			case SERIAL_NUMBER:
			case TEMPERATURE_AND_FAN_STATUS:
			case FIRMWARE_VERSION: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			case INLET_AIR_TEMPERATURE:
				return String.valueOf((int) Math.round(Double.parseDouble(response)));
			default: {
				return null;
			}
		}
	}

	/**
	 * Maps a response string to a formatted Source VC#IN# property value.
	 *
	 * @param property the property name
	 * @param response the response string
	 * @return the formatted value as a map for INPUT_INFO, or empty map if not applicable
	 */
	public static Map<SourceVCINProperty, String> mapToSourceVCINProperties(SourceVCINProperty property, String response) {
		if (property != SourceVCINProperty.INPUT_INFO || response == null) {
			return Collections.emptyMap();
		}
		String[] values = response.split(Constant.SPACE);
		if (values.length <= 11) {
			return Collections.emptyMap();
		}

		Map<SourceVCINProperty, String> inputInfo = new EnumMap<>(SourceVCINProperty.class);
		inputInfo.put(SourceVCINProperty.SOURCE_PRESENT, values[0].equals("TRUE") ? "YES" : "NO");
		inputInfo.put(SourceVCINProperty.RESOLUTION, String.format("%sx%s %sHz", values[1], values[2], values[4]));
		inputInfo.put(SourceVCINProperty.COLOR_SPACE, values[5]);
		inputInfo.put(SourceVCINProperty.COLOR_SUBSAMPLING, values[6].replaceAll(Constant.BETWEEN_CHARS_REGEX, Constant.COLON));
		inputInfo.put(SourceVCINProperty.COLOR_DEPTH, values[7]);
		inputInfo.put(SourceVCINProperty.HORIZONTAL_FREQUENCY, formatFloat(values[10]));
		inputInfo.put(SourceVCINProperty.PIXEL_FREQUENCY, formatFloat(values[11]));

		return inputInfo;
	}

	/**
	 * Maps a response string to a formatted zone property value.
	 *
	 * @param property the property name
	 * @param response the response string
	 * @return the mapped value, or null if not applicable
	 */
	public static String mapToZoneProperty(ZoneProperty property, String response) {
		if (property == null || response == null) {
			return null;
		}
		switch (property) {
			case INPUT:
			case SOURCE:
			case ASPECT:
			case EXPECTED_SOURCE_HEIGHT:
			case EXPECTED_SOURCE_WIDTH:
			case ORDER: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Maps a preset property from the response.
	 *
	 * @param property the property name
	 * @param response the raw response string
	 * @return the formatted value, or {@code null} if not applicable
	 */
	public static String mapToPresetProperty(String property, String response) {
		if (Objects.equals(property, PresetProperty.ACTIVE_PRESET.getName())
				|| Objects.equals(property, PresetProperty.ACTIVE_PRESET_NAME.getName())
				|| property.matches(Constant.PRESET_NAME_REGEX)) {
			return response;
		} else {
			return null;
		}
	}

	/**
	 * Formats the given network status property based on its response.
	 * Returns the raw value for string properties or a capitalized value for boolean fields.
	 *
	 * @param property the property to map
	 * @param response the raw response string
	 * @return the formatted value, or {@code null} if invalid
	 */
	public static String mapToNetworkStatusProperty(NetworkStatusProperty property, String response) {
		if (property == null || response == null) {
			return null;
		}
		switch (property) {
			case HOSTNAME:
			case IP_ADDRESS:
			case SUBNET_MASK:
			case GATEWAY:
			case DNS_SERVER_1:
			case DNS_SERVER_2:
			case MAC_ADDRESS: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : response;
			}
			case DHCP_ENABLED: {
				return response.equals(Constant.UNREACHABLE) ? Constant.NONE : toCapitalizationCase(response);
			}
			default: {
				return null;
			}
		}
	}

	/**
	 * Extracts the preset ID from a given preset string using a regular expression.
	 *
	 * @param preset the input preset string
	 * @return the extracted preset ID or an empty string if not found
	 */
	public static String extractPresetID(String preset) {
		Pattern pattern = Pattern.compile(Constant.PRESET_REGEX);
		Matcher matcher = pattern.matcher(preset);

		return matcher.find() ? matcher.group().replaceAll("\\D+", Constant.EMPTY) : Constant.EMPTY;
	}

	/**
	 * Checks if the given preset ID matches the currently active preset.
	 * <p>
	 * This method retrieves the active preset ID from the provided presets map,
	 * and compares it against the specified preset ID.
	 * </p>
	 *
	 * @param presets a map containing preset properties and their corresponding values
	 * @param presetID the ID of the preset to verify
	 * @return {@code true} if the given preset ID matches the active preset; {@code false} otherwise
	 */
	public static boolean isActivePreset(Map<String, String> presets, String presetID) {
		if (presets == null) {
			return false;
		}
		String value = presets.get(PresetProperty.ACTIVE_PRESET.getName());

		return value != null && value.equals(presetID);
	}

	/**
	 * Converts a string to capitalization case. Replaces dots with spaces, lowers all characters,
	 * and capitalizes the first character.
	 *
	 * @param str the input string
	 * @return the transformed string in capitalization case, or null if input is null
	 */
	private static String toCapitalizationCase(String str) {
		if (str == null) {
			return null;
		}
		String mappedStr = str.replace(Constant.DOT, Constant.SPACE).toLowerCase();

		return Character.toUpperCase(mappedStr.charAt(0)) + mappedStr.substring(1);
	}

	/**
	 * Formats a numeric string as a float with up to two decimal places.
	 * If the number is a whole number, no decimal places are shown.
	 *
	 * @param input the numeric string to format
	 * @return the formatted number as a string
	 * @throws NumberFormatException if the input is not a valid number
	 */
	private static String formatFloat(String input) {
		double value = Double.parseDouble(input);

		return (value == Math.floor(value)) ? String.format("%.0f", value) : String.format("%.2f", value);
	}
}
