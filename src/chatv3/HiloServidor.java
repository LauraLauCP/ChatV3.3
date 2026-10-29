/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tarde
 */
public class HiloServidor extends Thread {

    ObjetoCompartido oc;
    Socket s;
    String id;

    public HiloServidor(ObjetoCompartido oc, Socket s, String id) {
        this.oc = oc;
        this.s = s;
        this.id = id;
    }

    

    public void run() {

        DataInputStream dis = null;

        Mensaje msg = new Mensaje();
        Boolean alta = false;
        
        
        do {
            try {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                DataInputStream dis2 = new DataInputStream(s.getInputStream());
                dos.writeUTF("Introduzca Nickname");
                id = dis2.readUTF();
            } catch (Exception e) {

            }
            alta = oc.altaUsuario(this.id, this.s);
            try {
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                
                if (!alta) {
                    dos.writeUTF("Ese nickname ya está dado de alta, por favor introduzca otro");
                } else {
                    oc.canalesPorDefecto(this.id);
                    dos.writeUTF("Inicio de sesión exitoso");
                    dos.writeUTF("Mensaje general -> sin comandos | Mensaje privado -> .private | Crear canal -> .newChannel | Unirse a canal -> .joinChannel | Hablar por canal -> .msgChannel nombre_canal| Lista de canales -> .listChannels | Dejar canal -> .leaveChannel nombre_canal");
                    System.out.println("El usuario "+ this.id+ " ha iniciado sesión");
                }
            } catch (IOException ex) {
                Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (!alta);

        try {
            dis = new DataInputStream(s.getInputStream());
            do {
                msg.setCuerpo(dis.readUTF());
                oc.gestionarMensaje(this.id, msg, this.s);
            } while (true);
        } catch (Exception e) {
            System.out.println("Desconexión del cliente " + this.id);
        } finally {
            oc.bajaUsuario(this.id);
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException ex) {
                    System.out.println("Error cerrando el DataInputStream " + this.id);
                }
                try {
                    s.close();
                } catch (IOException ex) {
                    System.out.println("Error cerrando el socket del cliente" + this.id);
                }
            }

        }
    }
}
