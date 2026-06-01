package uy.edu.um;
import uy.edu.um.doors.ProcessConsole;
import uy.edu.um.doors.ProcessManagerImpl;

//Viky no puede mas con su vida, matenme
//Manu Test
//Seba Test, HOLAAA
public class Main {
    public static void main(String[] args) {

        //ProcessConsole pc = new ProcessConsole(new ProcessManagerImpl());
        //pc.init();
        ProcessManagerImpl omi=new ProcessManagerImpl();
        omi.loadProcessAndUserData("process.csv", "users.csv");
        System.out.println(omi.usuarios.toString());
        System.out.println(omi.procesosNuevos);

    }
}