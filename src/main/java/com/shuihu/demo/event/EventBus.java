package com.shuihu.demo.event;

import java.util.*;
import java.util.function.Consumer;

/**
 * 事件总线 —— 发布/订阅模式
 */
public class EventBus {
    private static final Map<Class<? extends GameEvent>, List<Consumer<GameEvent>>> listeners = new HashMap<>();

    public static <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                 .add(event -> listener.accept((T) event));
    }

    public static void publish(GameEvent event) {
        List<Consumer<GameEvent>> handlers = listeners.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(h -> h.accept(event));
        }
    }
}
