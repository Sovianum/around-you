package ru.mail.park.aroundyou;

import android.support.annotation.Nullable;

/**
 * Created by sergey on 01.11.17.
 */

public class ListenerHandler<T> {
    private T listener;

    public ListenerHandler(final T listener) {
        this.listener = listener;
    }

    @Nullable
    public T getListener() {
        return listener;
    }

    public void unregister() {
        listener = null;
    }
}
