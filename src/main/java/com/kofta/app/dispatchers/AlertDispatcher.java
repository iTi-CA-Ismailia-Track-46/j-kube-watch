package com.kofta.app.dispatchers;

import com.kofta.app.events.PodEvent;

public interface AlertDispatcher<T extends PodEvent> {
    void dispatch(T event);
}
