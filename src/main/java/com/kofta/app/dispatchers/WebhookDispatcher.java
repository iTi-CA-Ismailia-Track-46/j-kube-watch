package com.kofta.app.dispatchers;

import com.kofta.app.events.ImageEvent;

public class WebhookDispatcher implements AlertDispatcher<ImageEvent> {

    @Override
    public void dispatch(ImageEvent event) {
        event.imageName();
    }
}
