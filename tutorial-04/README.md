# Routing

`exchange`에서 `queue`로 내려질 때 `routingKey`를 조건으로 분배를 결정할 수 있다.

![](https://www.rabbitmq.com/img/tutorials/python-four.png)

```
instance 2 [x] Received 'Hello to debug 1'
instance 2 [x] Received 'Hello to info 2'
instance 2 [x] Received 'Hello to warn 3'
instance 2 [x] Received 'Hello to error 4' <<
instance 1 [x] Received 'Hello to error 4' <<
instance 2 [x] Received 'Hello to debug 5'
instance 2 [x] Received 'Hello to info 6'
instance 2 [x] Received 'Hello to warn 7'
instance 1 [x] Received 'Hello to error 8' <<
instance 2 [x] Received 'Hello to error 8' <<
instance 2 [x] Received 'Hello to debug 9'
instance 2 [x] Received 'Hello to info 10'
instance 2 [x] Received 'Hello to warn 11'
instance 1 [x] Received 'Hello to error 12' <<
instance 2 [x] Received 'Hello to error 12' <<
instance 2 [x] Received 'Hello to debug 13'
instance 2 [x] Received 'Hello to info 14'
instance 2 [x] Received 'Hello to warn 15'
instance 1 [x] Received 'Hello to error 16' <<
instance 2 [x] Received 'Hello to error 16' <<
instance 2 [x] Received 'Hello to debug 17'
```