package uy.edu.um;
import uy.edu.um.doors.ProcessConsole;
import uy.edu.um.doors.ProcessManagerImpl;

//Viky no puede mas con su vida
//Manu Test
public class Main {
    public static void main(String[] args) {

        ProcessConsole pc = new ProcessConsole(new ProcessManagerImpl());
        pc.init();

    }
}