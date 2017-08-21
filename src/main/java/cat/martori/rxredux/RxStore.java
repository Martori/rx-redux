package cat.martori.rxredux;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * A redux store that holds an state and can reduce it.
 *
 * @param <S> The type of the state holded in this store
 * @param <A> The type of the actions recognized by this store
 */
public class RxStore<S, A> {

    private S currentState;
    private Subject<A> actions = PublishSubject.create();
    private ConnectableObservable<S> states;
    private InnerDispatcher<A> dispatcher;
    private RxReducer<S, A> reducer;

    private Map<Consumer<S>, Disposable> disposables = new HashMap<>();

    /**
     * Creates a new redux store
     *
     * @param initialState the initial state the store will hold.
     * @param reducer      the reducer function to handle dispatched actions
     * @param middleware   a list of middleware to apply in order before the reducer
     */
    @SafeVarargs
    public RxStore(S initialState, RxReducer<S, A> reducer, RxMiddleware<S, A>... middleware) {
        this.currentState = initialState;
        this.reducer = reducer;
        this.dispatcher = applyMiddleware(this, middleware);
        this.states = actions
                .map(this::reduce)
                .publish(); //Use publish and connect so the map is executed only once per state
        this.states.connect();
    }

    /**
     * Dispatches a new action to compute a new state
     *
     * @param action the action to be dispatched
     */
    public void dispatch(A action) {
        dispatcher.dispatch(action);
    }

    /**
     * Retrieves the current state holded by the store
     *
     * @return the current state of the store
     */
    public S getState() {
        return currentState;
    }

    /**
     * Registers a consumer to be called each time a dispatched action is handled by the reducer
     *
     * @param afterDispatchState the consumer to be called with the new state
     * @return the same registered consumer so its easier to grab a reference to it.
     */
    public Consumer<S> subscribe(Consumer<S> afterDispatchState) {
        disposables.put(afterDispatchState, states.subscribe(afterDispatchState));
        return afterDispatchState;
    }

    /**
     * Unsubscribe a previously registered consumer
     *
     * @param consumer the registered consumer to be unsubscribed
     */
    public void unsubscribe(Consumer<S> consumer) {
        if (disposables.containsKey(consumer)) {
            disposables.get(consumer).dispose();
            disposables.remove(consumer);
        }
    }

    /**
     * Gets an observable of the store states
     *
     * @return An Observable of all the states to be generated be the store in the future
     */
    public Observable<S> getStates() {
        return states.autoConnect();
    }

    private S reduce(A action) {
        this.currentState = reducer.reduce(currentState, action);
        return currentState;
    }

    private InnerDispatcher<A> applyMiddleware(RxStore<S, A> store, RxMiddleware<S, A>[] middlewares) {
        List<RxMiddleware<S, A>> middlewareList = Arrays.asList(middlewares);
        Collections.reverse(middlewareList);

        InnerDispatcher<A> dispatch = convert(actions::onNext);
        return Observable.fromIterable(middlewareList)
                .map(RxStore::convert)
                .reduce(dispatch, (d, m) -> m.apply(store).wrap(d))
                .blockingGet();
    }

    /*
     * Helper interfaces and methods to make it easier to chain the middleware
     */
    private interface InnerDispatcher<A> {
        InnerDispatcher<A> dispatch(A var1);
    }

    private interface InnerMiddleware<S, A> {
        DispacherWrapper<A> apply(RxStore<S, A> store);
    }

    private interface DispacherWrapper<A> {
        InnerDispatcher<A> wrap(InnerDispatcher<A> next);
    }

    static private <S, A> InnerMiddleware<S, A> convert(RxMiddleware<S, A> m) {
        return (s) -> (n) -> new InnerDispatcher<A>() { //necessary so it can return itself
            public InnerDispatcher<A> dispatch(A var1) {
                m.dispatch(s, var1, convert(n));
                return this;
            }
        };
    }

    static private <A> InnerDispatcher<A> convert(RxDispatcher<A> d) {
        return new InnerDispatcher<A>() {
            @Override
            public InnerDispatcher<A> dispatch(A a) {
                d.dispatch(a);
                return this;
            }
        };
    }

    static private <A> RxDispatcher<A> convert(InnerDispatcher<A> d) {
        return d::dispatch;
    }

}
