package nl.brusque.iou;

public class WaitForPromiseSynchronously implements IFulfillerListener, IRejectorListener {
    public WaitForPromiseSynchronously(PromiseState promiseState) {
        promiseState.addFulfillerListener(this);
        promiseState.addRejectorListener(this);

        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFulfill(Object o) throws Exception {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void onReject(Object value) throws Exception {
        synchronized (this) {
            notifyAll();
        }
    }
}
