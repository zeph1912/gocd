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

package com.thoughtworks.go.apiv1.triggerwithoptionsview.representers

import com.thoughtworks.go.config.EnvironmentVariablesConfig
import com.thoughtworks.go.domain.JobResult
import com.thoughtworks.go.domain.JobState
import com.thoughtworks.go.domain.MaterialRevisions
import com.thoughtworks.go.domain.buildcause.BuildCause
import com.thoughtworks.go.helper.EnvironmentVariablesConfigMother
import com.thoughtworks.go.helper.MaterialConfigsMother
import com.thoughtworks.go.helper.MaterialsMother
import com.thoughtworks.go.helper.ModificationsMother
import com.thoughtworks.go.presentation.pipelinehistory.JobHistory
import com.thoughtworks.go.presentation.pipelinehistory.PipelineInstanceModel
import com.thoughtworks.go.presentation.pipelinehistory.StageInstanceModels
import net.javacrumbs.jsonunit.fluent.JsonFluentAssert
import org.junit.jupiter.api.Test

class TriggerWithOptionsViewRepresenterTest {
  @Test
  void 'renders json'() {
    MaterialRevisions revisions = ModificationsMother.modifyOneFile(MaterialsMother.defaultSvnMaterialsWithUrl("http://example.com/svn/project"), "revision")

    StageInstanceModels stages = new StageInstanceModels()
    stages.addStage("unit1", JobHistory.withJob("test", JobState.Completed, JobResult.Passed, new Date()))
    stages.addFutureStage("unit2", false)

    PipelineInstanceModel model = PipelineInstanceModel.createPipeline("pipeline", -1, "label", BuildCause.createWithModifications(revisions, "bob"), stages)
    model.setMaterialConfigs(MaterialConfigsMother.defaultSvnMaterialConfigsWithUrl("http://example.com/svn/project"))

    EnvironmentVariablesConfig variables = EnvironmentVariablesConfigMother.environmentVariables()

    def actualJson = TriggerWithOptionsViewRepresenter.toJSON(new TriggerOptions(variables, model), null)
    JsonFluentAssert.assertThatJson(actualJson).isEqualTo([
      "variables": [
        ["name": "MULTIPLE_LINES", "secure": true, "value": "****"],
        ["name": "COMPLEX", "secure": false, "value": "This has very <complex> data"]
      ],
      "materials": [
        [
          "type"       : "Subversion", "name": "http___example.com_svn_project",
          "fingerprint": "f5f52bd94f0eaed410d7ca7843e0d8c693b2d6daf91fe037d55b566e862dcdae",
          "folder"     : "svnDir",
          "revision"   : [
            "date"             : ModificationsMother.TWO_DAYS_AGO_CHECKIN,
            "user"             : "lgao",
            "comment"          : "Fixing the not checked in files",
            "last_run_revision": "revision"
          ]
        ]
      ]
    ])
  }
}
