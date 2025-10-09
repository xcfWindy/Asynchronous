package com.example.future.service;

import com.example.future.util.CallableVoid;

import java.util.concurrent.CompletableFuture;

/**
 * 异步任务接口
 */
public interface CompletableFutureService {

    /**
     * 同时执行多个异步任务并等待执行完成
     * 异常已做处理
     * 抛出异常时,带有顺序,当第一个和第二个都抛异常时,优先抛出第一个.
     * 子类实现的方法只能返回空的泛型
     *
     * @param callableArray 异步任务数组
     */
    void allOfAndJoin(CallableVoid... callableArray) throws Exception;
//    void allOfAndJoin(CompletableFuture<Void> future);
}
