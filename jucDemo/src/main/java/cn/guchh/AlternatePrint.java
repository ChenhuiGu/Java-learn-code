package cn.guchh;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 交替打印ABC，例如ABCABCABC
 */
public class AlternatePrint {
    static Object object = new Object();

    public static void main(String[] args) {

        AtomicInteger threadNum = new AtomicInteger();

        //打印A
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                synchronized (object){
                    while (threadNum.get() != 0){
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("A");
                    threadNum.set(1);
                    object.notifyAll();
            }
            }
        },"t1").start();
        //打印B
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                synchronized (object){
                    while (threadNum.get() != 1){
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("B");
                    threadNum.set(2);
                    object.notifyAll();
                }
            }
        },"t2").start();
        //打印C
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                synchronized (object){
                    while (threadNum.get() != 2){
                        try {
                            object.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print("C");
                    threadNum.set(0);
                    object.notifyAll();
                }
            }
        },"t1").start();
    }
}
