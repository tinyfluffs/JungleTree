module org.jungletree.core {
    exports org.jungletree.core to org.jungletree.startup;

    provides org.jungletree.api.Server with org.jungletree.core.JungleServer;

    requires static lombok;

    requires org.jungletree.api;
    requires org.jungletree.net;

    requires org.apache.logging.log4j;
    requires tomlj;

}
