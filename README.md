# Android-YiNote
一个简单的云笔记软件，支持自动备份，微信分享等功能

这两天对笔记软件进行了迭代，利用Bmob后端，实现了用户注册于笔记同步功能，下面对实现要点进行记录。
####**备份功能**
![这里写图片描述](http://img.blog.csdn.net/20160615113034692)

####**同步功能**
![这里写图片描述](http://img.blog.csdn.net/20160615114103980)
##使用技术
1. Bmob后端云SDK的使用;
2. Service后台执行计划任务(自动将笔记上传),发送广播,通知更新;
3. ContentProvider提供数据;
4. sqlite实现本地缓存
## Material Design记事本的实现
参照这篇文章吧，我只是对它进行了封装

[Android 入门项目NoteBook](http://blog.csdn.net/y874961524/article/details/51494623)

## 注册用户
注册用户使用了BmobSDK的功能,Bmob会自动实现登录状态的维持,这点非常简单。

## Service后台执行计划任务
从配置文件中读取信息，看是否需要使用Service进行同步
有两种解决方案
1. Timer类实现
2. Android Alarm（闹钟）机制

这里使用了第一种，注意防止线程泄露。

## 微信分享
这个APP的资格还在审核，随后会更新。


Blog地址：

[http://blog.csdn.net/y874961524](http://blog.csdn.net/y874961524)

内测平台下载
[http://pre.im/yinote](http://pre.im/yinote)
