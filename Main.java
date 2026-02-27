import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    final static String ARCHIVO_REGLAS_C1 = "caso1/reglas_ciber.txt";
    final static String ARCHIVO_HECHOS_C1 = "caso1/hechos_ciber.txt";

    final static String ARCHIVO_REGLAS_C2 = "caso2/reglas_taller.txt";
    final static String ARCHIVO_HECHOS_C2 = "caso2/hechos_taller.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String archivoReglas = "";
        String archivoHechos = "";
        boolean hechosSoloManual = false;

        System.out.println("\nSeleccione el caso:");
        System.out.println("1. Analista de ciberseguridad");
        System.out.println("2. Taller Mecánico");
        System.out.println("3. Cargar cualquier base de conocimiento (archivos .txt)");
        System.out.print("Opción: ");
        String opcion1 = scanner.nextLine();

        if (opcion1.equals("1")) {
            archivoReglas = ARCHIVO_REGLAS_C1;
            archivoHechos = ARCHIVO_HECHOS_C1;
        } else if (opcion1.equals("2")) {
            archivoReglas = ARCHIVO_REGLAS_C2;
            archivoHechos = ARCHIVO_HECHOS_C2;
        } else if (opcion1.equals("3")) {
            System.out.println("\nModo general (no depende de los ejemplos 1 y 2).");
            System.out.println("Carga de base de conocimiento desde archivos de texto.");
            System.out.println("Seleccione forma de carga:");
            System.out.println("1. Subir base por rutas (reglas + hechos)");
            System.out.println("2. Subir reglas por ruta y hechos manualmente");
            System.out.print("Opción: ");
            String modoCarga = scanner.nextLine().trim();
            System.out.println("Formato de rutas:");
            System.out.println("- Relativa: caso1/reglas_ciber.txt");
            System.out.println("- Absoluta: /home/usuario/proyecto/reglas.txt");
            System.out.println("Los hechos pueden cargarse por archivo o ingresarse manualmente.");
            System.out.print("Ruta del archivo de reglas (obligatoria): ");
            archivoReglas = scanner.nextLine().trim();
            if (archivoReglas.isEmpty()) {
                System.out.println("Debes ingresar una ruta de reglas para continuar.");
                return;
            }

            if (modoCarga.equals("2")) {
                hechosSoloManual = true;
            } else {
                System.out.print("Ruta del archivo de hechos: ");
                archivoHechos = scanner.nextLine().trim();
                if (archivoHechos.isEmpty()) {
                    System.out.println("No se indicó ruta de hechos. Se usará modo manual.");
                    hechosSoloManual = true;
                }
            }
        } else {
            System.out.println("Opción inválida");
            return;
        }

        List<Regla> reglas = cargarReglas(archivoReglas);
        if (reglas.isEmpty()) {
            archivoReglas = solicitarRutaAlternativaReglas(scanner, archivoReglas);
            if (archivoReglas == null) {
                System.out.println("No se pudo continuar sin reglas.");
                return;
            }
            reglas = cargarReglas(archivoReglas);
        }

        if (reglas.isEmpty()) {
            System.out.println("Base de conocimientos vacía. Abortando.");
            return;
        }

        List<String> hechos;
        if (hechosSoloManual) {
            System.out.println("\nNo se indicó archivo de hechos. Se usará captura manual.");
            mostrarInstruccionesCargaManual();
            hechos = capturarHechosManual(scanner);
        } else {
            hechos = cargarHechos(archivoHechos);
            if (hechos.isEmpty()) {
                System.out.print("No se pudieron cargar hechos desde '" + archivoHechos
                        + "'. ¿Desea ingresarlos manualmente? (s/n): ");
                String respuesta = scanner.nextLine().trim().toLowerCase();
                if (respuesta.equals("s") || respuesta.equals("si")) {
                    mostrarInstruccionesCargaManual();
                    hechos = capturarHechosManual(scanner);
                }
            }
        }

        MotorInferencia motor = new MotorInferencia(reglas, hechos);

        System.out.println("\nSeleccione el método de inferencia:");
        System.out.println("1. Encadenamiento hacia Adelante");
        System.out.println("2. Encadenamiento hacia Atrás");
        System.out.print("Opción: ");
        String opcion2 = scanner.nextLine();

        if (opcion2.equals("1")) {
            System.out.println("\nObjetivo del método: inferir todas las conclusiones posibles (cierre de hechos).");
            List<String> nuevos = motor.encadenamientoHaciaAdelante();
            mostrarResultadoAdelante(motor.getHechos(), nuevos);
        } else if (opcion2.equals("2")) {
            System.out.print("Ingrese el objetivo a demostrar: ");
            String objetivo = scanner.nextLine().trim();
            System.out.println("Objetivo solicitado: " + objetivo);
            motor.encadenamientoHaciaAtras(objetivo);
            mostrarHechosActuales(motor.getHechos());
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        preguntarGuardado(scanner, motor.getHechos(), archivoHechos);
    }

    private static List<String> capturarHechosManual(Scanner scanner) {
        List<String> hechos = new ArrayList<>();
        System.out.println("\nIngrese hechos (uno por línea). Escriba FIN para terminar:");
        while (true) {
            String hecho = scanner.nextLine().trim();
            if (hecho.equalsIgnoreCase("FIN")) {
                break;
            }
            if (!hecho.isEmpty() && !hechos.contains(hecho)) {
                hechos.add(hecho);
            }
        }
        return hechos;
    }

    private static void mostrarInstruccionesCargaManual() {
        System.out.println("\nInstrucciones de carga manual:");
        System.out.println("- Escribe un hecho por línea (ejemplo: sistema_lento).");
        System.out.println("- No uses comas ni conectores.");
        System.out.println("- Escribe FIN para terminar.");
    }

    private static String solicitarRutaAlternativaReglas(Scanner scanner, String rutaOriginal) {
        System.out.print("No se pudieron cargar reglas desde '" + rutaOriginal
                + "'. Ingrese una ruta alternativa (Enter para cancelar): ");
        String alternativa = scanner.nextLine().trim();
        if (alternativa.isEmpty()) {
            return null;
        }
        return alternativa;
    }

    private static void mostrarResultadoAdelante(List<String> hechosFinales, List<String> nuevos) {
        System.out.println("\nHechos inferidos nuevos:");
        if (nuevos.isEmpty()) {
            System.out.println("- Ninguno");
        } else {
            for (String hecho : nuevos) {
                System.out.println("- " + hecho);
            }
        }

        System.out.println("\nBase de hechos final:");
        for (String hecho : hechosFinales) {
            System.out.println("- " + hecho);
        }
    }

    private static void mostrarHechosActuales(List<String> hechos) {
        System.out.println("\nHechos conocidos al finalizar:");
        for (String hecho : hechos) {
            System.out.println("- " + hecho);
        }
    }

    private static void preguntarGuardado(Scanner scanner, List<String> hechos, String archivoOriginal) {
        System.out.print("\n¿Desea guardar la base de hechos actualizada? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        if (!respuesta.equals("s") && !respuesta.equals("si")) {
            System.out.println("No se guardaron cambios.");
            return;
        }

        Path rutaPorDefecto = obtenerRutaGuardadoPorDefecto(archivoOriginal);
        String sugerida = rutaPorDefecto == null ? "(sin ruta por defecto)" : rutaPorDefecto.toString();
        System.out.println("Se guardarán los hechos finales (base actualizada).");
        System.out.print("Ruta de salida (Enter para usar " + sugerida + "): ");
        String ruta = scanner.nextLine().trim();
        if (ruta.isEmpty()) {
            if (rutaPorDefecto == null) {
                System.out.println("No hay ruta por defecto. Debes indicar una ruta de salida.");
                System.out.print("Ruta de salida: ");
                ruta = scanner.nextLine().trim();
                if (ruta.isEmpty()) {
                    System.out.println("Guardado cancelado por ruta vacía.");
                    return;
                }
            } else {
                ruta = rutaPorDefecto.toString();
            }
        }

        guardarHechos(hechos, ruta);
    }

    private static List<Regla> cargarReglas(String archivo) {
        List<Regla> reglas = new ArrayList<>();
        Path rutaReglas = resolverRutaArchivo(archivo);
        if (rutaReglas == null) {
            System.out.println("Error al cargar las reglas: no se encontró '" + archivo + "'");
            return reglas;
        }
        System.out.println("Archivo de reglas cargado desde: " + rutaReglas);
        if (esRutaTemporalIDE(rutaReglas)) {
            System.out.println("Advertencia: estás usando una ruta temporal del IDE (workspaceStorage/bin).");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaReglas.toFile()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty() && !linea.trim().startsWith("#")) {
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
        Path rutaHechos = resolverRutaArchivo(archivo);
        if (rutaHechos == null) {
            System.out.println("Error al cargar los hechos: no se encontró '" + archivo + "'");
            return hechos;
        }
        System.out.println("Archivo de hechos cargado desde: " + rutaHechos);
        if (esRutaTemporalIDE(rutaHechos)) {
            System.out.println("Advertencia: estás usando una ruta temporal del IDE (workspaceStorage/bin).");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaHechos.toFile()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty() && !linea.trim().startsWith("#")) {
                    String hecho = linea.trim();
                    if (!hechos.contains(hecho)) {
                        hechos.add(hecho);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar los hechos: " + e.getMessage());
        }

        return hechos;
    }

    private static void guardarHechos(List<String> hechos, String archivo) {
        Path rutaSalida = resolverRutaGuardado(archivo);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaSalida.toFile()))) {
            for (String hecho : hechos) {
                pw.println(hecho);
            }
            System.out.println("Hechos guardados en: " + rutaSalida);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    private static Path resolverRutaArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.trim().isEmpty()) {
            return null;
        }

        Path entrada = Paths.get(rutaRelativa);
        if (!entrada.isAbsolute()) {
            Path proyecto = resolverDirectorioProyecto();
            if (proyecto != null) {
                Path desdeProyecto = proyecto.resolve(rutaRelativa).normalize();
                if (Files.exists(desdeProyecto)) {
                    return desdeProyecto;
                }
            }
        }

        Path directa = Paths.get(rutaRelativa).toAbsolutePath().normalize();
        if (Files.exists(directa)) {
            return directa;
        }

        Path encontrada = buscarEnPadres(Paths.get("").toAbsolutePath().normalize(), rutaRelativa);
        if (encontrada != null) {
            return encontrada;
        }

        try {
            Path baseClase = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath()
                    .normalize();
            return buscarEnPadres(baseClase, rutaRelativa);
        } catch (Exception e) {
            return null;
        }
    }

    private static Path buscarEnPadres(Path inicio, String rutaRelativa) {
        Path actual = inicio;
        Path candidataBin = null;
        for (int i = 0; i < 8 && actual != null; i++) {
            Path candidata = actual.resolve(rutaRelativa).normalize();
            if (Files.exists(candidata)) {
                String nombreDir = actual.getFileName() == null ? "" : actual.getFileName().toString();
                if (!nombreDir.equalsIgnoreCase("bin")) {
                    return candidata;
                }
                if (candidataBin == null) {
                    candidataBin = candidata;
                }
            }
            actual = actual.getParent();
        }

        return candidataBin;
    }

    private static Path resolverRutaGuardado(String ruta) {
        Path p = Paths.get(ruta);
        if (p.isAbsolute()) {
            return p.normalize();
        }
        return Paths.get("").toAbsolutePath().resolve(p).normalize();
    }

    private static boolean esRutaTemporalIDE(Path ruta) {
        String r = ruta.toString().replace('\\', '/');
        return r.contains("/.config/Code/User/workspaceStorage/") || r.contains("/jdt_ws/");
    }

    private static Path obtenerRutaGuardadoPorDefecto(String archivoOriginal) {
        if (archivoOriginal != null && !archivoOriginal.trim().isEmpty()) {
            Path rutaOriginal = resolverRutaArchivo(archivoOriginal);
            if (rutaOriginal != null) {
                return rutaOriginal;
            }
        }

        Path proyecto = resolverDirectorioProyecto();
        if (proyecto != null) {
            return proyecto.resolve("hechos_manual_guardados.txt").normalize();
        }
        return null;
    }

    private static Path resolverDirectorioProyecto() {
        Path desdeCwd = buscarDirectorioProyecto(Paths.get("").toAbsolutePath().normalize());
        if (desdeCwd != null) {
            return desdeCwd;
        }

        try {
            Path baseClase = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath()
                    .normalize();
            return buscarDirectorioProyecto(baseClase);
        } catch (Exception e) {
            return null;
        }
    }

    private static Path buscarDirectorioProyecto(Path inicio) {
        Path actual = inicio;
        Path candidataConCasos = null;
        Path candidataBin = null;
        for (int i = 0; i < 10 && actual != null; i++) {
            boolean tieneCaso1 = Files.isDirectory(actual.resolve("caso1"));
            boolean tieneCaso2 = Files.isDirectory(actual.resolve("caso2"));
            boolean tieneMainFuente = Files.exists(actual.resolve("Main.java"));
            String nombreDir = actual.getFileName() == null ? "" : actual.getFileName().toString();
            boolean esBin = nombreDir.equalsIgnoreCase("bin");

            if (tieneMainFuente && !esBin) {
                return actual;
            }
            if (tieneCaso1 && tieneCaso2 && !esBin && candidataConCasos == null) {
                candidataConCasos = actual;
            }
            if ((tieneCaso1 || tieneCaso2 || tieneMainFuente) && esBin && candidataBin == null) {
                candidataBin = actual;
            }
            actual = actual.getParent();
        }
        if (candidataConCasos != null) {
            return candidataConCasos;
        }
        return candidataBin;
    }
}
