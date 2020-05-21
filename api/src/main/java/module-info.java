module org.jungletree.api {
    exports org.jungletree.api;
    exports org.jungletree.api.entity;
    exports org.jungletree.api.exception;

    requires static lombok;

    uses org.jungletree.api.Server;
}
