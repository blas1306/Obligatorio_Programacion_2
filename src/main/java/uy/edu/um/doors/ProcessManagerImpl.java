package uy.edu.um.doors;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ProcessManagerImpl implements ProcessManager{

    // TEMPORAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     public ArrayList<Users> usuarios=new ArrayList<Users>();
    public ArrayList<Process> procesosNuevos=new ArrayList<Process>();


    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        loadUsers(usersCsvPath);
        loadProcess(processCsvPath);

    }

    private void loadProcess(String processCsvPath){
        String linea;

        try {
            BufferedReader LectorDeArchivo = new BufferedReader(new FileReader(processCsvPath));//BufferedReader lo que hace es como un intermedio entre file reader y el programa y tiene funciones tipo read line
            LectorDeArchivo.readLine();
            while ((linea=LectorDeArchivo.readLine())!=null){
                ArrayList<Events> eventosTotales=new ArrayList<Events>();
                String[] datos= linea.split(";");//Separa la linea por los punto y comas
                String helper= datos[3].substring(1, datos[3].length() - 1); //le quita { }
                String[] eventos= helper.split("#");
                for (int i =0; i<eventos.length; i++){
                    String[] evento = (eventos[i].split(":"));
                    String sin=evento[1].substring(1, evento[1].length() - 1);
                    ArrayList<String> sub= new ArrayList<>(Arrays.asList(sin.split(", ")));
                    Events ev= new Events(evento[0],sub);
                    eventosTotales.add(ev);
                }




                procesosNuevos.add(new Process( Integer.parseInt(datos[0]),Integer.parseInt(datos[1]),datos[2],eventosTotales));//Integer.parseInt( pasa a int las cosas)
            }
            LectorDeArchivo.close();
        }catch (NumberFormatException | IOException e ){
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void loadUsers(String processCsvPath){
        String linea;

        try {
            BufferedReader LectorDeArchivo = new BufferedReader(new FileReader(processCsvPath));//BufferedReader lo que hace es como un intermedio entre file reader y el programa y tiene funciones tipo read line
            LectorDeArchivo.readLine();
            while ((linea=LectorDeArchivo.readLine())!=null){
                String[] datos= linea.split(";");//Separa la linea por los punto y comas
                usuarios.add(new Users( Integer.parseInt(datos[0]),datos[1],datos[2]));//Integer.parseInt( pasa a int las cosas)
            }
            LectorDeArchivo.close();
        }catch (NumberFormatException | IOException e ){
            System.out.println("Error: " + e.getMessage());
        }

    }




    @Override
    public void prepareProcesses() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void executeNextProcess() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
