package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products;

/**
 * Enum representing different product subtypes with their respective details.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum ProductSubType {
	NONE("None"),
	TWO_K_PIXEL("2k Pixel Architecture"),
	OPEN_PIXEL("Open Pixel Architecture"),
	ENHANCED_OPEN_PIXEL("Enhanced Open Pixel Architecture");

	private final String value;

	ProductSubType(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
