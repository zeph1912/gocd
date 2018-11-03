module server.main {
    exports com.thoughtworks.go.server.domain;
    requires config.server.main;
    requires spring.context;
    requires spring.beans;
    requires slf4j.api;
    requires java.mail;
    requires org.apache.commons.lang3;
    requires cloning;
    requires org.apache.commons.io;
    requires commons.lang;
    requires spring.web;
    requires spring.webmvc;
    requires javax.servlet.api;
    requires dom4j;
    requires jdom2;
    requires spring.orm;
    requires hibernate.ehcache;
    requires spring.tx;
    requires commons.collections4;
}