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

import com.thoughtworks.go.util.*;
import org.apache.commons.io.FileUtils;

import java.io.*;

import static com.thoughtworks.go.util.ExceptionUtils.bomb;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.mock;

public class ConfigMigrator {
    public static GoConfigMigration migrate(final File configFile) {
        ConfigElementImplementationRegistry registry = ConfigElementImplementationRegistryMother.withNoPlugins();

        GoConfigMigration upgrader = new GoConfigMigration(new GoConfigMigration.UpgradeFailedHandler() {
            public void handle(Exception e) {
                String content = "";
                try {
                    content = FileUtils.readFileToString(configFile, UTF_8);
                } catch (IOException e1) {
                }
                throw bomb(e.getMessage() + ": content=\n" + content + "\n" + (e.getCause() == null ? "" : e.getCause().getMessage()), e);
            }
        }, mock(ConfigRepository.class), new TimeProvider(), new ConfigCache(), registry
        );
        //TODO: LYH & GL GoConfigMigration should be able to handle stream instead of binding to file
        upgrader.upgradeIfNecessary(configFile, "N/A");
        return upgrader;
    }



    public static String migrate(String configXml) throws IOException {
        File tempFile = TestFileUtil.createTempFile("cruise-config.xml");
        FileUtils.writeStringToFile(tempFile, configXml, UTF_8);
        migrate(tempFile);
        String newConfigXml = FileUtils.readFileToString(tempFile, UTF_8);
        tempFile.delete();
        return newConfigXml;
    }

    public static GoConfigHolder loadWithMigration(String xml) {
        GoConfigHolder configHolder;
        try {
            configHolder = loadWithMigration(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return configHolder;
    }

    public static GoConfigHolder loadWithMigration(InputStream input) throws Exception {
        ConfigElementImplementationRegistry registry = ConfigElementImplementationRegistryMother.withNoPlugins();

        return loadWithMigration(input, registry);
    }

    public static GoConfigHolder loadWithMigration(InputStream input, final ConfigElementImplementationRegistry registry) throws Exception {
        File tempFile = TestFileUtil.createTempFile("cruise-config.xml");
        try {
            MagicalGoConfigXmlLoader xmlLoader = new MagicalGoConfigXmlLoader(new ConfigCache(), registry);
            FileUtils.copyInputStreamToFile(input, tempFile);
            migrate(tempFile);
            return xmlLoader.loadConfigHolder(FileUtils.readFileToString(tempFile, UTF_8));
        } finally {
          FileUtils.deleteQuietly(tempFile);
        }
    }

    public static CruiseConfig load(String content) {
        try {
            ConfigElementImplementationRegistry registry = ConfigElementImplementationRegistryMother.withNoPlugins();

            return new MagicalGoConfigXmlLoader(new ConfigCache(), registry).loadConfigHolder(migrate(content)).config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
