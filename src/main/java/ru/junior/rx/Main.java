package ru.junior.rx;

public class Main {
    public static void main(String[] args) throws Exception {
        Observable<Integer> numbers = Observable.create((observer, d) -> {
            observer.onNext(1);
            observer.onNext(2);
            observer.onNext(3);
            observer.onComplete();
        });

        numbers
                .filter(x -> x > 1)
                .map(x -> x * 10)
                .flatMap(x -> Observable.<String>create((o, d) -> {
                    o.onNext("number = " + x);
                    o.onComplete();
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(new Observer<>() {
                    public void onNext(String item) {
                        System.out.println(item + " thread: " + Thread.currentThread().getName());
                    }

                    public void onError(Throwable t) {
                        System.out.println("error: " + t.getMessage());
                    }

                    public void onComplete() {
                        System.out.println("done");
                    }
                });

        Thread.sleep(1000);
    }
}
