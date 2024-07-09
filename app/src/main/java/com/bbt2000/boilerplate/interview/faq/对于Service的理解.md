##### 定义
服务（Service）是Android的四大组件之一，是Android中实现程序后台运行的解决方案，适合去执行那些不需要和用户交互而且还要求长期运行的任务。

Service 的运行不依赖于任何用户界面，即使程序被切换到后台，或者用户打开了另外一个应用程序，Service 仍然能够保持正常运行。
Service 并不是运行在一个独立的进程当中的 ，而是依赖于创建服务时所在的应用程序进程。当某个应用程序进程被杀掉时，所有依赖于该进程的服务也会停止运行。
Service 并不会自动开启线程，所有代码都是默认运行在主线程中。我们需要在服务内部手动创建子线程，并在这里执行具体的任务。

##### Service的生命周期
startService方式启动：
startService方式启动的Service，会一直在后台运行，直至调用stopService或自身调用stopSelf方法。

bindService方式启动：
bindService方式启动的Service，需要调用unbindService来停止该Service，或者启动该Service的Context不存在了，Service也会被销毁。该方式启动的Service的生命周期依附于启动它的Context。

startService + bindService两种方式：
当Service在被启动(startService)的同时又被绑定(bindService)，调用unbindService将不会停止该Service，必须继续调用stopService或Service自身调用stopSelf来停止服务。
所以说为了停止（销毁）一个Service，需要取消service的所有绑定（断开连接），并且如果有调用startService方法启动该service，需要调用一次stopService（或stopSelf），Service才会停止。

##### 回调执行次数
onCreate，onBind，onUnbind，onDestroy只会执行一次；
onStartCommand次数与startService次数一致；

##### Activity与Service如何交互
1.通过Binder对象。Service被绑定的时候，onBind()方法执行，返回一个Binder对象给发起绑定的Activity。Activity绑定Service成功后，拿到Binder对象，就可以调用Binder对象的方法了。

2.使用监听器。在Activity与Service绑定成功以后，Activity通过Binder对象拿到Service的引用（Binder中要提供获取Service引用的方法），然后就可以给Service设置监听器了。最后Service适时的调用监听器中的接口。

3.使用广播接收器。

4.跨进程的Activity与Service交互，需要用到AIDL编程。