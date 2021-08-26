package start.creat;

import start.ITask;
import start.MainTask;

import java.util.ArrayList;
import java.util.List;

public class TaskF extends MainTask {
    @Override
    public List<Class<? extends ITask>> dependsOn() {
        List<Class<? extends ITask>> list = new ArrayList<>();
        list.add(TaskA.class);
        return list;
    }

    @Override
    public void run() {
        System.out.println("TaskF run ..." + Thread.currentThread().getName());
    }
}
