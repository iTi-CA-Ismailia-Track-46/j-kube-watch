package com.kofta.app.dispatchers;

import com.kofta.app.events.PodEvent;
import com.kofta.app.registry.AlertRegistry;

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
