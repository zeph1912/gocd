module agent.launcher.main {
    requires rack.hack.main;
    requires util.main;
    requires db.main;
    requires slf4j.api;
    requires org.apache.commons.io;
    exports com.thoughtworks.go.agent.launcher;
}