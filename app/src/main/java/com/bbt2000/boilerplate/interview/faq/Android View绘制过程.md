##### 概述
在ActivityThread的handleResumeActivity方法中，会调用WindowManager的 addView方法，将DecorView添加到Activity的Window中，同时WindowManager也会创建ViewRootImpl实例。

View的绘制流程正是从ViewRootImpl的performTraversal方法开始的，它经过测量（measure）、布局（layout）和绘制（draw）三个过程才能把一个 View 绘制出来。measure方法用于测量View的宽高，layout用于确定View在父容器中的放置位置，draw负责具体的绘制操作。


