package cn.guchh;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import static cn.guchh.utils.Sleeper.sleep;

@Slf4j(topic = "c.saleTicket")
public class SaleTicket {
    public static void main(String[] args){
        Window window = new Window(2000);
        List<Integer> sellCount = new Vector<>();
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            Thread thread = new Thread(()->{
                int count = window.sell(randomAmount());
                sellCount.add(count);
            });
            list.add(thread);
            thread.start();
        }
        //for (Thread thread : list) {
        //    thread.join();
        //}
        list.forEach((t ->{
            try {
                t.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        } ));
        log.info("selled count:{}",sellCount.stream().mapToInt(c->c).sum());
        log.info("remainder count:{}",window.getCount());

    }
    static Random random = new Random();

    public static int randomAmount(){
        return random.nextInt(5) +1;
    }
}

class Window{
    private int count;

    public Window(int count){
        this.count = count;
    }

    public int getCount(){
        return count;
    }

    public int sell(int amount){
        if(count >= amount){
            count -= amount;
            sleep(0.01);
            return amount;
        }else {
            return 0;
        }
    }
}
