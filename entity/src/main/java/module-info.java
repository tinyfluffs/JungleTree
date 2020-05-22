module org.jungletree.entity {
    exports org.jungletree.entity to org.jungletree.core;

    requires static lombok;

    requires org.apache.logging.log4j;
    requires org.jungletree.api;
}
