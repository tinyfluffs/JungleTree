module org.jungletree.api {
    exports org.jungletree.api;
    exports org.jungletree.api.chat;
    exports org.jungletree.api.entity;
    exports org.jungletree.api.exception;

    requires static lombok;
    
    requires org.json;

    uses org.jungletree.api.Server;
}
