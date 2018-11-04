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

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.config.commands.EntityConfigUpdateCommand;
import com.thoughtworks.go.config.update.AdminsConfigUpdateCommand;
import com.thoughtworks.go.config.Username;
import com.thoughtworks.go.server.service.result.LocalizedOperationResult;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.thoughtworks.go.i18n.LocalizedMessage.saveFailedWithReason;

@Component
public class AdminsConfigService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AdminsConfigService.class);

    private final GoConfigService goConfigService;
    private EntityHashingService entityHashingService;

    @Autowired
    public AdminsConfigService(GoConfigService goConfigService, EntityHashingService entityHashingService) {
        this.goConfigService = goConfigService;
        this.entityHashingService = entityHashingService;
    }

    public AdminsConfig systemAdmins() {
        return goConfigService.serverConfig().security().adminsConfig();
    }

    public void update(Username currentUser, AdminsConfig config, String md5, LocalizedOperationResult result) {
        updateConfig(currentUser, result, new AdminsConfigUpdateCommand(goConfigService, config, currentUser, result, entityHashingService, md5));
    }

    private void updateConfig(Username currentUser, LocalizedOperationResult result, EntityConfigUpdateCommand<AdminsConfig> command) {
        try {
            goConfigService.updateConfig(command, currentUser);
        } catch (Exception e) {
            if (e instanceof GoConfigInvalidException) {
                result.unprocessableEntity("Validation failed while updating System Admins, check errors.");
            } else {
                if (!result.hasMessage()) {
                    LOGGER.error(e.getMessage(), e);
                    result.internalServerError(saveFailedWithReason("An error occurred while updating the System Admins. Please check the logs for more information."));
                }
            }
        }
    }
}


