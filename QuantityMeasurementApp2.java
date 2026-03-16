public class QuantityMeasurementApp2 {
    public static class Feet{
        private final double value;
        Feet(double value){
            this.value=value;
        }
        public boolean equals(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            Feet feet=(Feet) obj;
            return Double.compare(this.value,feet.value)==0;
        }


    }
    public static class Inch{
        private final double value;
        Inch(double value){
            this.value=value;
        }
        public boolean equals(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            Inch inch=(Inch) obj;
            return Double.compare(this.value,inch.value)==0;
        }



    }
    public static void demostrateFeetEquality(){
        Feet f1=new Feet(5);
        Feet f2=new Feet(5);
        Feet f3=new Feet(7);
        System.out.println(f1.equals(f2));
        System.out.println(f2.equals(f3));

    }
    public static void demostrateInchesEquality(){
        Inch i1=new Inch(2);
        Inch i2=new Inch(2);
        Inch i3=new Inch(7);
        System.out.println(i1.equals(i2));
        System.out.println(i2.equals(i3));

    }
    public static void main(String[] args){
        demostrateFeetEquality();
        demostrateInchesEquality();
    }
}
