
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    final static String ARCHIVO_REGLAS_C1 = "Motor-inferencia-IA/caso1/reglas_ciber.txt";
    final static String ARCHIVO_HECHOS_C1 = "Motor-inferencia-IA/caso1/hechos_ciber.txt";

    final static String ARCHIVO_REGLAS_C2 = "Motor-inferencia-IA/caso2/reglas_taller.txt";
    final static String ARCHIVO_HECHOS_C2 = "Motor-inferencia-IA/caso2/hechos_taller.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String archivoReglas = "";
        String archivoHechos = "";

        System.out.println("\n--- SISTEMA EXPERTO: MOTOR DE INFERENCIA ---");
        System.out.println("Seleccione el caso de estudio:");
        System.out.println("1. Analista de ciberseguridad");
        System.out.println("2. Taller Mecánico");
        System.out.print("Opción: ");
        String opcion1 = scanner.nextLine();

        if (opcion1.equals("1")) {
            archivoReglas = ARCHIVO_REGLAS_C1;
            archivoHechos = ARCHIVO_HECHOS_C1;
        } else if (opcion1.equals("2")) {
            archivoReglas = ARCHIVO_REGLAS_C2;
            archivoHechos = ARCHIVO_HECHOS_C2;
        } else {
            System.out.println("Opción inválida. Saliendo...");
            return;
        }

        // Carga de datos
        List<Regla> reglas = cargarReglas(archivoReglas);
        List<String> hechos = cargarHechos(archivoHechos);

        if (reglas.isEmpty()) {
            System.out.println("No se pudieron cargar las reglas. Verifique el archivo.");
            return;
        }

        MotorInferencia motor = new MotorInferencia(reglas, hechos);

        System.out.println("\nHechos iniciales cargados: " + hechos);
        System.out.println("Seleccione el método de inferencia:");
        System.out.println("1. Encadenamiento hacia Adelante");
        System.out.println("2. Encadenamiento hacia Atrás");
        System.out.print("Opción: ");
        String opcion2 = scanner.nextLine();

        if (opcion2.equals("1")) {
            motor.encadenamientoAdelante();
        } else if (opcion2.equals("2")) {
            System.out.print("\nIngrese la meta (objetivo) a demostrar: ");
            String meta = scanner.nextLine();
            if (motor.encadenamientoAtras(meta)) {
                System.out.println("\n>>> ÉXITO: La meta '" + meta + "' ha sido demostrada.");
            } else {
                System.out.println("\n>>> FALLO: No se pudo demostrar la meta '" + meta + "'.");
            }
        } else {
            System.out.println("Opción de inferencia no válida.");
        }

        System.out.print("\n¿Desea guardar la nueva lista de hechos finales? (si/no): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();

        if (respuesta.equals("si")) {
            // Definimos la ruta de salida 
            String carpetaCaso = (opcion1.equals("1")) ? "Motor-inferencia-IA/caso1/" : "Motor-inferencia-IA/caso2/";
            String archivoSalida = carpetaCaso + "hechos_finales_resultados.txt";

            guardarHechosFinales(motor.getHechos(), archivoSalida);
        } else {
            System.out.println("Programa finalizado sin guardar cambios.");
        }

        scanner.close();
    }

    private static void guardarHechosFinales(List<String> hechos, String nombreArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (String hecho : hechos) {
                writer.println(hecho);
            }
            System.out.println(">>> Información guardada exitosamente en: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    private static List<Regla> cargarReglas(String archivo) {
        List<Regla> reglas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("[")) {
                    reglas.add(new Regla(linea));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar las reglas: " + e.getMessage());
        }
        return reglas;
    }

    private static List<String> cargarHechos(String archivo) {
        List<String> hechos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("[")) {
                    hechos.add(linea);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los hechos: " + e.getMessage());
        }
        return hechos;
    }
}
