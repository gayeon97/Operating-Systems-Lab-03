import java.io.*;
import java.util.*;

public class Banker_Unclean {

	/**
	 * this method sorts all the Tasks that were executed in the same time frame (namely, a cycle)
	 * by the order of the tasks were created
	 * If there are 3 tasks in total, the 1st task will have index of 0, the 2nd will have an index of 1, and the 3rd one will have an index of 2.
	 * So the ordering is done by the ascending index of the task.
	 * No two tasks have same index because that would mean that those two are actually the same task
	 * @param executedT an arraylist of Tasks that contains all the Tasks whose activity was executed during a cycle
	 */
	static void sortExecutedTasksList(ArrayList<Task> executedT) {
		Collections.sort(executedT, new Comparator<Task>() {
			@Override
			public int compare(Task o1, Task o2) {		

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
	 * this main method is where all the action happens. 
	 * The file is read from the command line argument, which is the name of the file containing the input.
	 * The optimistic resource manager and the Banker's algorithm are implemented here.
	 * The output is generated as standard output for both Opt. resource manager and the Banker.
	 * @param args these are the command line arguments passed in when running this Java program
	 */
	public static void main(String[] args) {		

		int numTasks = 0; //represents the number of Tasks in the system
		int numResources = 0; //represents the number of resource types the resource manager has
		ArrayList<Integer> listOfUnitsOfEachResource = new ArrayList<>(); //represents the list of units of each resource type
		ArrayList<Integer> listOfReturnedUnitsOfEachResource = new ArrayList<>(); //represents the list of units returned of each resource type
		ArrayList<Task> tasksOptimistic = new ArrayList<>(); //represents the list of tasks in the system for the optimistic resource manager
		ArrayList<Task> tasksBanker = new ArrayList<>(); //represents the list of tasks in the system for the Banker's resource manager
		ArrayList<Task> tasksFinalizedOpt = new ArrayList<>(); //represents the finalized list of tasks in the system after Opt. resource manager finishes
		ArrayList<Task> tasksFinalizedBkr = new ArrayList<>(); //represents the finalized list of tasks in the system after Bkr resource manager finishes
		ArrayList<Task> executedTasks = new ArrayList<>(); //represents a list of tasks that was executed during the current cycle
		int cycle = 0; //represents the fixed unit of time. So 1 cycle means 1 unit of time has passed
		int totalTime = 0; //represents the total time it took for all Tasks to terminate
		int totalWaitingTime = 0; //represents the total waiting time of all Tasks
		String error = ""; //represents the error generated from the Banker's Alg

		//get the claims for the Banker's algorithm
		//HashMap<Integer,Integer> claimsBnkr = new HashMap<>();

		Scanner sc = null;
		//scanner is reading from a file created from the name of the file passed as the 1st parameter to the command line argument
		try {
			//System.out.println(args[0]);
			sc = new Scanner(new File(args[0])); 
		} catch (Exception e){ //the file name was NOT passed in as the 1st parameter to the command line argument
			System.out.println("Please make sure that you are giving the file name as a command line argument.");
		}

		while (sc.hasNext()) { //read from the file	
			//			//this will be the HashMap for storing the list of each resources requested for each Task
			//			HashMap<Integer, Integer> tempListRequests = new HashMap<>();

			//save T, the number of Tasks
			numTasks = sc.nextInt();
			//System.out.println("This is the number of tasks: " + numTasks);

			//save R, the number of resource types
			numResources = sc.nextInt();
			//System.out.println("This is the number of resources: " + numResources);

			//loop through R many times to save the number of units present of each type
			for (int i = 0; i < numResources; i ++) {
				//for each resource type, save the number of units available into the "listOfUnitsOfEachResource"
				listOfUnitsOfEachResource.add(sc.nextInt());
				//System.out.printf("Resource R%d has %d units.\n", i+1, listOfUnitsOfEachResource.get(i));

				//for each resource type, we are also creating a returnedUnits variable that is of value 0 for now
				listOfReturnedUnitsOfEachResource.add(0);
				//System.out.printf("Resource R%d has %d returned units.\n", i+1, listOfReturnedUnitsOfEachResource.get(i));

				//				//for each resource type, we are also putting zero as the units the current Task requests 
				//				tempListRequests.put(i, 0);
			}

			//'T' many Tasks need to be made and stored into a list storing all the Tasks
			for (int i = 0; i < numTasks; i ++) {
				//create and add a new Task of index i to the arrayList of tasks for Opt. resource manager
				tasksOptimistic.add(new Task(i));
				//System.out.printf("Optimistic: Task R%d has index: %d.\n", i+1, tasksOptimistic.get(i).getIndex());

				//create and add a new Task of index i to the arrayList of tasks for the Bnkr. resource manager
				tasksBanker.add(new Task(i));
				//System.out.printf("Banker: Task R%d has index: %d.\n", i+1, tasksBanker.get(i).getIndex());

				//create and add a new Task of index i to the finalized arrayList of tasks for the Opt. resource manager
				//for now, an empty Task is created and later appropriate Task will be set to appropriate index
				tasksFinalizedOpt.add(new Task(i));

				//create and add a new Task of index i to the finalized arrayList of tasks for the Bnkr. resource manager
				//for now, an empty Task is created and later appropriate Task will be set to appropriate index
				tasksFinalizedBkr.add(new Task(i));
			}

			//the first three numbers have been read, so until you reach the end of the file,
			//you are reading in different Tasks' activity informations 
			while (sc.hasNext()) {				
				//read the activity state (one of: initiate, request, release, terminate)
				//and save it to a temporary String, tempActivity
				String tempActivityType = sc.next();
				//System.out.printf("Activity: %s ", tempActivityType);

				//create an arrayList of integers to store informations of the activity
				ArrayList<Integer> activityInfo = new ArrayList<>();
				//for each activity, save the four unsigned integers 
				//that represent the information of the activity to the arraylist created above
				for (int i = 0; i < 4; i ++) {
					activityInfo.add(sc.nextInt());
					//System.out.printf("%d ",activityInfo.get(i));
				}
				//System.out.println();

				//				//get the claims for the Banker's algorithm
				//				HashMap<Integer,Integer> currentClaim = null;
				//				//if the current activity type is "initiate," save the initial claim of the current activity's resource type
				//				if (tempActivityType.equals("initiate")) {
				//					currentClaim = new HashMap<>();
				//					int claimedResourceIndex = activityInfo.get(2)-1;
				//					int claimedAmt = activityInfo.get(3);
				//					currentClaim.put(claimedResourceIndex, claimedAmt);
				//					System.out.printf("Task %d's claim of resource %d: %d\n",activityInfo.get(0), claimedResourceIndex+1,currentClaim.get(claimedResourceIndex));
				//				}

				//FOR OPTIMISTIC RESOURCE MANAGER
				//get the correct task from the list of tasks matching the 'task-number' of the current activity being read
				//System.out.printf("this is the Task %d's index from the list of tasks: %d\n\n",activityInfo.get(0), (activityInfo.get(0) - 1));
				Task tempTaskOp = tasksOptimistic.get(activityInfo.get(0) - 1);	
				//create a new Activity pair (activityName, listOfActivityInfo) using HashMap
				HashMap<String, ArrayList<Integer>> activityTemp0 = new HashMap<>();
				activityTemp0.put(tempActivityType, activityInfo);
				//save the new activity pair into the activities list
				tempTaskOp.getActivities().add(activityTemp0);

				//FOR BANKER RESOURCE MANAGER
				//get the correct task from the list of tasks matching the 'task-number' of the current activity being read
				//System.out.printf("this is the Task %d's index from the list of tasks: %d\n\n",activityInfo.get(0), (activityInfo.get(0) - 1));
				Task tempTaskBnkr = tasksBanker.get(activityInfo.get(0) - 1);
				//create a new Activity pair (activityName, listOfActivityInfo) using HashMap
				HashMap<String, ArrayList<Integer>> activityTemp1 = new HashMap<>();
				activityTemp1.put(tempActivityType, activityInfo);

				//				//if the current claim isn't null, save the current claim created to the Task we retrieved above
				//				if (currentClaim != null) {
				//					tempTaskBnkr.setClaims(currentClaim);
				//					System.out.println("claim saved!\n");
				//				}
				//save the new activity pair into the activities list
				tempTaskBnkr.getActivities().add(activityTemp1);

				//get the claims for the Banker's algorithm
				//HashMap<Integer,Integer> currentClaim = null;
				HashMap<Integer,Integer> claimsBnkr = tempTaskBnkr.getClaims();
				//if the current activity type is "initiate," save the initial claim of the current activity's resource type
				if (tempActivityType.equals("initiate")) {
					int claimedResourceIndex = activityInfo.get(2)-1;
					int claimedAmt = activityInfo.get(3);
					claimsBnkr.put(claimedResourceIndex, claimedAmt);
					//System.out.printf("Task %d's claim of resource %d: %d\n",activityInfo.get(0), claimedResourceIndex+1,claimsBnkr.get(claimedResourceIndex));
				}
				tempTaskBnkr.setClaims(claimsBnkr);

				//				placedelayer ++;
			} //end of the inner while loop for reading the file

		} //end of the outer while loop for reading the file

		/*
		//print out different activities list for each tasks of tasksOptimistic
		for (int i = 0; i < tasksOptimistic.size(); i ++) {

			Task task = tasksOptimistic.get(i);
			System.out.printf("Optimistic Manager's Task %d: \n", task.getTaskNum());

			ArrayList<HashMap<String, ArrayList<Integer>>> allActivities = task.getActivities();	
			//System.out.println(allActivities.size());
			for (int j = 0; j < allActivities.size(); j ++) {
				HashMap<String, ArrayList<Integer>> taskActivities = allActivities.get(j);
				for (Map.Entry m:taskActivities.entrySet()) { 
					System.out.println(m.getKey()+": "+m.getValue().toString()); 
				} 
			}
			System.out.println();
		} //end of the for loop for printing out activities list for debugging purposes

		//print out different activities list for each tasks of tasksBanker
		for (int i = 0; i < tasksBanker.size(); i ++) {

			Task task = tasksBanker.get(i);
			System.out.printf("Banker Manager's Task %d: \n", task.getTaskNum());

			ArrayList<HashMap<String, ArrayList<Integer>>> allActivities = task.getActivities();	
			//System.out.println(allActivities.size());
			for (int j = 0; j < allActivities.size(); j ++) {
				HashMap<String, ArrayList<Integer>> taskActivities = allActivities.get(j);
				for (Map.Entry m:taskActivities.entrySet()) { 
					System.out.println(m.getKey()+": "+m.getValue().toString()); 
				} 
			}
			System.out.println();
		}
		 */

		//FOR IMPLEMENTING THE OPTIMISTIC RESOURCE MANAGER
		boolean taskExecuted = false; //used to indicate whether a Task has been executed (if Task tried to satisfy an activity)
		//while (cycle < 7) {
		while (!tasksOptimistic.isEmpty()) { //run until there is no more task remaining in the list of tasks for the Opt. resource manager
			cycle ++; //increment to indicate time passing
			//System.out.println("\n-----------------------------\nCurrent Cycle number: " + cycle);

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
					//get the arraylist of information about the current activity
					ArrayList<Integer> activityInfos = currActivity.get("initiate"); 
					//System.out.println(Arrays.toString(activityInfos.toArray()));

					//the current task has been executed
					taskExecuted = true;

					//since the activity is initiate, the current task delays no resource of any type
					//so we put value 0 for the number of units the task delays of each resource type
					currTask.getResourcesHeld().put((activityInfos.get(2))-1, 0);
					//					for (Map.Entry m:currTask.getResourcesHeld().entrySet()) { 
					//						//System.out.println(m.getKey()+": "+m.getValue().toString()); 
					//						System.out.printf("Task %d's Resource #%d held units: %d\n",currTask.getTaskNum(),(int)m.getKey() + 1,m.getValue());
					//					} 

					//initiate has been granted, so remove the activity from the activities list
					currTask.getActivities().remove(0);
				} else {
					//if the task's activity is "request"
					if (currActivity.containsKey("request")) {
						//get the arraylist of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("request"); 
						//System.out.println("request" + Arrays.toString(activityInfos.toArray()));

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource requested by the task's activity
						int resourceRequested = activityInfos.get(3); //represents the number of the resource requested by the task's activity
						int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); //represents the available units of the resource requested
						//System.out.printf("Resource %d at index %d is requested: %d\n",resourceIndex+1,resourceIndex, resourceRequested);
						//System.out.printf("There are %d units available of Resource %d\n",resourceAvail,resourceIndex+1);

						int requestDelay = currTask.getRequestdelay(); //represents the delay counter kept by the current task
						int activityDelay = activityInfos.get(1); //represents the delay given by the current task's activity
						//System.out.printf("Task %d's Resource %d delay %d vs. requestDelay is: %d\n",currTask.getTaskNum(),resourceIndex+1,activityDelay, requestDelay);

						//check if there are enough resources for the request to be satisfied
						if (resourceRequested <= resourceAvail) {

							//if enough resources & if the requestDelay counter is equal to the activityDelay, the task's activity can be satisfied
							if (requestDelay == activityDelay) {
								//the current task has been executed
								taskExecuted = true;

								//System.out.printf("GRANTED: Task %d's request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);

								//reset the requestDelay to 0
								currTask.resetRequestdelay();

								//update the units requested for the resource as the value for the resourceHeld of requested resource type
								//this represents that current task is holding additional many units of the resource it has requested
								currTask.getResourcesHeld().put(resourceIndex,currTask.getResourcesHeld().get(resourceIndex)+resourceRequested);
								//System.out.printf("Task %d currently holds %d of resource %d\n",currTask.getTaskNum(),currTask.getResourcesHeld().get(resourceIndex),resourceIndex);

								//calculate the remaining resources available for the Opt. resource manager
								int newResourceAvail = resourceAvail - resourceRequested;
								//System.out.printf("There are %d units LEFT of Resource %d\n",newResourceAvail,resourceIndex+1);

								//update the list that stores the available units of each resource type
								listOfUnitsOfEachResource.set(resourceIndex, newResourceAvail);

								//the request has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

							} else { //the delay counter is NOT equal to the delay given by the current task's activity, 
								//so it needs to wait for the delay to finish

								//the current task has been executed
								taskExecuted = true;

								//System.out.printf("Task %d delays for request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
								//increase the delay counter
								currTask.increaseRequestdelay();
								//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getRequestdelay(),activityDelay);
							}

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");

						} else { //there AREN'T enough resources for the request to be satisfied, so wait
							//System.out.printf("WAITING: Task %d's request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
							currTask.increaseWaitTime();
							//System.out.printf("Task %d waits %d\n\n",currTask.getTaskNum(),currTask.getWaitTime());
						}
					} //end of "request" handling

					//if the task's activity is "release"
					else if (currActivity.containsKey("release")) {
						//get the arraylist of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("release"); 
						//System.out.println("release" + Arrays.toString(activityInfos.toArray()));

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource released by the task's activity
						int resourceReleased = activityInfos.get(3); //represents the number of the resource released by the task's activity
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(resourceIndex); //represents the overall returned units of the resource type
						//System.out.printf("Resource %d at index %d is released: %d\n",resourceIndex+1,resourceIndex,resourceReleased);
						//System.out.printf("There are %d units of returnedResource %d\n",resourceReturned,resourceIndex+1);

						int releasedelay = currTask.getReleasedelay(); //represents the delay counter kept by the current task
						int activitydelay = activityInfos.get(1); //represents the delay given by the current task's activity
						//System.out.printf("Task %d's Resource %d delays %d vs. requestdelay is: %d\n",currTask.getTaskNum(),resourceIndex+1,activitydelay, releasedelay);

						//if the releaseDelay counter is equal to the activityDelay, the task's activity can be satisfied
						if (releasedelay == activitydelay) {
							//the current task has been executed
							taskExecuted = true;

							//System.out.printf("RELEASED: Task %d's release for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);

							//reset the releasedelay to 0
							currTask.resetReleasedelay();

							//release the units held for the resource type specified
							currTask.getResourcesHeld().put(resourceIndex,0);
							//System.out.printf("Now Task %d delays %d of resource %d because of the release.\n",currTask.getTaskNum(),currTask.getResourcesHeld().get(resourceIndex),resourceIndex);

							//add the resource units released by the current task to the total units returned for the current resource type
							resourceReturned += resourceReleased;
							listOfReturnedUnitsOfEachResource.set(resourceIndex, resourceReturned);
							//System.out.printf("The returnedResource %d is now %d units\n",resourceIndex+1, resourceReturned);

							//request has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);

						} else { //the releasedelay counter is NOT equal to the delay given by the current task's activity, 
							//so it needs to wait for the delay to finish

							//the current task has been executed
							taskExecuted = true;

							//System.out.printf("Task %d delays for release for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
							currTask.increaseReleasedelay();
							//							System.out.printf("Task %d delays %d to release Resource %d\n",currTask.getTaskNum(),currTask.getReleasedelay(),resourceIndex+1);
							//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getReleasedelay(),activitydelay);
						}		

						//in the case where the last 'release' activity of the current task has been done
						//and there is only one activity remaining: TERMINATE, which does NOT require another cycle to complete
						if (currTask.getActivities().size() == 1 ) {
							HashMap<String, ArrayList<Integer>> finalActivity = currTask.getActivities().get(0); //we get the final activity, 'terminate'
							ArrayList<Integer> terminateInfo = finalActivity.get("terminate"); //get the info for 'terminate' activity
							//System.out.println("HERE terminate: " + Arrays.toString(terminateInfo.toArray()));

							//if the task does NOT have to delay to terminate, DO TERMINATE
							if (terminateInfo.get(1) == 0) {
								//the current task has been executed
								taskExecuted = true;

								//System.out.printf("TERMINATED: Task %d because there is NO delay for terminate\n", currTask.getTaskNum());

								//set the current cycle as the time the current task terminated
								currTask.setTimeTerminated(cycle);
								//System.out.println("Time terminated: " + currTask.getTimeTerminated());

								//terminate has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

								//System.out.println(currTask.getIndex());
								//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
								tasksFinalizedOpt.set(currTask.getIndex(), currTask);
								//remove the CURRENT TASK from the list of tasks since it is COMPLETED
								tasksOptimistic.remove(currTask); 
								i--; //do this so the order of reading from the list of tasks isn't disturbed

								//System.out.println(tasksFinalizedOpt.get(i).getTimeTerminated());
								//System.out.println("HEREEE");
							} else { //the task DOES have to delay to terminate
								//the current task has been executed
								taskExecuted = true;

								//System.out.printf("Task %d delays to terminate\n",currTask.getTaskNum());
								//increase the terminatedelay counter for the current task
								currTask.increaseTerminatedelay();
								//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getTerminatedelay(),terminateInfo.get(1));

								//pop the current Task and add it to the executedTasks list
								executedTasks.add(tasksOptimistic.remove(i));
								i--; //do this so the order of reading from the list of tasks isn't disturbed
								//System.out.println("THE TASK HAS BEEN POPPED\n");
							}
						} else { //in the case where there are still multiple activities waiting to be satisfied for the current task
							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");
						}
					} //end of "release" handling

					//if the task's activity is "terminate"
					else {	
						//get the arraylist of information about the current activity
						ArrayList<Integer> terminateInfo = currActivity.get("terminate"); 

						//the current task has been executed
						taskExecuted = true;

						//does NOT have to delay to terminate, so DO TERMINATE
						if (currTask.getTerminatedelay() == terminateInfo.get(1)) {

							//set the current cycle as the time the current task terminated
							currTask.setTimeTerminated(cycle);
							//System.out.println("Time terminated: " + currTask.getTimeTerminated());

							//terminate has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);
							//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
							tasksFinalizedOpt.set(currTask.getIndex(), currTask);
							//remove the CURRENT TASK from the list of tasks since it is COMPLETED
							tasksOptimistic.remove(currTask);
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						} else { //the task DOES have to delay to terminate
							//System.out.printf("Task %d delays to terminate\n",currTask.getTaskNum());
							//increase the terminatedelay counter for the current task
							currTask.increaseTerminatedelay();
							//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getTerminatedelay(),terminateInfo.get(1));

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksOptimistic.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");
						}
					} //end of 'terminate' handling

				} //end of handling activities other than 'initiate'				
			} ////end of the for loop for going through the entire list of tasks

			//if more than one task executed in the current cycle
			if (executedTasks.size() > 1) { 
				sortExecutedTasksList(executedTasks); //sort the list of executed tasks according to the task's index
			}

			//add the sorted executed tasks to the end of the list of tasks
			for (int z = 0; z < executedTasks.size(); z ++) { 
				tasksOptimistic.add(executedTasks.remove(z));
				z --;
			}

			//check for deadlock
			boolean canExecute = false;
			//if not a single task executed and the cycle is > 1 (meaning, some time has passed since the tasks' activities has been first executed)
			if ( !taskExecuted && cycle > 1) { //we encountered a deadlock!
				//try to resolve the deadlock
				while (!canExecute) {
					//get the Task with the lowest index
					int taskIndexToAbort = tasksOptimistic.get(0).getIndex(); //initializing the lowest index
					int indexInTasksOpt = 0; //the index of the Task with the lowest index WITHIN the list of tasks for Opt. resource manager
					//System.out.println("starts with Task: " + (taskIndexToAbort+1));
					for (int m = 1; m < tasksOptimistic.size(); m ++) {
						int nextIndex = tasksOptimistic.get(m).getIndex();
						if (taskIndexToAbort > nextIndex) { //if the currentTask's index is GREATER than the nextTask's index
							taskIndexToAbort = nextIndex; //set the currentTask to the nextTask's index that is smaller
							indexInTasksOpt = m; //save the position of the updated currentTask's WITHIN the list of tasks for Opt. resource manager
							//System.out.println("this is the task index to abort: " + taskIndexToAbort);
							//System.out.println("this is the task index in tasksOpt: " + indexInTasksOpt);
						}
					}
					//System.out.println("This is the task index to abort: " + taskIndexToAbort);
					//System.out.println("that task has index in tasksOpt: " + indexInTasksOpt);

					//this is the task to be aborted
					Task taskAbort = tasksOptimistic.get(indexInTasksOpt);
					//System.out.println(taskAbort.getActivities().get(0).get("request"));

					//set task's Abort to True
					taskAbort.doAbort();

					//release the resources of the task to be aborted
					HashMap<Integer,Integer> itsHeldRsrcs = taskAbort.getResourcesHeld();
					for (int n = 0; n < itsHeldRsrcs.size(); n ++) {
						int resourceHeld = itsHeldRsrcs.get(n); //get the units held of the current resource type
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(n); //get the currently all returned units of the current resource type
						//System.out.println("this is the resource held: " + resourceHeld);
						//System.out.println(resourceReturned);

						//add the resource release by the current task to the total units returned for the current resource
						resourceReturned += resourceHeld;
						listOfReturnedUnitsOfEachResource.set(n, resourceReturned);
						//System.out.printf("The returnedResource %d now has %d units\n",n, resourceReturned);
					}

					//System.out.println("the task that will be aborted is has index: " + taskAbort.getIndex());

					//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
					tasksFinalizedOpt.set(taskAbort.getIndex(),taskAbort);
					//remove the CURRENT TASK from the list of tasks since it is ABORTED
					tasksOptimistic.remove(indexInTasksOpt);

					//go through the modified list of tasks to see if deadlock is resolved
					for (int b = 0; b < tasksOptimistic.size(); b ++) {
						Task t = tasksOptimistic.get(b);

						HashMap<String, ArrayList<Integer>> activity0 = t.getActivities().get(0);
						for (Map.Entry m : activity0.entrySet()) { 
							//System.out.println(m.getKey()+": "+m.getValue().toString()); 
							//System.out.println((String) m.getKey());
							ArrayList<Integer> info = activity0.get((String) m.getKey()); //get activity info of current task
							//System.out.println(info.get(3));
							//System.out.println(listOfReturnedUnitsOfEachResource.get(info.get(2)-1));
							//if the sum of the units of current resource type available and the units of current resource type released
							//is enough to satisfy the current task's request
							if (info.get(3) <= listOfReturnedUnitsOfEachResource.get(info.get(2)-1) + listOfUnitsOfEachResource.get(info.get(2)-1)) {
								canExecute = true; //the deadlock is resolved and you can execute
							}					
						}						
					} //end of the for loop for going through the modified list of tasks
				} //end of the while loop for resolving the deadlock

			} else { //a Task has been running, i.e. NO deadlock
				taskExecuted = false; //reset the taskExecuted to false for the next cycle
			}


		} //end of the while loop FOR IMPLEMENTING THE OPTIMISTIC RESOURCE MANAGER

		//sort the finalized list of tasks for the Opt. resource manager by task's index
		sortExecutedTasksList(tasksFinalizedOpt); 
		//System.out.println("IN THE END: " + tasksFinalizedOpt.size());		
		//print out each Task info after the Optimistic resource manager finishes
		System.out.printf("\n-----------------------------\n%14s\n","FIFO");
		for (Task ee: tasksFinalizedOpt) { //go through the sorted finalized list of tasks
			//System.out.println(ee.getTimeTerminated());
			//print each Task's info for the time taken, the waiting time, and the percentage of time spent waiting
			ee.printTaskInfo(); 
			totalTime += ee.getTimeTerminated(); //add up all Task's time taken
			totalWaitingTime += ee.getWaitTime(); //add up all Task's waiting time
		}

		//System.out.println( (int) Math.rint((totalWaitingTime/(totalTime*1.0)) * 100));
		//int percentage = (int) Math.rint((totalWaitingTime/(totalTime*1.0)) * 100);
		//System.out.printf("total %8d %4d %5d%s",totalTime,totalWaitingTime,100,"%");

		//Print the total time for all tasks, the total waiting time, and the overall percentage of time spent waiting
		System.out.printf("total %8d %4d %5d%s",(int) totalTime,(int) totalWaitingTime,Math.round((totalWaitingTime/(totalTime*1.0)) * 100),"%");
		System.out.println("\n-----------------------------");



		//FOR IMPLEMENTING THE BANKER'S RESOURCE MANAGER
		//reset the shared variables to default value of 0
		cycle = 0;
		totalTime = 0;
		totalWaitingTime = 0;
		boolean isSafeState = true;
		//while (cycle < 5) {
		while (!tasksBanker.isEmpty()) { //run until there is no more task remaining in the list of tasks for the Banker's resource manager
			cycle ++; //increment to indicate time passing
			//System.out.println("\n-----------------------------\nCurrent Cycle number: " + cycle);

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
					//get the arraylist of information about the current activity
					ArrayList<Integer> activityInfos = currActivity.get("initiate"); 
					//System.out.println("initiate " + Arrays.toString(activityInfos.toArray()));

					//handles the error where a tasks's initial claim EXCEEDS the resource present
					int resourceIndex = activityInfos.get(2)-1; //the index of the resource
					int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); //represents the available units of the resource claimed
					HashMap<Integer,Integer> currentClaims = currTask.getClaims();
					int resourceInitClaimed = currentClaims.get(resourceIndex);
					//if the amount of current resource type claimed is less than or equal to the amount of the resource type available
					if (resourceInitClaimed <= resourceAvail) {
						//no error, proceed with granting the 'initiate' activity
						//since the activity is initiate, the current task delays no resource of any type
						//so we put value 0 for the number of units the task delays of each resource type
						currTask.getResourcesHeld().put((activityInfos.get(2))-1, 0);
						//								for (Map.Entry m:currTask.getResourcesHeld().entrySet()) { 
						//									//System.out.println(m.getKey()+": "+m.getValue().toString()); 
						//									System.out.printf("Task %d's Resource #%d held units: %d\n",currTask.getTaskNum(),(int)m.getKey() + 1,m.getValue());
						//								} 

						//initiate has been granted, so remove the activity from the activities list
						currTask.getActivities().remove(0);
					} else { //error, bc the tasks's initial claim EXCEEDS the resource present
						//ABORT the task
						currTask.doAbort();
						int indexToAbort = i;
						//System.out.println("this is currTask index: " + currTask.getIndex());
						//System.out.println("this is index from i: " + i);
						//System.out.printf("ABORTED: Task %d\n",i+1);
						//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
						tasksFinalizedBkr.set(currTask.getIndex(),currTask);
						//remove the CURRENT TASK from the list of tasks since it is ABORTED
						tasksBanker.remove(i);
						i--; //do this so the order of reading from the list of tasks isn't disturbed

						error += String.format("Banker aborts Task %d before run begins:\n",currTask.getTaskNum());
						error += String.format("\tclaim for resource %d (%d) exceeds number of units present (%d)\n",resourceIndex+1,resourceInitClaimed,resourceAvail);
					}

				} else {
					//if the task's activity is "request"
					if (currActivity.containsKey("request")) {
						//get the arraylist of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("request"); 
						//System.out.println("request" + Arrays.toString(activityInfos.toArray()));

						HashMap<Integer,Integer> allClaims = currTask.getClaims();

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource requested by the task's activity
						int resourceRequested = activityInfos.get(3); //represents the number of the resource requested by the task's activity
						int resourceAvail = listOfUnitsOfEachResource.get(resourceIndex); //represents the available units of the resource requested
						//System.out.printf("Resource %d at index %d is requested: %d\n",resourceIndex+1,resourceIndex, resourceRequested);
						//System.out.printf("There are %d units available of Resource %d\n",resourceAvail,resourceIndex+1);

						int resourceClaimed = allClaims.get(resourceIndex); //represents the amount claimed of the current resource type by the current task
						//System.out.println("resourceClaimed: " + resourceClaimed);

						int requestDelay = currTask.getRequestdelay(); //represents the delay counter kept by the current task
						int activityDelay = activityInfos.get(1); //represents the delay given by the current task's activity
						//System.out.printf("Task %d's Resource %d delay %d vs. requestDelay is: %d\n",currTask.getTaskNum(),resourceIndex+1,activityDelay, requestDelay);

						isSafeState = true;
						for (Map.Entry m:currTask.getClaims().entrySet()) { 
							int claimResourceIndex = ((Integer) m.getKey()).intValue();
							//System.out.println("claimResourceIndex: " + claimResourceIndex);
							int numClaims = ((Integer) m.getValue()).intValue();
							//System.out.println("numClaims: " + numClaims);

							//System.out.printf("Resource %d's claim: %d\n",(int)m.getKey() + 1,m.getValue()); 

							//if the current task's number of claim for the current resource type EXCEEDS the number if resources available
							if (numClaims > listOfUnitsOfEachResource.get(claimResourceIndex)) {
								isSafeState = false;
							}
							//System.out.printf("Task %d's Resource #%d held units: %d\n",currTask.getTaskNum(),(int)m.getKey() + 1,m.getValue());
						} 
						//System.out.println("is safe state to run: " + isSafeState);

						//IF DON'T HAVE TO WAIT FOR DELAY
						if (requestDelay == activityDelay) {			

							//check if there are enough resources for the CLAIM to be satisfied
							if (resourceClaimed <= resourceAvail) {
								//System.out.printf("FIRST: Task %d's claim: %d VS. available resources: %d.\n",currTask.getTaskNum(),resourceClaimed,resourceAvail);
								//System.out.printf("Task %d has enough resources for the claim to be satisfied!\n",currTask.getTaskNum());

								//check if it is SafeState 
								if (isSafeState ) {
									//check if the current task's resources requested DOES NOT EXCEED its claim
									if (resourceRequested <= resourceClaimed) {
										//System.out.printf("Task %d's resource requested DOES NOT EXCEED its claim!\n",currTask.getTaskNum());
										//System.out.printf("Task %d's requested resources: %d VS. claim: %d .\n",currTask.getTaskNum(),resourceRequested,resourceClaimed);

										//System.out.printf("GRANTED: Task %d's request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);

										//reset the requestDelay to 0
										currTask.resetRequestdelay();

										//calculate the claim remaining for the current requested resource type
										resourceClaimed -= resourceRequested;
										//update the list that stores the claims of each resource type
										allClaims.put(resourceIndex, resourceClaimed);
										//System.out.printf("There are %d claim LEFT of Resource %d\n",allClaims.get(resourceIndex),resourceIndex+1);						
										//set the updated list of current task that stores the claims of each resource type 
										currTask.setClaims(allClaims);
										for (Map.Entry m:currTask.getClaims().entrySet()) { 
											//System.out.printf("Resource %d's remaining claim: %d\n",(int)m.getKey() + 1,m.getValue()); 
											//System.out.printf("Task %d's Resource #%d held units: %d\n",currTask.getTaskNum(),(int)m.getKey() + 1,m.getValue());
										} 

										for (Map.Entry m:currTask.getResourcesHeld().entrySet()) { 
											//System.out.printf("Resource %d's remaining claim: %d\n",(int)m.getKey() + 1,m.getValue()); 
											//System.out.printf("Task %d's Resource #%d held units: %d\n",currTask.getTaskNum(),(int)m.getKey() + 1,m.getValue());
											//System.out.printf("before: Task %d currently holds %d of resource %d\n",currTask.getTaskNum(),m.getValue(),(int)m.getKey()+1);
										} 

										//set the units requested for the resource as the value for the resourceHeld of requested resource type
										//this represents that current task is holding additional many units of the resource it has requested
										currTask.getResourcesHeld().put(resourceIndex,currTask.getResourcesHeld().get(resourceIndex)+resourceRequested);
										//System.out.printf("now: Task %d currently holds %d of resource %d\n",currTask.getTaskNum(),currTask.getResourcesHeld().get(resourceIndex),resourceIndex+1);

										//calculate the remaining resources available for the Opt. resource manager
										int newResourceAvail = resourceAvail - resourceRequested;
										//System.out.printf("There are %d units LEFT of Resource %d\n",newResourceAvail,resourceIndex+1);

										//update the list that stores the available units of each resource type
										listOfUnitsOfEachResource.set(resourceIndex, newResourceAvail);

										//the request has been granted, so remove the activity from the activities list
										currTask.getActivities().remove(0);

										//pop the current Task and add it to the executedTasks list
										executedTasks.add(tasksBanker.remove(i));
										i--; //do this so the order of reading from the list of tasks isn't disturbed
										//System.out.println("THE TASK HAS BEEN POPPED\n");

									} else { //the current task's resources requested DOES EXCEED its claim --> ERROR!
										//System.out.printf("ERROR: Task %d's RESOURCE REQUESTED does EXCEED its claim!\n\n",currTask.getTaskNum());

										String errorInfo = "";

										//so ABORT
										currTask.doAbort();

										//release all the resources of the task to be aborted
										HashMap<Integer,Integer> itsHeldRsrcs = currTask.getResourcesHeld();
										for (int n = 0; n < itsHeldRsrcs.size(); n ++) {
											int resourceHeld = itsHeldRsrcs.get(n); //get the units held of the current resource type
											int resourceReturned = listOfReturnedUnitsOfEachResource.get(n); //get the currently all returned units of the current resource type
											//System.out.println("this is the resource held: " + resourceHeld);
											//System.out.println(resourceReturned);

											//add the resource release by the current task to the total units returned for the current resource
											resourceReturned += resourceHeld;
											listOfReturnedUnitsOfEachResource.set(n, resourceReturned);
											//System.out.printf("The returnedResource %d now has %d units\n",n, resourceReturned);

											errorInfo += String.format(" %d unit of resource %d", resourceReturned, n+1);
										}

										//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
										tasksFinalizedBkr.set(currTask.getIndex(), currTask);
										//remove the CURRENT TASK from the list of tasks since it is COMPLETED
										tasksBanker.remove(currTask);

										error += String.format("During cycle %d-%d of Banker's algorithms\n",cycle-1,cycle);
										error += String.format("\tTask %d's request exceeds its claim; aborted;",currTask.getTaskNum());
										error += errorInfo;
										error += " available next cycle\n";
									}
									
								} else {
									//so the request CANNOT be satisfied, so WAIT

									//System.out.printf("SECOND: Task %d's claim: %d VS. available resources: %d.\n",currTask.getTaskNum(),resourceClaimed,resourceAvail);
									//System.out.printf("Task %d does NOT HAVE enough resources for the claim to be satisfied!\n",currTask.getTaskNum());
									//System.out.printf("WAITING: Task %d's request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
									currTask.increaseWaitTime();
									//System.out.printf("Task %d waits %d\n\n",currTask.getTaskNum(),currTask.getWaitTime());
								}
								
							} else { //the claim is bigger than the resources available
								//so the request CANNOT be satisfied, so WAIT

								//System.out.printf("SECOND: Task %d's claim: %d VS. available resources: %d.\n",currTask.getTaskNum(),resourceClaimed,resourceAvail);
								//System.out.printf("Task %d does NOT HAVE enough resources for the claim to be satisfied!\n",currTask.getTaskNum());
								//System.out.printf("WAITING: Task %d's request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
								currTask.increaseWaitTime();
								//System.out.printf("Task %d waits %d\n\n",currTask.getTaskNum(),currTask.getWaitTime());
							}

						} else { //the delay counter is NOT equal to the delay given by the current task's activity, 
							//so there is a DELAY and the current task needs to wait for the delay to finish

							//System.out.printf("Task %d delays for request for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
							//increase the delay counter
							currTask.increaseRequestdelay();
							//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getRequestdelay(),activityDelay);

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");
						}
					} //end of "request" handling

					//if the task's activity is "release"
					else if (currActivity.containsKey("release")) {
						//get the arraylist of information about the current activity
						ArrayList<Integer> activityInfos = currActivity.get("release"); 
						//System.out.println("release" + Arrays.toString(activityInfos.toArray()));

						int resourceIndex = activityInfos.get(2)-1; //represents the index of the resource released by the task's activity
						int resourceReleased = activityInfos.get(3); //represents the number of the resource released by the task's activity
						int resourceReturned = listOfReturnedUnitsOfEachResource.get(resourceIndex); //represents the overall returned units of the resource type
						//System.out.printf("Resource %d at index %d is released: %d\n",resourceIndex+1,resourceIndex,resourceReleased);
						//System.out.printf("There are %d units of returnedResource %d\n",resourceReturned,resourceIndex+1);

						int releasedelay = currTask.getReleasedelay(); //represents the delay counter kept by the current task
						int activitydelay = activityInfos.get(1); //represents the delay given by the current task's activity
						//System.out.printf("Task %d's Resource %d delays %d vs. requestdelay is: %d\n",currTask.getTaskNum(),resourceIndex+1,activitydelay, releasedelay);

						//if the releaseDelay counter is equal to the activityDelay, the task's activity can be satisfied
						if (releasedelay == activitydelay) {

							//System.out.printf("RELEASED: Task %d's release for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);

							//reset the releasedelay to 0
							currTask.resetReleasedelay();

							//get the task's current claim for the resource type that is being released
							int currentClaim = currTask.getClaims().get(resourceIndex);
							//System.out.printf("this is the remaining claim of resource %d: %d\n",resourceIndex+1,currentClaim);
							//recalculate the claim because resource has been released
							currTask.getClaims().put(resourceIndex,currentClaim + resourceReleased);
							//System.out.printf("this is the NEW claim of resource %d: %d\n",resourceIndex+1,currTask.getClaims().get(resourceIndex));

							//release the units held for the resource type specified
							currTask.getResourcesHeld().put(resourceIndex,0);
							//System.out.printf("Now Task %d delays %d of resource %d because of the release.\n",currTask.getTaskNum(),currTask.getResourcesHeld().get(resourceIndex),resourceIndex);

							//add the resource units released by the current task to the total units returned for the current resource type
							resourceReturned += resourceReleased;
							listOfReturnedUnitsOfEachResource.set(resourceIndex, resourceReturned);
							//System.out.printf("The returnedResource %d is now %d units\n",resourceIndex+1, resourceReturned);

							//request has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);

						} else { //the releasedelay counter is NOT equal to the delay given by the current task's activity, 
							//so it needs to wait for the delay to finish

							//System.out.printf("Task %d delays for release for Resource %d!\n",currTask.getTaskNum(),resourceIndex+1);
							currTask.increaseReleasedelay();
							//									System.out.printf("Task %d delays %d to release Resource %d\n",currTask.getTaskNum(),currTask.getReleasedelay(),resourceIndex+1);
							//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getReleasedelay(),activitydelay);
						}		

						//in the case where the last 'release' activity of the current task has been done
						//and there is only one activity remaining: TERMINATE, which does NOT require another cycle to complete
						if (currTask.getActivities().size() == 1 ) {
							HashMap<String, ArrayList<Integer>> finalActivity = currTask.getActivities().get(0); //we get the final activity, 'terminate'
							ArrayList<Integer> terminateInfo = finalActivity.get("terminate"); //get the info for 'terminate' activity
							//System.out.println("HERE terminate: " + Arrays.toString(terminateInfo.toArray()));

							//if the task does NOT have to delay to terminate, DO TERMINATE
							if (terminateInfo.get(1) == 0) {

								//System.out.printf("TERMINATED: Task %d because there is NO delay for terminate\n", currTask.getTaskNum());

								//set the current cycle as the time the current task terminated
								currTask.setTimeTerminated(cycle);
								//System.out.println("Time terminated: " + currTask.getTimeTerminated());

								//terminate has been granted, so remove the activity from the activities list
								currTask.getActivities().remove(0);

								//System.out.println(currTask.getIndex());
								//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
								tasksFinalizedBkr.set(currTask.getIndex(), currTask);
								//remove the CURRENT TASK from the list of tasks since it is COMPLETED
								tasksBanker.remove(currTask); 
								i--; //do this so the order of reading from the list of tasks isn't disturbed

								//System.out.println(tasksFinalizedBkr.get(i).getTimeTerminated());
								//System.out.println("HEREEE");
							} else { //the task DOES have to delay to terminate

								//System.out.printf("Task %d delays to terminate\n",currTask.getTaskNum());
								//increase the terminatedelay counter for the current task
								currTask.increaseTerminatedelay();
								//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getTerminatedelay(),terminateInfo.get(1));

								//pop the current Task and add it to the executedTasks list
								executedTasks.add(tasksBanker.remove(i));
								i--; //do this so the order of reading from the list of tasks isn't disturbed
								//System.out.println("THE TASK HAS BEEN POPPED\n");
							}
						} else { //in the case where there are still multiple activities waiting to be satisfied for the current task
							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");
						}
					} //end of "release" handling

					//if the task's activity is "terminate"
					else {	
						//get the arraylist of information about the current activity
						ArrayList<Integer> terminateInfo = currActivity.get("terminate"); 

						//does NOT have to delay to terminate, so DO TERMINATE
						if (currTask.getTerminatedelay() == terminateInfo.get(1)) {

							//set the current cycle as the time the current task terminated
							currTask.setTimeTerminated(cycle);
							//System.out.println("Time terminated: " + currTask.getTimeTerminated());

							//terminate has been granted, so remove the activity from the activities list
							currTask.getActivities().remove(0);
							//store the current task to the right index in the finalized list holding tasks for Opt. resource manager
							tasksFinalizedBkr.set(currTask.getIndex(), currTask);
							//remove the CURRENT TASK from the list of tasks since it is COMPLETED
							tasksBanker.remove(currTask);
							i--; //do this so the order of reading from the list of tasks isn't disturbed
						} else { //the task DOES have to delay to terminate
							//System.out.printf("Task %d delays to terminate\n",currTask.getTaskNum());
							//increase the terminatedelay counter for the current task
							currTask.increaseTerminatedelay();
							//System.out.printf("COMPUTING: Task %d's (%d of %d cycles)\n",currTask.getTaskNum(),currTask.getTerminatedelay(),terminateInfo.get(1));

							//pop the current Task and add it to the executedTasks list
							executedTasks.add(tasksBanker.remove(i));
							i--; //do this so the order of reading from the list of tasks isn't disturbed
							//System.out.println("THE TASK HAS BEEN POPPED\n");
						}
					} //end of 'terminate' handling

				} //end of handling activities other than 'initiate'				
			} ////end of the for loop for going through the entire list of tasks

			//if more than one task executed in the current cycle
			if (executedTasks.size() > 1) { 
				sortExecutedTasksList(executedTasks); //sort the list of executed tasks according to the task's index
			}

			//add the sorted executed tasks to the end of the list of tasks
			for (int z = 0; z < executedTasks.size(); z ++) { 
				tasksBanker.add(executedTasks.remove(z));
				z --;
			}

		} //end of the while loop FOR IMPLEMENTING THE BANKER's RESOURCE MANAGER

		//sort the finalized list of tasks for the Bnkr. resource manager by task's index
		sortExecutedTasksList(tasksFinalizedBkr); 
		//System.out.println("IN THE END: " + tasksFinalizedBkr.size());		

		//print any error detected
		System.out.println("\n-----------------------------\n" + error);
		//print out each Task info after the Banker's resource manager finishes
		System.out.printf("%14s\n","BANKER'S");
		for (Task ee: tasksFinalizedBkr) { //go through the sorted finalized list of tasks
			//System.out.println(ee.getTimeTerminated());
			//print each Task's info for the time taken, the waiting time, and the percentage of time spent waiting
			ee.printTaskInfo(); 
			totalTime += ee.getTimeTerminated(); //add up all Task's time taken
			totalWaitingTime += ee.getWaitTime(); //add up all Task's waiting time
		}

		//System.out.println( (int) Math.rint((totalWaitingTime/(totalTime*1.0)) * 100));
		//int percentage = (int) Math.rint((totalWaitingTime/(totalTime*1.0)) * 100);
		//System.out.printf("total %8d %4d %5d%s",totalTime,totalWaitingTime,100,"%");

		//Print the total time for all tasks, the total waiting time, and the overall percentage of time spent waiting
		System.out.printf("total %8d %4d %5d%s",(int) totalTime,(int) totalWaitingTime,Math.round((totalWaitingTime/(totalTime*1.0)) * 100),"%");
		System.out.println("\n-----------------------------");



	} //end of the main function

} //end of the Banker class
