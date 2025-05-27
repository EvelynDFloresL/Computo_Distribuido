package RedP2P;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;

public class RedP2P implements Runnable {

    private MulticastSocket socket;
    private InetAddress host;
    private int port;
    private JTextArea area;//mensajes 
    private JTextArea areaArchivos;//Archivos
    private JTextArea areaPeers;//peers conectados
    private String nombre;
    private HashMap<String, FileData> sharedFiles = new HashMap<>();
    private Set<String> connectedPeers = new HashSet<>();
    private JLabel notificacion;

    public void set_notificacion(JLabel notificacion) {
        this.notificacion = notificacion;
    }
    private Map<String, Integer> tiemposConectados = new HashMap<>();

    public Map<String, Integer> getTiemposConectados() {
        return tiemposConectados;
    }

    public RedP2P(String host, int port, String nombre) {
        try {
            this.socket = new MulticastSocket(port);
            this.host = InetAddress.getByName(host);
            this.port = port;
            this.nombre = nombre;
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Regresar el contenido de un array de bytes
    public class FileData {

        private String fileName;
        private byte[] fileContent;

        public FileData(String fileName, byte[] fileContent) {
            this.fileName = fileName;
            this.fileContent = fileContent;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getFileContent() {
            return fileContent;
        }
    }

    // Método para obtener la lista de archivos compartidos
    public Map<String, FileData> getSharedFiles() {
        return sharedFiles;
    }

    public void set_Area(JTextArea area) {
        this.area = area;
    }

    public void set_AreaArchivos(JTextArea areaArchivos) {
        this.areaArchivos = areaArchivos;
    }

    public void set_AreaPeers(JTextArea areaPeers) {
        this.areaPeers = areaPeers;
    }

    //METODO PARA ENVIAR MENSAJES AL CHAT
    public void enviar_Mensaje(String msj, String nombreRemitente) {
        Thread enviarHilo = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    byte[] mensaje = (nombreRemitente + ": " + msj).getBytes();
                    DatagramPacket paquete = new DatagramPacket(mensaje, mensaje.length, host, port);
                    //msj=msj.nextInt();
                    try {
                        socket.send(paquete);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                       Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        enviarHilo.start();
    }
//METODO PARA CARGAR EL ARCHIVO

    public void runUpload() {
        Thread uploadThread = new Thread(() -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                // Verificar si el archivo seleccionado es válido
                if (selectedFile != null && selectedFile.isFile()) {
                    String fileName = selectedFile.getName();

                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(selectedFile))) {
                        byte[] fileData = new byte[(int) selectedFile.length()];
                        bis.read(fileData);
                        enviarArchivo(fileData, fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Selección de archivo no válida");
                }
            }
        });
        uploadThread.start();
    }

// Variable para controlar si el archivo ha sido mostrado
    private boolean archivoMostrado = false;

//METODO PARA ENVIAR EL ARCHIVO A LA REDP2P
    public void enviarArchivo(byte[] fileData, String fileName) {
        Thread enviarHilo = new Thread(() -> {
            if (!archivoMostrado) {
                archivoMostrado = true; // Marca el archivo como mostrado
                String fileMessage = "[FILE_SHARE]:" + fileName;
                byte[] messageData = fileMessage.getBytes();
                DatagramPacket filePacket = new DatagramPacket(messageData, messageData.length, host, port);

                try {
                    guardarArchivoEnCarpeta(fileName, fileData);
                    socket.send(filePacket);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                // Enviar el contenido del archivo en un segundo mensaje
                DatagramPacket contentPacket = new DatagramPacket(fileData, fileData.length, host, port);
                try {
                    socket.send(contentPacket);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        enviarHilo.start();
        archivoMostrado = false;//Cambiar valor de la bandera para cargar más archivos
    }
//METODO PARA GUARDAR EL ARCHIVO QUE SE ENVIO A LA RED EN LA CARPETA "UPLOAD"

    private void guardarArchivoEnCarpeta(String fileName, byte[] fileData) throws IOException {
        String folderPath = "Upload";
        File folder = new File(folderPath);
        // Crear la carpeta si no existe
        if (!folder.exists()) {
            folder.mkdir();
        }
        // Crear el archivo en la carpeta y escribir los datos
        try (FileOutputStream fos = new FileOutputStream(new File(folder, fileName))) {
            fos.write(fileData);
        }
    }
//METODO PARA SOLICITAR EL PAQUETE

    private void solicitarArchivo(String fileName) {
        // Solicitar el archivo al usuario remoto
        FileData requestedFile = sharedFiles.get(fileName);
        // Llamar a la función para manejar la recepción y descarga del archivo
        descargarArchivo(fileName, requestedFile.getFileContent());        
    }
    
//METODO PARA DESCARGAR EL ARCHIVO EN LA CARPETA "Download"
    private void descargarArchivo(String fileName, byte[] fileContent) {
        String folderPath = "Download";
        File folder = new File(folderPath);

        // Crear la carpeta si no existe
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Crear el archivo en la carpeta y escribir los datos
        try (FileOutputStream fos = new FileOutputStream(new File(folder, fileName))) {
            fos.write(fileContent);
            //System.out.println("Descargado en Download/" + fileName);
            if (notificacion != null) {
                notificacion.setText("Archivo " + fileName + " descargado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //DETECTAR LA CONEXION DEL PEER
    private volatile boolean enviarHoraActivo = true;

    public void enviar_Hora(int hora, String nombreRemitente) {
        Thread enviarHilo = new Thread(() -> {
            while (enviarHoraActivo) {
                byte[] mensaje = ("[HoraPeer]:" + nombre + "-" + hora).getBytes();
                DatagramPacket paquete = new DatagramPacket(mensaje, mensaje.length, host, port);
                tiemposConectados.put(nombreRemitente, hora);
                try {
                    socket.send(paquete);
                } catch (IOException ex) {
                    //ex.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
            }
        });
        enviarHilo.start();
    }
// METODO PARA DETECTAR LA DESCONEXIÓN

    public void detenerEnvioHora() {
        enviarHoraActivo = false;
        // Enviar un mensaje de despedida con el prefijo BYE
        String mensajeDespedida = "[BYE]:" + nombre;
        enviarMensaje(mensajeDespedida);
    }

//METODO PARA OBTENER EL NOMBRE
    public String obtenerNombreDesdeMensaje(String mensaje) {
        // El formato esperado es "[HoraPeer]: nombre - hora"
        String[] partes = mensaje.split("-");
        if (partes.length == 2) {
            // Extraer el nombre
            return partes[0].substring("[HoraPeer]:".length());
        } else {
            return "NombreDesconocido";
        }
    }
//METODO PARA OBTENER LA HORA

    public int obtenerHoraDesdeMensaje(String mensaje) {
        // El formato esperado es "[HoraPeer]: nombre - hora"
        String[] partes = mensaje.split("-");
        if (partes.length == 2) {
            try {
                return Integer.parseInt(partes[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return -1;
        }
    }
//METODO PARA ACTUALIZAR LOS PEERS CONECTADOS

    private void actualizarPeersConectados(String nombrePeer, boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            if (conectado) {
                connectedPeers.add(nombrePeer);
            } else {
                connectedPeers.remove(nombrePeer);
            }
            actualizarInterfazPeersConectados();
        });
    }
//METODO PARA ACTUALIZAR LA INTERFAZ DE PEERS CONECTADOS

    private void actualizarInterfazPeersConectados() {
        SwingUtilities.invokeLater(() -> {
            // Limpiar el área de peers y agregar los nombres de los peers conectados
            areaPeers.setText("");
            for (String peer : connectedPeers) {
                areaPeers.append(peer + "\n");
            }
        });
    }

//METODO PARA ENVIAR MENSAJES
    private void enviarMensaje(String mensaje) {
        try {
            byte[] mensajeBytes = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(mensajeBytes, mensajeBytes.length, host, port);
            socket.send(paquete);
        } catch (IOException ex) {
        }
    }
//METODO PARA LA COMUNICACION DE PEERS

    public void run() {
        try {
            this.socket.joinGroup(host);

            //CLICK EN EL NOMBRE DEL ARCHIVO
            areaArchivos.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        int offset = areaArchivos.viewToModel(e.getPoint());
                        int rowStart = Utilities.getRowStart(areaArchivos, offset);
                        int rowEnd = Utilities.getRowEnd(areaArchivos, offset);
                        String fileName = areaArchivos.getText().substring(rowStart, rowEnd);
                        System.out.println("Archivo seleccionado: " + fileName);
                        // Llamar a la función para buscar el contenido del archivo 
                        solicitarArchivo(fileName);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            while (true) {
                byte[] buffer = new byte[65536];//Archivos de hasta 64KB
                //byte[] buffer = new byte[102400]; // Archivos de hasta 100KB
                //byte[] buffer = new byte[1048576];//Archivos de 1024KB = 1MG
                //byte[] buffer = new byte[2097152];//Archivos de 2048KB = 2MG
                //byte[] buffer = new byte[5242880];//Archivos de 5120KB = 5MG
                //byte[] buffer = new byte[10485760]; //Archivo de 10240KB = 10MB
                
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);       
                this.socket.receive(paquete);
                String mensajeRecibido = new String(paquete.getData(), 0, paquete.getLength());
                 
                //DETECTAR CUANDO UN PEER SE DESCONECTA
                if (mensajeRecibido.startsWith("[BYE]:")) {
                    String nombrePeerDesconectado = mensajeRecibido.substring("[BYE]:".length());
                    actualizarPeersConectados(nombrePeerDesconectado, false);
                    // ESCUCHAR LOS PEERS CONECTADOS
                } else if (mensajeRecibido.startsWith("[HoraPeer]:")) {
                    // Actualizar la lista de peers conectados
                    String nombrePeer = obtenerNombreDesdeMensaje(mensajeRecibido);
                    actualizarPeersConectados(nombrePeer, true);

                } else {
                    // Si el mensaje no comienza con "[HoraPeer]:", asumimos que el peer se desconectó
                    String nombrePeerDesconectado = obtenerNombreDesdeMensaje(mensajeRecibido);
                    actualizarPeersConectados(nombrePeerDesconectado, false);
                }
                // SOLICITUD DE ARCHIVOS
                if (mensajeRecibido.startsWith("[REQUEST_FILE]:")) {
                    String requestedFileName = mensajeRecibido.substring("[REQUEST_FILE]:".length());
                    FileData requestedFile = sharedFiles.get(requestedFileName);
                    // Llamar a la función para manejar la recepción y descarga del archivo
                    descargarArchivo(requestedFileName, requestedFile.getFileContent());
                }//ARCHIVOS COMPARTIDOS 
                else if (mensajeRecibido.startsWith("[FILE_SHARE]:")) {
                    // Recibir el nombre del archivo compartido
                    String sharedFileName = mensajeRecibido.substring("[FILE_SHARE]:".length());

                    // Recibir el contenido del archivo
                    DatagramPacket contentPacket = new DatagramPacket(buffer, buffer.length);
                    this.socket.receive(contentPacket);
                    byte[] fileContent = contentPacket.getData();

                    // Almacenar el archivo compartido en sharedFiles
                    sharedFiles.put(sharedFileName, new FileData(sharedFileName, fileContent));

                    // Mostrar el nombre del archivo en el área de archivos compartidos
                    SwingUtilities.invokeLater(() -> {
                        areaArchivos.append(sharedFileName + "\n");
                    });
                } else if (!mensajeRecibido.startsWith("[HoraPeer]:") && !mensajeRecibido.startsWith("[BYE]:")) {
                    //ENVIAR LOS MENSAJES AL CHAT
                    final String mensajeFinal = mensajeRecibido;
                    SwingUtilities.invokeLater(() -> {
                        area.append(mensajeFinal + "\n");  
                    });
                }
                // Después de procesar el mensaje
              
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
