package client.ui.structure;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Vector3<T extends Number> {

    private final T x;
    private final T y;
    private final T z;

    private Vector3(final T x, final T y, final T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static <T extends Number> Vector3<T> of(final T x, final T y, final T z) {
        return new Vector3<>(x, y, z);
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public T getZ() { return z; }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return decimalFormat.format(x) + "," + decimalFormat.format(y) + "," + decimalFormat.format(z);
    }

}