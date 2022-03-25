package cn.guchh;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 线程池的使用
 * - 构造线程池
 * - 线程数量
 * - 线程工厂，修改线程名称
 * @author chenhuigu
 */
@Slf4j(topic = "c.ThreadPoolExecutorDemo")
public class ThreadPoolExecutorDemo {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"MT-"+threadNumber.getAndIncrement());
            }
        });
        Future<String> future = pool.submit(() -> {
            log.info("start task");
            Thread.sleep(1000);

            return "result";
        });
        try {
            log.info("result:{}",future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        pool.shutdown();

    }

    private static void executeDemo(ExecutorService pool) {
        for (int i = 0; i < 3; i++) {
            int j = i;
            pool.execute(()->{
                log.info("执行任务{}",j);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
