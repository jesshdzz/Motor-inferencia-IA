import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GestorArchivos {

    // Abre una ventana visual para que el usuario pueda seleccionar manualmente el
    // archivo de reglas o hechos (.txt)
    public static String seleccionarArchivoConDialogo(String tipo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione archivo de " + tipo);
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        chooser.setFileFilter(filtro);

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            System.out.println("Error: Solo se permiten archivos .txt");
            return null;
        }

        if (!archivo.exists()) {
            System.out.println("Error: El archivo no existe.");
            return null;
        }

        return archivo.getAbsolutePath();
    }

    // Lee un archivo de texto, procesa cada línea válida y crea una lista con los
    // objetos de tipo 'Regla'
    public static List<Regla> cargarReglas(String rutaRef) {
        List<Regla> reglas = new ArrayList<>();
        Path ruta = resolverRutaArchivo(rutaRef);
        if (ruta == null) {
            System.out.println("Error al cargar reglas: No se encontró '" + rutaRef + "'");
            return reglas;
        }

        System.out.println("Cargando reglas desde: " + ruta);
        int lineaNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta.toFile()))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineaNum++;
                if (!linea.trim().isEmpty() && !linea.trim().startsWith("#")) {
                    try {
                        reglas.add(new Regla(linea));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Advertencia en línea " + lineaNum + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Excepción al cargar reglas: " + e.getMessage());
        }
        return reglas;
    }

    // Lee un archivo de texto y extrae una lista de las verdades o 'hechos'
    // iniciales del sistema
    public static List<String> cargarHechos(String rutaRef) {
        List<String> hechos = new ArrayList<>();
        Path ruta = resolverRutaArchivo(rutaRef);
        if (ruta == null) {
            System.out.println("Error al cargar hechos: No se encontró '" + rutaRef + "'");
            return hechos;
        }

        System.out.println("Cargando hechos desde: " + ruta);
        try (BufferedReader br = new BufferedReader(new FileReader(ruta.toFile()))) {
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
            System.out.println("Excepción al cargar hechos: " + e.getMessage());
        }
        return hechos;
    }

    // Guarda los hechos actuales (incluyendo los nuevos inferidos) sobreescribiendo
    // el archivo especificado
    public static void guardarHechos(List<String> hechos, String archivo) {
        Path rutaSalida = resolverRutaGuardado(archivo);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaSalida.toFile()))) {
            for (String hecho : hechos) {
                pw.println(hecho);
            }
            System.out.println("Hechos guardados existosamente en: " + rutaSalida);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo de hechos: " + e.getMessage());
        }
    }

    // Intenta encontrar la ruta real e inteligible de un archivo, ya sea un archivo
    // directo, en el proyecto, o hacia arriba en los directorios
    private static Path resolverRutaArchivo(String rutaRelativa) {
        if (rutaRelativa == null || rutaRelativa.trim().isEmpty()) {
            return null;
        }
        Path entrada = Paths.get(rutaRelativa);
        if (entrada.isAbsolute() && Files.exists(entrada)) {
            return entrada.normalize();
        }

        Path proyecto = resolverDirectorioProyecto();
        if (proyecto != null) {
            Path desdeProyecto = proyecto.resolve(rutaRelativa).normalize();
            if (Files.exists(desdeProyecto)) {
                return desdeProyecto;
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
            Path baseClase = Paths.get(GestorArchivos.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath().normalize();
            return buscarEnPadres(baseClase, rutaRelativa);
        } catch (Exception e) {
            return null;
        }
    }

    // Va buscando el archivo iterativamente en las carpetas superiores ('padres')
    // en caso de no hallarlo en la actual
    private static Path buscarEnPadres(Path inicio, String rutaRelativa) {
        Path actual = inicio;
        for (int i = 0; i < 8 && actual != null; i++) {
            Path candidata = actual.resolve(rutaRelativa).normalize();
            if (Files.exists(candidata)) {
                return candidata;
            }
            actual = actual.getParent();
        }
        return null;
    }

    // Decide cuál es el lugar correcto donde deben guardarse los archivos para no
    // perderlos en carpetas de binarios o temporales
    private static Path resolverRutaGuardado(String ruta) {
        Path p = Paths.get(ruta);
        if (p.isAbsolute()) {
            return p.normalize();
        }
        Path proyecto = resolverDirectorioProyecto();
        if (proyecto != null) {
            return proyecto.resolve(p).normalize();
        }
        return Paths.get("").toAbsolutePath().resolve(p).normalize();
    }

    // Encuentra la raíz de todo el proyecto (útil para cuando se ejecuta desde un
    // .jar o desde carpetas anidadas de compilación)
    private static Path resolverDirectorioProyecto() {
        Path desdeCwd = buscarDirectorioProyecto(Paths.get("").toAbsolutePath().normalize());
        if (desdeCwd != null)
            return desdeCwd;

        try {
            Path baseClase = Paths.get(GestorArchivos.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                    .toAbsolutePath().normalize();
            return buscarDirectorioProyecto(baseClase);
        } catch (Exception e) {
            return null;
        }
    }

    // Identifica el directorio del proyecto reconociendo carpetas o archivos clave
    // como 'caso1' o 'Main.java'
    private static Path buscarDirectorioProyecto(Path inicio) {
        Path actual = inicio;
        for (int i = 0; i < 10 && actual != null; i++) {
            boolean tieneCaso1 = Files.isDirectory(actual.resolve("caso1"));
            boolean tieneMainFuente = Files.exists(actual.resolve("Main.java"));
            String nombreDir = actual.getFileName() == null ? "" : actual.getFileName().toString();
            boolean esBin = nombreDir.equalsIgnoreCase("bin");

            if ((tieneCaso1 || tieneMainFuente) && !esBin) {
                return actual;
            }
            actual = actual.getParent();
        }
        return null;
    }
}
