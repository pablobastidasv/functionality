package co.pablobastidas.extensions;

import java.util.Objects;

public final class Pair<T, O> {

    private final T one;
    private final O two;

    public Pair(T one, O two) {
        this.one = one;
        this.two = two;
    }

    public T one() {
        return one;
    }

    public O two() {
        return two;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "one=" + one +
                ", two=" + two +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!Objects.equals(one, pair.one)) {
            return false;
        }
        return Objects.equals(two, pair.two);
    }

    @Override
    public int hashCode() {
        int result = one != null ? one.hashCode() : 0;
        result = 31 * result + (two != null ? two.hashCode() : 0);
        return result;
    }
}
