package ru.junior.rx;

public class SimpleDisposable implements Disposable {
    private boolean disposed;

    public void dispose() {
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }
}
