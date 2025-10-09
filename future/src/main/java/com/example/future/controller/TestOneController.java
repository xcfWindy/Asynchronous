package com.example.future.controller;

import com.example.future.service.CompletableFutureService;
import com.example.future.util.CallableVoid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test/one")
public class TestOneController {

    @Autowired
    private CompletableFutureService completableFutureService;

    @GetMapping("/fourFuture")
    public void FourFuture() throws Exception {
        log.info("fourFuture-start");
        completableFutureService.allOfAndJoin(
                //匿名内部类
                new CallableVoid() {
                    @Override
                    public Void call() throws Exception {
                        log.info("fourFuture-1");
                        //不需要返回值
                        return null;
                    }
                },//Lambda表达式
                () -> {
                    log.info("fourFuture-2");
                    //不需要返回值
                    return null;
                },
                () -> {
                    log.info("fourFuture-3");
                    //不需要返回值
                    return null;
                },
                () -> {
                    log.info("fourFuture-4");
                    //不需要返回值
                    return null;
                });
    }
}
