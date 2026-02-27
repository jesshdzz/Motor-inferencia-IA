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

        System.out.println("\n==================================");
        System.out.println("   Encadenamiento hacia adelante  ");
        System.out.println("==================================");

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
        System.out.println("\nNo hay más reglas que disparar.");
        System.out.println("Hechos finales: " + hechos);
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

    // Encadenamiento hacia atrás
    public boolean encadenamientoAtras(String objetivo) {
        System.out.println("\n==================================");
        System.out.println("    Encadenamiento hacia atrás    ");
        System.out.println("==================================");
        boolean resultado = probarObjetivo(objetivo, new ArrayList<>(), 1);
        return resultado;
    }

    private boolean probarObjetivo(String objetivo, List<String> PilaDeLlamadas, int nivel) {
        System.out.println("\n" + "\t".repeat(nivel-1) + "Objetivo actual: '" + objetivo + "'");
        if (contieneHechos(PilaDeLlamadas, objetivo)) {
            System.out.println("\t".repeat(nivel) + "Ciclo detectado. Abortando rama.");
            return false;
        }

        boolean estaNegado = objetivo.startsWith("NO ");
        String objetivoBase = estaNegado ? objetivo.substring(3) : objetivo;

        if (contieneHechos(hechos, objetivoBase)) {
            System.out.println("\t".repeat(nivel) + "El hecho '" + objetivoBase + "' ya es conocido\n");
            return !estaNegado;
        }

        PilaDeLlamadas.add(objetivo);
        boolean reglaEncontrada = false;

        for (Regla regla : reglas) {
            if (regla.getConsecuente().equals(objetivo)) {
                reglaEncontrada = true;
                System.out.println("\t".repeat(nivel) + "Disparando regla: " + regla.toString());

                boolean antecedentesVerificados = true;
                for (String antecedente : regla.getAntecedentes()) {
                    System.out.println("\t".repeat(nivel) + "Evaluando antecedente: " + antecedente);
                    if (!probarObjetivo(antecedente, new ArrayList<>(PilaDeLlamadas), nivel + 2)) {
                        antecedentesVerificados = false;
                        System.out.println("\tAntecedente '" + antecedente + "' falló.");
                        break;
                    }
                }

                if (antecedentesVerificados) {
                    System.out.println("\t".repeat(nivel) + "Objetivo '" + objetivoBase + "' verificado mediante reglas.");
                    hechos.add(objetivoBase);
                    return !estaNegado;
                }
            }
        }

        if (!reglaEncontrada) {
            System.out.print("\t".repeat(nivel) + "No se puede deducir '" + objetivoBase + "'. ¿Es un hecho verdadero? (S/N): ");
            Boolean respuesta = scanner.nextLine().trim().equalsIgnoreCase("s");
            if (respuesta) {
                hechos.add(objetivoBase);
                System.out.println("\t".repeat(nivel) + "Hecho '" + objetivoBase + "' confirmado por el usuario.");
                return !estaNegado;
            } else {
                System.out.println("\t".repeat(nivel) + "Hecho '" + objetivoBase + "' negado por el usuario.");
            }
        }

        return estaNegado;
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
