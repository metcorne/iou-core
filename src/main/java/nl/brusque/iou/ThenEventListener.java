package nl.brusque.iou;

final class ThenEventListener<TFulfill, TOutput> implements IEventListener<ThenEvent<TFulfill, TOutput>> {
    private final ResolvableManager<TFulfill> _resolvableManager;
    private final PromiseState<TFulfill> _promiseState;
    private final AbstractThenCallableStrategy _thenCaller;


    ThenEventListener(PromiseState<TFulfill> promiseState, ResolvableManager<TFulfill> resolvableManager, AbstractThenCallableStrategy thenCaller) {
        _promiseState      = promiseState;
        _resolvableManager = resolvableManager;
        _thenCaller        = thenCaller;
    }

    @Override
    public void process(ThenEvent<TFulfill, TOutput> event) throws Exception {
        IThenCallable<TFulfill, TOutput> fulfillable = event.getFulfillable();
        IThenCallable<Object, TOutput> rejectable    = event.getRejectable();

        addResolvable(fulfillable, rejectable, event.getNextPromise());
    }

    private void addResolvable(IThenCallable<TFulfill, TOutput> fulfillable, IThenCallable<Object, TOutput> rejectable, AbstractPromise<TOutput> nextPromise) throws Exception {
        _resolvableManager.add(new Resolvable<>(fulfillable, rejectable, nextPromise, _thenCaller));

        if (_promiseState.isRejected()) {
            _promiseState.reject(_promiseState.getRejectionReason());
        } else if (_promiseState.isResolved()) {
            _promiseState.fulfill(_promiseState.getResolvedWith());
        }
    }
}
