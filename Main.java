import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    final static String ARCHIVO_REGLAS_C1 = "caso1/reglas_ciber.txt";
    final static String ARCHIVO_HECHOS_C1 = "caso1/hechos_ciber.txt";

    final static String ARCHIVO_REGLAS_C2 = "caso2/reglas_taller.txt";
    final static String ARCHIVO_HECHOS_C2 = "caso2/hechos_taller.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.println("=========================================");
        System.out.println("   Bienvenido al Motor de Inferencia   ");
        System.out.println("=========================================");
        System.out.println(" Equipo 3: Jesús, Yoosavi, Sonia, Belén\n");

        while (continuar) {
            String archivoReglas = "";
            String archivoHechos = "";

            System.out.println("\nSeleccione el caso:");
            System.out.println("1. Analista de ciberseguridad");
            System.out.println("2. Taller Mecánico");
            System.out.println("3. Salir");
            System.out.print("Opción: ");

            String opcion1 = scanner.nextLine().trim();

            if (opcion1.equals("1")) {
                archivoReglas = ARCHIVO_REGLAS_C1;
                archivoHechos = ARCHIVO_HECHOS_C1;
            } else if (opcion1.equals("2")) {
                archivoReglas = ARCHIVO_REGLAS_C2;
                archivoHechos = ARCHIVO_HECHOS_C2;
            } else if (opcion1.equals("3")) {
                continuar = false;
                continue;
            } else {
                System.out.println("Opción inválida. Por favor, intente de nuevo.");
                continue;
            }

            List<Regla> reglas = cargarReglas(archivoReglas);
            List<String> hechos = cargarHechos(archivoHechos);

            if (reglas.isEmpty()) {
                System.out.println("Base de conocimientos vacía. Abortando.");
                continue;
            }

            MotorInferencia motor = new MotorInferencia(reglas, hechos);

            System.out.println("\nSeleccione el método de inferencia:");
            System.out.println("1. Encadenamiento hacia Adelante");
            System.out.println("2. Encadenamiento hacia Atrás");
            System.out.print("Opción: ");

            String opcion2 = scanner.nextLine().trim();

            if (opcion2.equals("1")) {
                motor.encadenamientoAdelante();
            } else if (opcion2.equals("2")) {
                System.out.print("Ingrese el objetivo a inferir: ");
                String objetivo = scanner.nextLine().trim();
                Boolean resultado = motor.encadenamientoAtras(objetivo);
                System.out.println("\nResultado final: El objetivo: " + objetivo + " es " +
                        (resultado != null && resultado ? "VERDADERO" : "FALSO"));

            } else {
                System.out.println("Opción inválida. Volviendo al menú principal.");
                continue;
            }

            System.out.print("\nDesea guardar los hechos inferidos? (S/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                guardarHechosInferidos(motor.getHechos(), archivoHechos);
            }

            System.out.print("\nDesea continuar con otro caso? (S/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                continuar = false;
            }
        }

        scanner.close();
        System.out.println("\nGracias por usar el motor de inferencia.");
    }

    private static List<Regla> cargarReglas(String archivo) {
        List<Regla> reglas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
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
                if (!linea.trim().isEmpty()) {
                    hechos.add(linea.trim());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los hechos: " + e.getMessage());
        }

        return hechos;
    }

    private static void guardarHechosInferidos(List<String> hechos, String archivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            for (String hecho : hechos) {
                pw.println(hecho);
            }
            System.out.println("Hechos inferidos guardados en: " + archivo);
        } catch (Exception e) {
            System.out.println("Error al guardar los hechos inferidos: " + e.getMessage());
        }
    }
}