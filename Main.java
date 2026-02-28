import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        System.out.println("=========================================");
        System.out.println("   Bienvenido al Motor de Inferencia   ");
        System.out.println("=========================================");
        System.out.println(" Equipo 3: Jesús, Yoosavi, Sonia, Belén\n");

        while (continuar) {
            System.out.println("\n--- Carga de Base de Conocimientos ---");
            System.out.print("¿Desea cargar desde archivos? (s/n): ");
            String respuesta = leerSN(scanner);

            List<Regla> reglas = new ArrayList<>();
            List<String> hechos = new ArrayList<>();

            if (respuesta.equalsIgnoreCase("s")) {
                // Cargar archivo de reglas
                String archivoReglas = seleccionarArchivo(scanner, "reglas", true);
                if (archivoReglas == null) continue; // Si canceló, volver al inicio
                reglas = cargarReglas(archivoReglas, scanner);
                if (reglas.isEmpty()) {
                    System.out.println("No se cargaron reglas válidas. Volviendo al menú.");
                    continue;
                }

                // Cargar archivo de hechos
                String archivoHechos = seleccionarArchivo(scanner, "hechos", false);
                if (archivoHechos == null) continue;
                hechos = cargarHechos(archivoHechos);
            } else {
                // Ingreso manual de hechos
                System.out.println("Ingrese los hechos iniciales (uno por línea). Línea vacía para terminar:");
                while (true) {
                    String hecho = scanner.nextLine().trim();
                    if (hecho.isEmpty()) break;
                    hechos.add(hecho);
                }
                // Archivo de reglas obligatorio
                String archivoReglas = seleccionarArchivo(scanner, "reglas", true);
                if (archivoReglas == null) continue;
                reglas = cargarReglas(archivoReglas, scanner);
                if (reglas.isEmpty()) continue;
            }

            if (reglas.isEmpty()) {
                System.out.println("No hay reglas cargadas. No se puede continuar.");
                continue;
            }

            MotorInferencia motor = new MotorInferencia(reglas, hechos);

            System.out.println("\nSeleccione el método de inferencia:");
            System.out.println("1. Encadenamiento hacia Adelante");
            System.out.println("2. Encadenamiento hacia Atrás");
            System.out.print("Opción: ");

            String opcion2 = scanner.nextLine().trim();
            while (!opcion2.equals("1") && !opcion2.equals("2")) {
                System.out.print("Opción inválida. Ingrese 1 o 2: ");
                opcion2 = scanner.nextLine().trim();
            }

            if (opcion2.equals("1")) {
                motor.encadenamientoAdelante();
            } else {
                System.out.print("Ingrese el objetivo a inferir: ");
                String objetivo = scanner.nextLine().trim();
                boolean resultado = motor.encadenamientoAtras(objetivo);
                System.out.println("\nResultado final: El objetivo '" + objetivo + "' es " +
                        (resultado ? "VERDADERO" : "FALSO"));
                ArbolInferenciaGraphviz.mostrar(motor.getArbolInferencia());
            }

            System.out.print("\n¿Desea guardar los hechos inferidos? (s/n): ");
            if (leerSN(scanner).equalsIgnoreCase("s")) {
                guardarHechos(motor.getHechos(), scanner);
            }

            System.out.print("\n¿Desea continuar con otro caso? (s/n): ");
            if (leerSN(scanner).equalsIgnoreCase("n")) {
                continuar = false;
            }
        }

        scanner.close();
        System.out.println("\nGracias por usar el motor de inferencia.");
    }


    private static String seleccionarArchivo(Scanner scanner, String tipo, boolean obligatorio) {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione archivo de " + tipo);

        FileNameExtensionFilter filtro =
                new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        chooser.setFileFilter(filtro);

        int resultado = chooser.showOpenDialog(null);

        if (resultado != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = chooser.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            System.out.println("Solo se permiten archivos .txt");
            return null;
        }

        if (!archivo.exists()) {
            System.out.println("El archivo no existe.");
            return null;
        }

        return archivo.getAbsolutePath();
    }

    // Lee y valida que la entrada sea exactamente 's', 'S', 'n' o 'N'
    private static String leerSN(Scanner scanner) {
        while (true) {
            String entrada = scanner.nextLine().trim();
            if (entrada.equalsIgnoreCase("s") || entrada.equalsIgnoreCase("n")) {
                return entrada;
            } else {
                System.out.print("Opción inválida. Ingrese 's' o 'n': ");
            }
        }
    }

    private static List<Regla> cargarReglas(String archivo, Scanner scanner) {
        List<Regla> reglas = new ArrayList<>();
        int lineaNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineaNum++;
                if (linea.trim().isEmpty()) continue;
                try {
                    Regla r = new Regla(linea);
                    reglas.add(r);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error en línea " + lineaNum + ": " + e.getMessage());
                    System.out.print("¿Desea continuar con las reglas válidas? (s/n): ");
                    if (leerSN(scanner).equalsIgnoreCase("n")) {
                        return new ArrayList<>(); // Devuelve lista vacía para abortar
                    }
                }
            }
            System.out.println("Reglas cargadas correctamente desde " + archivo + " (" + reglas.size() + " reglas)");
        } catch (Exception e) {
            System.out.println("Error al cargar las reglas: " + e.getMessage());
            return new ArrayList<>();
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
            System.out.println("Hechos cargados correctamente desde " + archivo + " (" + hechos.size() + " hechos)");
        } catch (Exception e) {
            System.out.println("Error al cargar los hechos: " + e.getMessage());
        }
        return hechos;
    }

    private static void guardarHechos(List<String> hechos, Scanner scanner) {
        System.out.print("Ingrese el nombre del archivo para guardar: ");
        String nombreArchivo = scanner.nextLine().trim();
        if (nombreArchivo.isEmpty()) {
            System.out.println("Guardado cancelado (nombre vacío).");
            return;
        }
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            System.out.print("El archivo ya existe. ¿Sobrescribir? (s/n): ");
            if (!leerSN(scanner).equalsIgnoreCase("s")) {
                System.out.println("Guardado cancelado.");
                return;
            }
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (String hecho : hechos) {
                pw.println(hecho);
            }
            System.out.println("Hechos guardados en: " + nombreArchivo);
        } catch (Exception e) {
            System.out.println("Error al guardar los hechos: " + e.getMessage());
        }
    }
}