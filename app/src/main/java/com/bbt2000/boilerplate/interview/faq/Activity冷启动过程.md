##### fork出App进程
在Launcher应用中点击App图标，即在Launcher进程中调用startActivity，会调用到system_server进程中AMS的功能;
AMS通过socket通知Zygote进程fork出新应用进程；

##### App进程初始化
实例化ActivityThread，它是应用程序进程中的核心类，是应用程序主线程的执行者；

实例化ApplicationThread，它是ActivityThread的静态嵌套类，实现了IApplicationThread接口（IPC接口），AMS通过它与应用进程进行通信；

开启应用程序主线程消息循环Looper.loop()；

将IApplicationThread接口绑定到AMS中，具体保存在了AMS中对应的ProcessRecord.thread，这样AMS就能通过该IApplicationThread接口发起向应用进程的调用。

##### Activity启动
AMS通过binder向应用程序进程发起调用，启动Activity。