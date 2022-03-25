package cn.guchh;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.shareData")
public class ShareData {
    static int counter = 0;
    static final Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (obj) {
                    counter++;
                }

            }

        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                counter--;
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("Counter:" + counter);
    }
}
