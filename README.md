Lab 3: Resource Manager
===============
**Gayeon Park**

*Operating Systems, Spring 2020*

For this lab, we are trying to replicate how a resource manger does resource allocation. An optimistic resource manager and the Banker's algorithm of Dijkstra are implemented in Java. 


The optimistic resource manger is simple: a request by the task is satisfied if possible. If not, the task has to wait. When the resources held by a task is released (aka the task is terminated), the pending requests by remaining tasks are satisfied in a FIFO manner. 


The Banker program (Banker.java) reads input from a file, whose name is given as a command line argument when you run the program. 
After reading all the input first, the program performs two simulations: one with the optimistic manager and one with the banker. Output is written to stdout (the screen).

Time is measured in fixed units called cycles (no fractional cycles are used for simplicity).
The manager can process one activity (initiate, request, or release) for each task in one cycle. However, the terminate activity does not require a cycle.

The input file begins with a line that contains two values 'T' (the number of tasks) and 'R' (the number of resource types), followed by R additional values (the number of units present of each resource type). Then come multiple input lines, each representing the next activity of a specific task. 

The possible activities are initiate, request, release, and terminate. All activities have the same format, namely a string followed by four unsigned integers.

The initiate activity, which must precede all others for that task, is written 
```
    initiate task-number delay resource-type initial-claim
```
(The optimistic manager ignores the claim.) If there are R resource types, there are R initiate activities for each task, each requiring one cycle. The delay value is not used for initiate; it is include it so that all activities have the same format.


The request and release activities are written
```
    request task-number delay resource-type number-requested
    release task-number delay resource-type number-released
```
The delay value represents the number of cycles between the completion of the previous activity for this process and the beginning of the current activity. 
For example, assume the previous activity was a request and the current activity is a release with a delay of 6. 
Then, after the request is satisfied, the process is delayed 6 cycles before making the release (presumably the task was computing during these 6 cycles, but that is not relevant to the manager and hence not relevant for the lab). The process retains its resources during the delay.


Finally the terminate operation, which does not require a cycle is written
```
    terminate task-number delay unused unused
```
The last two values are not used; they are included so that all activities have the same format.


Here is a breakdown of the "input-01" file to aid understanding:
```
line 1: 2 1 4
line 2: initiate 1 0 1 4
line 3: request 1 0 1 1
line 4: release 1 0 1 1
line 5: terminate 1 0 0 0
line 6: initiate 2 0 1 4
line 7: request 2 0 1 1
line 8: release 2 0 1 1
line 9: terminate 2 0 0 0
```
The line 1 indicates that this input has 2 tasks and 1 resource type, and 4 units of resource type #1.

Line 2 indicates that the run begins ('initiate' at cycle 0-1, the cycle starting at 0 and ending at 1) with task 1 claiming (all) 4 units of resource 1. 

Further down on line 6 we see that task 2 also claims 4 units of resource 1 during cycle 0-1.

From lines 3 and 7, we learn that each task requests a unit during cycle 1-2 and returns that unit during the next cycle after the request is granted. 

For the optimistic manager, the request is granted at 2 (the end of cycle 1-2) and the resource is returned during 2-3.

Input 1 has all delays zero. For the optimistic manager each task terminates at time 3.


Task.java file is created to organize all information and activities of a given input task so that it can be utilized by the Banker program to perform correct simulation for the resource allocation. 


The input files are located in the same 'src' folder as the Banker.java and Task.java files written for this lab. 


To run on crackle1 on cims.nyu.edu server, please first go to crackle1, where the folder "Lab3" is uploaded. Then navigate to the Banker.java file like the following from crackle1:
     -Change directory to Lab3, then change directory to src.
     Or you can do the following:
            cd Lab3/src
Inside of the "src" folder, Banker.java file is located.
To execute the program, please type the name of the java file (Banker) and the name of the input text file (don't include the .txt extension in the input file name you are passing on). 

Type the below instruction into the Terminal to compile the Banker.java program.
When compiling, you have to make sure that you are compiling BOTH the Banker.java file and the Task.java file because Banker.java uses an instance of Task class.

ONE IMPORTANT THING TO NOTE: the input text files you are testing the Banker.java file against MUST be in the same folder as the Banker.java file
So Banker.java file, Task.java file, and whatever input file you want to test HAVE to be in the same folder (namely, the src folder inside of the Lab3 folder).

### Compiling
```
javac Banker.java Task.java
```

### Running
To execute the code, type following:
```
java Banker inputTextFileName

```
For example, if you want to execute the Banker.java file with the contents of input-03.txt file, do the following: 

### Compiling
```
javac Banker.java Task.java
```

### Running
To execute the code, type following:
```
java Banker input-03

```
