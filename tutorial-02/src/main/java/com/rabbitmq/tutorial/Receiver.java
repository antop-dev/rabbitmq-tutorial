package com.rabbitmq.tutorial;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StopWatch;

@Slf4j
@RabbitListener(queues = RabbitConfig.QUEUE_NAME)
public class Receiver {
    private final int instance;

    public Receiver(int i) {
        this.instance = i;
    }

    @RabbitHandler
    public void receive(String in) throws InterruptedException {
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("instance {} [x] Received '{}'", instance, in);
        doWork(in);
        watch.stop();
        log.info("instance {} [x] Done in {}s", instance, watch.getTotalTimeSeconds());
    }

    private void doWork(String in) throws InterruptedException {
        long sleep = 0;
        for (char ch : in.toCharArray()) {
            if (ch == '.') {
                sleep += 1000;
            }
            if (ch == ',') {
                sleep += 500;
            }
        }
        Thread.sleep(sleep); // 작업이 시간이 소요된 것처럼 하기

        // test ack
        // 무조건 프로세스 죽이기
        // System.exit(0);
    }

}
