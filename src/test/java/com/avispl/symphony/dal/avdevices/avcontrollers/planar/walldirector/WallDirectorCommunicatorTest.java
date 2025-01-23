/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


public class WallDirectorCommunicatorTest {
    private ExtendedStatistics extendedStatistic;
    private WallDirectorCommunicator wallDirectorCommunicator;

    @BeforeEach
    void setUp() throws Exception {
        wallDirectorCommunicator = new WallDirectorCommunicator();
        wallDirectorCommunicator.setHost("127.0.0.1");
        wallDirectorCommunicator.setLogin("");
        wallDirectorCommunicator.setPassword("");
        wallDirectorCommunicator.setPort(57);
        wallDirectorCommunicator.init();
        wallDirectorCommunicator.connect();
    }

    @AfterEach
    void destroy() throws Exception {
        wallDirectorCommunicator.disconnect();
        wallDirectorCommunicator.destroy();
    }

    @Test
    void testMultipleStatistic() throws Exception {
        wallDirectorCommunicator.setConfigManagement("false");
        extendedStatistic = (ExtendedStatistics) wallDirectorCommunicator.getMultipleStatistics().get(0);
        Map<String, String> statistics = extendedStatistic.getStatistics();
        Assert.assertEquals(104, statistics.size());
    }

    @Test
    void testMultipleStatisticWithControl() throws Exception {
        wallDirectorCommunicator.setConfigManagement("true");
        extendedStatistic = (ExtendedStatistics) wallDirectorCommunicator.getMultipleStatistics().get(0);
        Map<String, String> statistics = extendedStatistic.getStatistics();
        List<AdvancedControllableProperty> advancedControllableProperties =  extendedStatistic.getControllableProperties();
        Assert.assertEquals(171, statistics.size());
        Assert.assertEquals(42, advancedControllableProperties.size());
    }

    @Test
    void testHistoricalProperties() throws Exception {
        wallDirectorCommunicator.setConfigManagement("true");
        wallDirectorCommunicator.setHistoricalProperties("VideoController01#Temperature(C), PowerSupply01#Temperature(C)");
        extendedStatistic = (ExtendedStatistics) wallDirectorCommunicator.getMultipleStatistics().get(0);
        Map<String, String> statistics = extendedStatistic.getStatistics();
        List<AdvancedControllableProperty> advancedControllableProperties =  extendedStatistic.getControllableProperties();
        Map<String, String> dynamicStats = extendedStatistic.getDynamicStatistics();
        Assert.assertEquals(169, statistics.size());
    }
}
