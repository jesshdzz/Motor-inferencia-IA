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

    // Encadenamiento hacia adelante
    public void encadenamientoAdelante() {
        boolean nuevosHechosAgregados = true;
        System.out.println("\nEncadenamiento hacia adelante:");

        while (nuevosHechosAgregados) {
            nuevosHechosAgregados = false;

            for (Regla regla : reglas) {
                if (contieneHechos(hechos, regla.getConsecuente())) {
                    continue;
                }
                if (evaluarAntecedentes(regla.getAntecedentes())) {
                    System.out.println("\nDisparando regla: " + regla.toString());
                    hechos.add(regla.getConsecuente());
                    System.out.println("Nuevo hecho inferido: " + regla.getConsecuente());
                    nuevosHechosAgregados = true;
                }
            }
        }
        System.out.println("\nNo hay mas reglas que disparar.");
    }

    private boolean evaluarAntecedentes(List<String> antecedentes) {
        for (String antecedente : antecedentes) {
            boolean estaNegado = antecedente.startsWith("NO ");
            String hechoBase = estaNegado ? antecedente.substring(3) : antecedente;

            boolean existeHecho = contieneHechos(hechos, hechoBase);
            if (estaNegado && existeHecho) {
                return false;
            }
            if (!estaNegado && !existeHecho) {
                return false;
            }
        }
        return true;
    }

    private boolean contieneHechos(List<String> lista, String hecho) {
        for (String item : lista) {
            if (item.equalsIgnoreCase(hecho)) {
                return true;
            }
        }
        return false;
    }
}
