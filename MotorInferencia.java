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

    // Encadenamiento hacia atrás
    public boolean encadenamientoAtras(String objetivo) {
        System.out.println("\nEncadenamiento hacia atrás:");
        return probarObjetivo(objetivo, new ArrayList<>());
    }

    private boolean probarObjetivo(String objetivo, List<String> PilaDeLlamadas) {
        if (contieneHechos(PilaDeLlamadas, objetivo)) {
            return false;
        }

        boolean estaNegado = objetivo.startsWith("NO ");
        String objetivoBase = estaNegado ? objetivo.substring(3) : objetivo;

        if (contieneHechos(hechos, objetivoBase)) {
            return !estaNegado;
        }

        PilaDeLlamadas.add(objetivo);
        boolean reglaEncontrada = false;

        for (Regla regla : reglas) {
            if (regla.getConsecuente().equals(objetivo)) {
                reglaEncontrada = true;
                System.out.println("\nDisparando regla para el objetivo: " + objetivo + " : " + regla.toString());

                boolean antecedentesVerificados = true;
                for (String antecedente : regla.getAntecedentes()) {
                    if (!probarObjetivo(antecedente, new ArrayList<>(PilaDeLlamadas))) {
                        antecedentesVerificados = false;
                        break;
                    }
                }

                if (antecedentesVerificados) {
                    System.out.println("Objetivo verificado mediante reglas: " + objetivoBase);
                    hechos.add(objetivoBase);
                    return !estaNegado;
                }
            }
        }

        if (!reglaEncontrada) {
            System.out.print("No se puede deducir '" + objetivoBase + "'. ¿Es un hecho verdadero? (S/N): ");
            Boolean respuesta = scanner.nextLine().trim().equalsIgnoreCase("s");
            if (respuesta) {
                hechos.add(objetivoBase);
                return !estaNegado;
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
