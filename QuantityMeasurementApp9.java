class Length {

    private final double value;
    private final LengthUnit unit;

    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12),
        YARDS(3.0),
        CENTIMETERS(0.0328084);

        private final double factor;

        LengthUnit(double factor) {
            this.factor = factor;
        }

        double toBase(double value) {
            return value * factor;
        }

        double fromBase(double base) {
            return base / factor;
        }
    }

    Length(double value, LengthUnit unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.toBase(value);
    }

    public Length convertTo(LengthUnit target) {
        double base = toBase();
        double result = target.fromBase(base);
        result = Math.round(result * 100.0) / 100.0;
        return new Length(result, target);
    }

    public Length add(Length other) {
        double sum = this.toBase() + other.toBase();
        double result = unit.fromBase(sum);
        result = Math.round(result * 100.0) / 100.0;
        return new Length(result, this.unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Length)) return false;

        Length other = (Length) o;
        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    public String toString() {
        return value + " " + unit;
    }
}
class QuantityWeight {
    private final double value;
    private final WeightUnit unit;

    enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double factor;

        WeightUnit(double factor) {
            this.factor = factor;
        }

        double toBase(double value) {
            return value * factor;
        }

        double fromBase(double base) {
            return base / factor;
        }
    }

    QuantityWeight(double value, WeightUnit unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.toBase(value);
    }

    public QuantityWeight convertTo(WeightUnit target) {
        double base = toBase();
        double result = target.fromBase(base);
        result = Math.round(result * 100.0) / 100.0;
        return new QuantityWeight(result, target);
    }

    public QuantityWeight add(QuantityWeight other) {
        double sum = this.toBase() + other.toBase();
        double result = unit.fromBase(sum);
        result = Math.round(result * 100.0) / 100.0;
        return new QuantityWeight(result, this.unit);
    }

    public QuantityWeight add(QuantityWeight other, WeightUnit target) {
        double sum = this.toBase() + other.toBase();
        double result = target.fromBase(sum);
        result = Math.round(result * 100.0) / 100.0;
        return new QuantityWeight(result, target);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuantityWeight)) return false;

        QuantityWeight other = (QuantityWeight) o;
        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    public String toString() {
        return value + " " + unit;
    }
}
public class QuantityMeasurementAPp9 {

    public static void main(String[] args) {

        Length l1 = new Length(12, Length.LengthUnit.INCHES);
        Length l2 = new Length(1, Length.LengthUnit.FEET);
        System.out.println(l1.equals(l2));

        Length a = new Length(1, Length.LengthUnit.FEET);
        Length b = new Length(12, Length.LengthUnit.INCHES);
        System.out.println(a.add(b));


        QuantityWeight w1 = new QuantityWeight(1.0, QuantityWeight.WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, QuantityWeight.WeightUnit.GRAM);
        System.out.println(w1.equals(w2));
        QuantityWeight w3 = new QuantityWeight(2.0, QuantityWeight.WeightUnit.POUND);
        System.out.println(w3.convertTo(QuantityWeight.WeightUnit.KILOGRAM));
        QuantityWeight w4 = new QuantityWeight(500.0, QuantityWeight.WeightUnit.GRAM);
        QuantityWeight w5 = new QuantityWeight(0.5, QuantityWeight.WeightUnit.KILOGRAM);
        System.out.println(w4.add(w5));
        System.out.println(w4.add(w5, QuantityWeight.WeightUnit.KILOGRAM));


        System.out.println(l1.equals(w1));
    }
}