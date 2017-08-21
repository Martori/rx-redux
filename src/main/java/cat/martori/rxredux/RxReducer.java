package cat.martori.rxredux;

/**
 * Functional interface that defines a reducer for a store.
 * <ul>
 * <li>A reducer MUST NOT produce any side effects.</li>
 * <li>A reducer MUST NOT use any other resources than the provided to compute its output.</li>
 * <li>A reducer MUST ALWAYS return a valid state.</li>
 * <li>A reducer MUST NOT change the received state, but instead return a new one.</li>
 * </ul>
 *
 * @param <S> The type of the state
 * @param <A> The type of the actions
 */
@FunctionalInterface
public interface RxReducer<S, A> {
    /**
     * Reduce a state by applying the action to it.
     * <ul>
     * <li>Only the {@code state} and {@code action} should be used to determine the output of this function.</li>
     * <li>The return state should be valid.</li>
     * <li>The state should not be changed.</li>
     * </ul>
     *
     * @param state  current state
     * @param action the action to be dispatched
     * @return a new state that is the result of applying the {@code action} on the {@code state}
     */
    S reduce(S state, A action);
}
