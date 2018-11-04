/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.server.service;

import static java.util.Arrays.asList;
import java.util.List;

import static com.thoughtworks.go.helper.PipelineConfigMother.pipelineConfigWithTimer;

import com.thoughtworks.go.server.service.result.ServerHealthStateOperationResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class TimerSchedulerQuartzIntegrationTest {
    private StdSchedulerFactory quartzSchedulerFactory;
    private Scheduler scheduler;
    private SystemEnvironment systemEnvironment;

    @Before
    public void setUp() throws Exception {
        quartzSchedulerFactory = new StdSchedulerFactory();
        scheduler = quartzSchedulerFactory.getScheduler();
        systemEnvironment = new SystemEnvironment();
        scheduler.start();
    }

    @After
    public void tearDown() throws SchedulerException {
        quartzSchedulerFactory.getScheduler().shutdown();
    }

    @Test
    public void shouldExecuteScheduledJobsWhenInvokedFromQuartz() throws InterruptedException {
        PipelineConfig uat = pipelineConfigWithTimer("uat", "* * * * * ?");
        PipelineConfig dist = pipelineConfigWithTimer("dist", "* * * * * ?");
        List<PipelineConfig> pipelineConfigs = asList(uat, dist);

        GoConfigService goConfigService = mock(GoConfigService.class);
        when(goConfigService.getAllPipelineConfigs()).thenReturn(pipelineConfigs);

        BuildCauseProducerService buildCauseProducerService = mock(BuildCauseProducerService.class);

        TimerScheduler timerScheduler = new TimerScheduler(scheduler, goConfigService, buildCauseProducerService, null, systemEnvironment);
        timerScheduler.initialize();

        pauseForScheduling();
        verify(buildCauseProducerService, atLeastOnce()).timerSchedulePipeline(eq(uat), any(
                ServerHealthStateOperationResult.class));
        verify(buildCauseProducerService, atLeastOnce()).timerSchedulePipeline(eq(dist), any(ServerHealthStateOperationResult.class));
    }

    @Test
    public void shouldUpdateJobsInTheQuartzSchedulerOnConfigChange() throws InterruptedException {
        PipelineConfig uat = pipelineConfigWithTimer("uat", "* * * * * ?");
        PipelineConfig dist = pipelineConfigWithTimer("dist", "* * * * * ?");
        List<PipelineConfig> pipelineConfigs = asList(uat, dist);

        GoConfigService goConfigService = mock(GoConfigService.class);
        when(goConfigService.getAllPipelineConfigs()).thenReturn(pipelineConfigs);

        BuildCauseProducerService buildCauseProducerService = mock(BuildCauseProducerService.class);

        TimerScheduler timerScheduler = new TimerScheduler(scheduler, goConfigService, buildCauseProducerService, null, systemEnvironment);
        timerScheduler.initialize();

        CruiseConfig cruiseConfig = new BasicCruiseConfig();
        cruiseConfig.getGroups().add(new BasicPipelineConfigs(uat));
        timerScheduler.onConfigChange(cruiseConfig);

        pauseForScheduling();
        verify(buildCauseProducerService, atLeastOnce()).timerSchedulePipeline(eq(uat), any(ServerHealthStateOperationResult.class));
    }

    private void pauseForScheduling() throws InterruptedException {
        Thread.sleep(1000);
    }

}
