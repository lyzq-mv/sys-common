package com.morv.syscommon.lang;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public interface Pipeline<T> {
    static <T> Pipeline<T> of(Iterable<T> iterable) {
        return iterable::forEach;
    }

    static <T> Pipeline<T> of(T... args) {
        return c -> {
            for (T arg : args) {
                c.accept(arg);
            }
        };
    }

    void each(Consumer<T> consumer);

    default <R> Pipeline<R> map(Function<T, R> function) {
        return c -> each(t -> c.accept(function.apply(t)));
    }

    default <R> Pipeline<R> flatMap(Function<T, Pipeline<R>> function) {
        return c -> each(t -> {
            Pipeline<R> apply = function.apply(t);
            apply.each(c);
        });
    }

    default <R> Pipeline<R> flatMapByColl(Function<T, Collection<R>> function) {
        return c -> each(t -> {
            Collection<R> apply = function.apply(t);
            for (R r : apply) {
                c.accept(r);
            }
        });
    }


    default Pipeline<T> filter(Predicate<T> predicate) {
        return c -> each(t -> {
            if (predicate.test(t)) {
                c.accept(t);
            }
        });
    }

    default Pipeline<T> peek(Consumer<T> consumer) {
        return c -> each(t -> {
            consumer.accept(t);
            c.accept(t);
        });
    }

    default List<T> toList() {
        List<T> list = new ArrayList<>();
        each(list::add);
        return list;
    }

    default <K, V> Map<K, V> toMap(Function<T, K> keyFunction, Function<T, V> valueFunction) {
        Map<K, V> map = new HashMap<>();
        each(t -> {
            K key = keyFunction.apply(t);
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("duplicate key:" + key);
            }
            map.put(key, valueFunction.apply(t));
        });
        return map;
    }

    default Optional<T> findFirst() {
        AtomicReference<T> reference = new AtomicReference<>();
        try {
            each(t -> {
                reference.set(t);
                throw new StopException();

            });
        } catch (StopException e) {
            //
        }
        return Optional.ofNullable(reference.get());
    }

    class StopException extends RuntimeException {

    }

}
