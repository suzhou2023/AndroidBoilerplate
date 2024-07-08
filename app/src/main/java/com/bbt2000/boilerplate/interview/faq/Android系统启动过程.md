##### init进程启动
Linux内核启动完成后，会启动第一个用户空间进程：init进程。
init进程会读取init.rc文件，启动servicemanager、Zygote等关键服务和进程。

##### Zygote进程启动
init进程根据init.rc文件启动Zygote进程；
创建并初始化Android虚拟机（ART）；
加载核心库和资源（Java类、JNI库、字体等）；
启动System Server进程；
创建一个socket服务端，等待其它进程的请求（比如启动新应用进程请求）；

##### System Server进程启动
System Server进程是Android系统的核心服务进程。它由Zygote进程启动，启动之后会初始化和启动各种系统服务：如AMS、PMS、WMS等，之后进入事件循环，等待和处理系统事件和请求。

##### Launcher启动
System Server通过AMS启动Launcher应用程序。
