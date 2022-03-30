Lab 3: Resource Manager
===============
**Gayeon Park**
*Operating Systems, Spring 2020*

For this program, we are trying to replicate how a resource manger does resource allocation. An optimistic resource manager and the Banker's algorithm of Dijkstra are implemented in Java. The optimistic resource manger is simple: a request by the task is satisfied if possible. If not, the task has to wait. When the resources held by the task is released, the pending requests by tasks are satisfied in a FIFO manner. 

The program reads input from a file, whose name is given as a command line argument when you run the program.

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
