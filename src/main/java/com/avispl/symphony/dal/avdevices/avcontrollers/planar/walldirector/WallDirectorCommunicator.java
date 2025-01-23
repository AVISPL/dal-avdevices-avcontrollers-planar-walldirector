/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.ListIDCommand;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.WallDirectorCommandList;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.WallDirectorConstant;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums.ColorTemperatureEnum;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums.OutputModeEnum;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums.ZoneAspectEnum;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums.CabinetInputEnum;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.enums.InputInfoEnum;
import com.avispl.symphony.dal.communicator.SocketCommunicator;
import com.avispl.symphony.dal.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * WallDirectorCommunicator is a communicator class for Wall Director devices.
 * The communicator is based on the SocketCommunicator class and uses a socket-based protocol
 * to communicate with the devices. It provides functionality to monitor and control Wall Director devices
 * by implementing the Monitorable and Controller interfaces.
 * <p>
 * As part of the Monitorable interface, it provides methods to track device statuses
 * and report failures or anomalies. The Controller interface enables functionality such as
 * sending commands to control device behavior or retrieve specific configuration details.
 * <p>
 * The communication protocol for Wall Director devices requires specific command and response
 * formats, which are handled internally by this class. Error handling and retries are also
 * incorporated for robust communication.
 * <p>
 * Note: Detailed specifications of the Wall Director socket protocol should be referred to
 * in the Wall Director Communication Protocol Guide.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/7/2025
 * @since 1.0.0
 */
public class WallDirectorCommunicator extends SocketCommunicator implements Monitorable, Controller {

    /**
     * Stores the extended statistics related to the current state of the system.
     */
    private ExtendedStatistics localExtendedStatistics;

    /**
     * A reentrant lock used to ensure thread-safe operations on shared resources.
     */
    private final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * Indicates whether the system is currently in an emergency delivery mode.
     * When true, the system may bypass certain regular monitoring operations.
     */
    private boolean isEmergencyDelivery;

    /**
     * A set that tracks the IDs of monitors that have failed or encountered issues.
     */
    private final Set<String> failedMonitor = new HashSet<>();

    /**
     * A counter that tracks the number of monitoring and controlling commands
     * executed by the system.
     */
    private int countMonitoringAndControllingCommand = 0;

    /**
     * A list of IDs representing the power supply components being monitored
     * or controlled.
     */
    private List<String> powerSupplyIDList = new ArrayList<>();

    /**
     * A list of IDs representing the video controllers being monitored
     * or controlled.
     */
    private List<String> videoControllerIDList = new ArrayList<>();

    /**
     * A list of IDs representing the zones being monitored or controlled.
     */
    private List<String> zoneIDList = new ArrayList<>();

    /**
     * A list of IDs representing the presets being monitored or controlled.
     */
    private List<String> presetIDList = new ArrayList<>();

    /**
     * A list of IDs representing the panels being monitored or controlled.
     */
    private List<String> panelIDList = new ArrayList<>();

    /**
     * The number of source inputs supported by the system.
     */
    private final int sourceInputCount = 4;

    /**
     * Pool for keeping all the async operations in, to track any operations in progress and cancel them if needed
     */
    private final List<Future> devicesExecutionPool = new CopyOnWriteArrayList<>();

    /**
     * To avoid timeout errors, caused by the unavailability of the control protocol, all polling-dependent communication operations (monitoring)
     * should be performed asynchronously. This executor service executes such operations.
     */
    private ExecutorService fetchingDataExSer;
    private ExecutorService timeoutManagementExSer;

    /**
     * Configurable property for historical properties, comma separated values kept as set locally
     */
    private Set<String> historicalProperties = new HashSet<>();

    /**
     * List of property groups to display
     */
    private List<String> displayPropertyGroups = Collections.singletonList("All");

    /**
     * Local cache stores data after a period of time
     */
    private final Map<String, String> localCacheMapOfPropertyNameAndValue = new HashMap<>();

    /**
     * Timestamp of the latest command sent to a device.
     */
    private long lastCommandTimestamp;

    /**
     * Apply default delay in between of all the commands performed by the adapter.
     */
    private long commandsCoolDownDelay = 200;

    /**
     * store pollingInterval adapter properties
     */
    private String pollingInterval;

    /**
     * store configTimeout adapter properties
     */
    private String configTimeout;

    /**
     * store configManagement adapter properties
     */
    private String configManagement;

    /**
     * configManagement in boolean value
     */
    private boolean isConfigManagement;

    /**
     * Retrieves {@link #pollingInterval}
     *
     * @return value of {@link #pollingInterval}
     */
    public String getPollingInterval() {
        return pollingInterval;
    }

    /**
     * Sets {@link #pollingInterval} value
     *
     * @param pollingInterval new value of {@link #pollingInterval}
     */
    public void setPollingInterval(String pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    /**
     * Retrieves {@link #configTimeout}
     *
     * @return value of {@link #configTimeout}
     */
    public String getConfigTimeout() {
        return configTimeout;
    }

    /**
     * Sets {@link #configTimeout} value
     *
     * @param configTimeout new value of {@link #configTimeout}
     */
    public void setConfigTimeout(String configTimeout) {
        this.configTimeout = configTimeout;
    }

    /**
     * Retrieves {@link #configManagement}
     *
     * @return value of {@link #configManagement}
     */
    public String getConfigManagement() {
        return configManagement;
    }

    /**
     * Sets {@link #configManagement} value
     *
     * @param configManagement new value of {@link #configManagement}
     */
    public void setConfigManagement(String configManagement) {
        this.configManagement = configManagement;
    }

    /**
     * Retrieves {@link #historicalProperties}
     *
     * @return value of {@link #historicalProperties}
     */
    public String getHistoricalProperties() {
        return String.join(",", this.historicalProperties);
    }

    /**
     * Sets {@link #historicalProperties} value
     *
     * @param historicalProperties new value of {@link #historicalProperties}
     */
    public void setHistoricalProperties(String historicalProperties) {
        this.historicalProperties.clear();
        Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
            this.historicalProperties.add(propertyName.trim());
        });
    }

    /**
     * Retrieves {@link #displayPropertyGroups}
     *
     * @return value of {@link #displayPropertyGroups}
     */
    public String getDisplayPropertyGroups() {
        return String.join(",", displayPropertyGroups);
    }

    /**
     * Sets {@link #displayPropertyGroups} value
     *
     * @param displayPropertyGroups new value of {@link #displayPropertyGroups}
     */
    public void setDisplayPropertyGroups(String displayPropertyGroups) {
        this.displayPropertyGroups = Arrays.stream(displayPropertyGroups.split(",")).map(String::trim).filter(StringUtils::isNotNullOrEmpty).collect(Collectors.toList());
    }

    /**
     * Constructor for WallDirectorCommunicator.
     * Initializes port, success, and error command lists with default values.
     */
    public WallDirectorCommunicator() {
        super();
        this.setPort(this.getPort());
        this.setCommandSuccessList(Collections.singletonList("\r\n"));
        this.setCommandErrorList(Collections.singletonList("\r\n"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override the send() method to add a cool down delay time after every send command
     */
    @Override
    public byte[] send(byte[] data) throws Exception {
        try {
            long currentTime = System.currentTimeMillis() - lastCommandTimestamp;
            //check next command wait commandsCoolDownDelay time
            if (currentTime < commandsCoolDownDelay) {
                Thread.sleep(commandsCoolDownDelay - currentTime);
            }
            lastCommandTimestamp = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Issuing command %s, timestamp: %s", data, lastCommandTimestamp));
            }
            return super.send(data);
        } finally {
            logger.debug("send data command successfully");
        }
    }

    @Override
    public void controlProperty(ControllableProperty controllableProperty) throws Exception {

    }

    @Override
    public void controlProperties(List<ControllableProperty> list) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("ControllableProperties can not be null or empty");
        }
        for (ControllableProperty p : list) {
            try {
                controlProperty(p);
            } catch (Exception e) {
                logger.error(String.format("Error when control property %s", p.getProperty()), e);
            }
        }
    }

    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        reentrantLock.lock();
        try {
            ExtendedStatistics extendedStatistics = new ExtendedStatistics();
            Map<String, String> stats = new HashMap<>();
            List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
            Map<String, String> dynamicStats = new HashMap<>();
            if (countMonitoringAndControllingCommand == 0) {
                countMonitoringAndControllingCommand = getNumberMonitoringAndControllingCommand();
            }
            if (!isEmergencyDelivery) {
                convertConfigManagement();
                failedMonitor.clear();
                retrieveListID();
                retrieveMonitoringAndControllingData();
                if (failedMonitor.size() == countMonitoringAndControllingCommand) {
                    throw new ResourceNotReachableException(String.format("There was an error while retrieving monitoring data for all %s properties.", countMonitoringAndControllingCommand));
                }
                destroyChannel();
                boolean showAllGroups = displayPropertyGroups.contains("All") || displayPropertyGroups.isEmpty();
                populateGeneralData(stats, advancedControllableProperties);
                populateNetworkStatus(stats);
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.PANEL)) {
                    populatePanel(stats, advancedControllableProperties, dynamicStats);
                }
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.POWER_SUPPLY)) {
                    populatePowerSupplies(stats, advancedControllableProperties, dynamicStats);
                }
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.SOURCE)) {
                    populateSources(stats, advancedControllableProperties);
                }
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.VIDEO_CONTROLLER)) {
                    populateVideoControllers(stats, advancedControllableProperties, dynamicStats);
                }
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.ZONE)) {
                    populateZones(stats, advancedControllableProperties);
                }
                if (showAllGroups || displayPropertyGroups.contains(WallDirectorConstant.PRESET)) {
                    populatePresets(stats, advancedControllableProperties);
                }
                extendedStatistics.setStatistics(stats);
                extendedStatistics.setDynamicStatistics(dynamicStats);
                extendedStatistics.setControllableProperties(advancedControllableProperties);
                localExtendedStatistics = extendedStatistics;
            }
            isEmergencyDelivery = false;
        } finally {
            reentrantLock.unlock();
        }
        return Collections.singletonList(localExtendedStatistics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalInit() throws Exception {
        fetchingDataExSer = Executors.newFixedThreadPool(1);
        timeoutManagementExSer = Executors.newFixedThreadPool(1);
        super.internalInit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalDestroy() {
        countMonitoringAndControllingCommand = 0;
        if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
            localExtendedStatistics.getStatistics().clear();
            localExtendedStatistics.getControllableProperties().clear();
        }
        if (!localCacheMapOfPropertyNameAndValue.isEmpty()) {
            localCacheMapOfPropertyNameAndValue.clear();
        }
        isEmergencyDelivery = false;
        isConfigManagement = false;
        failedMonitor.clear();
        try {
            fetchingDataExSer.shutdownNow();
            timeoutManagementExSer.shutdownNow();
        } catch (Exception e) {
            logger.warn("Unable to end the TCP connection.", e);
        } finally {
            super.internalDestroy();
        }
    }

    /**
     * Retrieves all ID lists by sequentially executing the appropriate commands.
     */
    private void retrieveListID() {
        powerSupplyIDList = retrieveIDs(ListIDCommand.POWER_SUPPLY);
        videoControllerIDList = retrieveIDs(ListIDCommand.VIDEO_CONTROLLER);
        zoneIDList = retrieveIDs(ListIDCommand.ZONE);
        presetIDList = retrieveIDs(ListIDCommand.PRESET);
        panelIDList = retrieveIDs(ListIDCommand.PANEL);
    }

    /**
     * Retrieves IDs for a specific command.
     *
     * @param command the command for which to retrieve IDs
     * @return a list of IDs, or an empty list if the response is null or invalid
     */
    private List<String> retrieveIDs(ListIDCommand command) {
        String response = sendRequest(command.getName(), command.getCommand());
        if (response != null) {
            String cleanedResponse = response.replace(command.getCommand().concat(WallDirectorConstant.COLON), WallDirectorConstant.EMPTY).trim();
            return Arrays.asList(cleanedResponse.split(WallDirectorConstant.SPACE_REGEX));
        }
        // Return an empty list if the response is null
        return new ArrayList<>();
    }

    /**
     * Retrieves data based on the given command property.
     *
     * @param property the command property from which to retrieve data
     */
    private void retrieveDataByCommandName(WallDirectorCommandList property) {
        if (WallDirectorConstant.EMPTY.equals(property.getCommand())) {
            return;
        }
        if (!isConfigManagement && property.isControl()) {
            return;
        }
        List<String> commands = handleCommand(property);
        for (int i = 0; i < commands.size(); i++) {
            String group = commands.size() > 1 ? property.getGroup().concat("0" + (i + 1)) : property.getGroup();
            String response = sendRequest(property.getName(), commands.get(i));
            if (response != null) {
                if (property.getGroup().equals(WallDirectorConstant.SOURCE)) {
                    group = "Source_" + extractVCIN(commands.get(i));
                }
                handleResponse(group, property.getName(), commands.get(i), response);
            }
        }
    }

    /**
     * Extracts the VC# and IN# substring from the given input string.
     *
     * @param input the input string containing the VC# and IN# pattern
     * @return the extracted VC# and IN# substring, or an empty string if not found
     */
    private String extractVCIN(String input) {
        String regex = "VC\\d+\\.IN\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group();
        }
        return WallDirectorConstant.EMPTY;
    }

    /**
     * Handles the response for a given group, property name, command, and response string.
     *
     * @param group    the group name
     * @param name     the property name
     * @param command  the command string
     * @param response the response string
     */
    private void handleResponse(String group, String name, String command, String response) {
        String propertyName = WallDirectorConstant.EMPTY.equals(group) ? name : group.concat(WallDirectorConstant.HASH).concat(name);

        if (response.contains(command)) {
            String value = response.replace(command.concat(WallDirectorConstant.COLON), WallDirectorConstant.EMPTY);
            localCacheMapOfPropertyNameAndValue.put(propertyName, value);
        }
    }

    /**
     * Generates a list of commands based on the provided property and its group.
     *
     * @param property the WallDirector command property
     * @return a list of formatted command strings
     */
    private List<String> handleCommand(WallDirectorCommandList property) {
        List<String> commands = new ArrayList<>();

        // Determine the commands to generate based on the group
        switch (property.getGroup()) {
            case WallDirectorConstant.POWER_SUPPLY:
                addCommandsForItems(commands, powerSupplyIDList, property.getCommand());
                break;
            case WallDirectorConstant.VIDEO_CONTROLLER:
                addCommandsForItems(commands, videoControllerIDList, property.getCommand());
                break;
            case WallDirectorConstant.ZONE:
                addCommandsForItems(commands, zoneIDList, property.getCommand());
                break;
            case WallDirectorConstant.PANEL:
                addCommandsForItems(commands, panelIDList, property.getCommand());
                break;
            case WallDirectorConstant.PRESET:
                handlePresetCommands(property, commands);
                break;
            case WallDirectorConstant.SOURCE:
                handleSourceCommands(commands, property);
                break;
            default:
                commands.add(property.getCommand());
                break;
        }
        return commands;
    }

    /**
     * Adds formatted commands to the list for each item in the provided ID list.
     *
     * @param commands the list to add commands to
     * @param idList   the list of IDs to use for formatting the commands
     * @param command  the command template
     */
    private void addCommandsForItems(List<String> commands, List<String> idList, String command) {
        for (String item : idList) {
            commands.add(String.format(command, item));
        }
    }

    /**
     * Handles the generation of preset commands.
     *
     * @param property the WallDirector command property
     * @param commands the list to add commands to
     */
    private void handlePresetCommands(WallDirectorCommandList property, List<String> commands) {
        if (property.equals(WallDirectorCommandList.PRESET_ACTIVE)) {
            commands.add(property.getCommand());
        } else {
            addCommandsForItems(commands, presetIDList, property.getCommand());
        }
    }

    /**
     * Handles the generation of source commands.
     *
     * @param commands the list to add commands to
     * @param property the WallDirector command property
     */
    private void handleSourceCommands(List<String> commands, WallDirectorCommandList property) {
        for (int i = 1; i <= sourceInputCount; i++) {
            for (String item : videoControllerIDList) {
                commands.add(String.format(property.getCommand(), item, i));
            }
        }
    }

    /**
     * Sends a command request and retrieves the response as a string.
     *
     * @param name    the property name associated with the command
     * @param command the command to send
     * @return the response as a string, or {@code null} if an error occurs or no response is received
     */
    private String sendRequest(String name, String command) {
        try {
            byte[] response = send(command.concat("?").getBytes(StandardCharsets.UTF_8));
            if (response == null || response.length == 0) {
                logger.error(String.format("Error when retrieving property name: %s", name));
                return null;
            }
            String result = new String(response, StandardCharsets.UTF_8);
            return result.replace("\n", WallDirectorConstant.EMPTY);
        } catch (Exception e) {
            logger.error(String.format("Error when retrieving property name: %s", name), e);
        }
        return null;
    }

    /**
     * This method is used to validate input config management from user
     */
    private void convertConfigManagement() {
        isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase("true");
    }

    /**
     * get number commands base on monitor or control
     *
     * @return a number of properties
     */
    private int getNumberMonitoringAndControllingCommand() {
        return (int) Arrays.stream(WallDirectorCommandList.values())
                .filter(item -> !item.getCommand().isEmpty())  // Filter out items with empty command
                .count();
    }

    /**
     * Using multi thread to implement get request
     * Thread 1 retrieves data.
     * Thread 2 manage the request timeout of thread 1
     */
    private void retrieveMonitoringAndControllingData() {
        List<WallDirectorCommandList> commands = Arrays.asList(WallDirectorCommandList.values());
        for (int i = 0; i < commands.size(); i++) {
            WallDirectorCommandList commandIndex = commands.get(i);
            // submit a thread to fetch data from the device.
            devicesExecutionPool.add(fetchingDataExSer.submit(() -> {
                retrieveDataByCommandName(commandIndex);
            }));

            //Using 2nd thread to monitor timeouts for commands executed in thread 1
            Future manageTimeOutWorkerThread = timeoutManagementExSer.submit(() -> {
                int timeoutCount = 1;
                while (!devicesExecutionPool.get(devicesExecutionPool.size() - 1).isDone() && timeoutCount <= 20) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.warn("Timeout worker interrupted exception", e);
                    }
                    timeoutCount++;
                }
                //If the Future is not completed after the defaultConfigTimeout =>  update the failedMonitor and destroy the connection.
                int lastIndex = devicesExecutionPool.size() - 1;
                if (!devicesExecutionPool.get(lastIndex).isDone()) {
                    if (StringUtils.isNotNullOrEmpty(commandIndex.getCommand())) {
                        failedMonitor.add(commandIndex.getGroup() + "#" + commandIndex.getName());
                    }
                    destroyChannel();
                    localCacheMapOfPropertyNameAndValue.remove(commandIndex.getName());
                    devicesExecutionPool.get(lastIndex).cancel(true);
                }
            });
            try {
                while (!manageTimeOutWorkerThread.isDone()) {
                    Thread.sleep(100);
                }
                manageTimeOutWorkerThread.get();
            } catch (Exception e) {
                logger.error(String.format("There was an error encountered while attempting to retrieve the name of the command: %s", commandIndex.getName()), e);
            }
        }
        devicesExecutionPool.removeIf(Future::isDone);
    }

    /**
     * Filters the list of {@link WallDirectorCommandList} enums by a specific group.
     *
     * @param group the group name to filter the enums by
     * @return a list of {@link WallDirectorCommandList} enums that belong to the specified group
     */
    private List<WallDirectorCommandList> filterByGroup(String group) {
        return Arrays.stream(WallDirectorCommandList.values())
                .filter(e -> e.getGroup().equals(group) && (isConfigManagement || !e.isControl()))
                .collect(Collectors.toList());
    }

    /**
     * Populates general data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store general properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populateGeneralData(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.EMPTY)) {
            String propertyName = item.getName();
            String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
            if (WallDirectorConstant.NONE.equals(value)) {
                stats.put(propertyName, value);
                continue;
            }
            switch (item) {
                case BACKLIGHT_MODE:
                    addAdvancedControlProperties(advancedControllableProperties, stats,
                            createSwitch(propertyName, "AUTO".equals(value) ? 0 : 1, WallDirectorConstant.AUTO, WallDirectorConstant.MANUAL), value);
                    break;
                case POWER_STANDBY:
                    addAdvancedControlProperties(advancedControllableProperties, stats,
                            createSwitch(propertyName, "FAST.START".equals(value) ? 0 : 1, "Fast Start", "Low Power"), value);
                    break;
                case SYSTEM_REBOOT:
                    addAdvancedControlProperties(advancedControllableProperties, stats,
                            createButton(propertyName, WallDirectorConstant.REBOOT, WallDirectorConstant.REBOOTING, 0), WallDirectorConstant.EMPTY);
                    break;
                case SYSTEM_POWER:
                    addAdvancedControlProperties(advancedControllableProperties, stats,
                            createSwitch(propertyName, "OFF".equals(value) ? 0 : 1, WallDirectorConstant.OFF, WallDirectorConstant.ON), value);
                    break;
                default:
                    stats.put(propertyName, value);
            }
        }
    }

    /**
     * Populates power supply data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store power supply properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populatePowerSupplies(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> dynamicStats) {
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.POWER_SUPPLY)) {
            for (int i = 0; i < powerSupplyIDList.size(); i++) {
                String propertyName = powerSupplyIDList.size() > 1 ? item.getGroup().concat("0" + (i + 1)).concat(WallDirectorConstant.HASH).concat(item.getName())
                        : item.getGroup().concat(WallDirectorConstant.HASH).concat(item.getName());
                String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
                if (WallDirectorConstant.NONE.equals(value)) {
                    stats.put(propertyName, value);
                    continue;
                }
                switch (item) {
                    case PS_REBOOT:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createButton(propertyName, WallDirectorConstant.REBOOT, WallDirectorConstant.REBOOTING, 0), WallDirectorConstant.EMPTY);
                        break;
                    case PS_TEMPERATURE:
                        boolean propertyListed = false;
                        if (!historicalProperties.isEmpty()) {
                            propertyListed = historicalProperties.contains(propertyName);
                        }
                        if (propertyListed) {
                            dynamicStats.put(propertyName, value);
                        } else {
                            stats.put(propertyName, value);
                        }
                        break;
                    default:
                        stats.put(propertyName, value);
                        break;
                }
            }
        }
    }

    /**
     * Populates video controller data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store video controller properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populateVideoControllers(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> dynamicStats) {
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.VIDEO_CONTROLLER)) {
            for (int i = 0; i < videoControllerIDList.size(); i++) {
                String propertyName = videoControllerIDList.size() > 1 ? item.getGroup().concat("0" + (i + 1)).concat(WallDirectorConstant.HASH).concat(item.getName())
                        : item.getGroup().concat(WallDirectorConstant.HASH).concat(item.getName());
                String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
                if (WallDirectorConstant.NONE.equals(value)) {
                    stats.put(propertyName, value);
                    continue;
                }
                switch (item) {
                    case VC_TEMPERATURE:
                        boolean propertyListed = false;
                        if (!historicalProperties.isEmpty()) {
                            propertyListed = historicalProperties.contains(propertyName);
                        }
                        if (propertyListed) {
                            dynamicStats.put(propertyName, value);
                        } else {
                            stats.put(propertyName, value);
                        }
                        break;
                    case VC_REBOOT:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createButton(propertyName, WallDirectorConstant.REBOOT, WallDirectorConstant.REBOOTING, 0), WallDirectorConstant.EMPTY);
                        break;
                    case OUTPUT_MODE:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createDropdown(propertyName, OutputModeEnum.toArray(), value), value);
                        break;
                    default:
                        stats.put(propertyName, value);
                        break;
                }
            }
        }
    }

    /**
     * Populates network status properties into the provided stats map.
     *
     * @param stats the map to store network status properties and values
     */
    private void populateNetworkStatus(Map<String, String> stats) {
        List<WallDirectorCommandList> generalList = filterByGroup(WallDirectorConstant.NETWORK);
        for (WallDirectorCommandList item : generalList) {
            String propertyName = item.getGroup() + WallDirectorConstant.HASH + item.getName();
            String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
            stats.put(propertyName, value);
        }
    }

    /**
     * Populates zone data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store zone properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populateZones(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
        List<WallDirectorCommandList> zoneList = filterByGroup(WallDirectorConstant.ZONE);
        for (WallDirectorCommandList item : zoneList) {
            for (int i = 0; i < zoneIDList.size(); i++) {
                String propertyName = zoneIDList.size() > 1 ? item.getGroup().concat("0" + (i + 1)).concat(WallDirectorConstant.HASH).concat(item.getName())
                        : item.getGroup().concat(WallDirectorConstant.HASH).concat(item.getName());
                String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
                if (WallDirectorConstant.NONE.equals(value)) {
                    stats.put(propertyName, value);
                    continue;
                }
                switch (item) {
                    case ZONE_ORDER:
                    case EXPECTED_SOURCE_HEIGHT:
                    case EXPECTED_SOURCE_WIDTH:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createNumeric(propertyName, value), value);
                        break;
                    case ZONE_ASPECT:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createDropdown(propertyName, ZoneAspectEnum.toArray(), value), value);
                        break;
                    default:
                        stats.put(propertyName, value);
                        break;
                }
            }
        }
    }

    /**
     * Populates preset data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store preset properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populatePresets(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.PRESET)) {
            if (item.equals(WallDirectorCommandList.PRESET_NAME)) {
                continue;
            }
            String properName = item.getGroup().concat(WallDirectorConstant.HASH).concat(item.getName());
            String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(properName));
            stats.put(properName, value);
        }
        if (isConfigManagement) {
            for (int i = 0; i < presetIDList.size(); i++) {
                String presetName = (presetIDList.size() > 1)
                        ? WallDirectorConstant.PRESET + "0" + (i + 1) + WallDirectorConstant.HASH + WallDirectorConstant.PRESET_NAME
                        : WallDirectorConstant.PRESET + WallDirectorConstant.HASH + WallDirectorConstant.PRESET_NAME;
                String propertyName = "Preset#Preset" + (i + 1) + localCacheMapOfPropertyNameAndValue.get(presetName);
                addAdvancedControlProperties(advancedControllableProperties, stats,
                        createButton(propertyName, WallDirectorConstant.RECALL, WallDirectorConstant.RECALLING, 0), WallDirectorConstant.EMPTY);
            }
        }
    }

    /**
     * Populates source data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store source properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     */
    private void populateSources(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.SOURCE)) {
            for (String s : videoControllerIDList) {
                for (int j = 1; j <= sourceInputCount; j++) {
                    String group = String.format("Source_VC%s.IN%s", s, j);
                    String propertyName = group.concat(WallDirectorConstant.HASH) + item.getName();
                    String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
                    if (WallDirectorConstant.NONE.equals(value)) {
                        stats.put(propertyName, value);
                        continue;
                    }
                    switch (item) {
                        case INPUT_BRIGHTNESS:
                        case INPUT_CONTRAST:
                            addAdvancedControlProperties(advancedControllableProperties, stats,
                                    createSlider(stats, propertyName.concat("(%)"), "0", "100", 0f, 100f, Float.valueOf(value)), value);
                            stats.put(propertyName.concat(WallDirectorConstant.CURRENT_VALUE).concat("(%)"), value);
                            break;
                        case INPUT_INFO:
                            String[] parts = value.split(WallDirectorConstant.SPACE_REGEX);
                            for (InputInfoEnum property : InputInfoEnum.values()) {
                                int position = property.getPosition();
                                if (position < parts.length) {
                                    if (property.equals(InputInfoEnum.HEIGHT) || property.equals(InputInfoEnum.WIDTH)) {
                                        stats.put(group + WallDirectorConstant.HASH + "Resolution", parts[InputInfoEnum.WIDTH.getPosition()] + WallDirectorConstant.COLON
                                                + parts[InputInfoEnum.HEIGHT.getPosition()]);
                                    } else {
                                        stats.put(group + WallDirectorConstant.HASH + property.getName(), parts[position]);
                                    }
                                }
                            }
                            break;
                        default:
                            stats.put(propertyName, value);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Populates panel data with advanced controllable properties and statistics.
     *
     * @param stats                          the map to store panel properties and their values
     * @param advancedControllableProperties the list to store advanced controllable properties
     * @param dynamicStats                   the map to store panel properties and their values
     */
    private void populatePanel(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> dynamicStats) {
        String[] colors = {"Red", "Green", "Blue"};
        for (WallDirectorCommandList item : filterByGroup(WallDirectorConstant.PANEL)) {
            for (int i = 0; i < panelIDList.size(); i++) {
                String group = panelIDList.size() > 1 ? item.getGroup().concat("0" + (i + 1)) : item.getGroup();
                String propertyName = group.concat(WallDirectorConstant.HASH).concat(item.getName());
                String value = getDefaultValueForNullData(localCacheMapOfPropertyNameAndValue.get(propertyName));
                if (WallDirectorConstant.NONE.equals(value)) {
                    stats.put(propertyName, value);
                    continue;
                }
                switch (item) {
                    case PANEL_TEMPERATURE:
                        boolean propertyListed = false;
                        if (!historicalProperties.isEmpty()) {
                            propertyListed = historicalProperties.contains(propertyName);
                        }
                        if (propertyListed) {
                            dynamicStats.put(propertyName, value);
                        } else {
                            stats.put(propertyName, value);
                        }
                        break;
                    case BALANCE_TEMPERATURE:
                        addAdvancedControlProperties(advancedControllableProperties, stats,
                                createDropdown(propertyName, ColorTemperatureEnum.toArray(), value), value);
                        break;
                    case PANEL_POSITION:
                        String[] position = value.trim().split(WallDirectorConstant.SPACE_REGEX);
                        if (position.length != 2) {
                            logger.error("Invalid position format when populate Position");
                        }
                        stats.put(group.concat(WallDirectorConstant.HASH).concat("PanelColumns"), position[0]);
                        stats.put(group.concat(WallDirectorConstant.HASH).concat("PanelRows"), position[1]);
                        break;
                    case WHITE_BALANCE:
                        String[] whiteColorValues = value.trim().split(WallDirectorConstant.SPACE_REGEX);
                        if (whiteColorValues.length != 3) {
                            logger.error("Invalid position format when populate Color White Balance");
                        }
                        for (int index = 0; index < colors.length; index++) {
                            String name = "ColorWhiteBalance" + colors[index];
                            String valueColor = whiteColorValues[index];
                            addAdvancedControlProperties(advancedControllableProperties, stats,
                                    createSlider(stats, group.concat(WallDirectorConstant.HASH).concat(name).concat("(%)"), "0", "100", 0f, 100f, Float.valueOf(valueColor)), valueColor);
                            stats.put(group.concat(WallDirectorConstant.HASH).concat(name).concat(WallDirectorConstant.CURRENT_VALUE).concat("(%)"), valueColor);
                        }
                        break;
                    case GRAY_BALANCE_GAMMA:
                        String[] grayColorValues = value.trim().split(WallDirectorConstant.SPACE_REGEX);
                        if (grayColorValues.length != 3) {
                            logger.error("Invalid position format when populate Color Gray Balance");
                        }
                        for (int index = 0; index < colors.length; index++) {
                            String name = group.concat(WallDirectorConstant.HASH).concat("ColorGrayBalance").concat(colors[index]);
                            String valueColor = grayColorValues[index];
                            addAdvancedControlProperties(advancedControllableProperties, stats,
                                    createSlider(stats, name, "1.8", "2.6", 1.8f, 2.6f, Float.valueOf(valueColor)), valueColor);
                            stats.put(name.concat(WallDirectorConstant.CURRENT_VALUE), valueColor);
                        }
                        break;
                    case CABINET_INPUT:
                        addAdvancedControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, CabinetInputEnum.toArray(), value), value);
                        break;
                    default:
                        stats.put(propertyName, value);
                        break;
                }
            }
        }
    }

    /**
     * check value is null or empty
     *
     * @param value input value
     * @return value after checking
     */
    private String getDefaultValueForNullData(String value) {
        return StringUtils.isNotNullOrEmpty(value) ? value.replace("\"", "") : WallDirectorConstant.NONE;
    }

    /**
     * Adds or updates an advanced controllable property and its value in the provided lists.
     *
     * @param advancedControllableProperties the list of controllable properties
     * @param stats                          the map of stats to update with the property value
     * @param property                       the property to add or update
     * @param value                          the associated value
     */
    private void addAdvancedControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
        if (property != null) {
            advancedControllableProperties.removeIf(controllableProperty -> controllableProperty.getName().equals(property.getName()));

            String propertyValue = StringUtils.isNotNullOrEmpty(value) ? value : WallDirectorConstant.EMPTY;
            stats.put(property.getName(), propertyValue);

            advancedControllableProperties.add(property);
        }
    }

    /**
     * Create switch is control property for metric
     *
     * @param name   the name of property
     * @param status initial status (0|1)
     * @return AdvancedControllableProperty switch instance
     */
    private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
        AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
        toggle.setLabelOff(labelOff);
        toggle.setLabelOn(labelOn);

        AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
        advancedControllableProperty.setName(name);
        advancedControllableProperty.setValue(status);
        advancedControllableProperty.setType(toggle);
        advancedControllableProperty.setTimestamp(new Date());

        return advancedControllableProperty;
    }

    /***
     * Create dropdown advanced controllable property
     *
     * @param name the name of the control
     * @param initialValue initial value of the control
     * @return AdvancedControllableProperty dropdown instance
     */
    private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
        AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
        dropDown.setOptions(values);
        dropDown.setLabels(values);

        return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
    }

    /**
     * Create numeric is control property for metric
     *
     * @param name        the name of the property
     * @param stringValue character string
     * @return AdvancedControllableProperty Text instance
     */
    private AdvancedControllableProperty createNumeric(String name, String stringValue) {
        AdvancedControllableProperty.Numeric text = new AdvancedControllableProperty.Numeric();
        return new AdvancedControllableProperty(name, new Date(), text, stringValue);
    }

    /***
     * Create AdvancedControllableProperty slider instance
     *
     * @param stats extended statistics
     * @param name name of the control
     * @param initialValue initial value of the control
     * @return AdvancedControllableProperty slider instance
     */
    private AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
        stats.put(name, initialValue.toString());
        AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
        slider.setLabelStart(labelStart);
        slider.setLabelEnd(labelEnd);
        slider.setRangeStart(rangeStart);
        slider.setRangeEnd(rangeEnd);

        return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
    }

    /**
     * Create a button.
     *
     * @param name         name of the button
     * @param label        label of the button
     * @param labelPressed label of the button after pressing it
     * @param gracePeriod  grace period of button
     * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
     */
    private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
        AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
        button.setLabel(label);
        button.setLabelPressed(labelPressed);
        button.setGracePeriod(gracePeriod);
        return new AdvancedControllableProperty(name, new Date(), button, "");
    }
}