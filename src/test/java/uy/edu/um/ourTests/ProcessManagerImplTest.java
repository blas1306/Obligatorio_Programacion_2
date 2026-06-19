package uy.edu.um.ourTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uy.edu.um.doors.Process;
import uy.edu.um.doors.ProcessManagerImpl;

import static org.junit.jupiter.api.Assertions.*;

class ProcessManagerImplTest {

    private ProcessManagerImpl manager;


    private final String UsersPath = "users.csv";
    private final String ProPath = "process.csv";


    @BeforeEach
    void setUp() {
        manager = new ProcessManagerImpl();
        manager.loadProcessAndUserData(ProPath, UsersPath);
    }

    @Test
    void loadProcessAndUserData() {
        assertDoesNotThrow(() ->
                manager.loadProcessAndUserData(ProPath, UsersPath)
        );//q no tire error

        // q todo tenga el tamaño correcto osea los nuevos mayores a cero (no ponemos el número por si lo cambian) y el resto tiene q quedar en cero , no se pq queda en verde
        assertTrue(manager.getNewProcessesCant() > 0);
        assertEquals(0, manager.getPendingProcessesCant());
        assertNull(manager.getRunningProcess());
        assertEquals(0, manager.getFinishedProcessesCant());
    }

    @Test
    void prepareProcesses() {

        int numPrev = manager.getNewProcessesCant();//se fija cuantos había antes

        assertDoesNotThrow(() -> manager.prepareProcesses());//q no tire error

        assertEquals(0, manager.getNewProcessesCant());// q ahora te quede en cero
        assertEquals(numPrev, manager.getPendingProcessesCant());//que no se pierda ninguno
        assertNull(manager.getRunningProcess());//que running processes sea vacío
        assertEquals(0, manager.getFinishedProcessesCant());//que los finalizados sean cero
    }

    @Test
    void executeNextProcess() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        Process Last = manager.getPendingProcessesLast();

        int numPrev = manager.getPendingProcessesCant();

        assertDoesNotThrow(() -> manager.executeNextProcess());

        assertEquals(Last,manager.getRunningProcess());
        assertEquals("RUNNING", manager.getRunningProcess().getStatus());
        assertEquals(numPrev - 1, manager.getPendingProcessesCant());
        assertTrue(manager.getPendingProcessesLast()!=Last);
    }

    @Test
    void finishProcessOk() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());


        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.finishProcessOk());

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCant());

        assertNotNull(manager.getLastFinishedProcess());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("OK", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void finishProcessError() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());

        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.finishProcessError());

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCant());

        assertNotNull(manager.getLastFinishedProcess());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("ERROR", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void terminateProcess() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());

        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.terminateProcess(87));

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCant());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("TERMINATED", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void printStatus() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatus());
    }

    @Test
    void printStatusVerbose() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusVerbose());
    }

    @Test
    void printStatusByUser() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusByUser(87));
    }

    @Test
    void printStatusByProcess() {
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusByProcess(35331));
    }
}