/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;




import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author tarde
 */
public class Servidor {

    public static void main(String[] args) {

        ServerSocket sc;
        ObjetoCompartido oc;
        Socket s = null;
        String nick = "";
        HiloServidor hs;

        try {
            sc = new ServerSocket(6000);
            oc = new ObjetoCompartido();
            

            do {

                s = sc.accept();
                hs = new HiloServidor(oc, s, nick);
                hs.start();
            } while (true);
        } catch (Exception e) {
            System.out.println("Se ha producido un error inesperado");
            e.printStackTrace();
        }
    }
}
