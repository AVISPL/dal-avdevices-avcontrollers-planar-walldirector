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
        wallDirectorCommunicator.setHost("172.31.15.19");
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
    void testLoginSuccess() throws Exception {
        wallDirectorCommunicator.setConfigManagement("true");
        extendedStatistic = (ExtendedStatistics) wallDirectorCommunicator.getMultipleStatistics().get(0);
        Map<String, String> statistics = extendedStatistic.getStatistics();
        List<AdvancedControllableProperty> advancedControllableProperties =  extendedStatistic.getControllableProperties();
        Assert.assertEquals(171, statistics.size());
    }
}
