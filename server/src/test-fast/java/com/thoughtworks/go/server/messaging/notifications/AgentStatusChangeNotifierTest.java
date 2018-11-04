/*
 * Copyright 2018 ThoughtWorks, Inc.
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

package com.thoughtworks.go.server.messaging.notifications;

import com.thoughtworks.go.domain.AgentInstance;
import com.thoughtworks.go.domain.AgentRuntimeStatus;
import com.thoughtworks.go.helper.AgentInstanceMother;
import com.thoughtworks.go.listener.AgentStatusChangeListener;
import com.thoughtworks.go.plugin.access.notification.NotificationPluginRegistry;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.server.service.ElasticAgentRuntimeInfo;
import com.thoughtworks.go.util.SystemEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AgentStatusChangeNotifierTest {
    @Mock
    private NotificationPluginRegistry notificationPluginRegistry;
    @Mock
    private PluginNotificationService pluginNotificationService;

    private AgentStatusChangeNotifier agentStatusChangeNotifier;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        agentStatusChangeNotifier = new AgentStatusChangeNotifier(notificationPluginRegistry, pluginNotificationService);
    }

    @Test
    public void shouldNotifyInterestedPluginsWithAgentInformation() {
        AgentInstance agentInstance = AgentInstanceMother.building();
        when(notificationPluginRegistry.isAnyPluginInterestedIn("agent-status")).thenReturn(true);

        agentStatusChangeNotifier.onAgentStatusChange(agentInstance);

        verify(pluginNotificationService).notifyAgentStatus(agentInstance);
}

    @Test
    public void shouldNotifyIfAgentIsElastic() throws Exception {
        ElasticAgentRuntimeInfo agentRuntimeInfo = new ElasticAgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, "/foo/one", null, "42", "go.cd.elastic-agent-plugin.docker");
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setElasticAgentId("42");
        agentConfig.setElasticPluginId("go.cd.elastic-agent-plugin.docker");
        agentConfig.setIpAddress("127.0.0.1");
        AgentInstance agentInstance = AgentInstance.createFromConfig(agentConfig, new SystemEnvironment(), mock(AgentStatusChangeListener.class));
        agentInstance.update(agentRuntimeInfo);

        when(notificationPluginRegistry.isAnyPluginInterestedIn("agent-status")).thenReturn(true);

        agentStatusChangeNotifier.onAgentStatusChange(agentInstance);

        verify(pluginNotificationService).notifyAgentStatus(agentInstance);
}

    @Test
    public void shouldNotifyInAbsenceOfPluginsInterestedInAgentStatusNotifications() throws Exception {
        AgentInstance agentInstance = AgentInstanceMother.building();

        when(notificationPluginRegistry.isAnyPluginInterestedIn("agent-status")).thenReturn(false);

        agentStatusChangeNotifier.onAgentStatusChange(agentInstance);

        verifyZeroInteractions(pluginNotificationService);
    }
}
