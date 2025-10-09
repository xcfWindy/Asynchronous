package com.example.future.controller;

import com.example.future.service.CompletableFutureService;
import com.example.future.util.CallableVoid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * TestOneController 单元测试类
 * 使用 Mockito 框架进行模拟测试
 * Mockito框架使用说明：
 * 1. @MockBean/@Mock: 创建模拟对象，替代真实对象进行测试
 * 2. when().thenReturn(): 指定当调用模拟对象的某个方法时返回特定值
 * 3. doNothing().when(): 指定当调用模拟对象的某个方法时不执行任何操作
 * 4. doThrow().when(): 指定当调用模拟对象的某个方法时抛出异常
 * 5. verify(): 验证模拟对象的某个方法是否被调用及调用次数
 * 6. times(): 指定期望的调用次数
 * 7. any(): 匹配任意参数值
 */
@ExtendWith(MockitoExtension.class)
public class TestOneControllerTest {

    /**
     * ## `@InjectMocks` 注解作用
     * <p>
     * ### 1. 核心功能
     * - **自动注入模拟对象**：`@InjectMocks` 注解用于标记需要进行依赖注入的测试对象，它会自动将标记为 `@Mock` 的模拟对象注入到被测试类的相应字段中
     * <p>
     * ### 2. 工作原理
     * - **字段注入**：自动将 `@Mock` 创建的CompletableFutureService对象注入到TestOneController类中的 [completableFutureService](file://D:\code\Asynchronous\future\src\main\java\com\example\future\controller\TestOneController.java#L15-L16) 字段
     * - **构造函数注入**：如果被测试类有构造函数，会尝试通过构造函数注入依赖
     * - **setter方法注入**：如果存在对应的setter方法，也会尝试通过setter方法注入
     * <p>
     * ### 3. 使用场景
     * - **单元测试隔离**：在不启动完整Spring上下文的情况下，通过Mockito框架实现依赖注入
     * - **提高测试效率**：避免了复杂的Spring配置，使测试运行更快速
     * - **控制测试环境**：可以精确控制哪些依赖被模拟，哪些使用真实对象
     * <p>
     * ### 4. 配合注解
     * - **`@Mock`**：创建模拟对象，与 `@InjectMocks` 配合使用
     * - **`@Spy`**：创建部分模拟对象，未被模拟的方法调用真实实现
     * - **`@Captor`**：创建参数捕获器，用于验证方法调用时的参数值
     * <p>
     * 在当前测试类中，`@InjectMocks` 确保了 [TestOneController](file://D:\code\Asynchronous\future\src\main\java\com\example\future\controller\TestOneController.java#L10-L47) 实例能够获得 `@Mock` 注解创建的 [CompletableFutureService](file://D:\code\Asynchronous\future\src\main\java\com\example\future\service\CompletableFutureService.java#L9-L21) 模拟对象，从而实现对控制器的单元测试。
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
     * 测试 FourFuture 方法正常执行的情况
     * 验证在服务成功执行时，completableFutureService 的 allOfAndJoin 方法被调用一次
     */
    @Test
    public void testFourFuture_WhenServiceSucceeds_ThenCallsServiceOnce() throws Exception {
        // Given
        doNothing().when(completableFutureService).allOfAndJoin(any(CallableVoid[].class));

        // When
        testOneController.FourFuture();

        // Then
        verify(completableFutureService, times(1)).allOfAndJoin(any(CallableVoid[].class));
    }

    /**
     * 测试 FourFuture 方法在 service 抛出异常时的行为
     * 验证当服务抛出异常时，FourFuture 方法会将异常抛出
     */
    @Test
    public void testFourFuture_WhenServiceThrowsException_ThenThrowsException() throws Exception {
        // Given
        doThrow(new RuntimeException("模拟异常"))
                .when(completableFutureService)
                .allOfAndJoin(any(CallableVoid[].class));

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            testOneController.FourFuture();
        });

        verify(completableFutureService, times(1)).allOfAndJoin(any(CallableVoid[].class));
    }

    /**
     * 测试 CallableVoid 匿名内部类的 call 方法
     * 验证 FourFuture 调用时传入了 4 个 CallableVoid 实例
     */
    @Test
    public void testCallableVoidCallMethod_WhenFourFutureCalled_ThenFourCallablesPassed() throws Exception {
        // Given
        doNothing().when(completableFutureService).allOfAndJoin(any(CallableVoid[].class));

        // When
        testOneController.FourFuture();

        // Then
        ArgumentCaptor<CallableVoid[]> captor = ArgumentCaptor.forClass(CallableVoid[].class);
        verify(completableFutureService, times(1)).allOfAndJoin(captor.capture());

        CallableVoid[] captured = captor.getValue();
        assertEquals(4, captured.length); // 确保传入了4个 CallableVoid 实例
    }
}
