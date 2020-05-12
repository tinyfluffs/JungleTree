module org.jungletree.core {
    exports org.jungletree.core;
    exports org.jungletree.core.exception;
    requires org.jungletree.api;
    requires org.jungletree.net;

    requires org.apache.logging.log4j;
    requires tomlj;
}
