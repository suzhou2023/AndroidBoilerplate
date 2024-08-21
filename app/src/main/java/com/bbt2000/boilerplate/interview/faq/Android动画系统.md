##### 视图动画
又叫做补间动画（Tween），可以对View产生移动、缩放、旋转、透明度四种动画效果
View的状态只是视觉上的效果，实际属性值没有改变
建议使用xml文件定义，更具可读性、可重用性

##### 属性动画
插值器Interpolator
属性值的变化率：线性、加速、减速等
估值器Evaluator
通过属性或对象的初值、终值以及属性值变化的百分比如何计算出对应的当前值

API:
ValueAnimator
可以对基础数值类型做动画：Int、Float、16进制ARGB颜色值
系统自带估值器：IntEvaluator、FloatEvaluator、ArgbEvaluator
如果对自定义类型对象做动画，需要自定义估值器，实现TypeEvaluator<T>接口

ObjectAnimator
可以对某个View的某个属性做动画
ObjectAnimator.ofFloat
ObjectAnimator.ofPropertyValuesHolder

AnimatorSet
组合多个ObjectAnimator，并且可以控制它们的时序

ViewPropertyAnimator
专门针对View控件设计的动画API，性能更好
View.animate()返回一个ViewPropertyAnimator对象，可以对多个属性值做动画，链式调用

LayoutTransition
从ViewGroup中添加、删除子View时，对子View的动画
系统默认动画淡入淡出，在xml的ViewGroup中配置animateLayoutChanges
可以自定义动画效果

##### 过渡动画（Transition）
需要对window设置属性：
window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
并设置window的enterTransition和returnTransition，系统自带有Explode、Slide、Fade三种效果
启动Activity时，使用ActivityOptionsCompat.makeSceneTransitionAnimation()构造options；如果有共享元素动画，需传入共享元素View和共用的名字
退出Activity，需调用finishAfterTransition()

##### 揭露动画（Reveal Effect）
略

##### 视图状态动画（View State）
略

##### 触摸反馈动画（Ripple Effect）
略

##### ConstraintLayout动画
略
##### AnimatedVectorDrawable（矢量图动画）
略

