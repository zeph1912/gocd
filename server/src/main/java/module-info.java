module server.main {
    exports com.thoughtworks.go.server.exceptions;
    exports com.thoughtworks.go.server.domain;
    exports com.thoughtworks.go.server.cache;
    exports com.thoughtworks.go.server.controller.actions;
    exports com.thoughtworks.go.server.controller;
    exports com.thoughtworks.go.server.dao;
    exports com.thoughtworks.go.server.dao.handlers;
    exports com.thoughtworks.go.server.dao.sparql;
    exports com.thoughtworks.go.server.dashboard;
    exports com.thoughtworks.go.server.datamigration;
    exports com.thoughtworks.go.server.domain.support.toggle;
    exports com.thoughtworks.go.server.domain.user;
    exports com.thoughtworks.go.server.messaging;
    exports com.thoughtworks.go.server.messaging.notifications;
    exports com.thoughtworks.go.server.newsecurity.filters;
    exports com.thoughtworks.go.server.newsecurity.handlers;
    exports com.thoughtworks.go.server.newsecurity.helpers;
    exports com.thoughtworks.go.server.newsecurity.models;
    exports com.thoughtworks.go.server.newsecurity.utils;
    exports com.thoughtworks.go.server.perf;
    exports com.thoughtworks.go.server.presentation;
    exports com.thoughtworks.go.server.scheduling;
    exports com.thoughtworks.go.server.security.userdetail;
    exports com.thoughtworks.go.server.sqlmigration;
    exports com.thoughtworks.go.server.ui;
    exports com.thoughtworks.go.server.util;
    exports com.thoughtworks.go.server.view;
    exports com.thoughtworks.go.server.view.artifacts;
    exports com.thoughtworks.go.server.web.i18n;
    exports com.thoughtworks.go.server.web;
    exports com.thoughtworks.go.server;
    opens com.thoughtworks.go.server.security;
    requires transitive config.server.main;
    requires transitive util.main;
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
    requires gson;
    requires jruby.rack;
    requires com.headius.invokebinder;
    requires java.xml;
    requires rdf4j.repository.api;
    requires rdf4j.repository.sail;
    requires rdf4j.sail.memory;
    requires rdf4j.model;
    requires rdf4j.query;
    requires rdf4j.queryresultio.sparqlxml;
    requires rdf4j.queryresultio.api;
    requires rdf4j.rio.api;
    requires rdf4j.rio.n3;
    requires rdf4j.rio.rdfxml;
    requires guava;
    requires org.apache.commons.codec;
    requires httpcore;
    requires mybatis;
    requires java.sql;
    requires spring.security.web;
    requires spring.security.core;
    requires java.xml.ws.annotation;

}