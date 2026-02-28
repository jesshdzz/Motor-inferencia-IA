import java.util.List;
import java.util.Scanner;

public class Main implements InteraccionManual {

    private Scanner scanner;

    public Main() {
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.iniciar();
    }

    public void iniciar() {
        boolean continuar = true;

        System.out.println("=========================================");
        System.out.println("  Motor de Inferencia - Versión Unificada ");
        System.out.println("=========================================");
        System.out.println(" Equipo 3: Jesús, Yoosavi, Sonia, Belén\n");

        while (continuar) {
            System.out.println("\n--- Carga de Base de Conocimientos ---");
            System.out.println("1. Cargar desde archivos visualmente (Ventanas)");
            System.out.println("2. Cargar escribiendo rutas (Consola)");
            System.out.println("3. Cargar usando rutas por defecto del Caso 1 (Ciberseguridad)");
            System.out.println("4. Cargar usando rutas por defecto del Caso 2 (Taller)");
            System.out.print("Opción: ");

            String opcionCarga = scanner.nextLine().trim();
            List<Regla> reglas = null;
            List<String> hechos = null;
            String archivoHechosNombre = "hechos.txt";

            if (opcionCarga.equals("1")) {
                String rutareglas = GestorArchivos.seleccionarArchivoConDialogo("reglas");
                if (rutareglas == null)
                    continue;
                reglas = GestorArchivos.cargarReglas(rutareglas);

                System.out.print("¿Deseas cargar hechos iniciales desde archivo? (s/n): ");
                if (leerSN().equals("s")) {
                    String rutaHechos = GestorArchivos.seleccionarArchivoConDialogo("hechos");
                    if (rutaHechos != null) {
                        hechos = GestorArchivos.cargarHechos(rutaHechos);
                        archivoHechosNombre = rutaHechos;
                    } else {
                        hechos = new java.util.ArrayList<>();
                    }
                } else {
                    hechos = capturarHechosManual();
                }

            } else if (opcionCarga.equals("2")) {
                System.out.print("Ruta del archivo de Reglas (.txt): ");
                String rutaReglas = scanner.nextLine().trim();
                reglas = GestorArchivos.cargarReglas(rutaReglas);

                System.out.print("Ruta del archivo de Hechos (.txt o en blanco para manual): ");
                String rutaHechos = scanner.nextLine().trim();
                if (rutaHechos.isEmpty()) {
                    hechos = capturarHechosManual();
                } else {
                    hechos = GestorArchivos.cargarHechos(rutaHechos);
                    archivoHechosNombre = rutaHechos;
                }
            } else if (opcionCarga.equals("3")) {
                reglas = GestorArchivos.cargarReglas("caso1/reglas_ciber.txt");
                hechos = GestorArchivos.cargarHechos("caso1/hechos_ciber.txt");
                archivoHechosNombre = "caso1/hechos_ciber.txt";
            } else if (opcionCarga.equals("4")) {
                reglas = GestorArchivos.cargarReglas("caso2/reglas_taller.txt");
                hechos = GestorArchivos.cargarHechos("caso2/hechos_taller.txt");
                archivoHechosNombre = "caso2/hechos_taller.txt";
            } else {
                System.out.println("Opción no válida.");
                continue;
            }

            if (reglas == null || reglas.isEmpty()) {
                System.out.println("Base de conocimientos sin reglas válidas. Abortando caso actual.");
                continue;
            }

            MotorInferencia motor = new MotorInferencia(reglas, hechos, this);

            System.out.println("\nSeleccione el método de inferencia:");
            System.out.println("1. Encadenamiento hacia Adelante (Sacar todas las conclusiones)");
            System.out.println("2. Encadenamiento hacia Atrás (Probar un objetivo)");
            System.out.print("Opción: ");

            String opcionInferencia = scanner.nextLine().trim();

            if (opcionInferencia.equals("1")) {
                List<String> nuevos = motor.encadenamientoHaciaAdelante();
                System.out.println("\n[Hechos Iniciales y Finales]: " + motor.getHechos());
                System.out.println("[Hechos puramente infereidos]: " + nuevos);
            } else if (opcionInferencia.equals("2")) {
                System.out.print("Ingrese el objetivo a demostrar: ");
                String objetivo = scanner.nextLine().trim();
                motor.encadenamientoHaciaAtras(objetivo);
                ArbolInferenciaGraphviz.mostrar(motor.getArbolInferencia());
                System.out.println("\nHechos finales tras el encadenamiento: " + motor.getHechos());
            } else {
                System.out.println("Opción de inferencia inválida.");
            }

            System.out.print("\n¿Desea guardar la base de hechos actualizada? (s/n): ");
            if (leerSN().equals("s")) {
                GestorArchivos.guardarHechos(motor.getHechos(), archivoHechosNombre);
            }

            System.out.print("\n¿Desea continuar con otro caso? (s/n): ");
            if (leerSN().equals("n")) {
                continuar = false;
            }
        }

        scanner.close();
        System.out.println("\nGracias por usar el motor de inferencia.");
    }

    // --- Implementación de la Interfaz para Desacoplar Interfaz Gráfica/Consola de
    // Lógica ---
    @Override
    public boolean preguntarHechoAlUsuario(String hecho) {
        System.out.print(">>> PREGUNTA: ¿El hecho '" + hecho + "' es verdadero en el problema real? (s/n): ");
        return leerSN().equals("s");
    }

    // Utilidades Console
    private String leerSN() {
        while (true) {
            String entrada = scanner.nextLine().trim().toLowerCase();
            if (entrada.equals("s") || entrada.equals("si"))
                return "s";
            if (entrada.equals("n") || entrada.equals("no"))
                return "n";
            System.out.print("Invalido. Ingrese 's' (Sí) o 'n' (No): ");
        }
    }

    private List<String> capturarHechosManual() {
        List<String> hechos = new java.util.ArrayList<>();
        System.out.println("Ingrese hechos (uno por línea). Escriba FIN para terminar:");
        while (true) {
            String hecho = scanner.nextLine().trim();
            if (hecho.equalsIgnoreCase("FIN"))
                break;
            if (!hecho.isEmpty() && !hechos.contains(hecho)) {
                hechos.add(hecho);
            }
        }
        return hechos;
    }
}