package cn.guchh;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic="c.reentrantLock")
public class ReentrantLockDemo {
    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        Thread t1 = new Thread(()->{
            try {
                if (!lock.tryLock(2, TimeUnit.SECONDS)) {
                    log.info("获取锁失败");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("锁被打断");
                return;
            }
            try{
                log.info("获取锁成功");
            }finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.info("获取锁");

        t1.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();

    }
}
