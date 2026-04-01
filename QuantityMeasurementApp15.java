import java.io.Serializable;
import java.util.*;

interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();

    default void validateOperationSupport(String operation) {}

    default boolean supportsArithmetic() {
        return true;
    }
}
enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCHES(1.0 / 12),
    YARDS(3.0),
    CENTIMETERS(0.0328084);

    private final double factor;

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
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

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
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

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}
enum TemperatureUnit implements IMeasurable {
    CELSIUS,
    FAHRENHEIT;

    public double convertToBaseUnit(double value) {
        if (this == CELSIUS) return value;
        return (value - 32) * 5 / 9;
    }

    public double convertFromBaseUnit(double baseValue) {
        if (this == CELSIUS) return baseValue;
        return (baseValue * 9 / 5) + 32;
    }

    public String getUnitName() {
        return name();
    }

    @Override
    public boolean supportsArithmetic() {
        return false;
    }

    @Override
    public void validateOperationSupport(String op) {
        throw new UnsupportedOperationException("Temperature does not support " + op);
    }
}
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null || !Double.isFinite(value))
            throw new IllegalArgumentException("Invalid input");
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Quantity<?> other)) return false;
        if (!unit.getClass().equals(other.unit.getClass())) return false;
        return Math.abs(this.toBase() - other.toBase()) < 1e-6;
    }

    public Quantity<U> convertTo(U target) {
        double base = toBase();
        double result = target.convertFromBaseUnit(base);
        return new Quantity<>(round(result), target);
    }

    public Quantity<U> add(Quantity<U> other) {
        unit.validateOperationSupport("ADD");
        double res = this.toBase() + other.toBase();
        return new Quantity<>(round(unit.convertFromBaseUnit(res)), unit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        unit.validateOperationSupport("SUBTRACT");
        double res = this.toBase() - other.toBase();
        return new Quantity<>(round(unit.convertFromBaseUnit(res)), unit);
    }

    public double divide(Quantity<U> other) {
        unit.validateOperationSupport("DIVIDE");
        return this.toBase() / other.toBase();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    public String toString() {
        return value + " " + unit.getUnitName();
    }
}
class QuantityDTO {
    public double value;
    public String unit;

    public QuantityDTO(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }
}

class QuantityModel<U extends IMeasurable> {
    public Quantity<U> quantity;

    public QuantityModel(Quantity<U> quantity) {
        this.quantity = quantity;
    }
}


class QuantityMeasurementEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    String operation;
    String result;

    public QuantityMeasurementEntity(String operation, String result) {
        this.operation = operation;
        this.result = result;
    }

    public String toString() {
        return operation +"=>"+ result;
    }
}
interface IQuantityMeasurementRepository {
    void save(QuantityMeasurementEntity entity);
    List<QuantityMeasurementEntity> findAll();
}

class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {
    private final List<QuantityMeasurementEntity> db = new ArrayList<>();

    public void save(QuantityMeasurementEntity e) {
        db.add(e);
    }

    public List<QuantityMeasurementEntity> findAll() {
        return db;
    }
}

interface IQuantityMeasurementService {
    <U extends IMeasurable> boolean compare(Quantity<U> q1, Quantity<U> q2);
    <U extends IMeasurable> Quantity<U> convert(Quantity<U> q, U target);
    <U extends IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2);
}

class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private final IQuantityMeasurementRepository repo;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repo) {
        this.repo = repo;
    }

    public <U extends IMeasurable> boolean compare(Quantity<U> q1, Quantity<U> q2) {
        boolean res = q1.equals(q2);
        repo.save(new QuantityMeasurementEntity("COMPARE", String.valueOf(res)));
        return res;
    }

    public <U extends IMeasurable> Quantity<U> convert(Quantity<U> q, U target) {
        Quantity<U> res = q.convertTo(target);
        repo.save(new QuantityMeasurementEntity("CONVERT", res.toString()));
        return res;
    }

    public <U extends IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2) {
        Quantity<U> res = q1.add(q2);
        repo.save(new QuantityMeasurementEntity("ADD", res.toString()));
        return res;
    }
}

class QuantityMeasurementController {
    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
    }

    public <U extends IMeasurable> void compare(Quantity<U> q1, Quantity<U> q2) {
        System.out.println(service.compare(q1, q2));
    }

    public <U extends IMeasurable> void convert(Quantity<U> q, U target) {
        System.out.println(service.convert(q, target));
    }

    public <U extends IMeasurable> void add(Quantity<U> q1, Quantity<U> q2) {
        System.out.println(service.add(q1, q2));
    }
}

public class QuantityMeasurementApp15 {
    public static void main(String[] args) {

        IQuantityMeasurementRepository repo = new QuantityMeasurementCacheRepository();
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repo);
        QuantityMeasurementController controller = new QuantityMeasurementController(service);
        Quantity<LengthUnit> l1 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12, LengthUnit.INCHES);

        controller.compare(l1, l2);
        controller.add(l1, l2);
        Quantity<WeightUnit> w1 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000, WeightUnit.GRAM);
        controller.compare(w1, w2);
        controller.add(w1, w2);
        Quantity<VolumeUnit> v1 = new Quantity<>(1, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000, VolumeUnit.MILLILITRE);
        controller.compare(v1, v2);
        Quantity<TemperatureUnit> t1 = new Quantity<>(0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(32, TemperatureUnit.FAHRENHEIT);

        controller.compare(t1, t2);
        repo.findAll().forEach(System.out::println);
    }
}