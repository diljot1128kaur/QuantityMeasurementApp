import java.util.Scanner;
public class QuantityMeasurementApp {
     static  class Feet{
        private final double value;
        Feet(double value){
            this.value=value;
        }
        public boolean eqauls(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            Feet feet=(Feet) obj;
            return Double.compare(feet.value,value)==0;
        }

     }
     public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        double val1=sc.nextDouble();
        double val2=sc.nextDouble();
        Feet feet1=new Feet(val1);
        Feet feet2=new Feet(val2);
        System.out.println(feet1.eqauls(feet2));
        sc.close();
     }
}
