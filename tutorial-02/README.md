# Work queues

![](https://www.rabbitmq.com/img/tutorials/python-two.png)

## Durable

큐 생성시 `durable`을 `true`로 설정하면 큐 서버가 내려가도 데이터를 잃지 않는다.

큐를 처음 생성 후 **옵션을 변경할 수 없다**.

![Imgur](https://i.imgur.com/dqdygaB.png)

```java
public class Queue extends AbstractDeclarable {
    /**
     * Construct a new queue, given a name and durability flag. The queue is non-exclusive and non auto-delete.
     *
     * @param name the name of the queue.
     * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
     */
    public Queue(String name, boolean durable) {
        this(name, durable, false, false, null);
    }
}
```

## Prefetch

아무 설정을 하지 않고 기본값을 사용할 경우 아래와 같이 두개의 `Receiver`에 반반씩 나눠서 처리된다.

`prefetch` 의 갯수 설정에 의해서 `Receiver`가 해야할 작업이 미리 정해지게 된다. 

예로 10개의 큐 데이터를 처리하는 시간이 **1번째가 6.5**초 **다른 9개가 1초**가 걸린다고 하면 아래와 같이 처리되게 된다. `prefetch` 설정에 의해서 처리해야 할 순서가 정해져 있는 것이다. (각각 250개까지 500개의 작업이 홀수는 1번 리시버 짝수는 2번 리시버)

![Imgur](https://i.imgur.com/wxqGFi8.png)

```
instance 1 [x] Received 'Hello #1 ......,'
instance 2 [x] Received 'Hello #2 .'
instance 2 [x] Done in 1.007s
instance 2 [x] Received 'Hello #4 .'
instance 2 [x] Done in 1.001s
instance 2 [x] Received 'Hello #6 .'
instance 2 [x] Done in 1.001s
instance 2 [x] Received 'Hello #8 .'
instance 2 [x] Done in 1.0s
instance 2 [x] Received 'Hello #10 .'
instance 2 [x] Done in 1.0s
instance 1 [x] Done in 6.51s
instance 1 [x] Received 'Hello #3 .'
instance 1 [x] Done in 1.0s
instance 1 [x] Received 'Hello #5 .'
instance 1 [x] Done in 1.0s
instance 1 [x] Received 'Hello #7 .'
instance 1 [x] Done in 1.001s
instance 1 [x] Received 'Hello #9 .'
instance 1 [x] Done in 1.0s
```

아래와 같이 처리하고 싶다면?

![Imgur](https://i.imgur.com/ykRHMQI.png)

`application.properties`에 `prefetch` 값을 설정하면 된다.

```properties
spring.rabbitmq.listener.simple.prefetch=1
```

```
instance 2 [x] Received 'Hello #2 .'
instance 1 [x] Received 'Hello #1 ......,'
instance 2 [x] Done in 1.001s
instance 2 [x] Received 'Hello #3 .'
instance 2 [x] Done in 1.0s
instance 2 [x] Received 'Hello #4 .'
instance 2 [x] Done in 1.0s
instance 2 [x] Received 'Hello #5 .'
instance 2 [x] Done in 1.002s
instance 2 [x] Received 'Hello #6 .'
instance 2 [x] Done in 1.002s
instance 2 [x] Received 'Hello #7 .'
instance 2 [x] Done in 1.001s
instance 2 [x] Received 'Hello #8 .'
instance 1 [x] Done in 6.503s
instance 1 [x] Received 'Hello #9 .'
instance 2 [x] Done in 1.001s
instance 2 [x] Received 'Hello #10 .'
instance 1 [x] Done in 1.001s
instance 2 [x] Done in 1.0s
```

## Acknowledgement

기본적으로 `Receiver`가 큐에서 데이터를 가져간 후 완료 신호(`ack`)를 보내야 큐에서 데이터를 지운다.

만약 가져간 데이터 처리 후에 프로세스가 죽어서 `ack`를 큐로 보내지 않으면 이 데이터는 큐에 다시 들어가게 된다.

11건의 데이터 중 2개의 데이터를 각각 `Receiver`가 가져가서 아직 완료 신호를 받지 못한 상태이다.

![Imgur](https://i.imgur.com/75vFQH1.png)

프로세스가 죽게 되면 다시 데이터가 복구 된다.

뭐.. 대충 이런 로그와 함께..

```
INFO [      Thread-10] o.s.a.r.l.SimpleMessageListenerContainer : Waiting for workers to finish.
INFO [      Thread-10] o.s.a.r.l.SimpleMessageListenerContainer : Workers not finished.
WARN [      Thread-10] o.s.a.r.l.SimpleMessageListenerContainer : Closing channel for unresponsive consumer: Consumer@3d1c1d26: tags=[[amq.ctag-Hd9CALLxOX1VFcGCi5kQfQ]], channel=Cached Rabbit Channel: AMQChannel(amqp://guest@127.0.0.1:5672/,1), conn: Proxy@7190b6c9 Shared Rabbit Connection: SimpleConnection@6de0ca54 [delegate=amqp://guest@127.0.0.1:5672/, localPort= 57875], acknowledgeMode=AUTO local queue size=0
```

다시 확인해보면 2건이 복구 되었다.

![Imgur](https://i.imgur.com/BcdYCLQ.png)

이 설정을 변경 하고 싶다면? `application.properties`에서 기본값을 변경할 수 있다.

```properties
spring.rabbitmq.listener.simple.acknowledge-mode=manual
```

## References

* [RabbitMQ - Queue](https://www.rabbitmq.com/queues.html)
* [RabbitMQ - Consumer Prefetch](https://www.rabbitmq.com/consumer-prefetch.html)
* [[RabbitMQ] Fair dispatch(prefetch 설정)](https://teragoon.wordpress.com/2012/01/27/fair-dispatchprefetch-%EC%84%A4%EC%A0%95/)