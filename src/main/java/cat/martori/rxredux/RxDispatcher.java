package cat.martori.rxredux;

/**
 * Helper interface to ease the writing of middleware
 *
 * @param <A> The type of action dispatched
 */
@FunctionalInterface
public interface RxDispatcher<A> {
    /**
     * Dispatch a new action to the store
     *
     * @param action the action to be dispatched
     */
    void dispatch(A action);
}
