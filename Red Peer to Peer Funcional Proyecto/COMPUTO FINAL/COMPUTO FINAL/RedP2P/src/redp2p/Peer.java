package RedP2P;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Peer extends javax.swing.JFrame {

    private RedP2P peer;
    private String nombre;
    private JTextArea jTextAreaPeers;
    private JButton jButtonUpload;
    private JTextArea jTextAreaSharedFiles;
    private Bully bully;
    
     
    public Peer() {
        initComponents();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String horaInicioFormatted = sdf.format(new Date());
        // Convertir la cadena de hora a un entero
        int horaInicio = Integer.parseInt(horaInicioFormatted);
        nombre = JOptionPane.showInputDialog("Ingresa tu nombre o deja Anonimo " + horaInicio + ":");
        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "Anonimo " + horaInicio;
        } else {
            nombre += " " + horaInicio;
        }
        setTitle("Peer Multicast - " + nombre);
        peer = new RedP2P("224.0.0.4", 5000, nombre);
        peer.set_Area(jTextArea1);
        peer.set_AreaPeers(jTextAreaPeers);
        peer.set_AreaArchivos(jTextAreaSharedFiles);
        
        peer.enviar_Hora(horaInicio, nombre);
        peer.set_notificacion(jLabel5);
        new Thread(peer).start();
        //COORDINADOR
        bully = new Bully("224.0.0.4", 5001, horaInicio);
        bully.set_coor(jLabel4);
        new Thread(bully).start();
        // Añadir un WindowListener para detectar el cierre de la ventana
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarInterfaz();
            }
        });
    }

@SuppressWarnings("unchecked")
private void initComponents() {
    jScrollPane1 = new javax.swing.JScrollPane();
    jTextArea1 = new javax.swing.JTextArea();
    jLabel3 = new javax.swing.JLabel();
    jTextField4 = new javax.swing.JTextField();
    jButton2 = new javax.swing.JButton();
    jMenuBar1 = new javax.swing.JMenuBar();
    jMenu1 = new javax.swing.JMenu();
    jMenuItem1 = new javax.swing.JMenuItem();
    jLabel4 = new javax.swing.JLabel();
    jButtonUpload = new javax.swing.JButton();
    jTextAreaSharedFiles = new javax.swing.JTextArea();
    jTextAreaPeers = new javax.swing.JTextArea();
    jLabel5 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Peer Multicast");

    jTextArea1.setColumns(20);
    jTextArea1.setRows(5);
    jScrollPane1.setViewportView(jTextArea1);

    jLabel3.setText("Msg");

    jButton2.setText("Send");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton2ActionPerformed(evt);
        }
    });

    jMenu1.setText("File");
    jMenuItem1.setText("Salir");
    jMenu1.add(jMenuItem1);
    jMenuBar1.add(jMenu1);
    setJMenuBar(jMenuBar1);

    jButtonUpload.setText("Cargar Archivo");
    jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButtonUploadActionPerformed(evt);
        }
    });
    
    jLabel4.setText("Coordinador");
    // Establecer el color del texto de jLabel4 a morado
    jLabel4.setForeground(new Color(128, 0, 128)); // Cambia el color a morado

    // Establecer el estilo del texto a negritas
    Font font = jLabel4.getFont();
    jLabel4.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));

    jTextAreaSharedFiles.setColumns(20);
    jTextAreaSharedFiles.setRows(5);
    jTextAreaSharedFiles.setBorder(javax.swing.BorderFactory.createTitledBorder("Archivos Compartidos"));
    
    jLabel5.setText("");
    jLabel5.setForeground(new Color(11, 153, 2));
    Font font5 = jLabel5.getFont();
    jLabel5.setFont(new Font(font5.getName(), Font.BOLD, font.getSize()));
     
    jTextAreaPeers.setColumns(20);
    jTextAreaPeers.setRows(5);
    jTextAreaPeers.setEditable(false);
    jTextAreaPeers.setBorder(javax.swing.BorderFactory.createTitledBorder("Peers Conectados"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(23, 23, 23)
                        .addComponent(jTextField4)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(50, 100, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(64, 64, 64)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTextAreaPeers, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 82, Short.MAX_VALUE)
                        .addComponent(jButtonUpload)
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel5) // Agrega jLabel5 aquí
                        .addGap(18, 18, 18)))
                .addComponent(jTextAreaSharedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap()
            )
    );
    layout.setVerticalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTextAreaSharedFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jTextAreaPeers, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createSequentialGroup()
                    .addGap(68, 68, 68)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(jButton2))
                .addGroup(layout.createSequentialGroup()
                    .addGap(50, 50, 50)
                    .addComponent(jLabel4) // Agrega jLabel4 aquí
                    .addGap(18, 18, 18)
                    .addComponent(jButtonUpload)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel5))) // Agrega jLabel5 aquí
            .addContainerGap(25, Short.MAX_VALUE))
);

    pack();
}
   
//AVISAR LA DESCONEXION
private void cerrarInterfaz() {
        peer.detenerEnvioHora();
        System.exit(0); 
    }
//BOTON PARA ENVIAR MENSAJE

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        String Mensaje = jTextField4.getText();
        peer.enviar_Mensaje(Mensaje, nombre);
        jTextField4.setText("");
    }

//BOTON PARA CARGAR EL ARCHIVO
   private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {
        peer.runUpload();
    }
    

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Peer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Peer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Peer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Peer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Peer().setVisible(true);
            }
        });
    }

    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField4;
}
