package uy.edu.um;
import uy.edu.um.doors.ProcessConsole;
import uy.edu.um.doors.ProcessManagerImpl;

//Viky no puede mas con su vida, matenme
//Manu Test
//Seba Test, HOLAAA
public class Main {
    public static void main(String[] args) {

       // ProcessConsole pc = new ProcessConsole(new ProcessManagerImpl());
        //pc.init();

        ProcessManagerImpl helper= new ProcessManagerImpl();
        helper.loadProcessAndUserData("process.csv","users.csv");
        try {
            helper.prepareProcesses();
        }catch (Exception e){

        }
        helper.printStatusByUser(25);

    }
}