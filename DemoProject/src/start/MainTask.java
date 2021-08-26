package start;

public abstract class MainTask extends MyAsyncTask {

    @Override
    public boolean runOnMainThread() {
        return true;
    }

}
