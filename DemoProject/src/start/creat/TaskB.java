package start.creat;

import start.ITask;
import start.MyAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class TaskB extends MyAsyncTask {

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task B run..."+ Thread.currentThread().getName());
    }
}
