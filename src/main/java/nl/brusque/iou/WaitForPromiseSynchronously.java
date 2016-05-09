package nl.brusque.iou;

public class WaitForPromiseSynchronously implements IFulfillerListener, IRejectorListener {
    private final PromiseState _promiseState;
    private static Integer _pendingPromises = 0;

    private static final class Lock { }
    private static final Object lock = new Lock();

    public WaitForPromiseSynchronously(PromiseState promiseState) {
        _promiseState = promiseState;

        _promiseState.addFulfillerListener(this);
        _promiseState.addRejectorListener(this);

        synchronized (lock) {
            _pendingPromises++;
        }
    }

    public void waitSynchronous() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFulfill(Object o) throws Exception {
        synchronized (lock) {
            if (!_promiseState.isPending()) {
                _pendingPromises--;
            }

            if (_pendingPromises <= 0) {
                lock.notifyAll();
            }
        }
    }

    @Override
    public void onReject(Object value) throws Exception {
        synchronized (_pendingPromises) {
            if (!_promiseState.isPending()) {
                _pendingPromises--;
            }

            if (_pendingPromises <= 0) {
                lock.notifyAll();
            }
        }
    }
}
