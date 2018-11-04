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

package com.thoughtworks.go.server.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.go.config.materials.Modification;
import com.thoughtworks.go.config.materials.ModifiedFile;
import com.thoughtworks.go.config.materials.ModifiedAction;
import com.thoughtworks.go.helper.MaterialsMother;

public class PipelineBuilder {
    private String name;
    private List<Stage> stages = new ArrayList<>();
    private String label;
    private MaterialRevisions materialRevisions = new MaterialRevisions();

    public PipelineBuilder named(String name) {
        this.name = name;
        return this;
    }

    public PipelineBuilder addStage(String stageName) {
        stages.add(new Stage(stageName, new JobInstances(), null, null, new TimeProvider()));
        return this;
    }

    public void addJob(String jobName) {
        JobInstance instance = new JobInstance(jobName, new TimeProvider());
        currentStage().getJobInstances().add(instance);
    }

    private Stage currentStage() {
        return stages.get(stages.size() - 1);
    }

    public Pipeline createPipeline() {
        Pipeline pipeline = new Pipeline(name, null, stages.toArray(new Stage[]{}));
        pipeline.setLabel(label);
        pipeline.setBuildCause(BuildCause.createWithModifications(materialRevisions, ""));
        return pipeline;
    }

    public PipelineBuilder label(String label) {
        this.label = label;
        return this;
    }

    public void addRevision(ModificationBuilder modification) {
        materialRevisions.addRevision(new MaterialRevision(MaterialsMother.hgMaterial(), modification.create()));
    }

    public static ModificationBuilder modification(String user, String comment) {
        ModificationBuilder builder = new ModificationBuilder(user, comment);
        return builder;
    }

    public static class ModificationBuilder {
        private final String user;
        private final String comment;
        private List<ModifiedFile> files = new ArrayList<>();

        public ModificationBuilder(String user, String comment) {
            this.user = user;
            this.comment = comment;
        }

        public Modification create() {
            Modification modification = new Modification(user, comment, null, new Date(), null);
            modification.setModifiedFiles(files);
            return modification;
        }

        ModificationBuilder withFile(String fileName) {
            files.add(new ModifiedFile(fileName, null, ModifiedAction.added));
            return this;
        }
    }
}
