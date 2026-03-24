import java.util.function.DoubleBinaryOperator;

interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}
enum LengthUnit implements IMeasurable {
    FEET(12.0),
    INCHES(1.0),
    YARDS(36.0),
    CENTIMETERS(0.393701);

    private final double factor;

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor; 
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return this.name();
    }
}
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor; 
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return this.name();
    }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor; // base = litre
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return this.name();
    }
}
class Quantity<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }
    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Divide by zero");
            return a / b;
        });

        private final DoubleBinaryOperator operation;

        ArithmeticOperation(DoubleBinaryOperator operation) {
            this.operation = operation;
        }

        public double compute(double a, double b) {
            return operation.applyAsDouble(a, b);
        }
    }
    private void validate(Quantity<U> other, U targetUnit, boolean checkTarget) {
        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Different categories not allowed");

        if (!Double.isFinite(this.value) || !Double.isFinite(other.value))
            throw new IllegalArgumentException("Invalid numbers");

        if (checkTarget && targetUnit == null)
            throw new IllegalArgumentException("Target unit required");
    }
    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation op) {
        validate(other, null, false);

        double base1 = this.toBase();
        double base2 = other.toBase();

        return op.compute(base1, base2);
    }
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit, true);

        double baseResult = performBaseArithmetic(other, ArithmeticOperation.ADD);
        double result = targetUnit.convertFromBaseUnit(baseResult);

        result = round(result);
        return new Quantity<>(result, targetUnit);
    }
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other, targetUnit, true);

        double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        double result = targetUnit.convertFromBaseUnit(baseResult);

        result = round(result);
        return new Quantity<>(result, targetUnit);
    }
    public double divide(Quantity<U> other) {
        double result = performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
        return round(result);
    }
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target can't be null");

        double base = this.toBase();
        double result = targetUnit.convertFromBaseUnit(base);

        result = round(result);
        return new Quantity<>(result, targetUnit);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) obj;

        if (this.unit.getClass() != other.unit.getClass())
            return false;

        double v1 = this.toBase();
        double v2 = ((Quantity<U>) other).toBase();

        return Math.abs(v1 - v2) < 1e-6;
    }
    private double round(double val) {
        return Double.parseDouble(String.format("%.2f", val));
    }
    public String toString() {
        return value + " " + unit.getUnitName();
    }
}
public class QuantityMeasurementApp13 {

    public static void main(String[] args) {
        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6, LengthUnit.INCHES);

        System.out.println(l1.add(l2));
        System.out.println(l1.subtract(l2));
        System.out.println(l1.divide(l2));
        Quantity<WeightUnit> w1 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(500, WeightUnit.GRAM);

        System.out.println(w1.add(w2));
        System.out.println(w1.subtract(w2));
        System.out.println(w1.divide(w2));
        Quantity<VolumeUnit> v1 = new Quantity<>(2, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500, VolumeUnit.MILLILITRE);

        System.out.println(v1.add(v2));
        System.out.println(v1.subtract(v2));
        System.out.println(v1.divide(v2));

        System.out.println(l1.convertTo(LengthUnit.INCHES));
        System.out.println(new Quantity<>(1, WeightUnit.KILOGRAM).equals(new Quantity<>(1000, WeightUnit.GRAM)));
    }
}