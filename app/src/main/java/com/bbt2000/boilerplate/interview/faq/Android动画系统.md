##### 视图动画

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

