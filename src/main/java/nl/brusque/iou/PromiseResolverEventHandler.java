package nl.brusque.iou;

import nl.brusque.iou.errors.TypeError;
import nl.brusque.iou.errors.TypeErrorException;

import java.util.ArrayDeque;

class PromiseResolverEventHandler<TResult extends AbstractPromise<TResult>> {
    private final AbstractThenCaller _fulfiller;
    private final AbstractThenCaller _rejector;

    private final PromiseStateHandler _promiseState = new PromiseStateHandler();
    private final EventDispatcher _eventDispatcher  = new EventDispatcher();
    private final ArrayDeque<Resolvable> _onResolve = new ArrayDeque<>();

    public PromiseResolverEventHandler(AbstractThenCaller fulfiller, AbstractThenCaller rejector) {
        _fulfiller = fulfiller;
        _rejector  = rejector;

        _eventDispatcher.addListener(ThenEvent.class, new ThenEventListener<>(_eventDispatcher, _promiseState, _onResolve));
        _eventDispatcher.addListener(FulfillEvent.class, new FulfillEventListener<>(_eventDispatcher, _promiseState));
        _eventDispatcher.addListener(FireFulfillsEvent.class, new FireFulfillsEventListener<>(_promiseState, _onResolve, _fulfiller));
        _eventDispatcher.addListener(RejectEvent.class, new RejectEventListener<>(_eventDispatcher, _promiseState));
        _eventDispatcher.addListener(FireRejectsEvent.class, new FireRejectsEventListener<>(_promiseState, _onResolve, _rejector));
    }

    synchronized void addThenable(Object onFulfilled, Object onRejected, TResult nextPromise) {
        _eventDispatcher.queue(new ThenEvent<>(new ThenEventValue<>(onFulfilled, onRejected, nextPromise)));
    }

    synchronized AbstractPromise<TResult> resolveWithValue(final AbstractPromise<TResult> promise, final Object o) {
        return resolvePromise(promise, FulfillEvent.class, RejectEvent.class, o);
    }

    synchronized AbstractPromise<TResult> rejectWithValue(final AbstractPromise<TResult> promise, final Object o) {
        return resolvePromise(promise, RejectEvent.class, RejectEvent.class, o);
    }

    private boolean testObjectEqualsPromise(Object o, AbstractPromise<TResult> promise) {
        return o != null && o.equals(promise);
    }

    private AbstractPromise<TResult> resolvePromise(final AbstractPromise<TResult> promise, final Class<? extends AbstractEvent> event, final Class<? extends AbstractEvent> onFailEvent, final Object o) {
        if (!_promiseState.isPending()) {
            return promise;
        }

        try {
            if (testObjectEqualsPromise(o, promise)) {
                throw new TypeErrorException();
            }
        } catch (TypeErrorException e) {
            // 2.3.1: If `promise` and `x` refer to the same object, fireRejectables `promise` with a `TypeError' as the reason.
            _eventDispatcher.queue(EventFactory.create(onFailEvent, new TypeError()));
            return promise;
        }

        _eventDispatcher.queue(EventFactory.create(event, o));

        return promise;
    }
}
