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

package com.thoughtworks.go.server.service.dd.reporting;

import com.thoughtworks.go.config.materials.PackageMaterialConfig;
import com.thoughtworks.go.config.materials.ScmMaterialConfig;
import com.thoughtworks.go.config.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.MaterialConfig;

public class ReportingFanInNodeFactory {
    public static ReportingFanInNode create(MaterialConfig material){
        if(material instanceof ScmMaterialConfig || material instanceof PackageMaterialConfig) return new ReportingRootFanInNode(material);
        if(material instanceof DependencyMaterialConfig) return new ReportingDependencyFanInNode(material);
        throw new RuntimeException("Not a valid material type");
    }
}
