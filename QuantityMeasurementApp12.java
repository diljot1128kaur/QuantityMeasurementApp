import java.util.*;
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
    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) {
        return value * factor; 
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() { return name(); }
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
    private double toBaseUnit() {
        return unit.convertToBaseUnit(value);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Quantity<?> other = (Quantity<?>) obj;
        if (!this.unit.getClass().equals(other.unit.getClass())) return false;

        return Math.abs(this.toBaseUnit() - other.toBaseUnit()) < 1e-6;
    }
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");
        double base = this.toBaseUnit();
        double result = targetUnit.convertFromBaseUnit(base);
         result = Double.parseDouble(String.format("%.2f", result));
         return new Quantity<>(result, targetUnit);
    }
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (other == null || targetUnit == null)
            throw new IllegalArgumentException("Invalid input");
        double sumBase = this.toBaseUnit() + other.toBaseUnit();
        double result = targetUnit.convertFromBaseUnit(sumBase);
        result = Double.parseDouble(String.format("%.2f", result));
        return new Quantity<>(result, targetUnit);
    }
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        if (other == null)
            throw new IllegalArgumentException("Invalid input");
        double diffBase = this.toBaseUnit() - other.toBaseUnit();
        double result = targetUnit.convertFromBaseUnit(diffBase);
        result = Double.parseDouble(String.format("%.2f", result));
        return new Quantity<>(result, targetUnit);
    }
    public double divide(Quantity<U> other) {
        if (other == null)
            throw new IllegalArgumentException("Invalid input");
        double divisor = other.toBaseUnit();
        if (divisor == 0)
            throw new ArithmeticException("Cannot divide by zero");

        return this.toBaseUnit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toBaseUnit(), unit.getClass());
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}
public class QuantityMeasurementApp12{

    public static void main(String[] args) {

        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6, LengthUnit.INCHES);

        System.out.println(l1.subtract(l2));
        System.out.println(l1.divide(l2));
        Quantity<WeightUnit> w1 = new Quantity<>(10, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5, WeightUnit.KILOGRAM);

        System.out.println(w1.subtract(w2));
        System.out.println(w1.divide(w2));
        Quantity<VolumeUnit> v1 = new Quantity<>(5, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(2, VolumeUnit.LITRE);

        System.out.println(v1.subtract(v2));
        System.out.println(v1.divide(v2));
    }
}