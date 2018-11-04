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

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.config.*;
import com.thoughtworks.go.config.elastic.ElasticProfile;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.config.packagerepository.PackageDefinition;
import com.thoughtworks.go.config.packagerepository.PackageRepository;
import com.thoughtworks.go.config.scm.SCM;
import com.thoughtworks.go.listener.ConfigChangedListener;
import com.thoughtworks.go.listener.EntityConfigChangedListener;
import com.thoughtworks.go.server.cache.GoCache;
import com.thoughtworks.go.server.domain.PluginSettings;
import com.thoughtworks.go.server.initializers.Initializer;
import com.thoughtworks.go.util.CachedDigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityHashingService implements ConfigChangedListener, Initializer {
    private GoConfigService goConfigService;
    private GoCache goCache;
    private static final String ETAG_CACHE_KEY = "GO_ETAG_CACHE".intern();
    private ConfigCache configCache;
    private ConfigElementImplementationRegistry registry;

    @Autowired
    public EntityHashingService(GoConfigService goConfigService, GoCache goCache, ConfigCache configCache, ConfigElementImplementationRegistry registry) {
        this.goConfigService = goConfigService;
        this.goCache = goCache;
        this.configCache = configCache;
        this.registry = registry;
    }

    public void initialize() {
        goConfigService.register(this);
        goConfigService.register(new PipelineConfigChangedListener());
        goConfigService.register(new BasicPipelineConfigsChangedListener());
        goConfigService.register(new MergePipelineConfigsChangedListener());
        goConfigService.register(new SCMConfigChangedListner());
        goConfigService.register(new TemplateConfigChangedListner());
        goConfigService.register(new EnvironmentConfigListener());
        goConfigService.register(new PackageRepositoryChangeListener());
        goConfigService.register(new ElasticAgentProfileConfigListener());
        goConfigService.register(new PackageListener());
        goConfigService.register(new SecurityAuthConfigListener());
        goConfigService.register(new ConfigRepoListener());
        goConfigService.register(new RoleConfigListener());
        goConfigService.register(new ArtifactStoreListener());
        goConfigService.register(new AdminsConfigListener());
    }

    @Override
    public void startDaemon() {
    }

    @Override
    public void onConfigChange(CruiseConfig newCruiseConfig) {
        goCache.remove(ETAG_CACHE_KEY);
    }

    public String md5ForEntity(PipelineTemplateConfig config) {
        String cacheKey = cacheKey(config, config.name());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(EnvironmentConfig config) {
        String cacheKey = cacheKey(config, config.name());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(PackageRepository config) {
        String cacheKey = cacheKey(config, config.getId());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(SCM config) {
        String cacheKey = cacheKey(config, config.getName());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(PipelineConfig config) {
        String cacheKey = cacheKey(config, config.name());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(ConfigRepoConfig config) {
        String cacheKey = cacheKey(config, config.getId());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(ElasticProfile config) {
        String cacheKey = cacheKey(config, config.getId());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(SecurityAuthConfig config) {
        String cacheKey = cacheKey(config, config.getId());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(Role config) {
        String cacheKey = cacheKey(config, config.getName());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(AdminsConfig config) {
        String cacheKey = cacheKey(config, "cacheKey");
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(PackageDefinition config) {
        String cacheKey = cacheKey(config, config.getId());
        return getFromCache(config, cacheKey);
    }

    public String md5ForEntity(PluginSettings pluginSettings) {
        String cacheKey = cacheKey(pluginSettings, pluginSettings.getPluginId());
        return getFromCache(cacheKey, pluginSettings);
    }

    public String md5ForEntity(ArtifactStore artifactStore) {
        String cacheKey = cacheKey(artifactStore, artifactStore.getId());
        return getFromCache(cacheKey, artifactStore);
    }

    private String cacheKey(Object domainObject, CaseInsensitiveString name) {
        return cacheKey(domainObject, name.toLower());
    }

    private String cacheKey(Object domainObject, String name) {
        return getClass(domainObject) + "." + name;
    }

    private String getFromCache(String cacheKey, Object dbObject) {
        String cachedMD5 = getFromCache(cacheKey);

        if (cachedMD5 != null) {
            return cachedMD5;
        }
        String md5 = CachedDigestUtils.md5Hex(new GsonBuilder().create().toJson(dbObject));
        goCache.put(ETAG_CACHE_KEY, cacheKey, md5);

        return md5;
    }

    private String getFromCache(Object domainObject, String cacheKey) {
        String cachedMD5 = getFromCache(cacheKey);

        if (cachedMD5 != null) {
            return cachedMD5;
        }

        String md5 = computeMd5For(domainObject);
        goCache.put(ETAG_CACHE_KEY, cacheKey, md5);

        return md5;
    }

    public void removeFromCache(Object domainObject, CaseInsensitiveString name) {
        removeFromCache(domainObject, name.toLower());
    }

    public void removeFromCache(Object domainObject, String name) {
        goCache.remove(ETAG_CACHE_KEY, cacheKey(domainObject, name));
    }

    private String getFromCache(String cacheKey) {
        return (String) goCache.get(ETAG_CACHE_KEY, cacheKey);
    }

    private String getClass(Object entity) {
        return entity.getClass().getName();
    }

    private String computeMd5For(Object domainObject) {
        String xml = new MagicalGoConfigXmlWriter(configCache, registry).toXmlPartial(domainObject);
        return CachedDigestUtils.md5Hex(xml);
    }

    public String md5ForEntity(RolesConfig roles) {
        List<String> md5s = new ArrayList<>();
        for (Role role : roles) {
            md5s.add(md5ForEntity(role));
        }
        return CachedDigestUtils.md5Hex(StringUtils.join(md5s, "/"));
    }

    public String md5ForEntity(UsageStatisticsReporting usageStatisticsReporting) {
        String cacheKey = cacheKey(usageStatisticsReporting, usageStatisticsReporting.getServerId());
        return getFromCache(cacheKey, usageStatisticsReporting);
    }

    public String md5ForEntity(DataSharingSettings dataSharingSettings) {
        String cacheKey = cacheKey(dataSharingSettings, "data_sharing_settings");
        return getFromCache(cacheKey, dataSharingSettings);
    }

    public String md5ForEntity(PipelineGroups pipelineGroups) {
        List<String> md5s = new ArrayList<>();
        for (PipelineConfigs pipelineConfigs : pipelineGroups) {
            md5s.add(md5ForEntity(pipelineConfigs));
        }
        return CachedDigestUtils.md5Hex(StringUtils.join(md5s, "/"));
    }

    public String md5ForEntity(PipelineConfigs pipelineConfigs) {
        String cacheKey = cacheKey(pipelineConfigs, pipelineConfigs.getGroup());
        return getFromCache(pipelineConfigs, cacheKey);
    }

    class PipelineConfigChangedListener extends EntityConfigChangedListener<PipelineConfig> {
        @Override
        public void onEntityConfigChange(PipelineConfig pipelineConfig) {
            removeFromCache(pipelineConfig, pipelineConfig.name());
        }
    }

    class SCMConfigChangedListner extends EntityConfigChangedListener<SCM> {
        @Override
        public void onEntityConfigChange(SCM scm) {
            removeFromCache(scm, scm.getName());
        }
    }

    class TemplateConfigChangedListner extends EntityConfigChangedListener<PipelineTemplateConfig> {
        @Override
        public void onEntityConfigChange(PipelineTemplateConfig pipelineTemplateConfig) {
            removeFromCache(pipelineTemplateConfig, pipelineTemplateConfig.name());
        }
    }

    class EnvironmentConfigListener extends EntityConfigChangedListener<BasicEnvironmentConfig> {
        @Override
        public void onEntityConfigChange(BasicEnvironmentConfig config) {
            removeFromCache(config, config.name());
        }
    }

    private class PackageRepositoryChangeListener extends EntityConfigChangedListener<PackageRepository> {
        @Override
        public void onEntityConfigChange(PackageRepository repo) {
            removeFromCache(repo, repo.getId());
        }
    }

    class ElasticAgentProfileConfigListener extends EntityConfigChangedListener<ElasticProfile> {
        @Override
        public void onEntityConfigChange(ElasticProfile profile) {
            removeFromCache(profile, profile.getId());
        }
    }

    private class PackageListener extends EntityConfigChangedListener<PackageDefinition> {
        @Override
        public void onEntityConfigChange(PackageDefinition entity) {
            removeFromCache(entity, entity.getId());
        }
    }

    class SecurityAuthConfigListener extends EntityConfigChangedListener<SecurityAuthConfig> {
        @Override
        public void onEntityConfigChange(SecurityAuthConfig profile) {
            removeFromCache(profile, profile.getId());
        }
    }

    class ConfigRepoListener extends EntityConfigChangedListener<ConfigRepoConfig> {
        @Override
        public void onEntityConfigChange(ConfigRepoConfig entity) {
            removeFromCache(entity, entity.getId());
        }
    }

    private class RoleConfigListener extends EntityConfigChangedListener<Role> {
        @Override
        public void onEntityConfigChange(Role entity) {
            removeFromCache(entity, entity.getName());
        }
    }

    private class ArtifactStoreListener extends EntityConfigChangedListener<ArtifactStore> {
        @Override
        public void onEntityConfigChange(ArtifactStore entity) {
            removeFromCache(entity, entity.getId());
        }
    }

    private class AdminsConfigListener extends EntityConfigChangedListener<AdminsConfig> {
        @Override
        public void onEntityConfigChange(AdminsConfig entity) {
            removeFromCache(entity, "cacheKey");
        }
    }

    private class BasicPipelineConfigsChangedListener extends EntityConfigChangedListener<BasicPipelineConfigs> {
        @Override
        public void onEntityConfigChange(BasicPipelineConfigs pipelineConfigs) {
            removeFromCache(pipelineConfigs, pipelineConfigs.getGroup());
        }
    }

    private class MergePipelineConfigsChangedListener extends EntityConfigChangedListener<MergePipelineConfigs> {
        @Override
        public void onEntityConfigChange(MergePipelineConfigs pipelineConfigs) {
            removeFromCache(pipelineConfigs, pipelineConfigs.getGroup());
        }
    }
}
