
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

    public void encadenamientoAdelante() {
        System.out.println("\n--- Iniciando Encadenamiento hacia Adelante ---");
        boolean nuevosHechos = true;

        while (nuevosHechos) {
            nuevosHechos = false;
            for (Regla regla : reglas) {
                // Si todos los antecedentes están en mis hechos y el consecuente aún no
                if (hechos.containsAll(regla.getAntecedentes()) && !hechos.contains(regla.getConsecuente())) {
                    hechos.add(regla.getConsecuente());
                    System.out.println("Se dispara la regla: " + regla);
                    System.out.println("Nuevo hecho deducido: " + regla.getConsecuente());
                    nuevosHechos = true;
                }
            }
        }
        System.out.println("\nHechos finales: " + hechos);
    }

    public boolean encadenamientoAtras(String meta) {
        // Si ya conocemos el hecho, es verdad
        if (hechos.contains(meta)) {
            return true;
        }

        // Buscamos reglas que tengan como consecuencia la meta
        for (Regla regla : reglas) {
            if (regla.getConsecuente().equals(meta)) {
                boolean todasLasPremisasSonVerdad = true;

                // Intentamos demostrar cada antecedente de esa regla
                for (String antecedente : regla.getAntecedentes()) {
                    if (!encadenamientoAtras(antecedente)) {
                        todasLasPremisasSonVerdad = false;
                        break;
                    }
                }

                if (todasLasPremisasSonVerdad) {
                    System.out.println("Meta '" + meta + "' demostrada usando: " + regla);
                    if (!hechos.contains(meta)) {
                        hechos.add(meta);

                    }
                    return true;
                }
            }
        }
        return false;
    }
}
