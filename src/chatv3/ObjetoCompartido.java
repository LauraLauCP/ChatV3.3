/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author tarde
 */
public class ObjetoCompartido {

    HashMap<String, Socket> general;
    HashMap<String, ArrayList<String>> canales;

    public ObjetoCompartido() {
        this.general = new HashMap<>();
        this.canales = new HashMap<>();
        this.canales.put("cocina", new ArrayList<String>());
        this.canales.put("deportes", new ArrayList<String>());
        this.canales.put("juegos", new ArrayList<String>());

    }

    public void canalesPorDefecto(String id) {
        this.canales.get("cocina").add(id);
        this.canales.get("deportes").add(id);
        this.canales.get("juegos").add(id);
    }

    public synchronized boolean altaUsuario(String id, Socket s) {
        for (HashMap.Entry<String, Socket> entrada : this.general.entrySet()) {
            String clave = entrada.getKey();
            if (id.equals(clave)) {
                return false;
            }
        }
        this.general.put(id, s);
        return true;

    }

    public synchronized void bajaUsuario(String id) {
        this.general.remove(id);
    }

    public void gestionarMensaje(String id, Mensaje msg, Socket s) {

        if (msg.getCuerpo().startsWith(".private")) {
            mensajePrivado(msg, id);
        } else if (msg.getCuerpo().startsWith(".newChannel")) {
            crearCanal(msg, id);

        } else if (msg.getCuerpo().startsWith(".joinChannel")) {
            unirseCanal(msg, id);
        } else if (msg.getCuerpo().startsWith(".msgChannel")) {
            mensajeCanal(msg, id);
        } else if (msg.getCuerpo().startsWith(".leaveChannel")) {
            dejarCanal(msg, id);
        } else if (msg.getCuerpo().startsWith(".listChannels")) {
            listarCanales(msg,id);
        } else {
            mensajeGeneral(msg, id);
        }

    }

    public void mensajeGeneral(Mensaje msg, String id) {
        for (HashMap.Entry<String, Socket> entrada : this.general.entrySet()) {
            String clave = entrada.getKey();
            Socket valor = entrada.getValue();
            if (!clave.equals(id)) {
                try {
                    DataOutputStream dos = new DataOutputStream(valor.getOutputStream());  
                    dos.writeUTF(id + "> " + msg.getCuerpo());
                } catch (Exception e) {
                    System.out.println("Error al enviar el mensaje");
                }
            }
        }
    }

    public void mensajePrivado(Mensaje msg, String id) {
        String[] mensaje = msg.getCuerpo().split(" ", 3);
        String[] mens = msg.getCuerpo().split(" ", 3);
        if (mens.length < 3) {
            autoMensaje("Escriba en el siguiente formato: .private nickname_destinatario mensaje", id);

        } else {
            String nickname = mensaje[1];
            for (HashMap.Entry<String, Socket> entrada : this.general.entrySet()) {
                String clave = entrada.getKey();
                Socket valor = entrada.getValue();
                if (clave.equals(nickname)) {
                    try {
                        DataOutputStream dos = new DataOutputStream(valor.getOutputStream()); 
                        dos.writeUTF("Mensaje privado de " + id + "> " + mensaje[2]);
                    } catch (Exception e) {
                        System.out.println("Error al enviar el mensaje");

                    }

                }
            }
        }
    }

    public synchronized void crearCanal(Mensaje msg, String id) {
        String[] mens = msg.getCuerpo().split(" ", 3);
        String nomCanal = mens[1];
        boolean exist = false;

        for (HashMap.Entry<String, ArrayList<String>> entr : this.canales.entrySet()) {
            String clav = entr.getKey();

            if (clav.equals(nomCanal)) {
                exist = true;
            }
        }
        if (!exist) {
            ArrayList<String> miembros = new ArrayList<>();
            miembros.add(id);

            canales.put(nomCanal, miembros);

            System.out.println("canal " + nomCanal + " creado con éxito");

            System.out.println(id + " ha creado y se ha unido al canal " + nomCanal);
            String mensaje = "Has creado y te has unido al canal " + nomCanal;
            autoMensaje(mensaje, id);

        } else {

            for (HashMap.Entry<String, Socket> entr : this.general.entrySet()) {
                Socket valor = entr.getValue();
                if (entr.getKey().equals(id)) {

                    try {
                        DataOutputStream dos = new DataOutputStream(valor.getOutputStream()); 
                        dos.writeUTF("Ese canal ya existe");
                    } catch (Exception e) {
                        System.out.println("Error al enviar el mensaje");

                    }

                }
            }
        }
    }

    public synchronized void unirseCanal(Mensaje msg, String id) {
        String[] mens = msg.getCuerpo().split(" ", 2);
        if (mens.length < 2) {
            autoMensaje("Escriba en el siguiente formato: .joinChannel nombre_canal", id);

        } else {
            String nomCanal = mens[1];
            boolean exist = false;
            boolean pertenecer = false;
            ArrayList<String> miembros = canales.get(nomCanal);
            for (HashMap.Entry<String, ArrayList<String>> entr : this.canales.entrySet()) {
                String clav = entr.getKey();

                if (clav.equals(nomCanal)) {
                    exist = true;

                    for (String miembro : miembros) {
                        if (miembro.equals(id)) {
                            pertenecer = true;
                        }
                    }

                }

            }
            if (pertenecer && exist) {
                String mensaje = "Ya perteneces al canal " + nomCanal;
                autoMensaje(mensaje, id);

            }
            if (!pertenecer && exist) {

                miembros.add(id);
                System.out.println(id + " se ha unido al canal " + nomCanal);
                String mensaje = "Te has unido al canal " + nomCanal;
                autoMensaje(mensaje, id);
                return;
            }

            if (!pertenecer && !exist) {

                String mensaje = "Ese canal no existe";
                autoMensaje(mensaje, id);

            }
        }
    }

    public void mensajeCanal(Mensaje msg, String id) {
        String[] mens = msg.getCuerpo().split(" ", 3);
        if (mens.length < 3) {
            autoMensaje("Escriba en el siguiente formato: .msgChannel nombre_canal mensaje", id);
            return;
        }

        String nomCanal = mens[1];
        String texto = mens[2];
        boolean exist = canales.containsKey(nomCanal);

        if (!exist) {
            String mensaje = "Ese canal no existe";
            autoMensaje(mensaje, id);
            return;
        }

        ArrayList<String> miembros = canales.get(nomCanal);
        boolean pertenecer = miembros.contains(id);

        if (!pertenecer) {
            String mensaje = "No estas en el canal " + nomCanal;
            autoMensaje(mensaje, id);
            return;
        }

        
        for (String miembro : miembros) {
            if (!miembro.equals(id)) {
                Socket socketMiembro = this.general.get(miembro);
                try {
                    
                    DataOutputStream dos
                            = new DataOutputStream(socketMiembro.getOutputStream());
                    dos.writeUTF(nomCanal + ">" + id + ": " + texto);
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje al canal");
                }
            }
        }
    }

    public synchronized void dejarCanal(Mensaje msg, String id) {
        String[] mens = msg.getCuerpo().split(" ", 2);
        if (mens.length < 2) {
            autoMensaje("Escriba en el siguiente formato: .joinChannel nombre_canal", id);

        } else {
            String nomCanal = mens[1];
            boolean exist = false;
            boolean pertenecer = false;
            ArrayList<String> miembros = canales.get(nomCanal);
            for (HashMap.Entry<String, ArrayList<String>> entr : this.canales.entrySet()) {
                String clav = entr.getKey();

                if (clav.equals(nomCanal)) {
                    exist = true;

                    for (String miembro : miembros) {
                        if (miembro.equals(id)) {
                            pertenecer = true;
                        }
                    }

                }

            }
            if (!pertenecer && exist) {
                String mensaje = "No perteneces al canal " + nomCanal;
                autoMensaje(mensaje, id);

            }
            if (pertenecer && exist) {

                miembros.remove(id);
                System.out.println(id + " ha dejado el canal " + nomCanal);
                String mensaje = "Has dejado el canal " + nomCanal;
                autoMensaje(mensaje, id);
                return;
            }

            if (!pertenecer && !exist) {

                String mensaje = "Ese canal no existe";
                autoMensaje(mensaje, id);

            }
        }

    }
    
    public void listarCanales(Mensaje msg, String id) {
        String canal="";
        for (HashMap.Entry<String, ArrayList<String>> entr : this.canales.entrySet()) {
            canal+=entr.getKey()+" | ";
            
        }
        autoMensaje(canal,id);
    }

    public void autoMensaje(String mensaje, String id) {
        for (HashMap.Entry<String, Socket> entr : this.general.entrySet()) {
            
            Socket valor = entr.getValue();
            if (entr.getKey().equals(id)) {

                try {
                    DataOutputStream dos = new DataOutputStream(valor.getOutputStream()); 
                    dos.writeUTF(mensaje);
                } catch (Exception e) {
                    System.out.println("Error al enviar el mensaje");

                }

            }
        }
    }

}
