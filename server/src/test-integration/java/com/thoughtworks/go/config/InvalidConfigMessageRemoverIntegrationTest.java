/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.config;

import com.thoughtworks.go.server.service.GoConfigService;
import com.thoughtworks.go.serverhealth.*;
import com.thoughtworks.go.util.GoConfigFileHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:WEB-INF/applicationContext-global.xml",
        "classpath:WEB-INF/applicationContext-dataLocalAccess.xml",
        "classpath:testPropertyConfigurer.xml"
})
public class InvalidConfigMessageRemoverIntegrationTest {

    @Autowired
    GoConfigService goConfigService;
    @Autowired ServerHealthService serverHealthService;
    @Autowired
    CachedGoConfig cachedGoConfig;
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();


    private File configFile;
    private GoConfigFileHelper configHelper;

    @Before
    public void setUp() throws Exception {
        File tempFolder = temporaryFolder.newFolder();
        configFile = new File(tempFolder, "cruise-config.xml");
        configFile.createNewFile();
        GoConfigFileHelper.clearConfigVersions();
        configHelper = new GoConfigFileHelper();
        configHelper.onSetUp();
        SystemEnvironment systemEnvironment = new SystemEnvironment();
        systemEnvironment.setProperty(SystemEnvironment.CONFIG_FILE_PROPERTY, configFile.getAbsolutePath());
        new ConfigRepository(systemEnvironment).initialize();
        serverHealthService.removeAllLogs();
    }

    @After
    public void tearDown() throws Exception {
        configFile.delete();
        configHelper.onTearDown();
        serverHealthService.removeAllLogs();
        GoConfigFileHelper.clearConfigVersions();
    }

    @Test
    public void shouldRemoveServerHealthServiceMessageAboutStartedWithInvalidConfiguration() {
        serverHealthService.update(ServerHealthState.warning("Invalid Configuration", "something",HealthStateType.general(HealthStateScope.forInvalidConfig())));
        InvalidConfigMessageRemover remover = new InvalidConfigMessageRemover(goConfigService, serverHealthService);
        remover.initialize();
        assertThat(serverHealthService.logs().isEmpty(), is(false));
        configHelper.addAgent("hostname", "uuid"); //Any change to the config file
        cachedGoConfig.forceReload();
        assertThat(serverHealthService.filterByScope(HealthStateScope.forInvalidConfig()).isEmpty(), is(true));
    }
}
