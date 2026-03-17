import java.util.Scanner;
public class QuantityMeasurementApp3{
    enum LengthUnit{
        FEET(1.0),
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
        public boolean equals(QuantityLength obj){
            if(this==obj) return true;
            if(obj==null || getClass() != obj.getClass()) return false;
            QuantityLength otherLength=(QuantityLength) obj;
            return Double.compare(this.toBaseUnit(), otherLength.toBaseUnit()) == 0;

        }
    }

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        double value1,value2;
        value1=sc.nextDouble();
        value2=sc.nextDouble();
        QuantityLength len1=new QuantityLength(value1,LengthUnit.FEET );
        QuantityLength len2=new QuantityLength(value2,LengthUnit.INCH );
        System.out.println(len1.equals(len2));
    }
}