package com.streamerbot.triggers;

import com.streamerbot.DoActionRequest;
import com.streamerbot.StreamerbotConfig;
import com.streamerbot.TriggerHandler;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Inject;

import net.runelite.api.Client;
@Slf4j
public abstract class BaseTrigger {
    @Inject
    protected StreamerbotConfig config;

    @Inject
    protected Client client;

    @Inject
    protected TriggerHandler triggerHandler;

    protected void sendAction(DoActionRequest request) {
        String json = request.toJson();
        triggerHandler.sendJson(json);
        log.info("Attempting to send JSON");
    }
}
