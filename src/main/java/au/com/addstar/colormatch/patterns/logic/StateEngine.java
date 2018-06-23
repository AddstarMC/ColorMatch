package au.com.addstar.colormatch.patterns.logic;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class StateEngine<T> implements Runnable {
    private BukkitTask mTask;
    private State<T> mState;
    private T mGame;
    private final Plugin mPlugin;

    public StateEngine(Plugin plugin) {
        mPlugin = plugin;
    }

    public void start(State<T> state, T game) {
        mGame = game;
        mTask = Bukkit.getScheduler().runTaskTimer(mPlugin, this, 5, 5);
        mState = state;
        mState.onStart(this, mGame);
    }

    public void end() {
        if (mTask != null)
            mTask.cancel();
    }

    public void setState(State<T> state) {
        mState.onEnd(this, mGame);
        mState = state;
        mState.onStart(this, mGame);
    }

    public void abortState(State<T> next) {
        mState = next;
        mState.onStart(this, mGame);
    }

    @Override
    public void run() {
        mState.onTick(this, mGame);
    }

    public void sendEvent(String name, Object data) {
        mState.onEvent(name, data, this, mGame);
    }

    public boolean isRunning() {
        return mState != null;
    }

    public State<T> getCurrentState() {
        return mState;
    }
}
