package com.jkube.app.dispatchers;

import com.jkube.app.events.PodEvent;

import java.util.List;

public class CompositeAlertDispatcher implements AlertDispatcher<PodEvent> {

    private final List<AlertDispatcher<PodEvent>> dispatchers;

    public CompositeAlertDispatcher(List<AlertDispatcher<PodEvent>> dispatchers) {
        this.dispatchers = dispatchers;
    }

    @Override
    public void dispatch(PodEvent event) {
        for (AlertDispatcher<PodEvent> dispatcher : dispatchers) {
            dispatcher.dispatch(event);
        }
    }
}
