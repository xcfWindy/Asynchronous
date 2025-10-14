package com.example.future.service.impl;

import com.example.future.service.CompletableFutureService;
import com.example.future.util.CallableVoid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Slf4j
@Service
public class CompletableFutureServiceImpl implements CompletableFutureService {

    private static Executor executor;

    private Executor getExecutor() {
        if (executor != null) {
            return executor;
        }
        synchronized (CompletableFutureServiceImpl.class) {
            if (executor != null) {
                return executor;
            }
            int corePoolSize = Runtime.getRuntime().availableProcessors();
            executor = new ThreadPoolExecutor(corePoolSize,
                    corePoolSize * 2 + 1,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingDeque<>(100),
                    //拒绝任务,调用者现场执行任务
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }
        return executor;
    }

    /**
     * 同时执行多个异步任务并等待任务执行完成
     *
     * @param callableArray 异步任务数组
     */
    @Override
    public void allOfAndJoin(CallableVoid... callableArray) throws Exception {
        // 将所有CallableVoid任务转换为CompletableFuture任务数组
        CompletableFuture[] futures = Arrays.stream(callableArray)
                .map(this::callAsync)
                .toArray(CompletableFuture[]::new);
        // 等待所有异步任务完成后再返回
        CompletableFuture.allOf(futures).join();
    }

    /**
     * 异步执行任务，自带异常处理机制
     *
     * @param callable 要执行的任务
     * @return CompletableFuture异步任务对象
     */
    public <T> CompletableFuture<T> callAsync(Callable<T> callable) {
        //将 Callable 包装成 Supplier 以适配 CompletableFuture.supplyAsync()
        //Supplier它主要用于提供数据，不接受任何参数，但返回一个结果。
        //当 CompletableFuture.supplyAsync 调度执行时，会调用 supplier.get()，进而执行 callable.call()
        Supplier<T> supplier = () -> {
            try {
                // 实际执行任务的地方
                return callable.call();
            } catch (Exception e) {
                // 将检查异常转换为运行时异常
                throw new RuntimeException(e);
            }
        };

        // 使用自定义线程池执行异步任务
        return supplyAsync(supplier);
    }

    /**
     * 使用自定义线程池执行异步任务
     *
     * @param supplier 任务供应器
     * @param <T>      返回值类型
     * @return CompletableFuture异步任务对象
     */
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        // supplyAsync用于异步执行任务并返回结果，使用自定义线程池而非默认ForkJoinPool
        return CompletableFuture.supplyAsync(supplier, getExecutor());
    }
}
/**
 * 任务执行时机
 * Callable<T> 包装成 Supplier<T> 时确实不会执行任务
 * 真正的任务执行发生在调用 CompletableFuture.supplyAsync 时
 * 该方法会立即将任务提交到线程池中异步执行
 * 调用流程分析
 * 当有4个任务时：
 * callAsync 方法会被调用4次
 * supplyAsync 方法也会被调用4次
 * 每次调用 supplyAsync 都会立即返回 CompletableFuture<T> 对象
 * supplyAsync 方法本身不会阻塞
 * 任务提交到线程池后立即返回 CompletableFuture 对象
 * 实际的任务执行在线程池的工作线程中进行
 * 由于使用了自定义线程池，4个任务会并发执行（根据线程池配置决定同时执行的数量），而不是串行执行。
 */
