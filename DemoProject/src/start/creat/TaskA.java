package start.creat;

import start.ITask;
import start.MyAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class TaskA extends MyAsyncTask {
    @Override
    public boolean needWait() {
        return true;
    }

    @Override
    public List<Class<? extends ITask>> dependsOn() {
        List<Class<? extends ITask>> list = new ArrayList<>();
        list.add(TaskE.class);
        return list;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task A run..." + Thread.currentThread().getName());
    }
}
