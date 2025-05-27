package RedP2P;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

public class Bully implements Runnable {

    private MulticastSocket socket;
    private InetAddress host;
    private int port;

    private int id;
    private boolean Coordinador = false;
    private boolean Elector_lock = false;
    private boolean imp = true;

    private int count = 0;
    private JLabel coor;
    private String mensaje = "";

    public Bully(String host, int port, int id) {
        this.id = id;

        try {
            socket = new MulticastSocket(port);
            this.host = InetAddress.getByName(host);
            socket.joinGroup(this.host);
            this.port = port;
        } catch (IOException ex) {
            Logger.getLogger(Bully.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void set_coor(JLabel coor) {
        this.coor = coor;
    }
    
    private void msg_Coordinador(int id) {
        byte buffer[] = ("Coordinador " + id).getBytes();
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, host, port);
        try {
            socket.send(paquete);
        } catch (IOException ex) {
            Logger.getLogger(Bully.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void msg_Eleccion(int id) {
        byte buffer[] = ("Eleccion " + id).getBytes();
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, host, port);
        try {
            socket.send(paquete);
        } catch (IOException ex) {
            Logger.getLogger(Bully.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        Runnable T_coordinador = new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramPacket paquete;
                    LinkedList pack;
                    socket.setTimeToLive(1);
                    while (true) {
                        if (Coordinador) {
                            //System.out.println("Soy el coordinador " + id);
                            msg_Coordinador(id);
                            mensaje = ("Soy el Coordinador");
                            if (coor != null) {
                                coor.setText(mensaje);
                            }
                        }
                        if (!Coordinador) {
                            try {

                                byte buffer[] = new byte[20];
                                paquete = new DatagramPacket(buffer, buffer.length);
                                socket.setSoTimeout(2000);
                                socket.receive(paquete);

                                pack = to_Split_Datagram(paquete.getData());

                                String msg = String.valueOf(pack.get(0));
                                int id_rec = Integer.parseInt(String.valueOf(pack.get(1)));

                                if (msg.equalsIgnoreCase("Coordinador")) {
                                    Elector_lock = false;
                                    //System.out.println("Coordinador actual es: " + id_rec + " soy: " + id);
                                    mensaje = ("Coordinador: tiene el id " + id_rec /*+ " soy: " + id + "\n"*/);
                                    if (coor != null) {
                                        coor.setText(mensaje);
                                    }

                                }
                                if (msg.equalsIgnoreCase("Eleccion")) {
                                    if (count > 2) { 
                                        Coordinador = true;
                                        //System.out.println("Count: " + count + " en: " + id + " Coordinador: " + Coordinador);
                                    }
                                    if (id > id_rec) {
                                        Elector_lock = true;
                                    }
                                    if (id == id_rec) {
                                        count++;
                                    }
                                }
                            } catch (IOException ex) {

                                if (!Elector_lock) {

                                    //System.out.println("Se envia mensaje eleccion: " + id);
                                    mensaje = ("Coordinador: en elección");
                                    if (coor != null) {
                                        coor.setText(mensaje);
                                    }
                                    msg_Eleccion(id);
                                }
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Bully.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Bully.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(T_coordinador).start();
    }

    private LinkedList to_Split_Datagram(byte[] cad) {
        LinkedList lista = new LinkedList();
        String alpha = "";
        String num = "";
        String cadena = new String(cad);
        for (char c : cadena.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                alpha += c;
            }
            if (Character.isDigit(c)) {
                num += c;
            }
        }
        lista.add(alpha);
        lista.add(Integer.parseInt(num));
        return lista;
    }

}
