module config.server.main {
    exports com.thoughtworks.go.config.commands;
    exports com.thoughtworks.go.config;
    exports com.thoughtworks.go.config.exceptions;
    exports com.thoughtworks.go.config.parser;
    exports com.thoughtworks.go.config.registry;
    exports com.thoughtworks.go.config.builder;
    exports com.thoughtworks.go.config.elastic;
    exports com.thoughtworks.go.config.materials;
    exports com.thoughtworks.go.config.merge;
    exports com.thoughtworks.go.config.parts;
    exports com.thoughtworks.go.config.pluggabletask;
    exports com.thoughtworks.go.config.preprocessor;
    exports com.thoughtworks.go.config.remote;
    exports com.thoughtworks.go.config.validation;
    exports com.thoughtworks.go.config.security;
    exports com.thoughtworks.go.config.update;

    requires spring.context;
    requires spring.beans;
    requires slf4j.api;
    requires org.apache.commons.lang3;
    requires cloning;
    requires org.apache.commons.io;
    requires spring.web;
    requires spring.webmvc;
    requires javax.servlet.api;
    requires dom4j;
    requires jdom2;
}