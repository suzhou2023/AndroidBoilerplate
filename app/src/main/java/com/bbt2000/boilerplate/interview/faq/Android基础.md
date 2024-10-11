##### Activity启动模式？
standard（栈顶可以存在多个实例）
默认模式，每次启动Activity时，都会创建一个新的实例，并将其放入当前任务的栈顶。适用于大多数情况，适合希望每次启动都创建新实例的Activity。
singleTop（栈顶只能存在一个实例）
如果要启动的Activity实例已经在任务栈的栈顶，则不会创建新的实例，而是重用栈顶的实例，并调用它的onNewIntent方法；否则，会创建新的实例。适用于那些在栈顶时不需要重复创建实例的Activity。
singleTask（任务栈只能存在一个实例）
系统会检查任务栈中是否存在要启动的Activity实例，如果存在，则会把该实例之上的所有Activity出栈，使该实例成为栈顶；如果不存在，则创建新的实例并将其放在新任务的栈顶。适用于希望一个任务中只有一个实例的Activity，如应用的主页面或启动页面。
singleInstance（任务栈有且仅有一个实例）
系统会创建一个新的任务栈，并且这个任务栈中只有这个Activity实例。任何其他应用启动这个Activity时，都会重用这个唯一实例。适用于需要独立运行，且不与其他Activity共享任务栈的Activity，如特殊的工具应用。

##### Android怎么加速启动Activity？
onCreate、onReume中不执行耗时操作；
把页面显示的View细分一下，利用Handler逐步显示，这样用户的看到的就是有层次有步骤的一个个View的展示，不会先看到一个黑屏，然后一下显示所有View；
减少主线程阻塞时间；
优化布局文件；

##### Android中进程的优先级？
1.前台进程
当前与用户交互的进程。
用户当前正在使用的 Activity、正在前台运行的 Service。最高优先级，不会被杀死。
2.可见进程
对用户可见但没有与用户直接交互的进程。
一个已暂停但对用户仍可见的 Activity（例如，弹出对话框后面显示的 Activity）。
高优先级，除非系统内存非常紧张，否则不会被杀死。
3.服务进程
运行着一个已启动服务的进程，虽然用户看不到，但对用户体验很重要。
后台播放音乐的服务。
中等优先级，当系统内存紧张时可能被杀死。
4.后台进程
当前对用户不可见的进程。
用户最近使用过的 Activity，但现在不再可见。
低优先级，当系统内存需要时，首先被杀死。
5.空进程
不包含任何活动组件，仅用于缓存和提高启动速度。
最低优先级，当系统需要内存时，会优先被杀死。

##### Context相关
1、Activity和Service以及Application的Context是不一样的,Activity继承自ContextThemeWraper.其他的继承自ContextWrapper。
2、每一个Activity和Service以及Application的Context是一个新的ContextImpl对象。
3、getApplication()用来获取Application实例的，但是这个方法只有在Activity和Service中才能调用的到。那也许在绝大多数情况下我们都是在Activity或者Servic中使用Application的，但是如果在一些其它的场景，比如BroadcastReceiver中也想获得Application的实例，这时就可以借助getApplicationContext()方法，getApplicationContext()比getApplication()方法的作用域会更广一些，任何一个Context的实例，只要调用getApplicationContext()方法都可以拿到我们的Application对象。
4、创建对话框时不可以用Application的context，只能用Activity的context。
5、利用Application、Service、BroadcastReceiver、ContentProvider的context启动Activity时，需要添加FLAG_ACTIVITY_NEW_TASK标志，因为这些context都没有与界面相关的任务栈。Activity的context不需要。

##### Bundle传递对象为什么需要序列化？
Bundle传递数据只支持基本数据类型，所以传递对象时需要序列化转换成可存储或可传输的本质状态（字节流）。序列化后的对象可以在网络、IPC（Activity、Service、BroadcastReceiver等）中进行传输，或存储到本地。

##### Serializable和Parcelable的区别？
Serializable是Java自带的序列化工具，利用反射机制，性能较差，但使用简单；
Parcelable是Android特有的序列化工具，需要手动编写序列化和反序列化逻辑，但是性能更好，使用频繁进行对象传递的场景。

##### JAR和AAR的区别
JAR包里面只有代码，AAR里面不光有代码还包括资源文件，比如 drawable 文件，xml资源文件。对于一些不常变动的 Android Library，我们可以直接引用 aar，加快编译速度。

##### Android中有哪几种xml解析方式？
1.SAX (Simple API for XML)
SAX 是一种事件驱动的解析方式，适用于处理大型 XML 文件，因为它不会将整个文档加载到内存中，而是通过事件回调逐行解析 XML。
内存占用小，解析速度快。但编码复杂，需要手动处理事件。
2.DOM (Document Object Model)
DOM 解析将整个 XML 文档加载到内存中，构建一个树形结构，适用于处理较小的 XML 文件。
易于理解和使用，可以随机访问文档的任意部分。但内存占用大，处理大型 XML 文件效率低。
3.PULL (XML Pull Parsing)
PULL 解析是 Android 独有的一种解析方式，类似于 SAX 但比 SAX 更加灵活和易用。
内存占用小，解析速度快，代码更简洁易读。缺点是需要手动解析 XML 结构。

##### Android中常见子线程更新UI的方式？
利用Handler在非UI线程中向UI线程发送消息和执行代码；
Activity.runOnUiThread(Runnable)；
View.post(Runnable)；
AsyncTask；
EventBus；

##### requestLayout、invalidate和postInvalidate的区别？
如果View的LayoutParams发生了改变，需要父布局对其进行重新测量、布局、绘制这三个流程，往往使用requestLayout；
而invalidate则是使当前View进行重绘，不会进行测量、布局流程，因此如果View只需要重绘而不需要测量，布局的时候，使用invalidate方法比requestLayout方法更高效；postInvalidate用于在非UI线程中重绘View；

##### assets目录与res目录的区别？
assets：不会在 R 文件中生成相应标记，存放到这里的资源在打包时会打包到程序安装包中。（通过 AssetManager 类访问这些文件）
res：会在 R 文件中生成 id 标记，资源在打包时如果使用到则打包到安装包中，未用到不会打入安装包中

##### 程序A能否接收到程序B的广播？
能，使用全局的BroadCastRecevier能进行跨进程通信，但是注意它只能被动接收广播。此外，LocalBroadCastRecevier只限于本进程的广播通信。



