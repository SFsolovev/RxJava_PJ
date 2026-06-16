# RxJava
Курсовая работа. Многопоточное и асинхронное программирование на Java.
Реализация аналогичной RxJava-библиотеки

## Базовые компоненты
- `Observer<T>` — с методами `onNext`, `onError`, `onComplete`.
- `Observable<T>` — хранит источник данных и позволяет подписаться через `subscribe`.
- `Disposable` — отменяет подписку.
- `Scheduler` — интерфейс для запуска задач в другом потоке.
- `Schedulers` — класс с тремя планировщиками.

Операторы:
- `map` — преобразует поток данных.
- `filter` — отфильтровывает ненужные элементы.
- `flatMap` — преобразует элементы в новый Observable.
- `subscribeOn` — запускает саму подписку в другом потоке.
- `observeOn` — переносит обработку событий в другой поток.

## Архитектура
Главный класс — `Observable`. Он создается через метод `create`. Внутри хранится функция `OnSubscribe`, которая знает, как отправлять данные наблюдателю.
Подписчик реализует интерфейс `Observer`. Когда поток работает, он вызывает:
- `onNext`, когда пришел новый элемент;
- `onError`, если произошла ошибка;
- `onComplete`, когда поток закончился.
`Disposable` сделан очень просто. Там только флаг `disposed`. Если вызвать `dispose`, флаг становится `true`. В операторах есть проверка этого флага.

## Schedulers
- `IOThreadScheduler` — аналог Schedulers.io(), использующий CachedThreadPool
- `ComputationScheduler` — аналог Schedulers.computation(), использующий FixedThreadPool
- `SingleThreadScheduler` — аналог Schedulers.single(), использующий один поток
Метод `subscribeOn` запускает источник данных в выбранном Scheduler. Метод `observeOn` запускает методы Observer в выбранном Scheduler.

## Тестирование
Проверяется:
- обычная подписка;
- работа `map` и `filter`;
- работа `flatMap`;
- передача ошибки в `onError`;
- работа `Disposable`;
- работа `subscribeOn` в другом потоке;
- работа `observeOn` в другом потоке.

Запуск:
```bash
mvn test
```

```bash
mvn compile exec:java -Dexec.mainClass=ru.junior.rx.Main
```
Если плагина `exec` нет, можно просто открыть `Main` в IDE и запустить его.
