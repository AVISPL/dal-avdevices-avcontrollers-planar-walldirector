package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

/**
 * Enum representing different types of Adapter metadata.
 *
 * @author Kevin / Symphony Dev Team<br>
 * @since 1.0.0
 */
public enum AdapterMetadataProperty {
	ADAPTER_BUILD_DATE("AdapterBuildDate"),
	ADAPTER_UPTIME("AdapterUptime"),
	ADAPTER_VERSION("AdapterVersion");

	private final String name;

	AdapterMetadataProperty(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
