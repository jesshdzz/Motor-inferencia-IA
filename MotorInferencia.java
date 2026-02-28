import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MotorInferencia {
    private List<Regla> reglas;
    private List<String> hechos;
    private NodoArbol raizArbol; // para almacenar el árbol de la última inferencia
    private Scanner scanner;

    public MotorInferencia(List<Regla> reglas, List<String> hechos) {
        this.reglas = reglas;
        this.hechos = hechos;
        this.scanner = new Scanner(System.in);
    }

    public NodoArbol getArbolInferencia() {
        return raizArbol;
    }

    public List<String> getHechos() {
        return hechos;
    }

    // Encadenamiento hacia adelante
    public void encadenamientoAdelante() {
        System.out.println("\n--- Encadenamiento hacia Adelante ---");
        System.out.println("Hechos iniciales: " + hechos);
        boolean cambio;
        int paso = 1;
        do {
            cambio = false;
            for (Regla r : reglas) {
                if (hechos.contains(r.getConsecuente())) {
                    continue;
                }
                boolean todosCumplidos = true;
                for (String ant : r.getAntecedentes()) {
                    if (!evaluarAntecedente(ant)) {
                        todosCumplidos = false;
                        break;
                    }
                }
                if (todosCumplidos) {
                    System.out.println("Paso " + paso + ": Disparando regla " + r);
                    hechos.add(r.getConsecuente());
                    System.out.println("   Nuevo hecho: " + r.getConsecuente());
                    paso++;
                    cambio = true;
                }
            }
        } while (cambio);
        System.out.println("Hechos finales: " + hechos);
        if (!cambio && paso == 1) {
            System.out.println("No se pudo disparar ninguna regla. Punto muerto alcanzado.");
        }
    }

    private boolean evaluarAntecedente(String antecedente) {
        boolean esNegado = antecedente.startsWith("NO ");
        String hechoBase = esNegado ? antecedente.substring(3) : antecedente;
        boolean existe = hechos.contains(hechoBase);
        return esNegado ? !existe : existe;
    }

    // Encadenamiento hacia atrás con árbol

    public boolean encadenamientoAtras(String objetivo) {
        System.out.println("\n--- Encadenamiento hacia Atrás ---");

        raizArbol = new NodoArbol(objetivo, false); // raíz es el objetivo
        List<String> pila = new ArrayList<>();

        boolean resultado = probarObjetivo(objetivo, pila, raizArbol);

        System.out.println("\nHechos actuales: " + hechos);
        return resultado;
    }


    private boolean probarObjetivo(String objetivo,
                               List<String> pila,
                               NodoArbol nodoPadre) {

        if (pila.contains(objetivo)) {
            System.out.println("Ciclo detectado al intentar probar '" + objetivo + "'.");
            nodoPadre.agregarHijo(new NodoArbol("Ciclo detectado", false));
            return false;
        }

        // Crear nodo para este objetivo
        NodoArbol nodoActual = new NodoArbol(objetivo, false);
        nodoPadre.agregarHijo(nodoActual);

        // Manejo de negación
        if (objetivo.startsWith("NO ")) {
            String hechoPositivo = objetivo.substring(3);

            if (hechos.contains(hechoPositivo)) {
                return false;
            }

            boolean positivoEsVerdadero =
                    probarObjetivo(hechoPositivo,
                                new ArrayList<>(pila),
                                nodoActual);

            if (positivoEsVerdadero) {
                return false;
            } else {
                System.out.print("No se puede deducir '" + objetivo + "'. ¿Es un hecho verdadero? (s/n): ");
                String respuesta = scanner.nextLine().trim();

                if (respuesta.equalsIgnoreCase("s")) {
                    return true;
                }

                // PUNTO MUERTO 
                nodoActual.agregarHijo(
                    new NodoArbol("Punto muerto: " + objetivo, false)
                );

                return false;
            }
        }

        // Si ya es hecho
        if (hechos.contains(objetivo)) {
            return true;
        }

        // Buscar reglas que concluyan el objetivo
        List<Regla> reglasConGoal = new ArrayList<>();
        for (Regla r : reglas) {
            if (r.getConsecuente().equals(objetivo)) {
                reglasConGoal.add(r);
            }
        }

        // si NO HAY REGLAS = PUNTO MUERTO
        if (reglasConGoal.isEmpty()) {
            System.out.print("No hay reglas para deducir '" + objetivo + "'. ¿Es un hecho verdadero? (s/n): ");
            String respuesta = scanner.nextLine().trim();

            if (respuesta.equalsIgnoreCase("s")) {
                hechos.add(objetivo);
                return true;
            }

            nodoActual.agregarHijo(
                new NodoArbol("Punto muerto: " + objetivo, false)
            );

            return false;
        }

        pila.add(objetivo);

        // Probar cada regla
        for (Regla r : reglasConGoal) {

            NodoArbol nodoRegla = new NodoArbol(r.toString(), true);
            nodoActual.agregarHijo(nodoRegla);

            boolean todosVerdaderos = true;

            for (String ant : r.getAntecedentes()) {
                if (!probarObjetivo(ant,
                                    new ArrayList<>(pila),
                                    nodoRegla)) {
                    todosVerdaderos = false;
                    break;
                }
            }

            if (todosVerdaderos) {
                hechos.add(objetivo);
                pila.remove(pila.size() - 1);
                return true;
            }
        }

        pila.remove(pila.size() - 1);

        // SI NINGUNA REGLA FUNCIONÓ = PUNTO MUERTO
        System.out.print("No se pudo probar con reglas. ¿El hecho '" + objetivo + "' es verdadero? (s/n): ");
        String respuesta = scanner.nextLine().trim();

        if (respuesta.equalsIgnoreCase("s")) {
            hechos.add(objetivo);
            return true;
        }

        nodoActual.agregarHijo(
            new NodoArbol("Punto muerto: " + objetivo, false)
        );

        return false;
    }
}