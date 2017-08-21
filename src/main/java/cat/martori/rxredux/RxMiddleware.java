package cat.martori.rxredux;

/**
 * A middleware is what allows you to introduce side effects and async operations to a RxStore in a controlled manner.
 * <p>
 * Your middleware must call {@code next.dispatch(action)} at some point or in some way for the action to be reduced at the store.
 * </p>
 *
 * @param <S> The type of states handled in the store
 * @param <A> The type of actions handled by this Middleware and the store
 */
@FunctionalInterface
public interface RxMiddleware<S, A> {

    /**
     * Handles an action before it reaches the store.
     *
     * @param store  the store that ultimately wild handle the operation. Holds the current state
     * @param action the action to be handled by this Middleware
     * @param next   the next dispatcher in the chain. Your Middleware must call {@link RxDispatcher#dispatch(Object)} for the action to reach the store
     */
    void dispatch(RxStore<S, A> store, A action, RxDispatcher<A> next);
}
