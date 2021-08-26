import start.TaskDispatchManager;
import start.creat.*;

public class Test {

    public static void main(String[] args) {
        TaskDispatchManager dispatchManager = TaskDispatchManager.getInstance();
        dispatchManager.addTask(new TaskA())
                .addTask(new TaskE())
                .addTask(new TaskD())
                .addTask(new TaskF())
                .addTask(new TaskB())
                .addTask(new TaskC())
                .start();
        dispatchManager.waitForTask();

        System.out.println("------ end ------" + Thread.currentThread().getName());
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
