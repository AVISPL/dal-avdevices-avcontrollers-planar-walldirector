/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common;

/**
 * Enum representing a list of commands and their associated properties
 * for controlling the Wall Director system.
 * Each enum constant is associated with a name, a command, a group, and a controllability flag.
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 1/17/2025
 * @since 1.0.0
 */
public enum WallDirectorCommandList {
    PANEL_MODEL("PanelModel", "WALL.MODEL","", false),
    POWER_STANDBY("PowerStandby", "STANDBY.MODE","", true),
    COLUMNS("PanelColumns", "MATRIX.LAYOUT.COLUMNS","", false),
    ROWS("PanelRows", "MATRIX.LAYOUT.ROWS","", false),
    BACKLIGHT_MODE("BacklightMode", "BACKLIGHT.MODE","", true),
    BACKLIGHT_INTENSITY("BacklightIntensity", "BACKLIGHT.INTENSITY","", true),
    FRAME_HEIGHT("FrameHeight", "WALL.HEIGHT","", true),
    FRAME_WIDTH("FrameWidth", "WALL.WIDTH","", true),
    SYSTEM_POWER("SystemPower", "SYSTEM.POWER","", true),
    SYSTEM_REBOOT("SystemReboot", "","", true),

    PS_ID("ID", "ID(PS%s)","PowerSupply", false),
    PS_MODEL_NAME("ModelName", "MODEL.NAME(PS%s)","PowerSupply", false),
    PS_SERIAL_NUMBER("SerialNumber", "SERIAL.NUMBER(PS%s)","PowerSupply", false),
    PS_TEMPERATURE("Temperature(C)", "TEMPERATURE(PS%s)","PowerSupply", false),
    PS_FIRMWARE_VERSION("FirmwareVersion", "FIRMWARE.VERSION(PS%s)","PowerSupply", false),
    PS_REBOOT("Reboot", "","PowerSupply", true),

    VC_ID("ID", "ID(VC%s)","VideoController", false),
    VC_MODEL_NAME("ModelName", "MODEL.NAME(VC%s)","VideoController", false),
    VC_SERIAL_NUMBER("SerialNumber", "SERIAL.NUMBER(VC%s)","VideoController", false),
    VC_TEMPERATURE("Temperature(C)", "TEMPERATURE(VC%s)","VideoController", false),
    VC_FIRMWARE_VERSION("FirmwareVersion", "FIRMWARE.VERSION(VC%s)","VideoController", false),
    VC_FAN_STATUS("FanStatus", "FAN.STATUS(VC%s)","VideoController", false),
    OUTPUT_MODE("OutputMode", "OUTPUT.MODE(VC%s)","VideoController", true),
    VC_REBOOT("Reboot", "","VideoController", true),

    SYSTEM_STATE("SystemState", "SYSTEM.STATE","Network", false),
    HOSTNAME("Hostname", "HOSTNAME","Network", false),
    DHCP("DHCP", "NETWORK.DHCP","Network", false),
    IP_ADDRESS("IPAddress", "IPV4.ADDRESS","Network", false),
    SUBNET_MASK("SubnetMask", "IPV4.NETMASK","Network", false),
    GATEWAY("Gateway", "IPV4.GATEWAY","Network", false),
    MAC_ADDRESS("MACAddress", "NETWORK.MAC","Network", false),
    DNS_1("DNS1", "NETWORK.DNS1","Network", false),
    DNS_2("DNS2", "NETWORK.DNS2","Network", false),

    ZONE_INPUT("Input", "ZONE.INPUT(%s)","Zone", false),
    ZONE_SOURCE("Source", "ZONE.SOURCE(%s)","Zone", false),
    ZONE_ASPECT("Aspect", "ZONE.ASPECT(%s)","Zone", true),
    EXPECTED_SOURCE_HEIGHT("ExpectedSourceHeight", "ZONE.EXPECTED.SOURCE.HEIGHT(%s)","Zone", true),
    EXPECTED_SOURCE_WIDTH("ExpectedSourceWidth", "ZONE.EXPECTED.SOURCE.WIDTH(%s)","Zone", true),
    ZONE_ORDER("OrderPosition", "ZONE.ORDER(%s)","Zone", true),

    PRESET_ACTIVE("PresetActiveName", "PRESET.ACTIVE","Preset", false),
    PRESET_NAME("PresetName", "PRESET.NAME(%s)","Preset", false),

    PANEL_ID("ID", "ID(PN%s)","Panel", false),
    PANEL_MODEL_NAME("ModelName", "MODEL.NAME(PN%s)","Panel", false),
    PANEL_SERIAL_NUMBER("SerialNumber", "SERIAL.NUMBER(PN%s)","Panel", false),
    PANEL_TEMPERATURE("Temperature(C)", "TEMPERATURE(PN%s)","Panel", false),
    PANEL_FIRMWARE_VERSION("FirmwareVersion", "FIRMWARE.VERSION(PN%s)","Panel", false),
    PANEL_VOLTAGE("Voltage(V)", "VOLTAGE(PN%s)","Panel", false),
    CABLE_LENGTH("CableLength", "PANEL.RX.LENGTH(PN%s)","Panel", false),
    SIGNAL_QUALITY("SignalQuality", "PANEL.SIGNAL.QUALITY(PN%s)","Panel", false),
    PANEL_POSITION("Position", "PANEL.POSITION(PN%s)","Panel", false),
    CONNECTION("VideoControllerOutput", "CONNECTION(PN%s)","Panel", false),
    BALANCE_TEMPERATURE("ColorWhiteBalanceTemperature", "COLOR.TEMP(PN%s)","Panel", true),
    WHITE_BALANCE("WhiteBalance", "WHITE.BALANCE(%s ALL)","Panel", true),
    GRAY_BALANCE_GAMMA("GrayBalanceGamma", "GRAY.BALANCE.GAMMA(%s ALL)","Panel", true),
    CABINET_INPUT("CabinetInput", "CABINET.INPUT(PN%s)","Panel", true),

    INPUT_BRIGHTNESS("InputBrightness", "INPUT.BRIGHTNESS(VC%s.IN%s)","Source", true),
    INPUT_CONTRAST("InputContrast", "INPUT.CONTRAST(VC%s.IN%s)","Source", true),
    INPUT_INFO("InputInfo", "INPUT.INFO(VC%s.IN%s ALL)","Source", false),

    ;
    private final String name;
    private final String command;
    private final String group;
    private boolean isControl;

    /**
     * WallDirectorCommandList instantiation
     *
     * @param name property name
     * @param command property command
     * @param isControl controlling or monitoring
     */
    WallDirectorCommandList(String name, String command, String group, boolean isControl) {
        this.name = name;
        this.command = command;
        this.group = group;
        this.isControl = isControl;
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
    public String getCommand() {
        return command;
    }

    /**
     * Retrieves {@code {@link #isControl}}
     *
     * @return value of {@link #isControl}
     */
    public boolean isControl() {
        return isControl;
    }

    /**
     * Retrieves {@link #group}
     *
     * @return value of {@link #group}
     */
    public String getGroup() {
        return group;
    }
}
