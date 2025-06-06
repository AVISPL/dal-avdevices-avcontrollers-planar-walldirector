/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.avispl.symphony.api.common.error.NotImplementedException;
import com.avispl.symphony.api.common.error.ServiceNotAvailableException;
import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Util;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.products.ProductFamily;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.AdapterMetadataProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.IDProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.NetworkStatusProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.PowerSupplyProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.PresetProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.SourceVCINProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VWGeneralProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VWPanelProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.VideoControllerProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties.ZoneProperty;
import com.avispl.symphony.dal.communicator.SocketCommunicator;

/**
 * WallDirectorCommunicator is a communicator class for Wall Director devices.
 * <p>
 * The {@code WallDirectorCommunicator} class is responsible for communicating
 * with the Planar WallDirector to retrieve and manage device statistics.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class WallDirectorCommunicator extends SocketCommunicator implements Monitorable, Controller {
	/**
	 * Number of VC input modules per video controller.
	 */
	private static final int VC_IN_SIZE = 4;
	/**
	 * Duration to wait after rebooting (in milliseconds).
	 */
	private static final long REBOOT_TIME = 3 * 60 * 1000L;

	/**
	 * Lock used to ensure thread-safe operations.
	 */
	private final ReentrantLock reentrantLock;
	/**
	 * Holds the application configuration properties loaded from the {@code application.properties} file.
	 */
	private final Properties applicationProperties;

	/**
	 * Store of extended statistics object.
	 */
	private ExtendedStatistics localExtendedStatistics;
	/**
	 * Map of ID properties to their corresponding list of IDs.
	 */
	private Map<IDProperty, List<String>> ids;
	/**
	 * Video Wall - General properties
	 */
	private Map<VWGeneralProperty, String> vwGeneral;
	/**
	 * Data of Video Wall - Panel mapped by PANEL#.
	 */
	private Map<String, Map<VWPanelProperty, String>> vwPanels;
	/**
	 * Data of Power Supplies mapped by PS#.
	 */
	private Map<String, Map<PowerSupplyProperty, String>> powerSupplies;
	/**
	 * Data of Video Controllers mapped by VC#.
	 */
	private Map<String, Map<VideoControllerProperty, String>> videoControllers;
	/**
	 * Data of Source - VN#IN# mapped by VN#IN#.
	 */
	private Map<String, Map<SourceVCINProperty, String>> sourceVCINs;
	/**
	 * Data of Zones mapped by ZONE#.
	 */
	private Map<String, Map<ZoneProperty, String>> zones;
	/**
	 * Presets properties.
	 */
	private Map<String, String> presets;
	/**
	 * Network Status properties.
	 */
	private Map<NetworkStatusProperty, String> networkStatus;
	/**
	 * Store of historical properties
	 */
	private Set<String> historicalProperties;
	/**
	 * A flag indicating whether the device has recently rebooted.
	 */
	private boolean isRebooted;

	public WallDirectorCommunicator() {
		this.reentrantLock = new ReentrantLock();
		this.applicationProperties = new Properties();

		this.localExtendedStatistics = new ExtendedStatistics();
		this.ids = new EnumMap<>(IDProperty.class);
		this.vwGeneral = new EnumMap<>(VWGeneralProperty.class);
		this.vwPanels = new HashMap<>();
		this.powerSupplies = new HashMap<>();
		this.videoControllers = new HashMap<>();
		this.sourceVCINs = new HashMap<>();
		this.zones = new HashMap<>();
		this.presets = new HashMap<>();
		this.networkStatus = new EnumMap<>(NetworkStatusProperty.class);
		this.historicalProperties = new HashSet<>();
		this.isRebooted = false;

		this.loadProperties(this.applicationProperties);
		this.setCommandSuccessList(Collections.singletonList(Constant.CR));
		this.setCommandErrorList(Collections.singletonList(Constant.CR));
	}

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(Constant.COMMA, this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(Constant.COMMA)).forEach(propertyName -> this.historicalProperties.add(propertyName.trim()));
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		this.reentrantLock.lock();
		try {
			if (isRebooted) {
				this.isRebooted = false;
				return Collections.singletonList(this.localExtendedStatistics);
			}
			this.setupData();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> statistics = new HashMap<>(this.getVWGeneralProperties());
			statistics.putAll(this.getAdapterMetadataProperties());
			statistics.putAll(this.getVWPanelProperties());
			statistics.putAll(this.getPowerSupplyProperties());
			statistics.putAll(this.getVideoControllerProperties());
			statistics.putAll(this.getSourceVCINProperties());
			statistics.putAll(this.getZoneProperties());
			statistics.putAll(this.getPresetProperties());
			statistics.putAll(this.getNetworkStatusProperties());

			List<AdvancedControllableProperty> controllableProperties = this.getGeneralControllers();
			controllableProperties.addAll(this.getPresetControllers());

			extendedStatistics.setStatistics(statistics);
			extendedStatistics.setControllableProperties(controllableProperties);
//		  Disabled temporarily due to the device not supporting the panel/power supply.
//		 	extendedStatistics.setDynamicStatistics(this.getDynamicStatistics(statistics));
			this.localExtendedStatistics = extendedStatistics;
			return Collections.singletonList(this.localExtendedStatistics);
		} finally {
			this.reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		this.reentrantLock.lock();
		try {
			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());

			switch (property) {
				case "SystemPower": {
					String command = String.format("%s=%s", Command.SYSTEM_POWER.getName(), value);

					this.sendControlCommand(Command.SYSTEM_POWER, command);
					break;
				}
				case "SystemReboot": {
					this.isRebooted = true;

					this.sendControlCommand(Command.SYSTEM_REBOOT, Command.SYSTEM_REBOOT.getName());
					this.destroyChannel();
					break;
				}
				default: {
					if (!property.matches(Constant.PRESET_RECALL_PATTERN)) {
						throw new IllegalArgumentException(Constant.UNKNOWN_CONTROLLER + property);
					}
					String presetID = Util.extractPresetID(property);
					String command = String.format("%s(%s)", Command.PRESET_RECALL.getName(), presetID);

					this.sendControlCommand(Command.PRESET_RECALL, command);
					break;
				}
			}
		} finally {
			this.reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				this.controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
			}
		}
	}

	@Override
	protected void internalDestroy() {
		this.logger.info("Internal destroy: Clearing internal data...");
		this.ids = null;
		this.vwGeneral = null;
		this.vwPanels = null;
		this.powerSupplies = null;
		this.videoControllers = null;
		this.sourceVCINs = null;
		this.zones = null;
		this.presets = null;
		this.networkStatus = null;
		this.historicalProperties = null;
		this.localExtendedStatistics = null;
		super.internalDestroy();
	}

	/**
	 * Loads properties from the {@code application.properties} file into the provided {@link Properties} object.
	 *
	 * @param properties The {@link Properties} object to load the configuration into.
	 * @throws ResourceNotReachableException if the properties file cannot be loaded.
	 */
	private void loadProperties(Properties properties) {
		try {
			properties.load(getClass().getResourceAsStream("/application.properties"));
		} catch (Exception e) {
			throw new FileSystemNotFoundException(Constant.UNABLE_TO_READ_PROPERTIES_FILE);
		}
	}

	/**
	 * Initializes all required data by fetching from the device.
	 * <p>
	 * If all essential values (IDs, general, and network status) are unreachable,
	 * marks the device as rebooted, disconnects, and throws a {@link ServiceNotAvailableException}.
	 * </p>
	 *
	 * @throws Exception if data fetching fails or device is unreachable
	 */
	private void setupData() throws Exception {
		this.ids = this.getIDsData();
		this.vwGeneral = this.getGeneralData();
		this.networkStatus = this.getNetworkStatusData();
		if (Util.areValuesUnreachable(this.ids) && Util.areValuesUnreachable(this.vwGeneral) && Util.areValuesUnreachable(this.networkStatus)) {
			this.isRebooted = true;
			this.disconnect();
			throw new ServiceNotAvailableException(Constant.DEVICE_DISCONNECTED);
		}
//		Disabled temporarily due to the device not supporting the panel/power supply.
//		this.vwPanels = this.getVWPanelsData();
//		this.powerSupplies = this.getPowerSuppliesData();
		this.videoControllers = this.getVideoControllersData();
		this.sourceVCINs = this.getSourceVCINsData();
		this.zones = this.getZonesData();
		this.presets = this.getPresetsData();
	}

	/**
	 * Retrieves IDs data by sending GET commands for each ID property.
	 *
	 * @return a map of IDProperty to list of IDs
	 */
	public Map<IDProperty, List<String>> getIDsData() {
		Map<IDProperty, List<String>> idsData = new EnumMap<>(IDProperty.class);

		Arrays.stream(IDProperty.values()).forEach(idProperty -> {
			String response = this.send(idProperty.getCommandContent());

			if (!Util.invalidValue(response)) {
				idsData.put(idProperty, Arrays.asList(response.split(Constant.SPACE)));
			}
		});
		return idsData;
	}

	/**
	 * Retrieves general data from the device by sending command requests based on each property.
	 * <p>
	 * The method initializes a map of general properties with their corresponding response values.
	 * It differentiates between LCD and non-LCD products to determine whether to collect column/row
	 * or width/height properties. Additionally, it skips sending commands for controllable properties,
	 * returning an empty string instead.
	 * </p>
	 *
	 * @return a map of {@link VWGeneralProperty} to their corresponding string responses.
	 */
	private Map<VWGeneralProperty, String> getGeneralData() {
		Map<VWGeneralProperty, String> generalData = new EnumMap<>(VWGeneralProperty.class);
		generalData.put(VWGeneralProperty.PRODUCT, this.send(VWGeneralProperty.PRODUCT.getCommandContent()));
		if (ProductFamily.isLCDProduct(generalData.get(VWGeneralProperty.PRODUCT))) {
			generalData.put(VWGeneralProperty.COLUMNS, this.send(VWGeneralProperty.COLUMNS.getCommandContent()));
			generalData.put(VWGeneralProperty.ROWS, this.send(VWGeneralProperty.ROWS.getCommandContent()));
		} else {
			generalData.put(VWGeneralProperty.WIDTH, this.send(VWGeneralProperty.WIDTH.getCommandContent()));
			generalData.put(VWGeneralProperty.HEIGHT, this.send(VWGeneralProperty.HEIGHT.getCommandContent()));
		}
		Set<VWGeneralProperty> excludedProps = EnumSet.of(
				VWGeneralProperty.PRODUCT,
				VWGeneralProperty.COLUMNS, VWGeneralProperty.ROWS,
				VWGeneralProperty.WIDTH, VWGeneralProperty.HEIGHT
		);
		Arrays.stream(VWGeneralProperty.values()).filter(p -> !excludedProps.contains(p))
				.forEach(property -> {
					String response = VWGeneralProperty.getOnlyControllableProperties().contains(property)
							? Constant.EMPTY
							: this.send(property.getCommandContent());
					generalData.put(property, response);
		});
		return generalData;
	}

	/**
	 * Retrieves data from all VW panels based on their IDs.
	 *
	 * @return a map of panel name to its property values
	 */
	Map<String, Map<VWPanelProperty, String>> getVWPanelsData() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.PN_IDS))) {
			return new HashMap<>();
		}
		Map<String, Map<VWPanelProperty, String>> panelsData = new HashMap<>();

		this.ids.get(IDProperty.PN_IDS).forEach(panelID -> {
			String name = Constant.PANEL_COMPONENT + panelID;
			Map<VWPanelProperty, String> properties = new EnumMap<>(VWPanelProperty.class);

			Arrays.stream(VWPanelProperty.values()).filter(property -> !property.getCommand().equals(Command.NONE))
					.forEach(property -> {
						String response = this.send(property.getCommandContent(panelID));

						properties.put(property, response);
					});
			panelsData.put(name, properties);
		});
		return panelsData;
	}

	/**
	 * Retrieves data from all power supplies based on their IDs.
	 *
	 * @return a map of power supply name to its property values
	 */
	Map<String, Map<PowerSupplyProperty, String>> getPowerSuppliesData() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.PS_IDS))) {
			return Collections.emptyMap();
		}

		Map<String, Map<PowerSupplyProperty, String>> powerSuppliesData = new HashMap<>();
		this.ids.get(IDProperty.PS_IDS).forEach(panelID -> {
			String name = Constant.PS_COMPONENT + panelID;
			Map<PowerSupplyProperty, String> properties = new EnumMap<>(PowerSupplyProperty.class);

			Arrays.stream(PowerSupplyProperty.values()).forEach(property -> {
				String response = property.getCommand().equals(Command.PS_STATUS)
						? this.send(property.getCommandContent())
						: this.send(property.getCommandContent(panelID));

				properties.put(property, response);
			});
			powerSuppliesData.put(name, properties);
		});
		return powerSuppliesData;
	}

	/**
	 * Retrieves data from all video controllers based on their IDs.
	 *
	 * @return a map of video controller modifier to its property values
	 */
	private Map<String, Map<VideoControllerProperty, String>> getVideoControllersData() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.VC_IDS))) {
			return Collections.emptyMap();
		}

		Map<String, Map<VideoControllerProperty, String>> videoControllersData = new HashMap<>();
		this.ids.get(IDProperty.VC_IDS).forEach(videoControllerID -> {
			Map<VideoControllerProperty, String> properties = new EnumMap<>(VideoControllerProperty.class);

			Arrays.stream(VideoControllerProperty.values()).forEach(property -> {
				String response = this.send(property.getCommandContent(videoControllerID));

				properties.put(property, response);
			});
			videoControllersData.put("VC" + videoControllerID, properties);
		});
		return videoControllersData;
	}

	/**
	 * Retrieves source VC IN data for each video controller and input index.
	 *
	 * @return a map of VC IN name to its property values
	 */
	private Map<String, Map<SourceVCINProperty, String>> getSourceVCINsData() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.VC_IDS))) {
			return Collections.emptyMap();
		}

		Map<String, Map<SourceVCINProperty, String>> sourcesData = new HashMap<>();
		this.ids.get(IDProperty.VC_IDS).forEach(vcId -> {
			for (int i = 1; i <= VC_IN_SIZE; i++) {
				String inID = String.valueOf(i);
				String vcIn = String.format(Constant.SOURCE_NAME_FORMAT, vcId, inID);
				Map<SourceVCINProperty, String> properties = new EnumMap<>(SourceVCINProperty.class);

				Arrays.stream(SourceVCINProperty.values()).filter(property -> !property.getCommand().equals(Command.NONE))
						.forEach(property -> {
							String response = this.send(property.getCommandContent(vcId, inID));

							properties.put(property, response);
						});
				sourcesData.put(vcIn, properties);
			}
		});
		return sourcesData;
	}

	/**
	 * Retrieves data from all zones based on their IDs.
	 *
	 * @return a map of zone name to its property values
	 */
	private Map<String, Map<ZoneProperty, String>> getZonesData() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.ZONE_IDS))) {
			return Collections.emptyMap();
		}

		Map<String, Map<ZoneProperty, String>> zonesData = new HashMap<>();
		this.ids.get(IDProperty.ZONE_IDS).forEach(zoneId -> {
			String propertyName = Constant.ZONE_COMPONENT + zoneId;
			Map<ZoneProperty, String> properties = new EnumMap<>(ZoneProperty.class);

			Arrays.stream(ZoneProperty.values()).forEach(property -> {
				String response = this.send(property.getCommandContent(zoneId));

				properties.put(property, response);
			});
			zonesData.put(propertyName, properties);
		});
		return zonesData;
	}

	/**
	 * Retrieves presets data including the active preset and placeholders for others.
	 *
	 * @return a map of preset name to its value or placeholder
	 */
	private Map<String, String> getPresetsData() {
		List<String> presetIds = this.ids.get(IDProperty.PRESET_IDS);
		if (CollectionUtils.isEmpty(presetIds)) {
			return Collections.emptyMap();
		}

		Map<String, String> presetsData = new HashMap<>();
		Arrays.stream(PresetProperty.values()).forEach(property -> {
			switch (property) {
				case PRESET: {
					presetIds.forEach(id -> presetsData.put(String.format(property.getName(), id), Constant.EMPTY));
					break;
				}
				case PRESET_NAME: {
					presetIds.forEach(id -> presetsData.put(String.format(property.getName(), id), this.send(property.getCommandContent(id))));
					break;
				}
				default: {
					presetsData.put(property.getName(), this.send(property.getCommandContent()));
					break;
				}
			}
		});
		return presetsData;
	}

	/**
	 * Retrieves the network status data for all defined network status properties.
	 *
	 * @return a map where the key is the network status property name and the value is its corresponding data.
	 */
	private Map<NetworkStatusProperty, String> getNetworkStatusData() {
		Map<NetworkStatusProperty, String> dataMap = new EnumMap<>(NetworkStatusProperty.class);

		Arrays.stream(NetworkStatusProperty.values()).forEach(property -> {
			String response = this.send(property.getCommandContent());

			dataMap.put(property, response);
		});
		return dataMap;
	}

	/**
	 * Retrieves general properties related to VW, mapping them using a utility method.
	 *
	 * @return A map of general VW properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no general VW properties are found.
	 */
	private Map<String, String> getVWGeneralProperties() {
		if (this.vwGeneral.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.vwGeneral.entrySet().parallelStream().forEach(property -> {
			String mappedValue = !property.getKey().equals(VWGeneralProperty.SYSTEM_REBOOT)
					? Util.mapToVWGeneralProperty(this.vwGeneral, property.getKey())
					: Constant.EMPTY;
			if (mappedValue != null) {
				properties.put(property.getKey().getName(), mappedValue);
			}
		});
		return properties;
	}

	/**
	 * Retrieves properties related to Adapter, mapping them using a utility method.
	 *
	 * @return A map of Adapter metadata properties with the property names as keys and their corresponding mapped values as values.
	 */
	private Map<String, String> getAdapterMetadataProperties() {
		Map<String, String> properties = new HashMap<>();
		Arrays.stream(AdapterMetadataProperty.values()).forEach(property -> {
			String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, Constant.ADAPTER_METADATA_GROUP, property.getName());
			properties.put(propertyName, Util.mapToAdapterMetadataProperty(property, this.applicationProperties));
		});

		return properties;
	}

	/**
	 * Retrieves properties related to VW panels, mapping them using a utility method.
	 *
	 * @return A map of VW panel properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no VW panel properties are found.
	 */
	private Map<String, String> getVWPanelProperties() {
		if (this.vwPanels.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.vwPanels.entrySet().parallelStream().forEach(vwPanel -> {
			String groupName = String.format(Constant.GROUP_NAME_FORMAT, Constant.VW_GROUP, vwPanel.getKey());

			vwPanel.getValue().forEach((property, value) -> {
				if (property.equals(VWPanelProperty.PANEL_POSITION)) {
					Map<VWPanelProperty, String> mappedPanelPosition = Util.mapToVWPanelProperties(property, value);
					mappedPanelPosition.forEach((k, v) -> properties.put(String.format(Constant.PROPERTY_NAME_FORMAT, groupName, k.getName()), v));
				} else {
					String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, groupName, property.getName());
					String mappedValue = Util.mapToVWPanelProperty(property, value);

					if (mappedValue != null) {
						properties.put(propertyName, mappedValue);
					}
				}
			});
		});
		return properties;
	}

	/**
	 * Retrieves properties related to power supplies, mapping them using a utility method.
	 *
	 * @return A map of power supply properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no power supply properties are found.
	 */
	private Map<String, String> getPowerSupplyProperties() {
		if (this.powerSupplies.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.powerSupplies.entrySet().parallelStream().forEach(vwPanel -> {
			String groupName = String.format(Constant.GROUP_NAME_FORMAT, Constant.PS_GROUP, vwPanel.getKey());

			vwPanel.getValue().forEach((property, value) -> {
				String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, groupName, property.getName());
				String mappedValue = Util.mapToPowerSupplyProperty(property, value);

				if (mappedValue != null) {
					properties.put(propertyName, mappedValue);
				}
			});
		});
		return properties;
	}

	/**
	 * Retrieves properties related to video controllers, mapping them using a utility method.
	 *
	 * @return A map of video controller properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no video controller properties are found.
	 */
	private Map<String, String> getVideoControllerProperties() {
		if (this.videoControllers.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.videoControllers.entrySet().parallelStream().forEach(videoController -> {
			String groupName = String.format(Constant.GROUP_NAME_FORMAT, Constant.VC_GROUP, videoController.getKey());

			videoController.getValue().forEach((property, value) -> {
				String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, groupName, property.getName());
				String mappedProperty = Util.mapToVideoControllerProperty(property, value);
				if (mappedProperty != null) {
					properties.put(propertyName, mappedProperty);
				}
			});
		});
		return properties;
	}

	/**
	 * Retrieves properties related to source VC#IN#s, mapping them using a utility method.
	 *
	 * @return A map of source VC#IN# properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no source VC#IN# properties are found.
	 */
	private Map<String, String> getSourceVCINProperties() {
		if (this.sourceVCINs.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.sourceVCINs.entrySet().parallelStream().forEach(sourceVCIN -> {
			String groupName = String.format(Constant.GROUP_NAME_FORMAT, Constant.SOURCE_GROUP, sourceVCIN.getKey());

			sourceVCIN.getValue().forEach((property, value) -> {
				if (property.equals(SourceVCINProperty.INPUT_INFO)) {
					Map<SourceVCINProperty, String> mappedProperty = Util.mapToSourceVCINProperties(property, value);
					if (!mappedProperty.isEmpty()) {
						mappedProperty.forEach((k, v) -> properties.put(String.format(Constant.PROPERTY_NAME_FORMAT, groupName, k.getName()), v));
					}
				}
			});
		});
		return properties;
	}

	/**
	 * Retrieves properties related to zones, mapping them using a utility method.
	 *
	 * @return A map of zone properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no zone properties are found.
	 */
	private Map<String, String> getZoneProperties() {
		if (this.zones.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.zones.entrySet().parallelStream().forEach(zone -> {
			String groupName = String.format(Constant.GROUP_NAME_FORMAT, Constant.ZONE_GROUP, zone.getKey());

			zone.getValue().forEach((property, value) -> {
				String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, groupName, property.getName());
				String mappedProperty = Util.mapToZoneProperty(property, value);

				if (mappedProperty != null) {
					properties.put(propertyName, mappedProperty);
				}
			});
		});
		return properties;
	}

	/**
	 * Retrieves properties related to presets, mapping them using a utility method.
	 *
	 * @return A map of preset properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no preset properties are found.
	 */
	private Map<String, String> getPresetProperties() {
		if (this.presets.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.presets.forEach((property, value) -> {
			String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, Constant.PRESET_GROUP, property);
			String mappedProperty;
			if (property.matches(Constant.PRESET_REGEX)) {
				String presetID = Util.extractPresetID(property);
				mappedProperty = Util.isActivePreset(this.presets, presetID) ? "Active" : Constant.EMPTY;
			} else {
				mappedProperty = Util.mapToPresetProperty(property, value);
			}
			if (mappedProperty != null) {
				properties.put(propertyName, mappedProperty);
			}
		});
		return properties;
	}

	/**
	 * Retrieves properties related to network status, mapping them using a utility method.
	 *
	 * @return A map of network status properties with the property names as keys and their corresponding mapped values as values.
	 * Returns an empty map if no network status properties are found.
	 */
	private Map<String, String> getNetworkStatusProperties() {
		if (this.networkStatus.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();

		this.networkStatus.entrySet().parallelStream().forEach(property -> {
			String propertyName = String.format(Constant.PROPERTY_NAME_FORMAT, Constant.NETWORK_STATUS_GROUP, property.getKey().getName());
			String mappedProperty = Util.mapToNetworkStatusProperty(property.getKey(), property.getValue());

			if (mappedProperty != null) {
				properties.put(propertyName, mappedProperty);
			}
		});
		return properties;
	}

	/**
	 * Generates general control properties for the system.
	 * This includes:
	 * <ul>
	 *     <li>A toggle switch for system power (On/Off)</li>
	 *     <li>A button to initiate a system reboot</li>
	 * </ul>
	 * The current system power state is read from {@code vwGeneral} and converted to a binary string ("1" for ON, "0" for OFF).
	 *
	 * @return a list of {@link AdvancedControllableProperty} objects representing general control actions
	 */
	private List<AdvancedControllableProperty> getGeneralControllers() {
		List<AdvancedControllableProperty> controllers = new ArrayList<>();
		String powerStatus = Optional.ofNullable(this.vwGeneral.get(VWGeneralProperty.SYSTEM_POWER))
				.map(v -> "ON".equals(v) ? "1" : "0").orElse("0");
		controllers.add(this.generateControllableSwitch(VWGeneralProperty.SYSTEM_POWER.getName(), "On", "Off", powerStatus));
		controllers.add(this.generateControllableButton(VWGeneralProperty.SYSTEM_REBOOT.getName(), "Reboot", "Rebooting", REBOOT_TIME));

		return controllers;
	}

	/**
	 * Generates preset control buttons for recalling different preset configurations.
	 * <p>
	 * For each preset ID retrieved from {@code ids}, this method creates a button labeled "Recall",
	 * which when pressed changes to "Recalling". These buttons are used to trigger preset recalls.
	 * </p>
	 *
	 * @return a list of {@link AdvancedControllableProperty} objects representing preset recall buttons
	 */
	private List<AdvancedControllableProperty> getPresetControllers() {
		if (CollectionUtils.isEmpty(this.ids.get(IDProperty.PRESET_IDS))) {
			return Collections.emptyList();
		}
		List<AdvancedControllableProperty> controllers = new ArrayList<>();

		this.ids.get(IDProperty.PRESET_IDS).forEach(presetID -> {
			if (Util.isActivePreset(this.presets, presetID)) {
				return;
			}
			String buttonName = Constant.PRESET_GROUP + "#" + String.format(PresetProperty.PRESET.getName(), presetID);

			controllers.add(this.generateControllableButton(buttonName, "Recall", "Recalling", 0));
		});
		return controllers;
	}

	/**
	 * Retrieves dynamic temperature statistics by matching known temperature IDs with component statistics.
	 *
	 * @return a map of matching component statistics, or an empty map if none are found.
	 */
	Map<String, String> getDynamicStatistics(Map<String, String> statistics) {
		if (CollectionUtils.isEmpty(this.historicalProperties) || MapUtils.isEmpty(statistics)) {
			return Collections.emptyMap();
		}
		Map<String, String> dynamicStatistics = new HashMap<>();
		statistics.entrySet().parallelStream().filter(statistic -> {
			String[] components = statistic.getKey().split(" - ");

			return components.length > 1 && this.historicalProperties.contains(components[1]);
		}).forEach(statistic -> dynamicStatistics.put(statistic.getKey(), statistic.getValue()));

		return dynamicStatistics;
	}

	/**
	 * Generates an {@link AdvancedControllableProperty} of type Switch with the specified name, labels, and value.
	 *
	 * @param switchName the name of the switch control property
	 * @param labelOn the label to display when the switch is in the "on" position
	 * @param labelOff the label to display when the switch is in the "off" position
	 * @param value the initial value of the switch (should be a Boolean or compatible object)
	 * @return an {@link AdvancedControllableProperty} configured as a switch control
	 */
	private AdvancedControllableProperty generateControllableSwitch(String switchName, String labelOn, String labelOff, Object value) {
		AdvancedControllableProperty.Switch toggleSwitch = new AdvancedControllableProperty.Switch();
		toggleSwitch.setLabelOn(labelOn);
		toggleSwitch.setLabelOff(labelOff);

		AdvancedControllableProperty controllableProperty = new AdvancedControllableProperty();
		controllableProperty.setName(switchName);
		controllableProperty.setValue(value);
		controllableProperty.setTimestamp(new Date());
		controllableProperty.setType(toggleSwitch);

		return controllableProperty;
	}

	/**
	 * Generates an {@link AdvancedControllableProperty} of type Button with the specified name, labels, and grace period.
	 *
	 * @param buttonName the name of the button control property
	 * @param label the label to display on the button
	 * @param labelPressed the label to display when the button is pressed
	 * @param gracePeriod the time in milliseconds before the button can be pressed again
	 * @return an {@link AdvancedControllableProperty} configured as a button control
	 */
	private AdvancedControllableProperty generateControllableButton(String buttonName, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);

		AdvancedControllableProperty controllableProperty = new AdvancedControllableProperty();
		controllableProperty.setName(buttonName);
		controllableProperty.setTimestamp(new Date());
		controllableProperty.setValue("N/A");
		controllableProperty.setType(button);

		return controllableProperty;
	}

	/**
	 * Sends a control command to the device and validates the response.
	 * <p>
	 * This method is intended for sending operational commands to controllable devices.
	 * It sends the specified command, checks the device's response, and throws an exception if the response is invalid.
	 * </p>
	 *
	 * @param type the type of command being sent, used for error context
	 * @param command the raw command string to send to the device
	 * @throws NotImplementedException if the device returns an invalid response
	 */
	private void sendControlCommand(Command type, String command) {
		String response = this.send(command);
		if (Util.invalidValue(response)) {
			throw new NotImplementedException(Constant.RESPONSE_INVALIDATED + type.getName());
		}
	}

	/**
	 * Sends an ASCII command to the device and returns the processed response.
	 * <p>
	 * Appends a CR character to the command before sending. Returns {@code UNREACHABLE} if response is {@code [-1]}.
	 * Returns {@code ACK} if acknowledged. Returns {@code NONE} if response is invalid or lacks a colon.
	 * Otherwise, returns the value after the colon.
	 * </p>
	 *
	 * @param command the command to send
	 * @return processed response or a constant (ACK, NONE, UNREACHABLE)
	 * @throws ResourceNotReachableException if communication with the device fails
	 */
	private String send(String command) {
		try {
			byte[] request = command.concat(Constant.CR).getBytes(StandardCharsets.US_ASCII);
			byte[] response = super.send(request);
			if (response.length == 1 && response[0] == -1) {
				this.logger.error(Constant.COMMAND_UNREACHABLE + command);
				return Constant.UNREACHABLE;
			}
			String cleanedResponse = new String(response, StandardCharsets.US_ASCII)
					.replace("\"", Constant.EMPTY)
					.replace(Constant.CR, Constant.EMPTY);
			if (cleanedResponse.contains(Constant.ACK)) {
				return Constant.ACK;
			}
			if (Constant.INVALID_RESPONSE_PATTERN.matcher(cleanedResponse).find()) {
				this.logger.warn(Constant.COMMAND_INVALIDATED + command);
				return Constant.NONE;
			}
			String[] responseParts = cleanedResponse.split(Constant.COLON_REGEX, 2);
			if (responseParts.length <= 1) {
				this.logger.warn(Constant.RESPONSE_INVALIDATED + command);
				return Constant.NONE;
			}
			return responseParts[1].isEmpty() ? Constant.NONE : responseParts[1];
		} catch (Exception e) {
			throw new ResourceNotReachableException(Constant.COMMAND_FAILED, e);
		}
	}
}
