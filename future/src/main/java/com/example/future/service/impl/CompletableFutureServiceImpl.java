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
        // 将Callable包装为Supplier，统一异常处理
        Supplier<T> supplier = () -> {
            try {
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
     * @param <T> 返回值类型
     * @return CompletableFuture异步任务对象
     */
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        // 使用自定义线程池而非默认ForkJoinPool
        return CompletableFuture.supplyAsync(supplier, getExecutor());
    }
}
