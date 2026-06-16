package ru.junior.rx;

import java.util.function.Function;
import java.util.function.Predicate;

public class Observable<T> {
    private final OnSubscribe<T> source;

    public interface OnSubscribe<T> {
        void subscribe(Observer<T> observer, Disposable disposable);
    }

    private Observable(OnSubscribe<T> source) {
        this.source = source;
    }

    public static <T> Observable<T> create(OnSubscribe<T> source) {
        return new Observable<>(source);
    }

    public Disposable subscribe(Observer<T> observer) {
        SimpleDisposable disposable = new SimpleDisposable();
        try {
            source.subscribe(observer, disposable);
        } catch (Throwable e) {
            observer.onError(e);
        }
        return disposable;
    }

    public <R> Observable<R> map(Function<T, R> mapper) {
        return create((observer, disposable) -> subscribe(new Observer<>() {
            public void onNext(T item) {
                if (!disposable.isDisposed()) {
                    observer.onNext(mapper.apply(item));
                }
            }

            public void onError(Throwable t) {
                observer.onError(t);
            }

            public void onComplete() {
                observer.onComplete();
            }
        }));
    }

    public Observable<T> filter(Predicate<T> predicate) {
        return create((observer, disposable) -> subscribe(new Observer<>() {
            public void onNext(T item) {
                if (!disposable.isDisposed() && predicate.test(item)) {
                    observer.onNext(item);
                }
            }

            public void onError(Throwable t) {
                observer.onError(t);
            }

            public void onComplete() {
                observer.onComplete();
            }
        }));
    }

    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        return create((observer, disposable) -> subscribe(new Observer<>() {
            public void onNext(T item) {
                if (!disposable.isDisposed()) {
                    mapper.apply(item).subscribe(new Observer<>() {
                        public void onNext(R item) {
                            observer.onNext(item);
                        }

                        public void onError(Throwable t) {
                            observer.onError(t);
                        }

                        public void onComplete() {
                        }
                    });
                }
            }

            public void onError(Throwable t) {
                observer.onError(t);
            }

            public void onComplete() {
                observer.onComplete();
            }
        }));
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return create((observer, disposable) -> scheduler.execute(() -> source.subscribe(observer, disposable)));
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return create((observer, disposable) -> subscribe(new Observer<>() {
            public void onNext(T item) {
                scheduler.execute(() -> observer.onNext(item));
            }

            public void onError(Throwable t) {
                scheduler.execute(() -> observer.onError(t));
            }

            public void onComplete() {
                scheduler.execute(observer::onComplete);
            }
        }));
    }
}
