package com.example.future.util;

import java.util.concurrent.Callable;

/**
 * 在需要使用Callable任务,但不需要返回值的场景使用.
 * 例如,并发编程中,我们需要使用ExecutorService来执行一些任务,这些任务可能是IO密集型的,也可能是CPU密集型的.
 * 在这些情况下,我们可以使用Callable接口来创建任务,然后使用ExecutorService的submit方法来提交任务.
 * 如果返回值不重要,我们就可以使用这个接口.
 */
public interface CallableVoid extends Callable<Void> {
}
