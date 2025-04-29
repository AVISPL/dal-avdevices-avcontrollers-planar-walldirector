/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.properties.PresetProperty;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.properties.VWGeneralProperty;

/**
 * WallDirectorCommunicatorTest class for test the methods of Planar WallDirector
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
class WallDirectorCommunicatorTest {
    private ExtendedStatistics extendedStatistics;
    private WallDirectorCommunicator wallDirectorCommunicator;

    @BeforeEach
    void setUp() throws Exception {
        this.wallDirectorCommunicator = new WallDirectorCommunicator();
        this.wallDirectorCommunicator.setHost("");
        this.wallDirectorCommunicator.setLogin("");
        this.wallDirectorCommunicator.setPassword("");
        this.wallDirectorCommunicator.setPort(57);
        this.wallDirectorCommunicator.init();
        this.wallDirectorCommunicator.connect();
    }

    @AfterEach
    void destroy() throws Exception {
        this.wallDirectorCommunicator.disconnect();
        this.wallDirectorCommunicator.destroy();
    }

    @Test
    void testGetMultipleStatistic() throws Exception {
        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);

        Map<String, String> statistics = this.extendedStatistics.getStatistics();
        this.verifyStatistics(statistics);
    }

    @Test
    void testControllablePropertyWithSystemPower() throws Exception {
        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);

        ControllableProperty systemPowerControl = new ControllableProperty();
        systemPowerControl.setProperty(VWGeneralProperty.SYSTEM_POWER.getName());
        systemPowerControl.setValue("1");
        this.wallDirectorCommunicator.controlProperty(systemPowerControl);

        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);
        String verifiedValue = this.extendedStatistics.getStatistics().get(VWGeneralProperty.SYSTEM_POWER.getName());
        Assertions.assertEquals("1", verifiedValue);
    }

    @Test
    void testControllablePropertyWithSystemReboot() throws Exception {
        ControllableProperty systemPowerControl = new ControllableProperty();
        systemPowerControl.setProperty(VWGeneralProperty.SYSTEM_REBOOT.getName());
        this.wallDirectorCommunicator.controlProperty(systemPowerControl);

        this.delayExecution(WallDirectorCommunicator.REBOOT_TIME);
        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);
        Map<String, String> statistics = this.extendedStatistics.getStatistics();
        if (!statistics.isEmpty()) {
            this.verifyStatistics(statistics);
        }
    }

    @Test
    void testControllablePropertyWithPresetRecall() throws Exception {
        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);

        ControllableProperty presetRecallControl = new ControllableProperty();
        presetRecallControl.setProperty("Presets#Preset2Recall");
        this.wallDirectorCommunicator.controlProperty(presetRecallControl);

        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);
        String verifiedValue = this.extendedStatistics.getStatistics().get("Presets#" + PresetProperty.ACTIVE_PRESET.getName());
        Assertions.assertEquals("2", verifiedValue);
    }

    @Test
    void testGetTemperatures() throws Exception {
        String tempProperties = "PANEL1#Temperature(C), PANEL2#Temperature(C), PS1#Temperature(C), PS2#Temperature(C)";
        this.wallDirectorCommunicator.setHistoricalProperties(tempProperties);
        this.extendedStatistics = (ExtendedStatistics) this.wallDirectorCommunicator.getMultipleStatistics().get(0);

        String historicalProperties = this.wallDirectorCommunicator.getHistoricalProperties();
        Assertions.assertEquals(historicalProperties.split(",").length, tempProperties.split(",").length);

        Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();
        this.verifyStatistics(dynamicStatistics);
    }

    private Map<String, String> filterGroupStatistics(Map<String, String> statistics, String groupName) {
        return statistics.entrySet().stream()
            .filter(e -> groupName == null ? !e.getKey().contains("#") : e.getKey().startsWith(groupName))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void verifyStatistics(Map<String, String> statistics) {
        List<Map<String, String>> groups = new ArrayList<>();
        groups.add(this.filterGroupStatistics(statistics, null));
        groups.add(this.filterGroupStatistics(statistics, Constant.VW_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.PS_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.VC_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.SOURCE_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.ZONE_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.PRESET_GROUP));
        groups.add(this.filterGroupStatistics(statistics, Constant.NETWORK_STATUS_GROUP));

        for (Map<String, String> initGroup : groups) {
            for (Map.Entry<String, String> initStatistics : initGroup.entrySet()) {
                Assertions.assertNotNull(initStatistics.getValue(), "Value is null with property: " + initStatistics.getKey());
            }
        }
    }

    private void delayExecution(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
