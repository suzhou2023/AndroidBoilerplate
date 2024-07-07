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
5、Context的数量等于Activity的个数 + Service的个数 +1，这个1为Application。

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