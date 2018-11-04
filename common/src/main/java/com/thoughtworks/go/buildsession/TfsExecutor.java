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

package com.thoughtworks.go.buildsession;

import com.thoughtworks.go.config.tfs.TfsMaterial;
import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.config.materials.AgentSubprocessExecutionContext;
import com.thoughtworks.go.config.materials.RevisionContext;
import com.thoughtworks.go.config.materials.mercurial.StringRevision;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import com.thoughtworks.go.util.command.LabeledOutputStreamConsumer;
import com.thoughtworks.go.util.command.TaggedStreamConsumer;

import java.io.File;

public class TfsExecutor implements BuildCommandExecutor {
    @Override
    public boolean execute(BuildCommand command, BuildSession buildSession) {
        String url = command.getStringArg("url");
        String username = command.getStringArg("username");
        String password = command.getStringArg("password");
        String domain = command.getStringArg("domain");
        String projectPath = command.getStringArg("projectPath");
        String revision = command.getStringArg("revision");
        File workingDir = buildSession.resolveRelativeDir(command.getWorkingDirectory());

        ConsoleOutputStreamConsumer consoleOutputStreamConsumer = new LabeledOutputStreamConsumer(TaggedStreamConsumer.PREP, TaggedStreamConsumer.PREP_ERR,
                buildSession.processOutputStreamConsumer());
        RevisionContext revisionContext = new RevisionContext(new StringRevision(revision));
        AgentSubprocessExecutionContext execCtx = new AgentSubprocessExecutionContext(buildSession.getAgentIdentifier(), workingDir.getAbsolutePath());

        createMaterial(url, username, password, domain, projectPath).updateTo(consoleOutputStreamConsumer, workingDir, revisionContext, execCtx);
        return true;
    }

    protected TfsMaterial createMaterial(String url, String username, String password, String domain, String projectPath) {
        return new TfsMaterial(new GoCipherWhichDoesNothingForAgent(), new UrlArgument(url), username, domain, password, projectPath);
    }

    /* There's no cipher on the agent side. In this case, the plain-text is used only in memory. */
    private class GoCipherWhichDoesNothingForAgent extends GoCipher {
        @Override
        public String encrypt(String plainText) {
            return plainText;
        }

        @Override
        public String decrypt(String cipherTextWhichIsActuallyPlainText) {
            return cipherTextWhichIsActuallyPlainText;
        }

    }
}
