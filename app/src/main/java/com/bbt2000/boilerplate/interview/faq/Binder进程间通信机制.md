##### 概述
Binder机制涉及到四个主体：service_manager、Server、Client、binder驱动；
service_manager、Server、Client分别存在于3个进程中，binder驱动则是内核中的共享空间；
service_manager是binder机制的守护进程，它提供服务的注册和查询功能；

##### 注册服务
- Server进程向binder驱动发起服务注册请求；
- binder驱动将注册请求转发给service_manager进程；
- service_manager进程添加该服务，实际上是服务端binder对象的handle；

##### 获取服务
- Client进程向binder驱动发起获取服务的请求，传递要获取服务的名称；
- binder驱动将该请求转发给service_manager进程；
- service_manager查找到Client需要的服务信息（handle）；
- 通过binder驱动将handle返回给Client进程；

##### 使用服务
- binder驱动在内核空间创建了一块缓存区；
- Server进程用户空间地址映射到该内核空间缓存区；
- Client进程通过系统调用copy_from_user发送数据到内核中的缓存区（当前线程被挂起），由于内存映射，相当于发送到了Server进程用户空间；
- binder驱动对Server进程进行通知，Server进程从线程池中取出线程，进行数据解包，并且调用目标方法，最后将数据写入自己的共享内存中；
- binder驱动通知Client进程获取返回结果，Client进程之前挂起的线程被重新唤醒，通过系统调用copy_to_user从内核缓存区接收Server返回的数据；

##### 说明
binder机制中所有数据和命令都是通过内核中转的，内核空间是客户端进程和服务端进程通信的桥梁。

