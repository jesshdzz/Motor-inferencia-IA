import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorInferencia {
    private final List<Regla> reglas;
    private final List<String> hechos;

    public MotorInferencia(List<Regla> reglas, List<String> hechos) {
        this.reglas = reglas;
        this.hechos = hechos;
    }

    // Compatibilidad con llamadas antiguas desde IDE/workspace desactualizado.
    public MotorInferencia(List<Regla> reglas, List<String> hechos, Scanner scanner) {
        this(reglas, hechos);
    }

    public List<String> getHechos() {
        return this.hechos;
    }

    public List<String> encadenamientoHaciaAdelante() {
        List<String> nuevosHechos = new ArrayList<>();
        boolean huboCambios;
        int iteracion = 1;

        System.out.println("\n=== ENCADENAMIENTO HACIA ADELANTE ===");
        // Se repite hasta que una pasada completa no agregue nuevos hechos.
        do {
            huboCambios = false;
            System.out.println("\nIteracion " + iteracion + ":");

            for (Regla regla : reglas) {
                System.out.println("Evaluando regla: " + regla);
                if (hechos.contains(regla.getConsecuente())) {
                    System.out.println("  No dispara (consecuente ya conocido).");
                    continue;
                }

                if (antecedentesCumplidos(regla)) {
                    hechos.add(regla.getConsecuente());
                    nuevosHechos.add(regla.getConsecuente());
                    huboCambios = true;
                    System.out.println("  Dispara: se agrega hecho = " + regla.getConsecuente());
                } else {
                    System.out.println("  No dispara (faltan antecedentes).");
                }
            }

            iteracion++;
        } while (huboCambios);

        return nuevosHechos;
    }

    public boolean encadenamientoHaciaAtras(String objetivo) {
        System.out.println("\n=== ENCADENAMIENTO HACIA ATRAS ===");
        List<String> pila = new ArrayList<>();
        System.out.println("Traza de demostracion:");
        boolean demostrado = demostrarObjetivo(objetivo.trim(), pila, 0);

        if (demostrado) {
            System.out.println("Objetivo demostrado: " + objetivo);
        } else {
            System.out.println("No fue posible demostrar el objetivo: " + objetivo);
        }
        return demostrado;
    }

    private boolean demostrarObjetivo(String objetivo, List<String> pila, int nivel) {
        String prefijo = "  ".repeat(nivel);
        System.out.println(prefijo + "Objetivo -> " + objetivo);

        if (objetivo.isEmpty()) {
            System.out.println(prefijo + "  Fallo: objetivo vacio.");
            return false;
        }
        if (esNegado(objetivo)) {
            String positivo = quitarNegacion(objetivo);
            System.out.println(prefijo + "  Es negado, intentar demostrar " + positivo + " y negar resultado.");
            boolean resultado = !demostrarObjetivo(positivo, new ArrayList<>(pila), nivel + 1);
            System.out.println(prefijo + "  Resultado negado de " + objetivo + ": " + resultado);
            return resultado;
        }
        if (hechos.contains(objetivo)) {
            System.out.println(prefijo + "  Hecho conocido: verdadero.");
            return true;
        }
        // Evita ciclos infinitos en objetivos recursivos.
        if (pila.contains(objetivo)) {
            System.out.println(prefijo + "  Fallo: ciclo detectado.");
            return false;
        }

        pila.add(objetivo);
        boolean existeReglaConObjetivo = false;
        for (Regla regla : reglas) {
            if (!regla.getConsecuente().equals(objetivo)) {
                continue;
            }
            existeReglaConObjetivo = true;
            System.out.println(prefijo + "  Evaluando regla: " + regla);

            boolean todosCumplidos = true;
            for (String antecedente : regla.getAntecedentes()) {
                boolean ok = demostrarAntecedente(antecedente, pila, nivel + 1);
                if (!ok) {
                    System.out.println(prefijo + "  Regla falla por antecedente: " + antecedente);
                    todosCumplidos = false;
                    break;
                }
            }
            if (todosCumplidos) {
                if (!hechos.contains(objetivo)) {
                    hechos.add(objetivo);
                }
                System.out.println(prefijo + "  Regla satisfecha, se infiere: " + objetivo);
                pila.remove(pila.size() - 1);
                return true;
            }
        }
        pila.remove(pila.size() - 1);

        if (!existeReglaConObjetivo) {
            System.out.println(prefijo + "  Punto muerto: no existe regla ni hecho para '" + objetivo + "'.");
            return false;
        }
        System.out.println(prefijo + "  Punto muerto: no se pudieron demostrar antecedentes de '" + objetivo + "'.");
        return false;
    }

    private boolean demostrarAntecedente(String antecedente, List<String> pila, int nivel) {
        String prefijo = "  ".repeat(nivel);
        System.out.println(prefijo + "Antecedente -> " + antecedente);

        if (esNegado(antecedente)) {
            String positivo = quitarNegacion(antecedente);
            boolean resultado = !demostrarObjetivo(positivo, new ArrayList<>(pila), nivel + 1);
            System.out.println(prefijo + "Resultado antecedente negado " + antecedente + ": " + resultado);
            return resultado;
        }
        boolean resultado = demostrarObjetivo(antecedente, pila, nivel + 1);
        System.out.println(prefijo + "Resultado antecedente " + antecedente + ": " + resultado);
        return resultado;
    }

    private boolean antecedentesCumplidos(Regla regla) {
        for (String antecedente : regla.getAntecedentes()) {
            if (esNegado(antecedente)) {
                String positivo = quitarNegacion(antecedente);
                if (hechos.contains(positivo)) {
                    return false;
                }
            } else if (!hechos.contains(antecedente)) {
                return false;
            }
        }
        return true;
    }

    private boolean esNegado(String termino) {
        return termino.startsWith("!");
    }

    private String quitarNegacion(String termino) {
        return termino.substring(1).trim();
    }

}
