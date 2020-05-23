package org.jungletree.api;

import java.util.concurrent.ScheduledExecutorService;

public interface Scheduler extends ScheduledExecutorService {
    String getName();
}
