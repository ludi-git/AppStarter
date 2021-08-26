package start.creat;

import start.MyAsyncTask;

public class TaskC extends MyAsyncTask {
    @Override
    public void run() {
        System.out.println("task C run..."+ Thread.currentThread().getName());
    }
}
