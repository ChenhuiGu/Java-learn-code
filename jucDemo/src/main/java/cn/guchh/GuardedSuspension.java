package cn.guchh;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.gs")
public class GuardedSuspension {
    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        // 获取结果
        new Thread(()->{
            log.info("获取结果");
            String response = (String) guardedObject.getResponse(3000);
            log.info("response:{}",response);
        },"t1").start();

        // 生成结果
        new Thread(()->{
            log.info("start download");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            guardedObject.setResponse(null);
        },"t2").start();
    }
}

class GuardedObject{
    private Object response;

    public synchronized Object getResponse(long waitTime){
        long begin = System.currentTimeMillis();
        long passTime = 0;
        //虚假唤醒
        while (response == null){
            try {
                long diff = waitTime - passTime;
                if(diff <= 0) break;
                this.wait(diff);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            passTime = System.currentTimeMillis() - begin;
        }
        return response;
    }

    public synchronized void setResponse(Object response){
        this.response = response;
        this.notifyAll();
    }
}
