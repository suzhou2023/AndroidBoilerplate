##### 定义
Handler机制是Android中基于单线消息队列模型的线程消息机制。

##### 解决问题
1.不能在非UI线程操作UI
2.不能在主线程执行耗时任务

##### 使用步骤
1.创建looper
2.启动looper
3.利用looper创建handler
4.利用handler发送消息

##### 内部工作原理
三大关键角色：Looper、MessageQueue、Handler
Looper、MessageQueue每个线程只有一个，并且相互独立（利用ThreadLocal来实现），Handler可以有多个。

工作流程如下：
1.利用线程的looper创建handler后，调用handler的send或post方法发送消息。注意创建handler和发送消息既可以在Looper线程，也可以在其他线程。
2.消息被加入到Looper线程的MessageQueue中，等待looper获取处理。
3.looper不断地从MessageQueue中获取消息，并调用对应handler的callback处理。如果有耗时任务，MessageQueue中后续的消息就要排队等待。

注意点：对MessageQueue的访问需要加锁，因为涉及到多线程并发（looper线程和发送消息的线程）；MessageQueue中没有消息时，Looper线程会进入等待状态。