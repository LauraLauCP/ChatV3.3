/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;




import java.io.DataInputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author tarde
 */
public class HiloReceptor extends Thread {

    Socket s;

    public HiloReceptor(Socket s) {
        this.s = s;
    }

    public void run() {
        try (DataInputStream dis = new DataInputStream(this.s.getInputStream());) {
            Mensaje msg = new Mensaje();
            do {
                
                LocalTime horaActual = LocalTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
                msg.setCuerpo(dis.readUTF());
                msg.setHoraEnvio(horaActual.format(formato));
                
                if (msg.getCuerpo().equals("Introduzca Nickname") || msg.getCuerpo().equals("Ese nickname ya está dado de alta, por favor introduzca otro") || msg.getCuerpo().equals("Inicio de seión exitoso")) {
                    System.out.println(msg.getCuerpo());
                } else {
                    System.out.println(msg.getCuerpo() + " " + msg.getHoraEnvio());
                }
            } while (true);
        } catch (Exception e) {

        }
    }
}
