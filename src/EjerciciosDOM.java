import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;


public class EjerciciosDOM {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ejercicio1a();
        ejercicio1b();
        ejercicio1c();
        ejercicio1d(sc);
        ejercicio2();
        ejercicio3();
    }

    private static Document nuevoDocumento() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.newDocument();
    }

    private static Document leerDocumento(String ruta) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new File(ruta));
        doc.getDocumentElement().normalize();
        limpiarEspacios(doc.getDocumentElement());
        return doc;
    }

    private static void limpiarEspacios(Node nodo) {
        NodeList hijos = nodo.getChildNodes();
        for (int i = hijos.getLength() - 1; i >= 0; i--) {
            Node h = hijos.item(i);
            if (h.getNodeType() == Node.TEXT_NODE && h.getTextContent().trim().isEmpty()) {
                nodo.removeChild(h);
            } else if (h.getNodeType() == Node.ELEMENT_NODE) {
                limpiarEspacios(h);
            }
        }
    }

    private static Transformer crearTransformer() throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return t;
    }

    private static void guardarDocumento(Document doc, String ruta) throws Exception {
        crearTransformer().transform(new DOMSource(doc), new StreamResult(new File(ruta)));
    }

    private static void mostrarDocumento(Document doc) throws Exception {
        StringWriter sw = new StringWriter();
        crearTransformer().transform(new DOMSource(doc), new StreamResult(sw));
        System.out.println(sw);
    }

    private static String texto(Element padre, String etiqueta) {
        return padre.getElementsByTagName(etiqueta).item(0).getTextContent().trim();
    }

    private static void agregarElemento(Document doc, Element padre, String etiqueta, String valor) {
        Element e = doc.createElement(etiqueta);
        e.appendChild(doc.createTextNode(valor));
        padre.appendChild(e);
    }

    public static void ejercicio1a() {
        System.out.println("Ejercicio 1a:");
        try {
            String[][] felinos = {
                    {"Leon", "Africa", "155"},
                    {"Tigre", "Asia", "215"},
                    {"Lince", "Europa", "20"},
                    {"PanteraTigris", "America", "95"}
            };

            Document doc = nuevoDocumento();
            Element zoo = doc.createElement("Zoo");
            doc.appendChild(zoo);
            Element felinosEl = doc.createElement("Felinos");
            zoo.appendChild(felinosEl);

            for (String[] f : felinos) {
                Element felino = doc.createElement("Felino");
                agregarElemento(doc, felino, "Nombre", f[0]);
                agregarElemento(doc, felino, "Origen", f[1]);
                agregarElemento(doc, felino, "Peso", f[2]);
                felinosEl.appendChild(felino);
            }

            guardarDocumento(doc, "Zoo.xml");
            System.out.println("Fichero Zoo.xml creado correctamente.\n");
        } catch (Exception e) {
            System.out.println("Error en ejercicio 1a: " + e.getMessage());
        }
    }

    public static void ejercicio1b() {
        System.out.println("\nEjercicio 1b:");
        try {
            Document doc = leerDocumento("Zoo.xml");
            NodeList lista = doc.getElementsByTagName("Felino");

            for (int i = 0; i < lista.getLength(); i++) {
                Element f = (Element) lista.item(i);
                double peso = Double.parseDouble(texto(f, "Peso"));
                if (peso > 100) {
                    System.out.println(texto(f, "Nombre") + " (" + texto(f, "Origen")
                            + ") - " + texto(f, "Peso") + " kg");
                }
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error en ejercicio 1b: " + e.getMessage());
        }
    }

    public static void ejercicio1c() {
        System.out.println("\nEjercicio 1c:");
        try {
            Document doc = leerDocumento("Zoo.xml");
            NodeList pesos = doc.getElementsByTagName("Peso");

            for (int i = 0; i < pesos.getLength(); i++) {
                Node p = pesos.item(i);
                double nuevo = Double.parseDouble(p.getTextContent().trim()) - 5;
                p.setTextContent(nuevo == Math.floor(nuevo)
                        ? String.valueOf((long) nuevo) : String.valueOf(nuevo));
            }

            guardarDocumento(doc, "Zoo2.xml");
            mostrarDocumento(doc);
        } catch (Exception e) {
            System.out.println("Error en ejercicio 1c: " + e.getMessage());
        }
    }

    public static void ejercicio1d(Scanner sc) {
        System.out.println("\nEjercicio 1d:");
        try {
            System.out.print("Introduce un continente (Africa, Asia, Europa, America): ");
            String continente = sc.nextLine().trim();

            Document doc = leerDocumento("Zoo.xml");
            NodeList lista = doc.getElementsByTagName("Felino");
            boolean encontrado = false;

            for (int i = 0; i < lista.getLength(); i++) {
                Element f = (Element) lista.item(i);
                if (texto(f, "Origen").equalsIgnoreCase(continente)) {
                    System.out.println(texto(f, "Nombre") + " - " + texto(f, "Peso") + " kg");
                    encontrado = true;
                }
            }
            if (!encontrado) {
                System.out.println("No hay animales del continente \"" + continente + "\".");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Error en ejercicio 1d: " + e.getMessage());
        }
    }

    private static Element buscarEmpresa(Document bolsa, String nombre) {
        NodeList empresas = bolsa.getElementsByTagName("Empresa");
        for (int i = 0; i < empresas.getLength(); i++) {
            Element e = (Element) empresas.item(i);
            if (texto(e, "Nombre").equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    public static void ejercicio2() {
        System.out.println("\nEjercicio 2:");
        try {
            Document bolsa = leerDocumento("src/Bolsa.xml");
            Document datos = leerDocumento("src/datos.xml");
            Element raiz = bolsa.getDocumentElement();

            NodeList operaciones = datos.getElementsByTagName("Operacion");

            for (int i = 0; i < operaciones.getLength(); i++) {
                Element op = (Element) operaciones.item(i);
                String tipo = op.getAttribute("id").trim().toUpperCase();
                String nombre = texto(op, "Nombre");
                Element existente = buscarEmpresa(bolsa, nombre);

                switch (tipo) {
                    case "A":
                        if (existente != null) {
                            System.out.println("ERROR (Alta): la empresa \"" + nombre + "\" ya existe.");
                            break;
                        }
                        String indice = texto(op, "Indice").toLowerCase();
                        String simbolo = texto(op, "Simbolo");
                        double precio;
                        try {
                            precio = Double.parseDouble(texto(op, "Precio"));
                        } catch (NumberFormatException nfe) {
                            System.out.println("ERROR (Alta): precio no válido para \"" + nombre + "\".");
                            break;
                        }
                        Element nueva = bolsa.createElement("Empresa");
                        nueva.setAttribute("indice", indice);
                        agregarElemento(bolsa, nueva, "Nombre", nombre);
                        agregarElemento(bolsa, nueva, "Simbolo", simbolo);
                        agregarElemento(bolsa, nueva, "Precio", String.valueOf(precio));
                        raiz.appendChild(nueva);
                        System.out.println("Alta correcta: " + nombre);
                        break;

                    case "B":
                        if (existente == null) {
                            System.out.println("ERROR (Baja): la empresa \"" + nombre + "\" no existe.");
                            break;
                        }
                        raiz.removeChild(existente);
                        System.out.println("Baja correcta: " + nombre);
                        break;

                    case "M":
                        if (existente == null) {
                            System.out.println("ERROR (Modificación): la empresa \"" + nombre + "\" no existe.");
                            break;
                        }
                        try {
                            double nuevoPrecio = Double.parseDouble(texto(op, "Precio"));
                            existente.getElementsByTagName("Precio").item(0)
                                    .setTextContent(String.valueOf(nuevoPrecio));
                            System.out.println("Modificación correcta: " + nombre
                                    + " -> nuevo precio " + nuevoPrecio);
                        } catch (NumberFormatException nfe) {
                            System.out.println("ERROR (Modificación): precio no válido para \"" + nombre + "\".");
                        }
                        break;

                    default:
                        System.out.println("ERROR: operación desconocida \"" + tipo + "\".");
                }
            }

            guardarDocumento(bolsa, "src/bolsaactualizada.xml");
            System.out.println("Fichero bolsaactualizada.xml creado correctamente.\n");
        } catch (Exception e) {
            System.out.println("Error en ejercicio 2: " + e.getMessage());
        }
    }

    private static String formato(double valor) {
        DecimalFormat df = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(valor);
    }

    public static void ejercicio3() {
        System.out.println("\nEjercicio 3:");
        try {
            Document doc = leerDocumento("src/alumnos.xml");
            String modulo = texto((Element) doc.getElementsByTagName("profesor").item(0), "modulo");

            NodeList alumnos = doc.getElementsByTagName("alumno");

            Document salida = nuevoDocumento();
            Element resultados = salida.createElement("Resultados");
            salida.appendChild(resultados);

            double suma = 0;
            for (int i = 0; i < alumnos.getLength(); i++) {
                Element a = (Element) alumnos.item(i);
                String dni = a.getAttribute("dni");

                double p1 = Double.parseDouble(texto(a, "practica1"));
                double p2 = Double.parseDouble(texto(a, "practica2"));
                double examen = Double.parseDouble(texto(a, "examen"));
                double fin = Double.parseDouble(texto(a, "final"));

                double mediaPracticas = (p1 + p2) / 2;
                double media = mediaPracticas * 0.30 + examen * 0.40 + fin * 0.30;
                suma += media;

                System.out.println(dni + " = " + formato(media));

                Element al = salida.createElement("Alumno");
                al.setAttribute("dni", dni);
                agregarElemento(salida, al, "NotaMedia", formato(media));
                resultados.appendChild(al);
            }

            double mediaClase = alumnos.getLength() > 0 ? suma / alumnos.getLength() : 0;
            System.out.println("Media total del módulo " + modulo + "=" + formato(mediaClase));

            Element mediaEl = salida.createElement("MediaTotal");
            mediaEl.setAttribute("modulo", modulo);
            mediaEl.setTextContent(formato(mediaClase));
            resultados.appendChild(mediaEl);

            guardarDocumento(salida, "src/alumnos_notas.xml");
            System.out.println("Fichero alumnos_notas.xml creado correctamente.\n");
        } catch (Exception e) {
            System.out.println("Error en ejercicio 3: " + e.getMessage());
        }
    }
}