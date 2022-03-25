package cn.guchh;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 计算1-10累加值
 * @author chenhuigu
 */
public class ForkJoinDemo {
    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool(2);
        MyTask task = new MyTask(1,10);
        Integer invoke = pool.invoke(task);
        System.out.println(invoke);
    }
}

class MyTask extends RecursiveTask<Integer> {
    int begin;
    int end;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    /**
     * 分治计算
     * - 斐波那契数列
     * - 二分计算
     * @return
     */
    @Override
    protected Integer compute() {
        if (begin == end){
            return begin;
        }
        if (end - begin == 1){
            return begin + end;
        }
        int mid = begin + (begin - end)/2;
        MyTask t1 = new MyTask(begin,mid);
        t1.fork();
        MyTask t2 = new MyTask(mid+1,end);
        t2.fork();
        return t1.join() + t2.join();
    }
}
