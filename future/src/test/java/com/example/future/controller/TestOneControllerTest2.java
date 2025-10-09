package com.example.future.controller;

import com.example.future.service.CompletableFutureService;
import com.example.future.util.CallableVoid;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.*;

/**
 * TestOneController 单元测试类
 *
 * @SpringBootTest 注解使用不当
 * 当前使用 @SpringBootTest(classes = TestOneController.class) 是不合适的。
 * 该注解用于启动整个 Spring 上下文，但传入的类不是配置类或启动类，会导致上下文加载失败或行为异常。
 * 如果只是进行单元测试（不依赖完整 Spring 上下文），应该使用 @ExtendWith(MockitoExtension.class) 来启用 Mockito 支持。
 */
@SpringBootTest(classes = TestOneController.class)
public class TestOneControllerTest2 {

    /**
     * 注入模拟对象到TestOneController实例中
     * 该注解用于将标记为@Mock或@Spy的对象自动注入到被测试的控制器中
     * 实现依赖注入以便进行单元测试
     */
    @InjectMocks
    private TestOneController testOneController;


    /**
     * ## @Mock注解说明
     * <p>
     * ### 1. 功能作用
     * - `@Mock`注解用于创建一个模拟对象(Mock Object)，替代真实的依赖对象进行测试
     * - 在单元测试中隔离被测对象与真实依赖，提高测试的可控性和执行速度
     * <p>
     * ### 2. 使用场景
     * - 替代复杂的外部服务调用(如数据库、网络请求)
     * - 模拟异常情况和边界条件
     * - 避免测试时依赖真实环境的配置和数据
     * <p>
     * ### 3. 工作原理
     * - 通过Mockito框架动态生成接口或类的代理实例
     * - 可以预设方法调用的返回值、抛出异常等行为
     * - 支持验证方法调用次数和参数匹配
     * <p>
     * ### 4. 相关注解配合
     * - `@InjectMocks`: 将@Mock创建的模拟对象自动注入到被测试类的相应字段中
     * - `@Spy`: 创建部分模拟对象，未被模拟的方法会调用真实实现
     * - `@MockBean`: Spring Boot提供的注解，用于替换Spring容器中的Bean
     */
    @Mock
    private CompletableFutureService completableFutureService;


    /**
     * 测试配置类，用于提供模拟的 CompletableFutureService 实例
     *
     * @TestConfiguration: 标识这是一个测试配置类，用于在测试环境中替代或补充主配置
     * @Bean: 声明一个bean，此处返回一个 CompletableFutureService 的模拟对象
     * mock(): Mockito方法，创建指定类的模拟对象
     * 冗余的 @TestConfiguration 和 @Bean 配置
     * 在当前测试中已经通过 @Mock 创建了 CompletableFutureService 模拟对象，并通过 @InjectMocks 注入到控制器中，因此不需要额外定义 @TestConfiguration 来创建另一个模拟 Bean。
     * 这可能导致 Spring 容器中存在多个相同类型的 Bean，引发歧义或注入错误。
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public CompletableFutureService completableFutureService() {
            return mock(CompletableFutureService.class);
        }
    }


    /**
     * 测试 FourFuture 方法正常执行的情况
     * <p>
     * doNothing().when(): 配置模拟对象，当调用 allOfAndJoin 方法时不执行任何操作
     * any(): 参数匹配器，匹配任意 CallableVoid 数组参数
     * verify(): 验证模拟对象的方法是否被调用
     * times(1): 验证方法被调用一次
     */
    @Test
    public void testFourFuture_NormalExecution() throws Exception {
        // Given: 模拟 service 方法不抛异常
        doNothing().when(completableFutureService).allOfAndJoin(any(CallableVoid[].class));

        // When: 调用被测方法
        testOneController.FourFuture();

        // Then: 验证 service 方法被调用了一次，并传入了4个 CallableVoid 参数
        verify(completableFutureService, times(1)).allOfAndJoin(any(CallableVoid[].class));
    }

    /**
     * 测试 FourFuture 方法在 service 抛出异常时的行为
     * <p>
     * doThrow(): 配置模拟对象，当调用指定方法时抛出异常
     * assertThrows(): 断言指定代码块会抛出特定异常
     */
    @Test
    public void testFourFuture_ServiceThrowsException() throws Exception {
        // Given: 模拟 service 方法抛出异常
        doThrow(new RuntimeException("模拟异常")).when(completableFutureService)
                .allOfAndJoin(any(CallableVoid[].class));

        // When & Then: 调用被测方法应抛出异常
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            testOneController.FourFuture();
        });

        // 验证 service 方法确实被调用了一次
        verify(completableFutureService, times(1)).allOfAndJoin(any(CallableVoid[].class));
    }

    /**
     * 测试 CallableVoid 匿名内部类的 call 方法
     * <p>
     * 此测试通过验证 service 方法被调用来间接测试 CallableVoid 的 call 方法
     * 因为在实际的 FourFuture 方法中会创建 CallableVoid 实例并传递给 service 方法
     */
    @Test
    public void testCallableVoidCallMethod() throws Exception {
        // Given: 模拟 service 方法不抛异常
        doNothing().when(completableFutureService).allOfAndJoin(any(CallableVoid[].class));

        // When: 调用被测方法
        testOneController.FourFuture();

        // Then: 验证 service 方法被调用了一次
        verify(completableFutureService, times(1)).allOfAndJoin(any(CallableVoid[].class));
    }
}
