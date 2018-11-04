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

package com.thoughtworks.go.apiv6.admin.pipelineconfig.representers.materials

import com.thoughtworks.go.apiv6.shared.representers.stages.ConfigHelperOptions

import com.thoughtworks.go.config.CaseInsensitiveString

import com.thoughtworks.go.config.materials.MaterialConfigs
import com.thoughtworks.go.config.materials.PasswordDeserializer
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig
import com.thoughtworks.go.helper.MaterialConfigsMother
import com.thoughtworks.go.security.GoCipher

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class SvnMaterialRepresenterTest implements MaterialRepresenterTest {


  def existingMaterial() {
    MaterialConfigsMother.svnMaterialConfig()
  }

  def getOptions() {
    def deserializer = mock(PasswordDeserializer.class)
    def map = new ConfigHelperOptions(mock(BasicCruiseConfig.class), deserializer)
    when(deserializer.deserialize(any(), any(), any())).thenReturn(new GoCipher().encrypt("pass"))
    return map
  }

  def existingMaterialWithErrors() {
    def svnConfig = new SvnMaterialConfig(new UrlArgument(''), '', '', true, new GoCipher(), true, null, false, '', new CaseInsensitiveString('!nV@l!d'))
    def materialConfigs = new MaterialConfigs(svnConfig);
    materialConfigs.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(), new PipelineConfig()))
    return materialConfigs.get(0)
  }

  def materialHash =
  [
    type: 'svn',
    attributes: [
      url: "url",
      destination: "svnDir",
      filter: [
        ignore: [
          "*.doc"
        ]
      ],
      invert_filter: false,
      name: "svn-material",
      auto_update: false,
      check_externals: true,
      username: "user",
      encrypted_password: new GoCipher().encrypt("pass")
    ]
  ]

  def expectedMaterialHashWithErrors =
  [
    type: "svn",
    attributes: [
      url: "",
      destination: "",
      filter: null,
      invert_filter: false,
      name: "!nV@l!d",
      auto_update: true,
      check_externals: true,
      username: ""
    ],
    errors: [
      name: ["Invalid material name '!nV@l!d'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."],
      url: ["URL cannot be blank"]
    ]
  ]
}
