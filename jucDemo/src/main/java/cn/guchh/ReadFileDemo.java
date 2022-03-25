package cn.guchh;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenhuigu
 * 任务：读取大批量文件
 */
@Slf4j(topic = "c.ThreadPoolDemo")
public class ReadFileDemo {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 30;
    private static final Long KEEP_ALIVE_TIME = 1L;
    public static void main(String[] args) {

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            Runnable worker = new ReadFile("file-"+i);
            threadPoolExecutor.execute(worker);
        }
        threadPoolExecutor.shutdown();
        while(!threadPoolExecutor.isTerminated()){
        }
        long end = System.currentTimeMillis();
        log.info("task is finish,total time:{}ms",end - start);
    }
}

@Slf4j(topic = "c.ReadFile")
class ReadFile implements Runnable{
    private String fileName;

    public ReadFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        log.info("start read file:{}",fileName);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
