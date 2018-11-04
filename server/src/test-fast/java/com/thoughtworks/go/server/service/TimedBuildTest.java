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

import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.config.Username;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class TimedBuildTest {

    @Test
    public void shouldReturnEmptyBuildCauseIfThereIsNoModification_whenTriggeringOnlyForMaterialChange() throws Exception {
        MaterialRevisions someRevisions = new MaterialRevisions(new MaterialRevision(MaterialsMother.gitMaterial("git://url"), ModificationsMother.aCheckIn("1", "file1.txt")));

        BuildType timedBuild = new TimedBuild();
        PipelineConfig timerConfig = PipelineConfigMother.pipelineConfigWithTimer("Timer", "* * * * * ?", true);
        BuildCause buildCause = timedBuild.onEmptyModifications(timerConfig, someRevisions);

        assertThat(buildCause, is(nullValue()));
    }

    @Test
    public void shouldReturnAForcedBuildCauseIfThereIsNoModification_whenTriggeringIrrespectiveOfMaterialChange() throws Exception {
        MaterialRevisions someRevisions = new MaterialRevisions(new MaterialRevision(MaterialsMother.gitMaterial("git://url"), ModificationsMother.aCheckIn("1", "file1.txt")));

        BuildType timedBuild = new TimedBuild();
        PipelineConfig timerConfig = PipelineConfigMother.pipelineConfigWithTimer("Timer", "* * * * * ?", false);
        BuildCause buildCause = timedBuild.onEmptyModifications(timerConfig, someRevisions);

        assertThat(buildCause.getMaterialRevisions(), is(someRevisions));
        assertThat(buildCause.getApprover(), is(Username.CRUISE_TIMER.getDisplayName()));
    }
}