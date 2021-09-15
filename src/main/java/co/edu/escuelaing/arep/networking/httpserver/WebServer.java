package co.edu.escuelaing.arep.networking.httpserver;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.reflections.Reflections;

import co.edu.escuelaing.arep.networking.httpserver.myspring.Component;
import co.edu.escuelaing.arep.networking.httpserver.myspring.Service;

/**
 * Clase que contiene todas las características del Webserver.
 * 
 * @author aleja 05/09/2021
 */
public class WebServer {

	/**
	 * Atributo que define el WebServer
	 */
	public static final WebServer _instance = new WebServer();

	/**
	 * Estructura que almacena los servicios con su ruta
	 */
	public static HashMap<String, Method> chm_services = new HashMap<>();
	public static HashMap<String, Object> cho_services = new HashMap<>();

	public WebServer() {

	}

	/**
	 * Preparando la comunicación para el intercambio de mensajes.
	 * 
	 * @param args - peticiones del cliente
	 * @param port - puerto de comunicación
	 * @throws IOException        - Cuando no es posible establecer la comunicación
	 * @throws URISyntaxException - Cuando no es posible interpretar la URI
	 */
	public void startSocket(String[] args, int port) throws IOException, URISyntaxException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 35000.");
			System.exit(1);
		}
		searchForComponents();
		boolean running = true;
		while (running) {
			Socket clientSocket = null;
			try {
				System.out.println("Listo para recibir ...");
				clientSocket = serverSocket.accept();

			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
			serverConnection(clientSocket);
		}
		serverSocket.close();
	}

	/**
	 * Carga cada fichero (.class) con la anotación de @Component del directorio
	 * raiz (classpath) de un paquete específico, quemado en la variable classpath.
	 * Se utiliza la librería reflection de google.
	 */
	private void searchForComponents() {
		String classpath = "co.edu.escuelaing.arep.networking.httpserver";

		Reflections reflections = new Reflections(classpath); // Por reflection obtenemos la lista de clases que se
																// encuentran dentro de ese paquete.
		if (reflections != null) {
			Set<Class<? extends Object>> allClasses = reflections.getTypesAnnotatedWith(Component.class);
			Object[] classesList = allClasses.toArray();

			if (classesList != null && classesList.length > 0) {

				for (Object ao_obj : classesList) {
					String c = ao_obj.toString().substring(6);
					loadServices(c);
				}
			}
		}
	}

	/**
	 * Carga los métodos con anotación @Service de la clase especificada e instancia
	 * la clase
	 * 
	 * @param classpath - classpath de la clase
	 */
	private void loadServices(String classpath) {
		try {
			Class<?> lc_class = Class.forName(classpath);

			for (Method m : lc_class.getMethods()) {
				if (m.isAnnotationPresent(Service.class)) {
					String uri = m.getAnnotation(Service.class).uri();
					chm_services.put(uri, m);
					cho_services.put(uri, lc_class.newInstance());
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Conectando el Cliente con el Servidor y atendiendo su petición(es).
	 * 
	 * @param clientSocket - Comunicación con el cliente
	 * @throws IOException        - Cuando no es posible establecer la comunicación.
	 * @throws URISyntaxException - Cuando no es posible interpretar la URI.
	 */
	public void serverConnection(Socket clientSocket) throws IOException, URISyntaxException {
		if (clientSocket != null) {

			PrintWriter out;
			BufferedReader in;
			OutputStream los_outputStream;
			InputStream lis_inputStream;

			los_outputStream = clientSocket.getOutputStream();
			lis_inputStream = clientSocket.getInputStream();

			if ((los_outputStream != null) && (lis_inputStream != null)) {

				String inputLine, outputLine;
				StringBuilder request;
				InputStreamReader lisr_inputStreamReader;

				request = new StringBuilder();
				lisr_inputStreamReader = new InputStreamReader(lis_inputStream);
				out = new PrintWriter(los_outputStream, true); // envío de msgs al Cliente.

				if (lisr_inputStreamReader != null) {
					in = new BufferedReader(lisr_inputStreamReader); // recibir msgs del Cliente

					if (in != null && in.ready()) {

						while ((inputLine = in.readLine()) != null) {
							System.out.println("Received: " + inputLine);
							request.append(inputLine);
							if (!in.ready()) {
								break;
							}
						}
					}
					String ls_request;
					ls_request = request.toString();

					if ((ls_request != null) && (!ls_request.isEmpty())) {

						String ls_uriStr;
						String[] las_request;

						las_request = ls_request.split(" ");

						if ((las_request != null)) {
							ls_uriStr = las_request[1];

							if ((ls_uriStr != null) && (!ls_uriStr.isEmpty())) {
								URI resourceURI;

								String mimeType = MimeType.getMimeType(ls_uriStr); // Obtener el tipo de contenido de
																					// los recursos
								resourceURI = new URI(ls_uriStr);

								if (resourceURI.toString().startsWith("/appuser")) {
									outputLine = getComponentResource(resourceURI);
									out.println(outputLine);
								} else {
									if (ls_uriStr.equals("/")
											|| (!mimeType.equals(MimeType.MIME_APPLICATION_OCTET_STREAM))) {

										if (mimeType.contains("image")) {
											String path = "target/classes/public" + resourceURI.getPath();
											File file = new File(path);

											if (file.exists()) {
												getImageResource(file, los_outputStream, mimeType);
											} else {
												out.println(default404Response());
											}
										} else {
											outputLine = getTextResource(resourceURI, mimeType);
											out.println(outputLine);
										}
									} else {
										throw new IOException("ServerConnection MimeType desconocido!");
									}
								}
							}
						}
					}
					out.close();
					in.close();
				}
			} else {
				throw new IOException("ServerConnection BufferReader input vacío o nulo!");
			}
			clientSocket.close();
		} else {
			throw new IOException("ServerConnection Socket no puede ser nulo");
		}
	}
	
	/**
	 * Permite acceder a un componente por reflexión e IoC
	 * @param resourceURI - Ruta del pojo requerido
	 * @return servicio del pojo especificado
	 */
	private String getComponentResource(URI resourceURI) {
		
		String response = default404Response();
		try {
			if (chm_services != null) {
				String ls_serviceURI = resourceURI.getPath().toString().replaceAll("/appuser", "");
				if (ls_serviceURI != null && !ls_serviceURI.isEmpty()) {
					Object lo_o = cho_services.get(ls_serviceURI);
					Method lm_m = chm_services.get(ls_serviceURI);
					if (lo_o != null && lm_m != null) {

						response = lm_m.invoke(lo_o, null).toString();
						response = htmlComponent(response);
						;
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
			Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, e);
			response = default404Response();
		}
		return response;
	}

	/**
	 * Permite leer un recurso de tipo .html, .css y .js
	 * 
	 * @param resourceURI - Ruta del recurso requerido de tipo text.
	 * @param mimeType    - Tipo de contenido del archivo.
	 * @return El contenido del recurso.
	 * @throws IOException - Cuando no es posible leer el recurso.
	 */
	public String getTextResource(URI resourceURI, String mimeType) throws IOException {
		StringBuilder response = new StringBuilder();

		Charset charset = Charset.forName("UTF-8");
		Path htmlFile = Paths.get("target/classes/public" + resourceURI.getPath());

		try (BufferedReader in = Files.newBufferedReader(htmlFile, charset)) {
			String str;

			response = new StringBuilder("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" + "\r\n");

			while ((str = in.readLine()) != null) {
				response.append(str + "\n");
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
			return default404Response();
		}
		return response.toString();
	}

	/**
	 * Permite leer un recurso de tipo .jpg, .png
	 * 
	 * @param file             - recurso extraido de disco tipo image
	 * @param los_outputStream - Salida de la escritura.
	 * @param mimeType         - tipo de contenido standard a través de la red.
	 * @throws IOException - Cuando no es posible leer el recurso.
	 */
	public void getImageResource(File file, OutputStream los_outputStream, String mimeType) throws IOException {
		try {
			BufferedImage in_image = ImageIO.read(file);
			if (in_image != null) {
				ByteArrayOutputStream lab_outputStream = new ByteArrayOutputStream();
				DataOutputStream writeimg = new DataOutputStream(los_outputStream);

				ImageIO.write(in_image, MimeType.getExt(mimeType), lab_outputStream);
				writeimg.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: " + mimeType + "\r\n" + "\r\n");
				writeimg.write(lab_outputStream.toByteArray());
			}
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
			throw new IOException("getImageResource DataOutputStream no puede ser nulo!");
		}
	}

	/**
	 * Página html por defecto al acceder a un componente
	 * @param response - Retorno del servicio, que en este caso es el nombre de un juego
	 * @return la página por defecto en html
	 */
	private String htmlComponent(String response) {
		String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n"
				+ "<html>\n" + "	<head>\n" + "		<meta charset=\"UTF-8\">\n" + "		<title>Inicio</title>\n"
				+ "	</head>\n" + "	<body>\n" + "		<h1> PRUEBA DE CONCEPTO CON POJO GAME</h1>\n" + "		<h1>"
				+ response + "</h1>\n" + "	</body>\n" + "</html>\n";
		return outputLine;
	}

	/**
	 * Página html por defecto al intentar conectar con el servidor
	 * 
	 * @return la página por defecto en html
	 */
	private String default404Response() {
		String outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" + "<!DOCTYPE html>\n"
				+ "<html>\n" + "	<head>\n" + "		<meta charset=\"UTF-8\">\n" + "		<title>Inicio</title>\n"
				+ "	</head>\n" + "	<body>\n" + "		<h1>NOT FOUND 404</h1>\n" + "	</body>\n" + "</html>\n";
		return outputLine;
	}
}