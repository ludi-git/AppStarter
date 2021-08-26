# AppStarter
Android启动优化思路

- IDEA 项目 （记录了优化思想不能直接使用；代码不是重点，思想更重要）

通过多线程并发执行任务；
通过拓扑排序任务
通过CountDownLatch等实现等待任务完成
可以切换是否在主线程运行
可以添加线程执行依赖

下面贴一张网络图 此项目只是中间主要部分；其余根据需求自己补充添加～
![image](https://user-images.githubusercontent.com/69558023/130965090-a34c2329-9671-4269-9f26-dfe19c576e4b.png)
