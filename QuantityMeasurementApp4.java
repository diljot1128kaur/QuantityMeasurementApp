import java.util.Scanner;
public class QuantityMeasurementApp4{
    enum LengthUnit{
        FEET(1.0),
        YARDS(3.0),
        CENTIMETER(0.0328084),
        INCH(1.0/12);
        private final double conversionFactor;
        LengthUnit(double conversionFactor){
            this.conversionFactor = conversionFactor;
        }
        public double toBaseUnit(double value){
            return value * conversionFactor;
        }

    }
    static class QuantityLength{
        private final double value;
        private final LengthUnit unit;
        public QuantityLength(double value, LengthUnit unit){
            this.value = value;
            this.unit = unit;
        }
        public double toBaseUnit(){
            return unit.toBaseUnit(value);
        }
        @Override
        public boolean equals(Object obj){
            if(this == obj) return true;
            if(obj == null || getClass() != obj.getClass()) return false;
            QuantityLength other = (QuantityLength) obj;
            return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;

        }
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        double value1,value2,value3,value4;
        value1=sc.nextDouble();
        value2=sc.nextDouble();
        value3=sc.nextDouble();
        value4=sc.nextDouble();
        QuantityLength len1=new QuantityLength(value1,LengthUnit.FEET );
        QuantityLength len2=new QuantityLength(value2,LengthUnit.INCH );
        QuantityLength len3=new QuantityLength(value3,LengthUnit.YARDS);
        QuantityLength len4=new QuantityLength(value4,LengthUnit.CENTIMETER);
        System.out.println(len1.equals(len2));
        System.out.println(len1.equals(len3));
        System.out.println(len1.equals(len4));
        System.out.println(len2.equals(len3));
    }
}