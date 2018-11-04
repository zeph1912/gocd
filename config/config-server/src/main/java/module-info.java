module config.server.main {
    exports com.thoughtworks.go.config;
    exports com.thoughtworks.go.config.exceptions;
    exports com.thoughtworks.go.config.registry;
    exports com.thoughtworks.go.config.preprocessor;
    exports com.thoughtworks.go.config.remote;
    exports com.thoughtworks.go.config.validation;
    requires gson;
    requires util.main;
    requires org.apache.commons.lang3;
    requires java.xml.ws.annotation;
    requires spring.context;
    requires spring.beans;
    requires slf4j.api;
    requires cloning;
    requires org.apache.commons.io;
    requires spring.web;
    requires spring.webmvc;
    requires javax.servlet.api;
    requires dom4j;
    requires jdom2;
    requires spring.tx;
    requires commons.collections4;
}