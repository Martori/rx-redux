package cat.martori.rxredux;

/**
 * Helper class to use with RxStore
 * It's completely optional and any other kind of action can be used
 *
 * @param <T> Enum defining the supported types of actions
 * @param <P> The type of the payload of this action (if any)
 */
public class RxAction<T extends Enum, P> {
    public final T type;
    public final P payload;

    public RxAction(T type) {
        this(type, null);
    }

    public RxAction(T type, P payload) {
        this.type = type;
        this.payload = payload;
    }

    public String toString() {
        return this.payload != null ? this.type.toString() + ": " + this.payload.toString() : this.type.toString();
    }
}
