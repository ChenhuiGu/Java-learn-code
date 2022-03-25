package cn.guchh;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j(topic = "c.ThreadPoolDemo")
public class ThreadPoolDemo {

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2,5,500,TimeUnit.MILLISECONDS);
        for (int i = 0; i < 7; i++) {
            int j = i;
            threadPool.execute(()->{
                //执行任务
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("执行任务{}",j);
            });
        }
    }

}

@Slf4j(topic = "c.ThreadPool")
class ThreadPool {
    //任务，阻塞队列
    private TaskQueue<Runnable> taskQueue;
    // 正在工作的线程集合
    private HashSet<Worker> workers = new HashSet<>();
    //最大线程数
    private int coreSize;
    //等待时间
    long timeout;
    TimeUnit unit;

    public ThreadPool(int coreSize, int capacity,long timeout, TimeUnit unit) {
        this.coreSize = coreSize;
        this.taskQueue = new TaskQueue<>(capacity);
        this.unit = unit;
        this.timeout = timeout;
    }

    // 执行任务
    public void execute(Runnable task) {
        //比较线程数与任务数
        if (workers.size() < coreSize) {
            //增加新线程并添加到集合中
            Worker worker = new Worker(task);
            workers.add(worker);
            log.info("开启线程");
            worker.start();
        } else {
            //无线程执行任务，放到队列内
            taskQueue.offer(task,timeout,unit);
            log.info("{}任务放入队列",task);
        }
    }

    class Worker extends Thread {
        private Runnable task;

        private Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            while (task != null || (task = taskQueue.get(timeout,unit)) != null) {
                try {
                    log.info("执行任务");
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            // 清除线程
            synchronized (workers) {
                log.info("清除线程");
                workers.remove(this);
            }
        }
    }
}

class TaskQueue<T> {
    private Deque<T> queue = new ArrayDeque<>();
    private ReentrantLock lock = new ReentrantLock();
    //生产者条件变量
    private Condition fillWaitSet = lock.newCondition();
    //消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();
    private int capacity;

    public TaskQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 阻塞获取
     *
     * @return
     */
    public T take() {
        //加锁
        lock.lock();
        try {
            //队列为空，等待
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            // 唤醒等待线程
            emptyWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    /**
     * 超时的阻塞获取
     *
     * @param timeout
     * @param unit
     * @return
     */
    public T get(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if (nanos <= 0) return null;
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            // 唤醒等待线程
            emptyWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞添加
     *
     * @param task
     */
    public void put(T task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    fillWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            fillWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    public void offer(T task, long timeout, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    if(nanos <= 0) return;
                    nanos = fillWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(task);
            fillWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    private int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }

    }

}
