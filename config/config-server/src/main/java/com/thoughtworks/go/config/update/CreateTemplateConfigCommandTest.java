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

package com.thoughtworks.go.config.update;

import com.thoughtworks.go.config.*;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.helper.*;
import com.thoughtworks.go.server.domain.Username;
import com.thoughtworks.go.server.service.ExternalArtifactsService;
import com.thoughtworks.go.server.service.SecurityService;
import com.thoughtworks.go.server.service.result.HttpLocalizedOperationResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CreateTemplateConfigCommandTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private ExternalArtifactsService externalArtifactsService;


    private HttpLocalizedOperationResult result;

    private Username currentUser;
    private BasicCruiseConfig cruiseConfig;
    private PipelineTemplateConfig pipelineTemplateConfig;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Before
    public void setup() {
        initMocks(this);
        result = new HttpLocalizedOperationResult();
        currentUser = new Username(new CaseInsensitiveString("user"));
        cruiseConfig = new GoConfigMother().defaultCruiseConfig();
        pipelineTemplateConfig = new PipelineTemplateConfig(new CaseInsensitiveString("template"), StageConfigMother.oneBuildPlanWithResourcesAndMaterials("stage", "job"));
    }

    @Test
    public void shouldAddNewTemplateToGivenConfig() throws Exception {
        CreateTemplateConfigCommand createTemplateConfigCommand = new CreateTemplateConfigCommand(pipelineTemplateConfig, currentUser, securityService, result, externalArtifactsService);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(false));
        createTemplateConfigCommand.update(cruiseConfig);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(true));
    }

    @Test
    public void shouldAddNewTemplateToConfigWithAuthorizationSetForGroupAdmin() throws Exception {
        when(securityService.isUserGroupAdmin(currentUser)).thenReturn(true);
        CreateTemplateConfigCommand createTemplateConfigCommand = new CreateTemplateConfigCommand(pipelineTemplateConfig, currentUser, securityService, result, externalArtifactsService);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(false));
        createTemplateConfigCommand.update(cruiseConfig);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(true));
        assertThat(pipelineTemplateConfig.getAuthorization(), is(new Authorization(new AdminsConfig(new AdminUser(currentUser.getUsername())))));
    }

    @Test
    public void shouldAddNewTemplateToConfigWithoutAuthorizationForSuperAdmin() throws Exception {
        when(securityService.isUserGroupAdmin(currentUser)).thenReturn(false);
        CreateTemplateConfigCommand createTemplateConfigCommand = new CreateTemplateConfigCommand(pipelineTemplateConfig, currentUser, securityService, result, externalArtifactsService);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(false));
        createTemplateConfigCommand.update(cruiseConfig);
        assertThat(cruiseConfig.getTemplates().contains(pipelineTemplateConfig), is(true));
        assertThat(pipelineTemplateConfig.getAuthorization(), is(new Authorization()));
    }

    @Test
    public void shouldNotContinueWithConfigSaveIfUserIsUnauthorized() {
        when(securityService.isUserAdmin(currentUser)).thenReturn(false);

        CreateTemplateConfigCommand command = new CreateTemplateConfigCommand(pipelineTemplateConfig, currentUser, securityService, result, externalArtifactsService);

        assertThat(command.canContinue(cruiseConfig), is(false));
        assertThat(result.message(), equalTo("Unauthorized to edit."));
    }

    @Test
    public void shouldContinueWithConfigSaveIsUserIsAGroupAdmin() {
        when(securityService.isUserAdmin(currentUser)).thenReturn(false);
        when(securityService.isUserGroupAdmin(currentUser)).thenReturn(true);

        CreateTemplateConfigCommand command = new CreateTemplateConfigCommand(pipelineTemplateConfig, currentUser, securityService, result, externalArtifactsService);

        assertThat(command.canContinue(cruiseConfig), is(true));
    }


    @Test
    public void shouldEncryptSecurePropertiesOfPipelineConfig() {
        PipelineTemplateConfig template = mock(PipelineTemplateConfig.class);
        CreateTemplateConfigCommand command = new CreateTemplateConfigCommand(template, null, securityService, result, externalArtifactsService);

        when(template.name()).thenReturn(new CaseInsensitiveString("p1"));
        CruiseConfig preprocessedConfig = mock(CruiseConfig.class);
        when(preprocessedConfig.findTemplate(new CaseInsensitiveString("p1"))).thenReturn(template);

        command.encrypt(preprocessedConfig);

        verify(template).encryptSecureProperties(eq(preprocessedConfig), any(PipelineTemplateConfig.class));
    }
}
