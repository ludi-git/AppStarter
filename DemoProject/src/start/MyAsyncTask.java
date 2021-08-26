package start;

import java.util.List;

public abstract class MyAsyncTask implements ITask {
    private boolean isRunFinish = false;

    @Override
    public boolean isFinish() {
        return isRunFinish;
    }

    @Override
    public void setFinish(boolean finish) {
        isRunFinish = finish;
    }

    @Override
    public boolean runOnMainThread() {
        return false;
    }

    @Override
    public List<Class<? extends ITask>> dependsOn() {
        return null;
    }

    @Override
    public boolean needWait() {
        return false;
    }
}
