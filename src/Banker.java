import java.io.*;
import java.util.*;

public class Banker {

	/**
	 * This method sorts all the Tasks that were executed in the same time frame (namely, a cycle)
	 * 	by the order the tasks were created.
	 * So if there're 3 tasks in total, the 1st task will have an index of 0, 
	 * 	the 2nd one will have an index of 1, and the 3rd task will have an index of 2.
	 * So the ordering is done by the ascending order of the index of the task.
	 * No two tasks have same index because that would mean that those two are actually the same task.
	 * @param executedT is an ArrayList of Tasks that contains all the Tasks whose activity was executed during a cycle
	 */
	static void sortExecutedTasksList(ArrayList<Task> executedT) {
		Collections.sort(executedT, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) { //compare two Tasks, o1 and o2	
				if (o1.getIndex() > o2.getIndex()) { //if the o1's index is greater than the o2's index
					return 1;
				} else if (o1.getIndex() < o2.getIndex()) { //if the o1's index is less than the o2's index
					return -1;
				} else { //if the o1's index is equal to the o2's index
					return 0;
				}
			}		
		});	
	}

	/**
	 * This main method is where all the action happens: 
	 * 	The file is read from the command line argument, which is the name of the file containing the input.
	 * 	The optimistic resource manager and the Banker's algorithm are implemented here.
	 * 	The output is generated as standard output for both the Optimistic resource manager and the Banker algorithm.
	 * @param args are the command line arguments passed in when running this Java program
	 */
	public static void main(String[] args) {		

		int numTasks = 0; //represents the number of Tasks in the system
		int numResources = 0; //represents the number of resource types
		ArrayList<Integer> listOfUnitsOfEachResource = new ArrayList<>(); //represents the list of units of each resource type available
		ArrayList<Integer> listOfReturnedUnitsOfEachResource = new ArrayList<>(); //represents the list of units returned of each resource type
		ArrayList<Task> tasksOptimistic = new ArrayList<>(); //represents the list of tasks in the system for the Optimistic resource manager
		ArrayList<Task> tasksBanker = new ArrayList<>(); //represents the list of tasks in the system for the Banker's algorithm
		ArrayList<Task> tasksFinalizedOpt = new ArrayList<>(); //represents the finalized list of tasks in the system after Opt. resource manager finishes
		ArrayList<Task> tasksFinalizedBkr = new ArrayList<>(); //represents the finalized list of tasks in the system after Banker's algorithm finishes
		ArrayList<Task> executedTasks = new ArrayList<>(); //represents a list of tasks that was executed during the current cycle
		int cycle = 0; //represents the fixed unit of time. So 1 cycle means 1 unit of time has passed
		int totalTime = 0; //represents the total time it took for all Tasks to terminate
		int totalWaitingTime = 0; //represents the total waiting time of all Tasks
		String errorOpt = ""; //represents the error generated from the Optimistic resource manager
		String errorBanker = ""; //represents the error generated from the Banker's Algorithm

		Scanner sc = null;
		//scanner is created from a file created from the name of the file passed as the 1st parameter to the command line argument
		try {
			sc = new Scanner(new File(args[0])); 
		} catch (Exception e){ //the file name was NOT passed in as the 1st parameter to the command line argument
			System.out.println("Please make sure that you are giving the file name as a command line argument.");
		}

		while (sc.hasNext()) { //the scanner reads from the file until there is no more content left to read	

			//save T, the number of Tasks
			numTasks = sc.nextInt();

			//save R, the number of resource types
			numResources = sc.nextInt();

			//loop through R many times to save R additional values, the number of units present of each resource type
			for (int i = 0; i < numResources; i ++) {
				//for each resource type, save the number of units available into the "listOfUnitsOfEachResource"
				listOfUnitsOfEachResource.add(sc.nextInt());

				//for each resource type, we are also creating a returnedUnits variable that is of value 0 for now.
				//we are storing in 0 because before the resource allocation, no resource has been allocated
				// and hence there is 0 unit of each resource type returned
				listOfReturnedUnitsOfEachResource.add(0);
			}

			//'T' many Tasks need to be made and stored into a list storing all the Tasks
			for (int i = 0; i < numTasks; i ++) {
				//create and add a new Task of index i to the arrayList of tasks for Optimistic resource manager
				tasksOptimistic.add(new Task(i));

				//create and add a new Task of index i to the arrayList of tasks for the Banker's Algorithm
				tasksBanker.add(new Task(i));

				//create and add a new Task of index i to the finalized arrayList of tasks for the Optimistic resource manager
				//for now, an empty Task is created and later appropriate Task will be set to the appropriate index
				tasksFinalizedOpt.add(new Task(i));

				//create and add a new Task of index i to the finalized arrayList of tasks for the Banker's Algorithm
				//for now, an empty Task is created and later appropriate Task will be set to the appropriate index
				tasksFinalizedBkr.add(new Task(i));
			}

			//the first three numbers have been read, so until you reach the end of the file,
			// you are reading in different Tasks' activity informations 
			while (sc.hasNext()) {				
				//read the activity state (one of: initiate, request, release, terminate)
				// and save it to a temporary String, tempActivity
				String tempActivityType = sc.next();

				//create an ArrayList of integers to store informations of the activity
				ArrayList<Integer> activityInfo = new ArrayList<>();
				//for the activity, save the four unsigned integers 
				// that represent the information of the activity to the 'activityInfo' ArrayList created above
				for (int i = 0; i < 4; i ++) {
					activityInfo.add(sc.nextInt());
				}

				//FOR OPTIMISTIC RESOURCE MANAGER
				//get the correct task from the list of tasks that matches the 'task-number' of the current activity being read
				Task tempTaskOp = tasksOptimistic.get(activityInfo.get(0) - 1);	
				//create a new Activity pair (activityName, listOfActivityInfo) using HashMap
				HashMap<String, ArrayList<Integer>> activityTemp0 = new HashMap<>();
				activityTemp0.put(tempActivityType, activityInfo);
				//save the new activity pair into the retrieved task's activities list
				tempTaskOp.getActivities().add(activityTemp0);

				//FOR BANKER'S ALGORITHM
				//get the correct task from the list of tasks matching the 'task-number' of the current activity being read
				Task tempTaskBnkr = tasksBanker.get(activityInfo.get(0) - 1);
				//create a new Activity pair (activityName, listOfActivityInfo) using HashMap
				HashMap<String, ArrayList<Integer>> activityTemp1 = new HashMap<>();
				activityTemp1.put(tempActivityType, activityInfo);
				//save the new activity pair into the retrieved task's activities list
				tempTaskBnkr.getActivities().add(activityTemp1);

				//get the list of claims for the Banker's algorithm
				HashMap<Integer,Integer> claimsBnkr = tempTaskBnkr.getClaims();
				//if the current activity type is "initiate," save the initial claim of the current activity's resource type
				if (tempActivityType.equals("initiate")) {
					int claimedResourceIndex = activityInfo.get(2)-1;
					int claimedAmt = activityInfo.get(3);
					claimsBnkr.put(claimedResourceIndex, claimedAmt);
				}
				//then set 'claimsBnkr' as the list of claims for the retrieved task
				tempTaskBnkr.setClaims(claimsBnkr);

			} //end of the inner while loop for reading the file

		} //end of the outer while loop for reading the file


		//FOR IMPLEMENTING THE OPTIMISTIC RESOURCE MANAGER
		boolean taskExecuted = false; //used to indicate whether a Task has been executed (if Task tried to satisfy an activity)

		//run until there is no more task remaining in the list of tasks for the Optimistic resource manager
		while (!tasksOptimistic.isEmpty()) { 
			cycle ++; //increment to indicate time passing

			//for each resources available, 
			for (int k = 0; k < listOfUnitsOfEachResource.size(); k ++) {
				//add back the each returned amount from the previous run
				listOfUnitsOfEachResource.set(k, listOfUnitsOfEachResource.get(k) + listOfReturnedUnitsOfEachResource.get(k));
				//reset each returnedResources value to 0
				listOfReturnedUnitsOfEachResource.set(k,0);
			}

			//go through the entire list of tasks
			for (int i = 0; i < tasksOptimistic.size(); i ++) { 

				//get the current task
				Task currTask = tasksOptimistic.get(i);

				//get the first activity of the current task's activities list
				HashMap<String, ArrayList<Integer>> currActivity = currTask.getActivities().get(0);

				//if the task's activity is "initiate"
				if (currActivity.containsKey("initiate")) {
					//get the ArrayList of information about the current activity
					ArrayList<Integer> activityInfos = currActivity.get("initiate"); 

					//the current task has been executed
					taskExecuted = true;

					//since the activity is initiate, the current task holds no resource of any type
					//so we put value 0 for the number of units the task holds of the current resource type
					currTask.getResourcesHeld().put((activityInfos.get(2))-1, 0);

					//initiate has been granted, so remove the activity from the activities list
					currTask.getActivities().remove(0);
				} else {
					//if the task's activity is "request"
					if (currActivity.containsKey("request")) {
						//get the ArrayList of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("request"); 

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource requested by the task's activity
						int resourceRequested = activityInfos.get(3); //represents the number of the resource requested by the task's activity
						int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); //represents the available units of the resource requested

						int requestDelay = currTask.getRequestdelay(); //represents the delay counter kept by the current task
						int activityDelay = activityInfos.get(1); //represents the delay given by the current task's activity

						//check if there are enough resources for the request to be satisfied
						if (resourceRequested <= resourceAvail) {

							//if there are enough resources & the requestDelay counter is equal to the activityDelay, 
							// the task's activity can be satisfied
							if (requestDelay == activityDelay) {
								
								//the current task has been executed
								taskExecuted = true;

								//reset the requestDelay to 0
								currTask.resetRequestdelay();

								//first calculate the new amount of units held for the current resource as: 
								//      current value of resourceHeld + value of resourceRequested
								//  then update the units held for the current resource as the sum of those two numbers (shown above) 
								currTask.getResourcesHeld().put(resourceIndex,currTask.getResourcesHeld().get(resourceIndex)+resourceRequested);

								//calculate the remaining units available for the current resource type
								int newResourceAvail = resourceAvail - resourceRequested;
								//update the list that stores the available units of each resource type
								listOfUnitsOfEachResource.set(resourceIndex, newResourceAvail);

								//the request has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

							} else { //the delay counter is NOT equal to the delay given by the current task's activity, 
										//so it needs to wait for the delay to finish

								//the current task has been executed
								taskExecuted = true;

								//increase the requestdelay counter
								currTask.increaseRequestdelay();
							}

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed

						} else { //there AREN'T enough resources for the request to be satisfied, so wait
							currTask.increaseWaitTime();
						}
					} //end of "request" handling

					//if the task's activity is "release"
					else if (currActivity.containsKey("release")) {
						//get the ArrayList of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("release"); 

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource released by the task's activity
						int resourceReleased = activityInfos.get(3); //represents the number of the resource released by the task's activity
						//represents the overall returned units of the current resource type
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(resourceIndex); 

						int releasedelay = currTask.getReleasedelay(); //represents the delay counter kept by the current task
						int activitydelay = activityInfos.get(1); //represents the delay given by the current task's activity

						//if the releaseDelay counter is EQUAL to the activityDelay, 
						// the task's activity can be satisfied
						if (releasedelay == activitydelay) {
							
							//the current task has been executed
							taskExecuted = true;

							//reset the releasedelay to 0
							currTask.resetReleasedelay();

							//release the units held for the resource type specified
							currTask.getResourcesHeld().put(resourceIndex,0);

							//add the resource units released by the current task 
							// to the total units returned for the current resource type
							resourceReturned += resourceReleased;
							//then update the list that stores the total returned units of each resource type
							listOfReturnedUnitsOfEachResource.set(resourceIndex, resourceReturned);

							//request has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);

						} else { //the releasedelay counter is NOT equal to the delay given by the current task's activity, 
									//so it needs to wait for the delay to finish

							//the current task has been executed
							taskExecuted = true;
							
							//increase the releasedelay counter
							currTask.increaseReleasedelay();
						}		

						//in the case where the last 'release' activity of the current task has been done
						//and there is only one activity remaining: TERMINATE, which does NOT require another cycle to complete
						if (currTask.getActivities().size() == 1 ) {
							//we get the final activity, 'terminate'
							HashMap<String, ArrayList<Integer>> finalActivity = currTask.getActivities().get(0); 
							//get the info for 'terminate' activity
							ArrayList<Integer> terminateInfo = finalActivity.get("terminate"); 

							//if the task does NOT have to delay to terminate, DO TERMINATE
							if (terminateInfo.get(1) == 0) {
								//the current task has been executed
								taskExecuted = true;

								//set the current cycle as the time the current task terminated
								currTask.setTimeTerminated(cycle);

								//terminate has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

								//store the current task to the right index 
								// in the finalized list holding tasks for Opt. resource manager
								tasksFinalizedOpt.set(currTask.getIndex(), currTask);
								//remove the CURRENT TASK from the list of tasks since it is COMPLETED
								tasksOptimistic.remove(currTask); 
								i--; //do this so the order of reading from the list of tasks isn't disturbed

							} else { //the task DOES have to delay to terminate
								
								//the current task has been executed
								taskExecuted = true;

								//increase the terminatedelay counter for the current task
								currTask.increaseTerminatedelay();

								//pop the current Task and add it to the executedTasks list
								executedTasks.add(tasksOptimistic.remove(i));
								i--; //do this so the order of reading from the list of tasks isn't disturbed
							}
						} else { //in the case where there are still multiple activities left 
									//that are waiting to be satisfied for the current task
							
							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						}
					} //end of "release" handling

					//if the task's activity is "terminate"
					else {	
						//get the ArrayList of information about the current activity
						ArrayList<Integer> terminateInfo = currActivity.get("terminate"); 

						//the current task has been executed
						taskExecuted = true;

						//does NOT have to delay to terminate, so DO TERMINATE
						if (currTask.getTerminatedelay() == terminateInfo.get(1)) {
							
							//set the current cycle as the time the current task terminated
							currTask.setTimeTerminated(cycle);

							//terminate has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);
							//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
							tasksFinalizedOpt.set(currTask.getIndex(), currTask);
							//remove the CURRENT TASK from the list of tasks since it is COMPLETED
							tasksOptimistic.remove(currTask);
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							
						} else { //the task DOES have to delay to terminate
							//increase the terminatedelay counter for the current task
							currTask.increaseTerminatedelay();

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						}
					} //end of 'terminate' handling

				} //end of handling activities other than 'initiate'				
			} //end of the for loop for going through the entire list of tasks

			//if more than one task executed in the current cycle
			if (executedTasks.size() > 1) { 
				//sort the list of executed tasks according to the task's index
				sortExecutedTasksList(executedTasks); 
			}

			//add the sorted executed tasks to the end of the list of tasks
			for (int z = 0; z < executedTasks.size(); z ++) { 
				tasksOptimistic.add(executedTasks.remove(z));
				z --;
			} //after this, the list 'executedTasks' should be empty

			//check for deadlock
			boolean canExecute = false;
			//if not a single task executed and the cycle is > 1, 
			// meaning, some time has passed since the tasks' activities has been first executed
			if ( !taskExecuted && cycle > 1) { //we encountered a deadlock!
				//store the error message
				errorOpt += String.format("A deadlock has occured at cycle %d!\n", cycle);
				
				//the counter for how many times a task had to be aborted
				int keeper = 0;
				//try to resolve the deadlock
				while (!canExecute) {
					//if the counter for the number of times a task had been aborted 
					// is greater than 0
					if (keeper > 0) {
						//the deadlock is remaining after aborting at least one task, so store additional error message
						errorOpt += "The deadlock still remains!\n";
					}
					
					//GET the Task with the lowest index
					//initializing the lowest index
					int taskIndexToAbort = tasksOptimistic.get(0).getIndex(); 
					//the index of the Task with the lowest index WITHIN the list of tasks for Opt. resource manager
					int indexInTasksOpt = 0; 
					
					//go through the entire list of tasks for the Opt. resource manager to find the task with lowest index
					for (int m = 1; m < tasksOptimistic.size(); m ++) {
						int nextIndex = tasksOptimistic.get(m).getIndex();
						
						//if the currentTask's index is GREATER than the nextTask's index
						if (taskIndexToAbort > nextIndex) { 
							//set the currentTask to the nextTask's index that is smaller
							taskIndexToAbort = nextIndex; 
							//save the position of the updated currentTask's WITHIN the list of tasks for Opt. resource manager
							indexInTasksOpt = m; 
						}
					}
					
					//this is the task to be aborted
					Task taskAbort = tasksOptimistic.get(indexInTasksOpt);
					
					//store the info for the task being aborted
					errorOpt += String.format("Task %d is aborted and its resources are\n" + 
							"available next cycle (%d-%d)\n",taskAbort.getTaskNum(),cycle,cycle+1);

					//the task is to be aborted
					taskAbort.doAbort();

					//release the resources of the task to be aborted
					HashMap<Integer,Integer> itsHeldRsrcs = taskAbort.getResourcesHeld();
					for (int n = 0; n < itsHeldRsrcs.size(); n ++) {
						//get the units held of the current resource type by the current task
						int resourceHeld = itsHeldRsrcs.get(n); 
						//get the currently all returned units of the current resource type
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(n); 

						//add the resource release by the current task to the total units returned for the current resource
						resourceReturned += resourceHeld;
						//then update the list that stores the total returned units of each resource type
						listOfReturnedUnitsOfEachResource.set(n, resourceReturned);
					}

					//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
					tasksFinalizedOpt.set(taskAbort.getIndex(),taskAbort);
					//remove the CURRENT TASK from the list of tasks since it is ABORTED
					tasksOptimistic.remove(indexInTasksOpt);

					//go through the modified list of tasks to see if deadlock is resolved
					for (int b = 0; b < tasksOptimistic.size(); b ++) {
						Task t = tasksOptimistic.get(b);

						HashMap<String, ArrayList<Integer>> activity0 = t.getActivities().get(0);
						for (Map.Entry m : activity0.entrySet()) { 
							//get activity info of current task
							ArrayList<Integer> info = activity0.get((String) m.getKey()); 

							//if the sum of the units of current resource type available 
							// and the units of current resource type released is enough to satisfy the current task's request
							if (info.get(3) <= listOfReturnedUnitsOfEachResource.get(info.get(2)-1) + listOfUnitsOfEachResource.get(info.get(2)-1)) {
								canExecute = true; //the deadlock is resolved and you can execute
							}					
						}						
					} //end of the for loop for going through the modified list of tasks
					
					//increase the counter that keeps track of
					// how many times a task had to be aborted
					keeper ++;
					
				} //end of the while loop for resolving the deadlock

			} else { //a Task has been running, i.e. NO deadlock
				taskExecuted = false; //reset the taskExecuted to false for the next cycle
			}
			
		} //end of the while loop FOR IMPLEMENTING THE OPTIMISTIC RESOURCE MANAGER

		//sort the finalized list of tasks for the Optimistic resource manager by task's index
		sortExecutedTasksList(tasksFinalizedOpt); 
		//print any error detected
		System.out.println("\n-----------------------------\n" + errorOpt);
		System.out.printf("%14s\n","FIFO");
		//print out each Task info after the Optimistic resource manager finishes
		for (Task ee: tasksFinalizedOpt) { //go through the sorted finalized list of tasks
			//print each Task's info for the time taken, the waiting time, and the percentage of time spent waiting
			ee.printTaskInfo(); 
			totalTime += ee.getTimeTerminated(); //add up all Task's time taken
			totalWaitingTime += ee.getWaitTime(); //add up all Task's waiting time
		}

		//Print the total time for all tasks, the total waiting time, and the overall percentage of time spent waiting
		System.out.printf("total %8d %4d %5d%s",(int) totalTime,(int) totalWaitingTime,Math.round((totalWaitingTime/(totalTime*1.0)) * 100),"%");
		System.out.println("\n-----------------------------");



		//FOR IMPLEMENTING THE BANKER'S RESOURCE MANAGER
		//reset the shared variables to default value of 0
		cycle = 0;
		totalTime = 0;
		totalWaitingTime = 0;
		boolean isSafeState = true; //this variable is used for indicating if it is safe state or not

		//run until there is no more task remaining in the list of tasks for the Banker's Algorithm
		while (!tasksBanker.isEmpty()) { 
			cycle ++; //increment to indicate time passing

			//for each resources available, 
			for (int k = 0; k < listOfUnitsOfEachResource.size(); k ++) {
				//add back the each returned amount from the previous run
				listOfUnitsOfEachResource.set(k, listOfUnitsOfEachResource.get(k) + listOfReturnedUnitsOfEachResource.get(k));
				//reset each returnedResources value to 0
				listOfReturnedUnitsOfEachResource.set(k,0);
			}

			//go through the entire list of tasks
			for (int i = 0; i < tasksBanker.size(); i ++) { 

				//get the current task
				Task currTask = tasksBanker.get(i);

				//get the first activity of the current task's activities list
				HashMap<String, ArrayList<Integer>> currActivity = currTask.getActivities().get(0);

				//if the task's activity is "initiate"
				if (currActivity.containsKey("initiate")) {
					//get the ArrayList of information about the current activity
					ArrayList<Integer> activityInfos = currActivity.get("initiate"); 

					//HANDLE the error where a tasks's initial claim EXCEEDS the resource present
					//represents the index of the resource
					int resourceIndex = activityInfos.get(2)-1; 
					//represents the available units of the resource claimed
					int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); 
					
					//represents the list of claims for the current task
					HashMap<Integer,Integer> currentClaims = currTask.getClaims();
					int resourceInitClaimed = currentClaims.get(resourceIndex);
					//if the amount of current resource type claimed is less than or equal 
					// to the amount of the resource type available
					if (resourceInitClaimed <= resourceAvail) {
						//no error, proceed with granting the 'initiate' activity
						//since the activity is initiate, the current task holds no resource of any type
						//so we put value 0 for the number of units the task holds of each resource type
						currTask.getResourcesHeld().put((activityInfos.get(2))-1, 0);

						//initiate has been granted, so remove the activity from the activities list
						currTask.getActivities().remove(0);
						
					} else { //error, bc the tasks's initial claim EXCEEDS the resource present
						//the task is to be aborted
						currTask.doAbort();
						//store the current task to the right index in the finalized list holding tasks for Banker's Algorithm
						tasksFinalizedBkr.set(currTask.getIndex(),currTask);
						//remove the CURRENT TASK from the list of tasks since it is ABORTED
						tasksBanker.remove(i);
						i--; //do this so the order of reading from the list of tasks isn't disturbed

						//then we store the information for the error that happened
						// because the task's initial claim exceeded the resources present
						errorBanker += String.format("Banker aborts Task %d before run begins:\n",currTask.getTaskNum());
						errorBanker += String.format("\tclaim for resource %d (%d) exceeds number of units present (%d)\n",resourceIndex+1,resourceInitClaimed,resourceAvail);
					}

				} else {
					//if the task's activity is "request"
					if (currActivity.containsKey("request")) {
						//get the ArrayList of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("request"); 

						//get the list of claims for the current Task
						HashMap<Integer,Integer> allClaims = currTask.getClaims();

						//represents the index of the resource requested by the task's activity
						int resourceIndex = activityInfos.get(2)-1; 
						//represents the number of the resource requested by the task's activity
						int resourceRequested = activityInfos.get(3); 
						//represents the available units of the resource requested
						int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); 
						//represents the amount claimed of the current resource type by the current task
						int resourceClaimed = allClaims.get(resourceIndex); 

						int requestDelay = currTask.getRequestdelay(); //represents the delay counter kept by the current task
						int activityDelay = activityInfos.get(1); //represents the delay given by the current task's activity

						//first, set isSafeState to TRUE
						isSafeState = true;
						//go through the entire list of claims for the current Task
						// to check if it is a safe state for the current Task to run
						for (Map.Entry m:currTask.getClaims().entrySet()) { 
							//represents the index of the current resource type claimed
							int claimResourceIndex = ((Integer) m.getKey()).intValue();
							//represents the number of claims of the current resource that the current Task has
							int numClaims = ((Integer) m.getValue()).intValue();

							//if the current task's claim for the current resource type EXCEEDS 
							// the number of resources available
							if (numClaims > listOfUnitsOfEachResource.get(claimResourceIndex)) {
								//set the safe state to FALSE
								isSafeState = false;
							}
						} 

						//IF DON'T HAVE TO WAIT FOR DELAY
						if (requestDelay == activityDelay) {			

							//check if there are enough resources for the CLAIM to be satisfied
							if (resourceClaimed <= resourceAvail) {

								//check if it is safe state 
								if (isSafeState) {
									//YES, it is safe state to execute
									
									//check if the current task's resources requested DOES NOT EXCEED its claim
									if (resourceRequested <= resourceClaimed) {
										// THE REQUEST CAN BE GRANTED

										//reset the requestDelay to 0
										currTask.resetRequestdelay();

										//calculate the claim remaining for the current requested resource type
										resourceClaimed -= resourceRequested;
										//update the list that stores the claims of each resource type
										allClaims.put(resourceIndex, resourceClaimed);
										//update the claims list of current task to the modified claims list from above
										currTask.setClaims(allClaims);
										
										//first calculate the new amount of units held for the current resource as: 
										//      current value of resourceHeld + value of resourceRequested
										//  then update the units held for the current resource as the sum of those two numbers (shown above) 
										currTask.getResourcesHeld().put(resourceIndex,currTask.getResourcesHeld().get(resourceIndex)+resourceRequested);

										//calculate the remaining resources available for the Banker's Algorithm
										int newResourceAvail = resourceAvail - resourceRequested;
										//update the list that stores the available units of each resource type
										listOfUnitsOfEachResource.set(resourceIndex, newResourceAvail);

										//the request has been granted, so remove the activity from the activities list
										currTask.getActivities().remove(0);

										//pop the current Task and add it to the executedTasks list
										executedTasks.add(tasksBanker.remove(i));
										i--; //do this so the order of reading from the list of tasks isn't disturbed

									} else { //the current task's resources requested DOES EXCEED its claim --> ERROR!

										//the task is to be aborted
										currTask.doAbort();

										//create a string to store how many of each resource was released by the task being aborted
										String errorInfo = "";
										//release all the resources of the task to be aborted
										HashMap<Integer,Integer> itsHeldRsrcs = currTask.getResourcesHeld();
										for (int n = 0; n < itsHeldRsrcs.size(); n ++) {
											//get the units held of the current resource type by the current task
											int resourceHeld = itsHeldRsrcs.get(n); 
											//get the currently all returned units of the current resource type
											int resourceReturned = listOfReturnedUnitsOfEachResource.get(n); 

											//add the resource released by the current task to the total units returned for the current resource
											resourceReturned += resourceHeld;
											listOfReturnedUnitsOfEachResource.set(n, resourceReturned);

											//store how many units of which resource has been returned
											errorInfo += String.format(" %d unit(s) of resource %d", resourceReturned, n+1);
										}

										//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
										tasksFinalizedBkr.set(currTask.getIndex(), currTask);
										//remove the CURRENT TASK from the list of tasks since it is COMPLETED
										tasksBanker.remove(currTask);

										//then save the error that occured because
										// the task's requests exceeded its claims during execution
										errorBanker += String.format("During cycle %d-%d of Banker's algorithms\n",cycle-1,cycle);
										errorBanker += String.format("\tTask %d's request exceeds its claim; aborted;",currTask.getTaskNum());
										errorBanker += errorInfo;
										errorBanker += " available next cycle\n";
									}
									
								} else { //NO, it is NOT safe state to execute
									//so the request CANNOT be satisfied, so WAIT
									//increase the wait time of the current task
									currTask.increaseWaitTime();
								}
								
							} else { //the claim is BIGGER than the resources available				
								//so the request CANNOT be satisfied, so WAIT
								//increase the wait time of the current task
								currTask.increaseWaitTime();
							}

						} else { //the delay counter is NOT equal to the delay given by the current task's activity, 
									//so there is a DELAY and the current task needs to wait for the delay to finish

							//increase the delay counter
							currTask.increaseRequestdelay();

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						}
						
					} //end of "request" handling

					//if the task's activity is "release"
					else if (currActivity.containsKey("release")) {
						//get the ArrayList of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("release"); 

						//represents the index of the resource released by the task's activity
						int resourceIndex = activityInfos.get(2)-1; 
						//represents the number of the resource released by the task's activity
						int resourceReleased = activityInfos.get(3); 
						//represents the overall returned units of the resource type
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(resourceIndex); 

						int releasedelay = currTask.getReleasedelay(); //represents the delay counter kept by the current task
						int activitydelay = activityInfos.get(1); //represents the delay given by the current task's activity

						//if the releaseDelay counter is EQUAL to the activityDelay, the task's activity can be satisfied
						if (releasedelay == activitydelay) {

							//reset the releasedelay to 0
							currTask.resetReleasedelay();

							//get the task's current claim for the resource type that is being released
							int currentClaim = currTask.getClaims().get(resourceIndex);
							//recalculate the claim because resource has been released
							currTask.getClaims().put(resourceIndex,currentClaim + resourceReleased);
							
							//release the units held for the resource type specified by the current task
							currTask.getResourcesHeld().put(resourceIndex,0);

							//add the resource units released by the current task 
							// to the total units returned for the current resource type
							resourceReturned += resourceReleased;
							listOfReturnedUnitsOfEachResource.set(resourceIndex, resourceReturned);

							//request has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);

						} else { //the releasedelay counter is NOT equal to the delay given by the current task's activity, 
									//so it needs to wait for the delay to finish
							
							//increase the delay counter
							currTask.increaseReleasedelay();
						}		

						//in the case where the last 'release' activity of the current task has been done
						//and there is only one activity remaining: TERMINATE, which does NOT require another cycle to complete
						if (currTask.getActivities().size() == 1 ) {
							//we get the final activity, 'terminate'
							HashMap<String, ArrayList<Integer>> finalActivity = currTask.getActivities().get(0); 
							//get the info for 'terminate' activity
							ArrayList<Integer> terminateInfo = finalActivity.get("terminate"); 

							//if the task does NOT have to delay to terminate, DO TERMINATE
							if (terminateInfo.get(1) == 0) {

								//set the current cycle as the time the current task terminated
								currTask.setTimeTerminated(cycle);

								//terminate has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

								//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
								tasksFinalizedBkr.set(currTask.getIndex(), currTask);
								//remove the CURRENT TASK from the list of tasks since it is COMPLETED
								tasksBanker.remove(currTask); 
								i--; //do this so the order of reading from the list of tasks isn't disturbed

							} else { //the task DOES have to delay to terminate

								//increase the terminatedelay counter for the current task
								currTask.increaseTerminatedelay();

								//pop the current Task and add it to the executedTasks list
								executedTasks.add(tasksBanker.remove(i));
								i--; //do this so the order of reading from the list of tasks isn't disturbed
							}
						} else { //in the case where there are still multiple activities waiting to be satisfied for the current task
							
							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						}
					} //end of "release" handling

					//if the task's activity is "terminate"
					else {	
						//get the ArrayList of information about the current activity
						ArrayList<Integer> terminateInfo = currActivity.get("terminate"); 

						//does NOT have to delay to terminate, so DO TERMINATE
						if (currTask.getTerminatedelay() == terminateInfo.get(1)) {

							//set the current cycle as the time the current task terminated
							currTask.setTimeTerminated(cycle);

							//terminate has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);
							//store the current task to the right index in the finalized list holding tasks for Banker's Algorithm
							tasksFinalizedBkr.set(currTask.getIndex(), currTask);
							//remove the CURRENT TASK from the list of tasks since it is COMPLETED
							tasksBanker.remove(currTask);
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							
						} else { //the task DOES have to delay to terminate

							//increase the terminatedelay counter for the current task
							currTask.increaseTerminatedelay();

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						}
					} //end of 'terminate' handling

				} //end of handling activities other than 'initiate'				
			} //end of the for loop for going through the entire list of tasks

			//if more than one task executed in the current cycle
			if (executedTasks.size() > 1) { 
				//sort the list of executed tasks according to the task's index
				sortExecutedTasksList(executedTasks); 
			}

			//add the sorted executed tasks to the end of the list of tasks
			for (int z = 0; z < executedTasks.size(); z ++) { 
				tasksBanker.add(executedTasks.remove(z));
				z --;
			} //after this, the list 'executedTasks' should be empty

		} //end of the while loop FOR IMPLEMENTING THE BANKER'S ALGORITHM

		//sort the finalized list of tasks for the Banker's algorithm by task's index
		sortExecutedTasksList(tasksFinalizedBkr); 

		//print any error detected
		System.out.println("\n-----------------------------\n" + errorBanker);
		System.out.printf("%16s\n","BANKER'S");
		//print out each Task info after the Banker's resource manager finishes
		for (Task ee: tasksFinalizedBkr) { //go through the sorted finalized list of tasks
			//print each Task's info for the time taken, the waiting time, and the percentage of time spent waiting
			ee.printTaskInfo(); 
			totalTime += ee.getTimeTerminated(); //add up all Task's time taken
			totalWaitingTime += ee.getWaitTime(); //add up all Task's waiting time
		}

		//Print the total time for all tasks, the total waiting time, and the overall percentage of time spent waiting
		System.out.printf("total %8d %4d %5d%s",(int) totalTime,(int) totalWaitingTime,Math.round((totalWaitingTime/(totalTime*1.0)) * 100),"%");
		System.out.println("\n-----------------------------");

	} //end of the main function

} //end of the Banker class
