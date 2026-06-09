package uy.edu.um.doors;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import uy.edu.um.Exceptions.ProcessosYaEnSistemas;
import uy.edu.um.Exceptions.UsuarioYaEnSistema;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.MyStackImpl;

public class ProcessManagerImpl implements ProcessManager{


    //Placeholder
    private MyHashImpl userList=new MyHashImpl();

    private MyQueueImpl<Process> newProcesses= new MyQueueImpl<>();
    private MyHeapImpl<Process> pendingProcesses = new MyHeapImpl<>(false);
    private MyStackImpl<Process> runningProcess = new MyStackImpl<>();
    private MyStackImpl finishedProcesses;

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

    private void writeLog(String message) {
        String fileName = "DOORS_PROCESS_LOG_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("[" + timestamp + "]: " + message + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error escribiendo log: " + e.getMessage());
        }
    }

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



                Process nuevoProcess=new Process( Integer.parseInt(datos[0]),Integer.parseInt(datos[1]),datos[2],eventosTotales);
                if (newProcesses.contains(nuevoProcess)){
                    throw new ProcessosYaEnSistemas("Proceso  ya en sistema");
                }
                newProcesses.enqueue(nuevoProcess);//Integer.parseInt( pasa a int las cosas)
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
                Users usuarioNuevo=new Users( Integer.parseInt(datos[0]),datos[1],datos[2]);
                if (userList.contains(datos[0])){
                    throw new UsuarioYaEnSistema("Usuario ya en sistema");
                }
                userList.put(Integer.parseInt(datos[0]),usuarioNuevo);//Integer.parseInt( pasa a int las cosas)
            }
            LectorDeArchivo.close();
        }catch (NumberFormatException | IOException e ){
            System.out.println("Error: " + e.getMessage());
        }

    }


    @Override
    public void prepareProcesses() throws EmptyQueueException {
        int cantidad = newProcesses.size();

        for (int i = 0; i < cantidad; i++) {
            Process toPrepare = newProcesses.dequeue();
            int cpuEvents = 0;
            int ramEvents = 0;
            int diskEvents = 0;
            for (int j = 0; j < toPrepare.getEvents().size(); j++) {
                if (Objects.equals(toPrepare.getEvents().get(j).getType(), "CPU")) {
                    cpuEvents++;
                } else if (Objects.equals(toPrepare.getEvents().get(j).getType(), "RAM")) {
                    ramEvents++;
                } else {
                    diskEvents++;
                }
            }

            int prioUser;
            Users user = (Users) userList.get(toPrepare.getUid());
            if (Objects.equals(user.getType(), "ADMIN")) {
                prioUser = 32;
            } else {
                prioUser = 16;
            }

            int priority = (8*cpuEvents + 2*ramEvents + 2*diskEvents) / toPrepare.getEvents().size() + prioUser * toPrepare.getEvents().size();
            toPrepare.setPriority(priority);
            toPrepare.setStatus("PENDING");

            writeLog(
                    "NEW PENDING PROCESS: PID=" + toPrepare.getPid()
                            + " | " + toPrepare.getName()
                            + " | USER:" + user.getAlias()
                            + " UID:" + user.getUid()
                            + " | P=" + toPrepare.getPriority()
            );

            pendingProcesses.insert(toPrepare);
        }
    }

    @Override
    public void executeNextProcess() {

        if (runningProcess.isEmpty()) {
            Process toRun = pendingProcesses.remove();
            toRun.setStatus("RUNNING");
            runningProcess.push(toRun);
            //Implementar log
        } else {
            System.out.println("ERROR, no se puede ejecutar más de un programa a la vez.");
        }
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
