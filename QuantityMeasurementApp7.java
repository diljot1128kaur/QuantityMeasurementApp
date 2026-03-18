import java.util.*;
class Length {
    private double value;
    private LengthUnit unit;
    public enum LengthUnit {
        Feet(12.0),
        Inches(1.0),
        Centimeters(0.393701),
        Yards(36.0);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double getConversionFactor() {
            return conversionFactor;
        }
    }
    public Length(double value, LengthUnit unit) {
        if (unit == null || !Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.value = value;
        this.unit = unit;
    }
    private double convertToBaseUnit() {
        return value * unit.getConversionFactor();
    }
    public boolean compare(Length thatLength) {
        if (thatLength == null) return false;

        double val1 = this.convertToBaseUnit();
        double val2 = thatLength.convertToBaseUnit();

        return Math.abs(val1 - val2) < 1e-6;
    }

    
    public Length add(Length thatLength) {
        if (thatLength == null) {
            throw new IllegalArgumentException("Length cannot be null");
        }

        
        double val1 = this.convertToBaseUnit();
        double val2 = thatLength.convertToBaseUnit();
        double sumBase = val1 + val2;
        double result = sumBase / this.unit.getConversionFactor();
        result = Double.parseDouble(String.format("%.2f", result));

        return new Length(result, this.unit);
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Length other = (Length) o;

        return Math.abs(this.convertToBaseUnit() - other.convertToBaseUnit()) < 1e-6;
    }
    public Length convertTo(LengthUnit targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target can't be null");

        double val = this.convertToBaseUnit();
        val = val / targetUnit.getConversionFactor();

        val = Double.parseDouble(String.format("%.2f", val));

        return new Length(val, targetUnit);
    }

    @Override
    public String toString() {
        return String.format("%.2f", value) + " " + unit;
    }
}

public class QuantityMeasurementApp7 {
    public static boolean demonstrateLengthEquality(Length length1, Length length2) {
        return length1.equals(length2);
    }
    public static boolean demonstrateLengthComparison(double val1, Length.LengthUnit unit1,double val2, Length.LengthUnit unit2) {
        Length l1 = new Length(val1, unit1);
        Length l2 = new Length(val2, unit2);
        return l1.compare(l2);
    }

    public static Length demonstrateLengthConversion(double value,Length.LengthUnit fromUnit,Length.LengthUnit toUnit) {
        if (fromUnit == null || toUnit == null)
            throw new IllegalArgumentException("Units can't be null");

        Length obj = new Length(value, fromUnit);
        return obj.convertTo(toUnit);
    }

    public static Length demonstrateLengthConversion(Length length,
                                                     Length.LengthUnit toUnit) {
        return length.convertTo(toUnit);
    }

    public static Length demonstrateLengthAddition(Length length1, Length length2) {
        if (length1 == null || length2 == null)
            throw new IllegalArgumentException("Lengths can't be null");

        return length1.add(length2);
    }

    public static void main(String[] args) {

        // Equality Tests
        Length l1 = new Length(36.0, Length.LengthUnit.Inches);
        Length l2 = new Length(1.0, Length.LengthUnit.Yards);
        System.out.println(l1.equals(l2));

        Length l3 = new Length(100.0, Length.LengthUnit.Centimeters);
        Length l4 = new Length(39.3701, Length.LengthUnit.Inches);
        System.out.println(l3.equals(l4));
        System.out.println(demonstrateLengthComparison(1.0,Length.LengthUnit.Yards,36.0,Length.LengthUnit.Inches));
        System.out.println(demonstrateLengthConversion(1.0,Length.LengthUnit.Feet,Length.LengthUnit.Inches));

        Length a = new Length(1.0, Length.LengthUnit.Feet);
        Length b = new Length(12.0, Length.LengthUnit.Inches);
        System.out.println(a.add(b));

        Length c = new Length(1.0, Length.LengthUnit.Yards);
        Length d = new Length(12.0, Length.LengthUnit.Inches);
        System.out.println(c.add(d));

        Length e = new Length(100.0, Length.LengthUnit.Centimeters);
        Length f = new Length(1.0, Length.LengthUnit.Feet);
        System.out.println( e.add(f));
    }
}