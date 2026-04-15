package com.jkube.app.dispatchers;

import com.jkube.app.events.PodEvent;
import com.jkube.app.registry.AlertRegistry;

import java.util.ArrayList;
import java.util.List;

public class DispatcherFactory {

    public static CompositeAlertDispatcher build(AlertRegistry registry, boolean emailEnabled) {
        List<AlertDispatcher<PodEvent>> dispatchers = new ArrayList<>();
        dispatchers.add(new ConsoleAlertDispatcher());

        if (emailEnabled) {
            dispatchers.add(new EmailAlertDispatcher(registry));
        }

        return new CompositeAlertDispatcher(dispatchers);
    }
}
