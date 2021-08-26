package start;

import java.util.List;

public interface ITask {
    boolean isFinish();

    boolean runOnMainThread();

    List<Class<? extends ITask>> dependsOn();

    boolean needWait();

    void run();

    void setFinish(boolean isFinish);


}
