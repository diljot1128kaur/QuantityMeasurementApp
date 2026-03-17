import java.util.Scanner;
public class QuantityMeasurementApp3{
    static class QuantityLength{
        private final double value;
        private final String unit;
        QuantityLength(double value,String unit){
            this.value=value;
            this.unit=unit.toLowerCase();
        }
        private static final double INCH_FEET=1.0/12;
        private double ConvertFeet(){
            if(unit.equals("feet")) return value;
            else if(unit.equals("inch")) return value*INCH_FEET;
            else throw new IllegalArgumentException("Invalid unit");

        }
        public boolean equals(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            QuantityLength quantityLength=(QuantityLength) obj;
            return Double.compare(this.ConvertFeet(),quantityLength.ConvertFeet())==0;
        }
    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        double val1=sc.nextDouble();
        String unit1=sc.next();
        double val2=sc.nextDouble();
        String unit2=sc.next();


        QuantityLength q1=new QuantityLength(val1, unit1);
        QuantityLength q2=new QuantityLength(val2, unit2);
        System.out.println(q1.equals(q2));
        sc.close();
    }
}