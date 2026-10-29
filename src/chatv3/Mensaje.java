/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatv3;

/**
 *
 * @author L_199
 */
public class Mensaje {
    private String cuerpo;
    private String horaEnvio;

    public Mensaje() {
    }
    
    

    public Mensaje(String cuerpo, String horaEnvio) {
        this.cuerpo = cuerpo;
        this.horaEnvio= horaEnvio;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getHoraEnvio() {
        return horaEnvio;
    }

    public void setHoraEnvio(String horaEnvio) {
        this.horaEnvio = horaEnvio;
    }
    
    
    
}
