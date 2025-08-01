package de.koware.cfs.chatwith10k.util;

public record Result<T>(T result) {

    public T result() {
        return result;
    }
}
