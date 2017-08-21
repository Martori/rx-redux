package cat.martori.rxredux;

import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("Convert2Lambda")
public class RxStoreTest {

    private RxStore<Integer, Action> store;
    private int initialState = 0;

    @Test
    public void testGetState() throws Exception {
        store = new RxStore<>(initialState, this::reducer);
        assertEquals("The state retrieved should be the initial state if no action has been dispatched", initialState, store.getState().intValue());
        store.dispatch(INC);
        assertNotEquals("The state should have changed after dispatching a new action", initialState, store.getState().intValue());
    }

    @Test
    public void testDispatch() throws Exception {
        store = new RxStore<>(initialState, this::reducer);
        store.dispatch(INC);
        assertEquals("The state should increase by one after calling INC", initialState + 1, store.getState().intValue());
        store.dispatch(DEC);
        assertEquals("The state should decrease by one after calling DEC", initialState, store.getState().intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetStates() throws Exception {
        Consumer<Integer> callback = Mockito.mock(Consumer.class);
        Consumer<Integer> callback2 = Mockito.mock(Consumer.class);
        store = new RxStore<>(initialState, this::reducer);

        store.getStates().subscribe(callback);
        verify(callback, never()).accept(any());

        store.dispatch(INC);
        verify(callback).accept(initialState + 1);

        store.getStates().map(i -> i * (-1)).subscribe(callback2); // we can map without changing the other subscriptions
        verify(callback2, never()).accept(any());

        store.dispatch(INC);
        verify(callback).accept(initialState + 2);
        verify(callback2).accept((initialState + 2) * (-1));

        assertEquals("subscribing through states should not change the outcome", initialState + 2, store.getState().intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSubscribe() throws Exception {
        Consumer<Integer> callback = Mockito.mock(Consumer.class);
        Consumer<Integer> callback2 = Mockito.mock(Consumer.class);
        store = new RxStore<>(initialState, this::reducer);

        store.subscribe(callback);
        verify(callback, Mockito.never()).accept(any());
        verify(callback2, Mockito.never()).accept(any());

        store.dispatch(INC);
        verify(callback).accept(initialState + 1);
        verify(callback2, Mockito.never()).accept(any());

        store.subscribe(callback2);
        store.dispatch(INC);
        verify(callback).accept(initialState + 2);
        verify(callback2).accept(initialState + 2);

        store.dispatch(DEC);
        verify(callback, times(2)).accept(initialState + 1);
        verify(callback2).accept(initialState + 1);

        assertEquals("The state at the end should not be influenced by the subscriptions", initialState + 1, store.getState().intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnsubscribe() throws Exception {
        Consumer<Integer> callback = Mockito.mock(Consumer.class);
        store = new RxStore<>(initialState, this::reducer);

        //setup
        store.subscribe(callback);
        verify(callback, Mockito.never()).accept(any());

        //verify the first call
        store.dispatch(INC);
        verify(callback).accept(any());

        //test case
        store.unsubscribe(callback);
        store.dispatch(INC);
        store.dispatch(DEC);
        store.dispatch(INC);
        //verify that there where no more calls
        verify(callback).accept(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBadUnsubscribe() throws Exception {
        Consumer<Integer> callback = Mockito.mock(Consumer.class);
        store = new RxStore<>(initialState, this::reducer);

        //nothing should happen even when trying to unsubscribe a nonexistent subscription
        store.unsubscribe(callback);
    }

    private RxMiddleware<Integer, Action> middlewareA = new RxMiddleware<Integer, Action>() {
        @Override
        public void dispatch(RxStore<Integer, Action> store, Action action, RxDispatcher<Action> next) {
            next.dispatch(action);
        }
    };
    private RxMiddleware<Integer, Action> middlewareB = new RxMiddleware<Integer, Action>() {
        @Override
        public void dispatch(RxStore<Integer, Action> store, Action action, RxDispatcher<Action> next) {
            next.dispatch(DEC);
        }
    };

    @Test
    public void testOneMiddleware() throws Exception {
        RxMiddleware<Integer, Action> middlewareAspy = spy(middlewareA);
        store = new RxStore<>(initialState, this::reducer, middlewareAspy);

        verify(middlewareAspy, never()).dispatch(any(), any(), any());
        store.dispatch(INC);
        verify(middlewareAspy).dispatch(eq(store), eq(INC), any());

        assertEquals("The final state should be the same as with no middleware", initialState + 1, store.getState().intValue());
    }

    @Test
    public void testMultipleMiddleware() throws Exception {
        RxMiddleware<Integer, Action> middlewareAspy = spy(middlewareA);
        RxMiddleware<Integer, Action> middlewareBspy = spy(middlewareB);
        store = new RxStore<>(initialState, this::reducer, middlewareBspy, middlewareAspy);

        verify(middlewareBspy, never()).dispatch(any(), any(), any());
        verify(middlewareAspy, never()).dispatch(any(), any(), any());

        store.dispatch(INC);
        verify(middlewareBspy).dispatch(eq(store), eq(INC), any());
        verify(middlewareAspy).dispatch(eq(store), eq(DEC), any());

        assertEquals("The final state should reflect the effect of the middleware on the action flow", initialState - 1, store.getState().intValue());
    }

    enum B{
        A,C
    }

    /*
     * Implement a basic reducer for the action types declared
     */
    private Integer reducer(Integer i, Action a) {
        switch (a.type) {
            case DEC:
                return i - 1;
            case INC:
                return i + 1;
            default:
                return i;
        }
    }

    /*
     * Declare basic increase and decrease action to be used in the tests
     */
    enum ActionType {
        INC,
        DEC
    }

    private static class Action extends RxAction<ActionType, Object> {
        public Action(ActionType type) {
            super(type);
        }
    }

    /*
     * Instantiate the actions types for ease of use
     */
    private static Action INC = new Action(ActionType.INC);
    private static Action DEC = new Action(ActionType.DEC);
}