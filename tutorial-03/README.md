# Publish / Subscribe

한 큐에 데이터가 들어오면 그 큐를 이용하는 모든 서비스에 동일하게 데이터를 내려줄 수 없을까?

![](https://www.rabbitmq.com/img/tutorials/python-three-overall.png)

`fanout` 타입의 `exchange`가 각각 `Consumer`들의 전용 큐(`exclusive: true`)에 데이터를 넣어준다.

![Imgur](https://i.imgur.com/iIXZTBQ.png)