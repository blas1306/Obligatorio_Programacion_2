package uy.edu.um.doors;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jdk.jfr.Event;
import uy.edu.um.Exceptions.ProcessosYaEnSistemas;
import uy.edu.um.Exceptions.UsuarioYaEnSistema;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStackImpl;

public class ProcessManagerImpl implements ProcessManager{


    //Placeholder
    private MyHashImpl userList=new MyHashImpl();

    private MyQueueImpl<Process> newProcesses= new MyQueueImpl<>();
    private MyHeapImpl<Process> pendingProcesses = new MyHeapImpl<>(false);
    Process runningProcess=null;

    private MyStackImpl<Process> finishedProcesses = new MyStackImpl<>();


    public ProcessManagerImpl() {
        clearTodayLog();
    }

    private void clearTodayLog() {
        String fileName = "DOORS_PROCESS_LOG_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (FileWriter writer = new FileWriter(fileName, false)) {
            // Vacía el archivo al iniciar el sistema
        } catch (IOException e) {
            System.out.println("Error limpiando log: " + e.getMessage());
        }
    }

    private void writeLog(String message) {
        String fileName = "DOORS_PROCESS_LOG_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("[" + timestamp + "]: " + message + System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error escribiendo log: " + e.getMessage());
        }
    }

    private void saveFinishedProcess(Process toFinish) throws EmptyStackException {

        if (finishedProcesses.size() < MAX_FINISHED_PROCESS_ON_RAM) {

            finishedProcesses.push(toFinish);

        } else {

            StringBuilder overflowMsg = new StringBuilder();

            while (!finishedProcesses.isEmpty()) {

                Process overflow = finishedProcesses.pop();
                Users user = (Users) userList.get(overflow.getUid());

                overflowMsg.append(
                        "PID=" + overflow.getPid()
                                + " " + overflow.getName()
                                + " | STATE: " + overflow.getFinishedType()
                                + " | USER:" + user.getAlias()
                                + " UID:" + overflow.getUid()
                );
                if (!finishedProcesses.isEmpty()) {
                    overflowMsg.append("\n");
                }
            }

            writeLog("Finished process stack overflow" + "\n" + overflowMsg);

            finishedProcesses.push(toFinish);
        }
    }

    private String processBasic(Process process) {
        Users user = (Users) userList.get(process.getUid());

        return "PID=" + process.getPid()
                + " | " + process.getName()
                + " | USER:" + user.getAlias()
                + " UID:" + user.getUid()
                + " | P=" + process.getPriority();
    }

    private String processFinished(Process process) {
        Users user = (Users) userList.get(process.getUid());

        return "PID=" + process.getPid()
                + " " + process.getName()
                + " | STATE: " + process.getFinishedType()
                + " | USER:" + user.getAlias()
                + " UID:" + user.getUid();
    }

    private void printEvents(Process process) {
        for (Events event : process.getEvents()) {
            System.out.println("EVENT: " + event.getType()
                    + " | Instructions " + event.getInstructions());
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

        if (runningProcess==null) {
            Process toRun = pendingProcesses.remove();
            toRun.setStatus("RUNNING");
            runningProcess= toRun;

            Users user = (Users) userList.get(toRun.getUid());
            StringBuilder events = new StringBuilder();
            for (int i = 0; i < toRun.getEvents().size(); i++) {
                Events temp = toRun.getEvents().get(i);
                events.append("EVENT: " + temp.getType() + "| Instructions " + temp.getInstructions().toString());
                if (i != toRun.getEvents().size() -1) {
                    events.append( "\n");
                }
            }
            writeLog("EXECUTING PROCESS: PID=" + toRun.getPid() + " | USER:" + user.getAlias() + " UID:"+ user.getUid() + "\n" + events);
        } else {
            System.out.println("ERROR, no se puede ejecutar más de un programa a la vez.");
        }
    }

    @Override
    public void finishProcessOk() throws EmptyStackException {
        if (runningProcess!=null) {
            Process toFinish = runningProcess;
            runningProcess=null;

            toFinish.setStatus("FINISHED");
            toFinish.setFinishedType("OK");

            saveFinishedProcess(toFinish);

            writeLog(
                    "ENDING PROCESS: PID=" + toFinish.getPid()
                            + " | STATE: OK"
            );
        } else {
            System.out.println("No hay procesos ejecutándose.");
        }
    }

    @Override
    public void finishProcessError() throws EmptyStackException {
        if (runningProcess==null) {
            System.out.println("No hay procesos ejecutándose.");
            return;
        }

        Process toFinish = runningProcess;
        runningProcess=null;

        toFinish.setStatus("FINISHED");
        toFinish.setFinishedType("ERROR");

        saveFinishedProcess(toFinish);

        writeLog(
                "ENDING PROCESS: PID=" + toFinish.getPid()
                        + " | STATE: ERROR"
        );
    }

    @Override
    public void terminateProcess(int uid) throws EmptyStackException {
        if (runningProcess==null) {
            System.out.println("No hay procesos ejecutándose.");
            return;
        }

        Users killer = (Users) userList.get(uid);

        if (killer == null) {
            System.out.println("No existe un usuario con UID " + uid);
            return;
        }

        Process toFinish = runningProcess;
        runningProcess=null;

        toFinish.setStatus("FINISHED");
        toFinish.setFinishedType("TERMINATED");

        saveFinishedProcess(toFinish);

        writeLog(
                "ENDING PROCESS: PID=" + toFinish.getPid()
                        + " | STATE: TERMINATED"
                        + " by USER:" + killer.getAlias()
                        + " UID:" + killer.getUid()
        );
    }

    @Override
    public void printStatus() {
        System.out.println("PROCESS STATUS");

        System.out.println("EXECUTING:");
        if (runningProcess==null) {
            System.out.println("No hay procesos ejecutándose.");
        } else {
            System.out.println(processBasic(runningProcess));
        }

        System.out.println("PENDING:");
        if (pendingProcesses.isEmpty()) {
            System.out.println("No hay procesos pendientes.");
        } else {
            MyHeapImpl<Process> auxHeap = new MyHeapImpl<>(false);

            while (!pendingProcesses.isEmpty()) {
                Process process = pendingProcesses.remove();
                System.out.println(processBasic(process));
                auxHeap.insert(process);
            }

            while (!auxHeap.isEmpty()) {
                pendingProcesses.insert(auxHeap.remove());
            }
        }

        System.out.println("FINISHED:");
        if (finishedProcesses.isEmpty()) {
            System.out.println("No hay procesos finalizados.");
        } else {
            for (int i = finishedProcesses.size() - 1; i >= 0; i--) {
                System.out.println(processFinished(finishedProcesses.get(i)));
            }
        }
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("PROCESS STATUS - VERBOSE");

        System.out.println("EXECUTING:");
        if (runningProcess==null) {
            System.out.println("No hay procesos ejecutándose.");
        } else {
            Process process = runningProcess;
            System.out.println(processBasic(process));
            printEvents(process);
        }

        System.out.println("PENDING:");
        if (pendingProcesses.isEmpty()) {
            System.out.println("No hay procesos pendientes.");
        } else {
            MyHeapImpl<Process> auxHeap = new MyHeapImpl<>(false);

            while (!pendingProcesses.isEmpty()) {
                Process process = pendingProcesses.remove();
                System.out.println(processBasic(process));
                printEvents(process);
                auxHeap.insert(process);
            }

            while (!auxHeap.isEmpty()) {
                pendingProcesses.insert(auxHeap.remove());
            }
        }

        System.out.println("FINISHED:");
        if (finishedProcesses.isEmpty()) {
            System.out.println("No hay procesos finalizados.");
        } else {
            for (int i = finishedProcesses.size() - 1; i >= 0; i--) {
                Process process = finishedProcesses.get(i);
                System.out.println(processFinished(process));
                printEvents(process);
            }
        }
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("PROCESS STATUS - By User");

        if (!(runningProcess==null)) {

            Process process = runningProcess;
            if (process.getUid()==uid){
            System.out.println("Executing: " + processBasic(process));
            }
        }



        if (!pendingProcesses.isEmpty()) {
            MyHeapImpl<Process> auxHeap = new MyHeapImpl<>(false);

            while (!pendingProcesses.isEmpty()) {
                Process process = pendingProcesses.remove();
                if(process.getUid()==uid){
                System.out.println("Pending: " + processBasic(process));
                }
                auxHeap.insert(process);
            }
            while (!auxHeap.isEmpty()) {
                pendingProcesses.insert(auxHeap.remove());
            }
        }

       //¿ESTO NO ESTÁ INVERTIDO?
        if (!finishedProcesses.isEmpty()) {
            for (int i = finishedProcesses.size() - 1; i >= 0; i--) {
                Process process = finishedProcesses.get(i);
                if (process.getUid()==uid){
                System.out.println("Finished: " + processFinished(process));
                }
            }
        }
        System.out.println("Work in progress");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("PROCESS STATUS - By Process");

        Process found = null;

        // Busca en el proceso en ejecución
        if (runningProcess != null && runningProcess.getPid() == pid) {
            found = runningProcess;
        }

        // Busca los pendientes
        if (found == null && !pendingProcesses.isEmpty()) {
            MyHeapImpl<Process> auxHeap = new MyHeapImpl<>(false);

            while (!pendingProcesses.isEmpty()) {
                Process process = pendingProcesses.remove();

                if (process.getPid() == pid) {
                    found = process;
                }

                auxHeap.insert(process);
            }

            // Restaura el heap
            while (!auxHeap.isEmpty()) {
                pendingProcesses.insert(auxHeap.remove());
            }
        }

        // Busca los finalizados
        if (found == null && !finishedProcesses.isEmpty()) {
            for (int i = finishedProcesses.size() - 1; i >= 0; i--) {
                Process process = finishedProcesses.get(i);

                if (process.getPid() == pid) {
                    found = process;
                    break;
                }
            }
        }

        // Muestra la información
        if (found == null) {
            System.out.println("Process not found.");
            return;
        }

        // Información completa del proceso
        System.out.println(processFinished(found));

        // Eventos asociados
        System.out.println("EVENTS:");
        for (Events event : found.getEvents()) {
            System.out.println(event);
        }

    }

}
