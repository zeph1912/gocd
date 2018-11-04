module util.main {
    requires org.apache.commons.io;
    requires java.xml;
    requires org.apache.commons.lang3;
    requires slf4j.api;
    requires gson;
    requires jdom2;
    requires httpcore;
    requires httpclient;
    exports com.thoughtworks.go.util.command;
    exports com.thoughtworks.go.util;
    exports com.thoughtworks.go.util.json;
    exports com.thoughtworks.go.util.pool;

}