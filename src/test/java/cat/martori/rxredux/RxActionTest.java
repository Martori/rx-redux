package cat.martori.rxredux;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by karibul on 19/08/2017.
 */
public class RxActionTest {

    enum Type {
        A,
        B
    }

    RxAction<Type, Integer> basicActionWValue;
    RxAction<Type, ?> basicAction;

    @Before
    public void setUp() throws Exception {
        basicAction = new RxAction<>(Type.A);
        basicActionWValue = new RxAction<>(Type.B, 2);
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals("Action without payload should equal its type", Type.A.toString(), basicAction.toString());
        Assert.assertEquals("Action with payload should include its type and payload", String.format("%s: %s", Type.B.toString(), 2), basicActionWValue.toString());
    }

}