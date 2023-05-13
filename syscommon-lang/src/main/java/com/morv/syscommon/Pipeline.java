package com.morv.syscommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 管道，用于处理流式计算逻辑
 *
 * @param <T> 管道中元数类型
 */
public interface Pipeline<T> {
    void each(Consumer<T> consumer);

    /**
     * 通过集合获取管道
     *
     * @param collection
     * @param <T>
     * @return
     */
    static <T> Pipeline<T> of(Collection<T> collection) {
        return collection::forEach;
    }

    /**
     * 通过数据获取管道
     *
     * @param args
     * @param <T>
     * @return
     */
    static <T> Pipeline<T> of(T... args) {
        return c -> {
            for (T arg : args) {
                c.accept(arg);
            }
        };
    }

    /**
     * map方法,加工出新的元素
     *
     * @param function
     * @param <R>
     * @return
     */
    default <R> Pipeline<R> map(Function<T, R> function) {
        return c -> each(t -> c.accept(function.apply(t)));
    }

    /**
     * 过滤方法
     *
     * @param predicate 过来条件
     * @return
     */
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

    /**
     * 结束方法，将处理后的元素放入List
     *
     * @return
     */
    default List<T> toList() {
        List<T> list = new ArrayList<>();
        each(list::add);
        return list;
    }

}
