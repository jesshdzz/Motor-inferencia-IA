import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorInferencia {
    private List<Regla> reglas;
    private List<String> hechos;
    private Scanner scanner;

    public MotorInferencia(List<Regla> reglas, List<String> hechos) {
        this.reglas = reglas;
        this.hechos = hechos;
        this.scanner = new Scanner(System.in);
    }

    public List<String> getHechos() {
        return this.hechos;
    }
}
