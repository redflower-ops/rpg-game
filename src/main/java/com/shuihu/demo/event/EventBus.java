package com.shuihu.demo.event;

import java.util.*;
import java.util.function.Consumer;

/**
 * 事件总线 —— 发布/订阅模式
 */
public class EventBus {
    private static final Map<Class<? extends GameEvent>, List<Consumer<GameEvent>>> listeners = new HashMap<>();

    public static <T extends GameEvent> Runnable subscribe(Class<T> eventType, Consumer<T> listener) {
        Consumer<GameEvent> wrapper = event -> listener.accept((T) event);
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(wrapper);
        return () -> {
            List<Consumer<GameEvent>> list = listeners.get(eventType);
            if (list != null) list.remove(wrapper);
        };
    }

    public static void publish(GameEvent event) {
        List<Consumer<GameEvent>> handlers = listeners.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(h -> h.accept(event));
        }
    }

    /** 清空指定事件类型的所有监听器 */
    public static void clear(Class<? extends GameEvent> eventType) {
        listeners.remove(eventType);
    }

    /** 清空所有监听器 */
    public static void clearAll() {
        listeners.clear();
    }
}
