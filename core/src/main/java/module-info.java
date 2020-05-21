module org.jungletree.core {
    exports org.jungletree.core to org.jungletree.startup;
    exports org.jungletree.core.exception to org.jungletree.startup;

    requires org.apache.logging.log4j;
    requires org.jungletree.api;
    requires org.jungletree.net;
    requires tomlj;
}
