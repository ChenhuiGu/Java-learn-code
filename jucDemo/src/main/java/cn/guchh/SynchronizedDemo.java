package cn.guchh;
import lombok.extern.slf4j.Slf4j;
import static cn.guchh.utils.Sleeper.sleep;

@Slf4j(topic = "c.synchronizedDemo")
public class SynchronizedDemo {
    public static void main(String[] args) {
        Room r1 = new Room();
        new Thread(()->{r1.a();}).start();
        new Thread(r1::b).start();
    }

}

@Slf4j(topic = "c.synchronizedDemo")
class Room{
    public static synchronized void a(){
        log.info("a init");
        sleep(1);
        log.info("a");
    }

    public synchronized void b(){
        log.info("b");
    }
}
