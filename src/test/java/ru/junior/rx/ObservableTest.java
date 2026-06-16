package ru.junior.rx;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ObservableTest {
    @Test
    void simpleSubscribeWorks() {
        List<Integer> list = new ArrayList<>();
        boolean[] done = {false};

        Observable.<Integer>create((o, d) -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        }).subscribe(new Observer<>() {
            public void onNext(Integer item) {
                list.add(item);
            }

            public void onError(Throwable t) {
                fail();
            }

            public void onComplete() {
                done[0] = true;
            }
        });

        assertEquals(List.of(1, 2), list);
        assertTrue(done[0]);
    }

    @Test
    void mapAndFilterWork() {
        List<Integer> list = new ArrayList<>();

        Observable.<Integer>create((o, d) -> {
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onComplete();
        }).filter(x -> x > 1).map(x -> x * 10).subscribe(new EmptyObserver<>() {
            public void onNext(Integer item) {
                list.add(item);
            }
        });

        assertEquals(List.of(20, 30), list);
    }

    @Test
    void flatMapWorks() {
        List<String> list = new ArrayList<>();

        Observable.<Integer>create((o, d) -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        }).flatMap(x -> Observable.<String>create((o, d) -> {
            o.onNext("a" + x);
            o.onComplete();
        })).subscribe(new EmptyObserver<>() {
            public void onNext(String item) {
                list.add(item);
            }
        });

        assertEquals(List.of("a1", "a2"), list);
    }

    @Test
    void errorGoesToOnError() {
        RuntimeException error = new RuntimeException("bad");
        Throwable[] saved = {null};

        Observable.<Integer>create((o, d) -> {
            throw error;
        }).subscribe(new EmptyObserver<>() {
            public void onError(Throwable t) {
                saved[0] = t;
            }
        });

        assertSame(error, saved[0]);
    }

    @Test
    void disposableWorks() {
        Disposable d = Observable.<Integer>create((o, disp) -> {
            o.onNext(1);
        }).subscribe(new EmptyObserver<>());

        assertFalse(d.isDisposed());
        d.dispose();
        assertTrue(d.isDisposed());
    }

    @Test
    void subscribeOnUsesOtherThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String main = Thread.currentThread().getName();
        String[] thread = {null};

        Observable.<Integer>create((o, d) -> {
            thread[0] = Thread.currentThread().getName();
            o.onNext(1);
            o.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe(new EmptyObserver<>() {
            public void onComplete() {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotEquals(main, thread[0]);
    }

    @Test
    void observeOnUsesOtherThread() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String main = Thread.currentThread().getName();
        String[] thread = {null};

        Observable.<Integer>create((o, d) -> {
            o.onNext(1);
            o.onComplete();
        }).observeOn(Schedulers.single()).subscribe(new EmptyObserver<>() {
            public void onNext(Integer item) {
                thread[0] = Thread.currentThread().getName();
            }

            public void onComplete() {
                latch.countDown();
            }
        });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotEquals(main, thread[0]);
    }

    static class EmptyObserver<T> implements Observer<T> {
        public void onNext(T item) {
        }

        public void onError(Throwable t) {
        }

        public void onComplete() {
        }
    }
}
