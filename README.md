# CPU Scheduling Simulation Project

**Course:** COSC 439: Operating Systems
**Project:** CPU Scheduling Simulation
**Language:** Java

---

## Table of Contents
1. [Introduction to the Project](#introduction-to-the-project)
2. [Scheduling Algorithms Used](#scheduling-algorithms-used)
3. [Implementation Details](#implementation-details)
4. [Study of the Results](#study-of-the-results)
5. [Conclusion](#conclusion)
6. [How to Run](#how-to-run)

---

## Introduction to the Project

### Purpose
The purpose of this project is to develop a comprehensive CPU scheduling simulation program that demonstrates the behavior and performance characteristics of different scheduling algorithms used in modern operating systems. This simulation helps illustrate how various scheduling strategies affect process execution, waiting times, and overall system efficiency.

### Overview
This project implements a discrete-time CPU scheduling simulator that models the execution of processes under different scheduling policies. The simulator accounts for:
- Process arrival times
- CPU burst times
- Process priorities
- Input/Output (I/O) operations and their timing
- Context switching between processes

The implementation simulates four scheduling algorithms:
1. **First-Come First-Served (FCFS)**
2. **Shortest Job First (SJF)**
3. **Priority Scheduling**
4. **Round-Robin (RR)**

### Scope
The simulation is designed to handle a minimum of 20 processes, though the current implementation can scale to any number of processes. Each process can be configured with:

- **Arrival Time**: When the process enters the ready queue
- **Burst Time**: Total CPU time required for completion
- **Priority**: Numerical priority value (lower number = higher priority)
- **I/O Start Time**: When the process requests I/O (relative to its CPU execution start)
- **I/O Duration**: How long the I/O operation takes

### Process Attributes
Each process in the simulation has the following attributes:

**Static Attributes** (defined at creation):
- `pid` - Process identifier
- `arrivalTime` - Time when process arrives in the system
- `burstTime` - Total CPU time required
- `priority` - Priority level (1 = highest priority)
- `ioStartTime` - CPU time units before requesting I/O
- `ioDuration` - Length of I/O operation

**Dynamic Attributes** (tracked during simulation):
- `remainingTime` - CPU time left to complete
- `cpuTimeExecuted` - Total CPU time used so far
- `completionTime` - Time when process finishes
- `turnaroundTime` - Total time from arrival to completion
- `readyQueueWaitingTime` - Time spent waiting in ready queue
- `ioWaitingTime` - Time spent waiting in I/O queue

### Measurable Metrics
The simulation tracks and reports the following performance metrics:

1. **Completion Time**: The time at which a process finishes execution
2. **Turnaround Time**: `completionTime - arrivalTime` (total time in system)
3. **Waiting Time (Ready Queue)**: Time spent waiting for CPU in the ready queue
4. **Waiting Time (I/O Queue)**: Time spent waiting for I/O device
5. **Average Turnaround Time**: Mean turnaround time across all processes
6. **Average Waiting Time**: Mean waiting time in ready queue across all processes

These metrics allow for quantitative comparison of scheduling algorithm performance and efficiency.

---

## Scheduling Algorithms Used

### 1. First-Come First-Served (FCFS)

**Description:**
FCFS is the simplest CPU scheduling algorithm where processes are executed in the order they arrive in the ready queue. It operates on a first-in-first-out (FIFO) basis.

**Implementation Details:**
- Uses a FIFO queue (`ArrayDeque`) for the ready queue
- No preemption - once a process starts, it runs until completion or I/O request
- When CPU is idle, the first process in the ready queue is selected
- Processes arriving at the same time are handled in the order they appear in the process list

**Characteristics:**
- **Advantages**: Simple to implement, fair in terms of arrival order, no starvation
- **Disadvantages**: Poor average waiting time, convoy effect (short processes wait for long ones)
- **Preemptive**: No

### 2. Shortest Job First (SJF)

**Description:**
SJF selects the process with the smallest remaining CPU time. This algorithm minimizes average waiting time for a given set of processes.

**Implementation Details:**
- Uses a dynamic list to allow selection by remaining time
- Selection strategy: `min(Comparator.comparingInt(p -> p.remainingTime))`
- Each time CPU becomes idle, scans ready queue for process with shortest remaining time
- Non-preemptive - once selected, process runs until completion or I/O

**Characteristics:**
- **Advantages**: Optimal average waiting time, efficient for batch systems
- **Disadvantages**: Starvation possible for long processes, requires knowledge of burst times
- **Preemptive**: No (this implementation is non-preemptive SJF)

### 3. Priority Scheduling

**Description:**
Each process is assigned a priority, and the CPU is allocated to the process with the highest priority (lowest priority number in this implementation).

**Implementation Details:**
- Uses a dynamic list for priority-based selection
- Selection strategy: `min(Comparator.comparingInt(p -> p.priority))`
- Priority value 1 = highest priority, higher numbers = lower priority
- Non-preemptive - process runs until completion or I/O once started

**Characteristics:**
- **Advantages**: Flexible, can prioritize important processes, models real-world systems
- **Disadvantages**: Starvation possible for low-priority processes, priority inversion issues
- **Preemptive**: No (this is non-preemptive priority scheduling)

### 4. Round-Robin (RR)

**Description:**
Round-Robin is a preemptive scheduling algorithm designed for time-sharing systems. Each process gets a small unit of CPU time (quantum), after which it is preempted and moved to the end of the ready queue.

**Implementation Details:**
- Uses FIFO queue for fair round-robin ordering
- Configurable time quantum (default: 2 time units in demo)
- Tracks `quantumRemaining` for current process
- Preemption occurs when quantum expires, even if process not complete
- Process returns to end of ready queue after preemption

**Characteristics:**
- **Advantages**: Fair CPU distribution, good response time, no starvation
- **Disadvantages**: Higher average waiting time than SJF, performance depends on quantum size
- **Preemptive**: Yes

---

## Implementation Details

### Technical Architecture

The project is structured into five main Java classes:

1. **Process.java** - Represents a single process with all attributes
2. **CPUSchedulingSimulator.java** - Contains all four scheduling algorithm implementations
3. **SimulationUtils.java** - Utility methods for copying processes and printing results
4. **ProcessGenerator.java** - Helper for generating test process sets
5. **Main.java** - Driver program to run simulations

### Core Simulation Logic

Each scheduling algorithm follows a similar discrete-time simulation pattern:

```
Initialize time = 0, completed = 0
While (completed < total processes):
    1. Handle process arrivals at current time
    2. Start I/O operation if I/O queue has waiting processes
    3. Increment waiting times for processes in queues
    4. Select next process for CPU (algorithm-specific)
    5. Execute CPU work (decrement remaining time)
    6. Check for I/O request and move to I/O queue if needed
    7. Execute I/O work (decrement I/O remaining time)
    8. Check for process completion or preemption
    9. Increment time
```

### I/O Handling

The simulator includes sophisticated I/O support:

- **I/O Request Timing**: Each process can specify when it needs I/O (`ioStartTime`)
- **Concurrent I/O**: I/O operations run concurrently with CPU execution
- **I/O Queue**: Separate queue manages processes waiting for I/O
- **Single I/O Device**: Simulates one I/O device (realistic for disk operations)
- **Return to Ready**: After I/O completes, process returns to ready queue

### Deep Copy Mechanism

To enable running multiple simulations on the same process set, the implementation uses a deep copy mechanism:

```java
public static List<Process> deepCopyProcesses(List<Process> original) {
    List<Process> copy = new ArrayList<>();
    for (Process p : original) {
        copy.add(p.copyForSimulation());
    }
    return copy;
}
```

This ensures each simulation starts with fresh process state while preserving the original process definitions.

### Development Insights

**Key Design Decisions:**

1. **Separation of Static and Dynamic State**: Process attributes are divided into immutable static fields (arrival time, burst time) and mutable dynamic fields (remaining time, waiting time). This allows clean reset and reuse of processes.

2. **Queue vs. List Selection**: FCFS and Round-Robin use `Queue` (FIFO), while SJF and Priority use `List` to enable priority-based selection with stream operations.

3. **Time-Driven Simulation**: The discrete-time approach (incrementing time by 1 each iteration) provides clarity and correctness, though it may be less efficient than event-driven simulation for sparse workloads.

4. **I/O Architecture**: The single I/O device model with separate I/O queue accurately reflects many real systems where a single disk or network interface is shared.

**Challenges Faced:**

1. **I/O Timing Complexity**: Correctly handling the transition from CPU → I/O → Ready Queue required careful state management. The `hasDoneIO` flag prevents processes from requesting I/O multiple times.

2. **Preemption in Round-Robin**: Ensuring quantum-based preemption didn't interfere with I/O transitions or completion detection required careful ordering of checks.

3. **Waiting Time Calculation**: Distinguishing between ready queue waiting time and I/O queue waiting time required incrementing counters for all processes in respective queues at each time step.

4. **Import Statement Issues**: Initial compilation failed due to missing imports (`ArrayList`, `Comparator`), which was resolved by adding proper import statements.

---

## Study of the Results

### Test Case
The following test case was used to evaluate all four algorithms:

| PID | Arrival | Burst | Priority | I/O Start | I/O Duration |
|-----|---------|-------|----------|-----------|--------------|
| P1  | 0       | 10    | 1        | 2         | 3            |
| P2  | 2       | 6     | 1        | 1         | 2            |
| P3  | 4       | 8     | 1        | 3         | 1            |

### FCFS Results

```
PID   Arrive  Burst   Prio   IOstart  IOdur   Complete   Turnaround   Wait(ready)  Wait(IO)
P1    0       10      1      2        3       15         15           4            0
P2    2       6       1      1        2       20         18           10           2
P3    4       8       1      3        1       25         21           14           0

Average Turnaround Time: 18.00
Average Waiting Time (ready queue): 9.33
```

**Analysis:**
- P1 starts immediately at time 0
- P1 requests I/O at time 2, completes at time 15
- P2 and P3 experience significant waiting due to FCFS ordering
- Convoy effect visible: shorter processes wait for longer ones
- Total completion time: 25 units

### SJF Results

```
PID   Arrive  Burst   Prio   IOstart  IOdur   Complete   Turnaround   Wait(ready)  Wait(IO)
P1    0       10      1      2        3       25         25           14           0
P2    2       6       1      1        2       12         10           2            2
P3    4       8       1      3        1       17         13           6            0

Average Turnaround Time: 16.00
Average Waiting Time (ready queue): 7.33
```

**Analysis:**
- P1 starts first (only process available)
- When P1 goes to I/O, P2 (shorter) is selected over P3
- P2 completes at time 12, P3 at time 17, P1 at time 25
- **Best average turnaround time** (16.00) and **best average waiting time** (7.33)
- Demonstrates SJF optimality for minimizing average waiting time

### Priority Results

```
PID   Arrive  Burst   Prio   IOstart  IOdur   Complete   Turnaround   Wait(ready)  Wait(IO)
P1    0       10      1      2        3       15         15           4            0
P2    2       6       1      1        2       20         18           10           2
P3    4       8       1      3        1       25         21           14           0

Average Turnaround Time: 18.00
Average Waiting Time (ready queue): 9.33
```

**Analysis:**
- All processes have same priority (1), so behavior identical to FCFS
- Demonstrates that priority scheduling reduces to FCFS when priorities are equal
- Would show different behavior with varied priorities

### Round-Robin Results (Quantum = 2)

```
PID   Arrive  Burst   Prio   IOstart  IOdur   Complete   Turnaround   Wait(ready)  Wait(IO)
P1    0       10      1      2        3       24         24           16           0
P2    2       6       1      1        2       22         20           14           2
P3    4       8       1      3        1       25         21           17           0

Average Turnaround Time: 21.67
Average Waiting Time (ready queue): 15.67
```

**Analysis:**
- CPU time shared fairly among processes
- Frequent context switches (every 2 time units)
- **Highest average turnaround time** (21.67) and **highest waiting time** (15.67)
- Trade-off: better responsiveness at cost of throughput
- Small quantum (2) causes overhead to dominate for this workload

### Comparative Analysis

| Algorithm | Avg Turnaround | Avg Waiting | Best For |
|-----------|----------------|-------------|----------|
| **FCFS**  | 18.00          | 9.33        | Simple batch systems, predictability |
| **SJF**   | **16.00**      | **7.33**    | Minimizing average waiting time |
| **Priority** | 18.00       | 9.33        | Mixed workload with varying importance |
| **Round-Robin** | 21.67   | 15.67       | Time-sharing, interactive systems |

**Key Insights:**

1. **SJF is optimal** for this workload in terms of average waiting and turnaround time
2. **Round-Robin has worst metrics** but would provide best response time for interactive processes
3. **FCFS and Priority performed identically** because all processes had same priority
4. **I/O operations significantly impact** all algorithms, adding complexity to scheduling decisions

### Performance Characteristics

**Throughput**: SJF > FCFS = Priority > Round-Robin
**Response Time**: Round-Robin > SJF > Priority > FCFS
**Fairness**: Round-Robin > FCFS > Priority > SJF
**Starvation Risk**: SJF (high), Priority (high), FCFS (none), Round-Robin (none)

---

## Conclusion

### Key Findings

This CPU scheduling simulation project successfully demonstrated the implementation and comparative analysis of four fundamental scheduling algorithms. The key findings include:

1. **No Universal Best Algorithm**: Each scheduling algorithm has distinct trade-offs. SJF minimizes average waiting time but can starve long processes. Round-Robin ensures fairness but increases turnaround time. FCFS is simple but inefficient. Priority scheduling offers flexibility but risks starvation.

2. **I/O Significantly Impacts Scheduling**: The addition of I/O operations adds realistic complexity. Algorithms must handle transitions between CPU and I/O states, manage multiple queues, and optimize both CPU and I/O device utilization.

3. **Context Matters**: The "best" algorithm depends on system goals:
   - **Batch systems**: SJF or FCFS
   - **Interactive systems**: Round-Robin
   - **Real-time systems**: Priority scheduling
   - **General-purpose**: Hybrid approaches (e.g., Multi-Level Feedback Queue)

4. **Quantum Size in Round-Robin**: The time quantum significantly affects Round-Robin performance. Smaller quanta improve response time but increase context switch overhead. Larger quanta reduce overhead but approach FCFS behavior.

### Insights and Significance of CPU Scheduling

**Why CPU Scheduling Matters:**

CPU scheduling is fundamental to operating system performance and user experience. Modern operating systems must:

- **Maximize CPU Utilization**: Keep the CPU busy executing user processes
- **Minimize Response Time**: Provide quick feedback to interactive users
- **Ensure Fairness**: Prevent process starvation and provide equitable resource allocation
- **Optimize Throughput**: Complete as many processes as possible per unit time
- **Meet Real-Time Constraints**: Guarantee deadlines for time-critical processes

**Real-World Applications:**

1. **Linux Completely Fair Scheduler (CFS)**: Uses a red-black tree and virtual runtime to provide fair CPU allocation, inspired by concepts from this project.

2. **Windows Thread Scheduler**: Employs multi-level feedback queues with priority-based and round-robin elements.

3. **Real-Time Operating Systems**: Use priority-based preemptive scheduling to guarantee response times for critical tasks.

4. **Cloud Computing**: Scheduling algorithms allocate virtual CPU time among containers and virtual machines, directly affecting cost and performance.

### Practical Relevance

This simulation demonstrates that:

- **Simple algorithms can be effective**: FCFS works well for batch systems despite its simplicity
- **Optimization has costs**: SJF minimizes waiting time but requires burst time prediction
- **Fairness requires overhead**: Round-Robin ensures fairness through frequent context switches
- **I/O complicates everything**: Real systems must coordinate CPU and I/O scheduling

### Future Enhancements

Potential improvements to this simulation include:

1. **Multi-Level Feedback Queue (MLFQ)**: Combining multiple algorithms dynamically
2. **Preemptive SJF and Priority**: Allowing higher-priority processes to preempt running processes
3. **Multiple I/O Devices**: Modeling different I/O device types with varying speeds
4. **CPU Affinity**: Simulating multi-core systems with cache effects
5. **Aging**: Preventing starvation in Priority scheduling by gradually increasing priority

### Summary

This project successfully implemented four CPU scheduling algorithms with realistic I/O support, demonstrated their comparative performance, and illustrated fundamental operating system concepts. The simulation provides a foundation for understanding how modern operating systems manage process execution and optimize system resources.

The choice of scheduling algorithm has profound implications for system performance, user experience, and resource utilization. By studying these algorithms, we gain insight into the complex trade-offs operating system designers face and the importance of selecting appropriate scheduling policies for different computing environments.

---

## How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line or terminal access

### Compilation

Navigate to the project directory and compile all Java files:

```bash
cd "c:\Users\Justin Dill\Documents\College\Towson\2025\Fall-2025\Operating-Systems\Project-1"
javac src/*.java
```

### Execution

Run the main program:

```bash
java src.Main
```

### Expected Output

The program will execute all four scheduling algorithms sequentially and display results for each, including:
- Process details (PID, arrival, burst, priority, I/O info)
- Completion and turnaround times
- Waiting times (ready queue and I/O queue)
- Average turnaround and waiting times

### Customizing Test Cases

Edit `src/Main.java` to modify the process list:

```java
processes.add(new Process("P1", burstTime, arrivalTime, priority, ioStartTime, ioDuration));
```

You can also adjust the Round-Robin quantum in the `simulateRoundRobin()` call.

---

**Author**: [Your Name]
**Date**: November 2025
**Course**: COSC 439 - Operating Systems
**Institution**: Towson University
