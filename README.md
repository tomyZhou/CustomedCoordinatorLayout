谷歌新出的Android Design Support Library带来了新的兼容的md风格控件，其中的coordinatorlayout配合appbarlayout再指定behavior可以实现滚动的效果，然而很遗憾，在应用到项目的时候，出现各种问题。

具体问题描述参考如下：
http://blog.csdn.net/oushangfeng123/article/details/47326511

我遇到的问题是：不滑动recyclerView,点击某个按钮就要让隐藏的AppBarLayout显示出来，系统的
CoordinatorLayout不好实现，于是自己做了。

上面链接里给出的效果做的很好，就有一点不能实现随处上滑隐藏的功能，于是稍加修改。

http://blog.csdn.net/nnmmbb/article/details/77503461

 ![image](https://github.com/tomyZhou/CustomedCoordinatorLayout/blob/master/scrrenshot.gif)