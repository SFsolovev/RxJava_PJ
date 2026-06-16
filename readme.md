# RxJava
Курсовая работа. Многопоточное и асинхронное программирование на Java.
Реализация аналогичной RxJava-библиотеки

## Описание

В рамках данной работы была реализована собственная система реактивных потоков с использованием паттерна Observer.

Проект поддерживает:

* создание Observable;
* подписку через Observer;
* операторы map;
* операторы filter;
* оператор flatMap;
* обработку ошибок;
* управление подписками через Disposable;
* выполнение задач в различных потоках через Scheduler.

## Архитектура

Основные компоненты проекта:

### Observer

Интерфейс для получения событий из потока.

Методы:

```java
onNext(T item)
onError(Throwable t)
onComplete()
```

### Observable

Источник данных.

Создается через метод:

```java
Observable.create(...)
```

Поддерживает подписку:

```java
observable.subscribe(...)
```

### Disposable

Используется для отмены подписки.

Методы:

```java
dispose()
isDisposed()
```

## Операторы

### map

Преобразование элементов потока.

Пример:

```java
Observable.create(...)
    .map(x -> x * 2);
```

### filter

Фильтрация элементов потока.

Пример:

```java
Observable.create(...)
    .filter(x -> x > 10);
```

### flatMap

Преобразование элемента в новый Observable.

Пример:

```java
Observable.create(...)
    .flatMap(x -> Observable.create(...));
```

## Scheduler

Для управления потоками выполнения реализован интерфейс:

```java
public interface Scheduler {
    void execute(Runnable task);
}
```

### IOThreadScheduler

Использует CachedThreadPool.

Подходит для операций ввода-вывода.

### ComputationScheduler

Использует FixedThreadPool.

Подходит для вычислительных задач.

### SingleThreadScheduler

Использует один поток выполнения.

Подходит для последовательной обработки данных.

## subscribeOn и observeOn

### subscribeOn

Определяет поток, в котором будет выполнена подписка.

```java
observable.subscribeOn(new IOThreadScheduler());
```

### observeOn

Определяет поток обработки элементов.

```java
observable.observeOn(new ComputationScheduler());
```

## Обработка ошибок

Ошибки передаются подписчику через метод:

```java
onError(Throwable t)
```

Это позволяет централизованно обрабатывать исключения во время выполнения потока.

## Тестирование

Для проверки работы библиотеки были написаны unit-тесты.

Проверены:

* Observable;
* Observer;
* map;
* filter;
* flatMap;
* Disposable;
* subscribeOn;
* observeOn;
* обработка ошибок;
* работа Scheduler.

Все тесты проходят успешно.

## Пример использования

```java
Observable.create(emitter -> {
    emitter.onNext(1);
    emitter.onNext(2);
    emitter.onNext(3);
    emitter.onComplete();
})
.filter(x -> x > 1)
.map(x -> x * 10)
.subscribe(new Observer<Integer>() {

    @Override
    public void onNext(Integer item) {
        System.out.println(item);
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Completed");
    }
});
```

Результат:

```text
20
30
Completed
```

## Вывод

В ходе выполнения работы была реализована упрощенная версия библиотеки RxJava с поддержкой реактивных потоков, операторов преобразования данных, управления потоками выполнения и тестирования основных сценариев работы.
