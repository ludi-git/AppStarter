package start;


import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskDispatchManager {
    private final List<ITask> allTask = new ArrayList<>();
    // 存储每个节点的入度
    private final HashMap<Class<? extends ITask>, Integer> deeps = new HashMap<>();
    // 存储Class和对象的关系
    private final HashMap<Class<? extends ITask>, ITask> tasks = new HashMap<>();
    // 0 代表没有加入过 1 代表加入过了
    private final HashMap<ITask, Integer> isAddResult = new HashMap<>();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;
    private ExecutorService executorService;
    private int needWaitCount = 0;
    private CountDownLatch countDownLatch;
    // 存储结果
    private final Queue<ITask> result = new LinkedList<>();


    private TaskDispatchManager() {
        executorService = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    }

    private static class SingleHolder {
        public static TaskDispatchManager instance = new TaskDispatchManager();
    }

    public static TaskDispatchManager getInstance() {
        return SingleHolder.instance;
    }

    public TaskDispatchManager addTask(ITask task) {
        if (task.needWait()) {
            needWaitCount++;
        }
        allTask.add(task);
        return this;
    }

    public void waitForTask() {
        if (needWaitCount > 0) {
            countDownLatch = new CountDownLatch(needWaitCount);
            if (needWaitCount > 0) {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void countDownLatch() {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void start() {
        // （1）如果执行主线程任务的时候不想切换到主线程那么必须在主线程启动任务  也可以采用第二种 在子线程中启动执行任务 然后主线程任务切换到主线程
        // 我写的java项目直接简单判断了一下 android用 Looper 判断一下就行
        if (!"main".equals(Thread.currentThread().getName())) {
            throw new IllegalStateException("must in mainThread");
        }

        // （2）为了防止主线程执行的任务阻塞其他任务start 在子线程中开始  遇到需要在主线程中的任务切换到主线程执行
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
        //System.out.println("CORE_POOL_SIZE = " + CORE_POOL_SIZE);
        // 拓扑排序 （像我那种启动task的方式 其实排序不排序都可以了） 从队列头中拿出task执行
        sortTaskByDependsOn();
        while (!result.isEmpty()) {
            ITask poll = result.poll();
            List<Class<? extends ITask>> classes = poll.dependsOn();
            if (classes == null || classes.isEmpty()) {
                runTask(poll);
            } else {
                boolean isAllFinish = true;
                for (Class<? extends ITask> aClass : classes) {
                    ITask task = tasks.get(aClass);
                    if (!task.isFinish()) {
                        isAllFinish = false;
                        break;
                    }
                }
                if (isAllFinish) {
                    runTask(poll);
                } else {
                    result.offer(poll);
                }
            }
        }
//            }
//        });


    }


    private void runTask(ITask task) {
        if (task.runOnMainThread()) {
            // 如果是在子线程启动任务的话  此处应该通过Handler等方式切换到主线程运行
            // 如果是在主线程启动任务 则直接执行就可以
            task.run();
            task.setFinish(true);
        } else {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    task.run();
                    task.setFinish(true);
                    countDownLatch();
                }
            });
        }
    }

    private void sortTaskByDependsOn() {
        for (ITask task : allTask) {
            // 初始值都是 0
            isAddResult.put(task, 0);
            tasks.put(task.getClass(), task);
            // 设置每个task的入度
            List<Class<? extends ITask>> classes = task.dependsOn();
            if (classes != null) {
                deeps.put(task.getClass(), classes.size());
            }
        }
        for (ITask value : tasks.values()) {
            sortByDeep(value);
        }
    }

    private void sortByDeep(ITask task) {
        Integer deep = deeps.get(task.getClass());
        if ((deep == null || deep == 0)) {
            if (isAddResult.get(task) == 0) {
                result.offer(task);
                isAddResult.put(task, 1);
            }
        } else {
            List<Class<? extends ITask>> classes = task.dependsOn();
            for (Class<? extends ITask> aClass : classes) {
                sortByDeep(tasks.get(aClass));
                int resultDeep = --deep;
                deeps.put(task.getClass(), resultDeep);
                if (resultDeep == 0 && isAddResult.get(task) == 0) {
                    result.offer(task);
                    isAddResult.put(task, 1);
                }
            }
        }
    }

}
