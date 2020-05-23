module org.jungletree.scheduler {
    exports org.jungletree.scheduler;

    requires static lombok;

    requires org.apache.logging.log4j;
    requires org.jungletree.api;

    provides org.jungletree.api.Scheduler with
            org.jungletree.scheduler.NetworkScheduler,
            org.jungletree.scheduler.WorldScheduler,
            org.jungletree.scheduler.PluginScheduler,
            org.jungletree.scheduler.EntityScheduler,
            org.jungletree.scheduler.ChatScheduler;
}
