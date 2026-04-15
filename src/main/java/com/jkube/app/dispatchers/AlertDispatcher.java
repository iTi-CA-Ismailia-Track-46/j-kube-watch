package com.jkube.app.dispatchers;

import com.jkube.app.events.PodEvent;

public interface AlertDispatcher<T extends PodEvent> {
    void dispatch(T event);
}
