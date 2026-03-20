enum LengthUnit{
    FEET(12.0),
    INCHES(1.0),
    CENTIMETERS(0.393701),
    YARDS(36.0);

    private final double conversionFactor;

    LengthUnit(double conversionFactor){
        this.conversionFactor=conversionFactor;
    }

    public double convertToBaseUnit(double value){
        return value*conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue){
        return baseValue/conversionFactor;
    }
}

class Length{
    private double value;
    private LengthUnit unit;

    public Length(double value,LengthUnit unit){
        if(unit==null) throw new IllegalArgumentException("Unit cannot be null");
        this.value=value;
        this.unit=unit;
    }

    private double convertToBaseUnit(){
        return unit.convertToBaseUnit(value);
    }

    private double round(double value){
        return Math.round(value*100.0)/100.0;
    }

    public boolean compare(Length thatLength){
        return this.equals(thatLength);
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Length)) return false;
        Length i=(Length)o;
        return Double.compare(this.convertToBaseUnit(),i.convertToBaseUnit())==0;
    }

    @Override
    public int hashCode(){
        return Double.hashCode(convertToBaseUnit());
    }

    public Length convertTo(LengthUnit targetUnit){
        if(targetUnit==null) throw new IllegalArgumentException("Target can't be null");
        double baseValue=this.convertToBaseUnit();
        double val=targetUnit.convertFromBaseUnit(baseValue);
        return new Length(round(val),targetUnit);
    }

    public Length add(Length thatLength){
        if(thatLength==null) throw new IllegalArgumentException("Length cannot be null");
        double sum=this.convertToBaseUnit()+thatLength.convertToBaseUnit();
        return addAndConvert(sum,unit);
    }

    public Length add(Length thatLength,LengthUnit targetUnit){
        if(thatLength==null||targetUnit==null) throw new IllegalArgumentException("Length cannot be null");
        double sum=this.convertToBaseUnit()+thatLength.convertToBaseUnit();
        return addAndConvert(sum,targetUnit);
    }

    private Length addAndConvert(double sum,LengthUnit targetUnit){
        double res=convertFromBaseToTargetUnit(sum,targetUnit);
        return new Length(res,targetUnit);
    }

    private double convertFromBaseToTargetUnit(double baseValue,LengthUnit targetUnit){
        double val=targetUnit.convertFromBaseUnit(baseValue);
        return round(val);
    }

    @Override
    public String toString(){
        return String.format("%.2f %s",value,unit);
    }
}

public class QuantityMeasurementApp8{
    public static boolean demonstrateLengthEquality(Length l1,Length l2){
        return l1.equals(l2);
    }

    public static Length demonstrateLengthAddition(Length l1,Length l2){
        if(l1==null||l2==null) throw new IllegalArgumentException("Length cannot be null");
        return l1.add(l2);
    }

    public static Length demonstrateLengthAddition(Length l1,Length l2,LengthUnit target){
        if(l1==null||l2==null||target==null) throw new IllegalArgumentException("invalid");
        return l1.add(l2,target);
    }

    public static boolean demonstrateLengthComparison(double val1,LengthUnit unit1,double val2,LengthUnit unit2){
        Length l1=new Length(val1,unit1);
        Length l2=new Length(val2,unit2);
        return l1.equals(l2);
    }

    public static Length demonstrateLengthConversion(double value,LengthUnit fromUnit,LengthUnit toUnit){
        if(fromUnit==null||toUnit==null) throw new IllegalArgumentException("Units can't be null");
        Length obj=new Length(value,fromUnit);
        return obj.convertTo(toUnit);
    }

    public static Length demonstrateLengthConversion(Length length,LengthUnit toUnit){
        return length.convertTo(toUnit);
    }

    public static void main(String[] args){
        Length l1=new Length(1.0,LengthUnit.FEET);
        Length l2=new Length(2.0,LengthUnit.INCHES);
        System.out.println(demonstrateLengthAddition(l1,l2,LengthUnit.FEET));
        System.out.println(demonstrateLengthAddition(l1,l2,LengthUnit.INCHES));
        System.out.println(demonstrateLengthEquality(l1,new Length(12.0,LengthUnit.INCHES)));
        System.out.println(demonstrateLengthConversion(1.0,LengthUnit.FEET,LengthUnit.CENTIMETERS));
    }
}