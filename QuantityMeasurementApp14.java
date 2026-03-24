import java.util.function.DoubleBinaryOperator;
@FunctionalInterface
interface SupportsArithmetic {
    boolean isSupported();
}

interface IMeasurable {

    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
        
    }
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

    public double getConversionFactor() { return factor; }

    public double convertToBaseUnit(double value) {
        return value * factor; 
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() { return name(); }
}
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() { return factor; }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() { return name(); }
}
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() { return factor; }

    public double convertToBaseUnit(double value) {
        return value * factor; 
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() { return name(); }
}
enum TemperatureUnit implements IMeasurable {

    CELSIUS(
        c -> c,
        c -> c
    ),

    FAHRENHEIT(
        f -> (f - 32) * 5 / 9,
        c -> (c * 9 / 5) + 32
    ),

    KELVIN(
        k -> k - 273.15,
        c -> c + 273.15
    );

    private final java.util.function.Function<Double, Double> toCelsius;
    private final java.util.function.Function<Double, Double> fromCelsius;
    private final SupportsArithmetic supportsArithmetic = () -> false;

    TemperatureUnit(
            java.util.function.Function<Double, Double> toCelsius,
            java.util.function.Function<Double, Double> fromCelsius) {
        this.toCelsius = toCelsius;
        this.fromCelsius = fromCelsius;
    }

    public double getConversionFactor() {
        return 1.0; 
    }

    public double convertToBaseUnit(double value) {
        return toCelsius.apply(value); 
    }

    public double convertFromBaseUnit(double baseValue) {
        return fromCelsius.apply(baseValue);
    }

    public String getUnitName() {
        return name();
    }

    @Override
    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does NOT support " + operation + " operation"
        );
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
    private enum Operation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Divide by zero");
            return a / b;
        });

        private final DoubleBinaryOperator op;

        Operation(DoubleBinaryOperator op) {
            this.op = op;
        }

        public double compute(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }
    private void validate(Quantity<U> other, String operation) {
        if (other == null)
            throw new IllegalArgumentException("Other cannot be null");

        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Different categories");

        this.unit.validateOperationSupport(operation);
        other.unit.validateOperationSupport(operation);
    }

    private double performBaseArithmetic(Quantity<U> other, Operation op) {
        validate(other, op.name());

        double b1 = this.toBase();
        double b2 = other.toBase();

        return op.compute(b1, b2);
    }
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double base = performBaseArithmetic(other, Operation.ADD);
        double result = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(result), targetUnit);
    }
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        double base = performBaseArithmetic(other, Operation.SUBTRACT);
        double result = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(result), targetUnit);
    }
    public double divide(Quantity<U> other) {
        return round(performBaseArithmetic(other, Operation.DIVIDE));
    }
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target null");

        double base = this.toBase();
        double result = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(result), targetUnit);
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) o;

        if (this.unit.getClass() != other.unit.getClass())
            return false;

        return Math.abs(this.toBase() -
                ((Quantity<U>) other).toBase()) < 1e-6;
    }

    private double round(double v) {
        return Double.parseDouble(String.format("%.2f", v));
    }

    public String toString() {
        return value + " " + unit.getUnitName();
    }
}

public class QuantityMeasurementApp14 {

    public static void main(String[] args) {
        Quantity<TemperatureUnit> t1 =new Quantity<>(0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 =new Quantity<>(32, TemperatureUnit.FAHRENHEIT);

        System.out.println(t1.equals(t2));

        System.out.println(new Quantity<>(100, TemperatureUnit.CELSIUS).convertTo(TemperatureUnit.FAHRENHEIT));

    
        try {
            System.out.println(t1.add(t2));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12, LengthUnit.INCHES);

        System.out.println(l1.add(l2));
    }
}
