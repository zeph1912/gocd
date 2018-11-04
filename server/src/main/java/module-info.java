module server.main {
    requires config.server.main;
    requires util.main;
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

}