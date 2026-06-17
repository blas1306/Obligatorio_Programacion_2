package uy.edu.um.ourTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uy.edu.um.doors.ProcessManagerImpl;

import static org.junit.jupiter.api.Assertions.*;

class ProcessManagerImplTest {

    private ProcessManagerImpl manager;


    private final String USERS_PATH = "users.csv";
    private final String PROCESSES_PATH = "process.csv";


    private final int UID_EXISTENTE = 87;
    private final int PID_EXISTENTE = 35331;

    @BeforeEach
    void setUp() {
        manager = new ProcessManagerImpl();
    }

    @Test
    void loadProcessAndUserData() {
        assertDoesNotThrow(() ->
                manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH)
        );

        assertTrue(manager.getNewProcessesCount() > 0);
        assertEquals(0, manager.getPendingProcessesCount());
        assertNull(manager.getRunningProcess());
        assertEquals(0, manager.getFinishedProcessesCount());
    }

    @Test
    void prepareProcesses() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);

        int numPrev = manager.getNewProcessesCount();

        assertDoesNotThrow(() -> manager.prepareProcesses());

        assertEquals(0, manager.getNewProcessesCount());
        assertEquals(numPrev, manager.getPendingProcessesCount());
        assertNull(manager.getRunningProcess());
    }

    @Test
    void executeNextProcess() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());

        int numPrev = manager.getPendingProcessesCount();

        assertDoesNotThrow(() -> manager.executeNextProcess());

        assertNotNull(manager.getRunningProcess());
        assertEquals("RUNNING", manager.getRunningProcess().getStatus());
        assertEquals(numPrev - 1, manager.getPendingProcessesCount());
    }

    @Test
    void finishProcessOk() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());

        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.finishProcessOk());

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCount());

        assertNotNull(manager.getLastFinishedProcess());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("OK", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void finishProcessError() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());

        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.finishProcessError());

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCount());

        assertNotNull(manager.getLastFinishedProcess());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("ERROR", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void terminateProcess() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.executeNextProcess());

        int pid = manager.getRunningProcess().getPid();

        assertDoesNotThrow(() -> manager.terminateProcess(UID_EXISTENTE));

        assertNull(manager.getRunningProcess());
        assertEquals(1, manager.getFinishedProcessesCount());
        assertNotNull(manager.getLastFinishedProcess());
        assertEquals(pid, manager.getLastFinishedProcess().getPid());
        assertEquals("FINISHED", manager.getLastFinishedProcess().getStatus());
        assertEquals("TERMINATED", manager.getLastFinishedProcess().getFinishedType());
    }

    @Test
    void printStatus() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatus());
    }

    @Test
    void printStatusVerbose() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusVerbose());
    }

    @Test
    void printStatusByUser() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusByUser(UID_EXISTENTE));
    }

    @Test
    void printStatusByProcess() {
        manager.loadProcessAndUserData(PROCESSES_PATH, USERS_PATH);
        assertDoesNotThrow(() -> manager.prepareProcesses());
        assertDoesNotThrow(() -> manager.printStatusByProcess(PID_EXISTENTE));
    }
}