/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;




import chatv3.HiloReceptor;
import chatv3.Mensaje;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author tarde
 */
public class Cliente {

    public static void main(String[] args) {
        Socket s = null;
        Scanner sc = new Scanner(System.in);
        Mensaje msg = new Mensaje();
        DataOutputStream dos = null;
        HiloReceptor hr = null;

        try {
            s = new Socket("localhost", 6000);
            dos = new DataOutputStream(s.getOutputStream());
            hr = new HiloReceptor(s);
            hr.start();
            do {
                msg.setCuerpo(sc.nextLine());
                if (!(msg.getCuerpo().equals(".exit"))) {
                    
                    dos.writeUTF(msg.getCuerpo());
                    
                }
            } while (!(msg.getCuerpo().equals(".exit")));
        } catch (Exception e) {
            System.out.println("Error en la comunicación");
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException ex) {
                    System.out.println("Error al cerrar el DataOutputStream");
                }
            }

            if (s != null) {
                try {
                    s.close();
                } catch (IOException ex) {
                    System.out.println("Error cerrando el socket");
                }
            }
        }

    }
}
