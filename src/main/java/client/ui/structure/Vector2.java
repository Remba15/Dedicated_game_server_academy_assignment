package client.ui.structure;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Vector2<T extends Number> {

    private final T x;
    private final T y;

    private Vector2(final T x, final T y) {
        this.x = x;
        this.y = y;
    }

    public static <T extends Number> Vector2<T> of(final T x, final T y) {
        return new Vector2<>(x, y);
    }

    public static Vector2<Float> of(final String x, final String y) {
        return new Vector2<>(Float.valueOf(x), Float.valueOf(y));
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(x) + "," + decimalFormat.format(y);
    }

}
