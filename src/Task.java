import java.util.*;

/**
 * The Task class contains all the information about a Task.
 * It includes an ArrayList of HashMap of <String, ArrayList<Integer>> pair that represents a list of all the activities of a task
 * 		**note that an activity is specified by a String (tells you the type of an activity: initiate, request, release, and terminate)
 *        and four unsigned integers that indicate information about the activity.
 * It also contains other private instance variables and public methods to store/manage information about the Task and the state of the Task.
 * It has one constructor that takes in an integer as its parameter.
 *            
 * @author gayeonpark
 *
 */
public class Task {
	
	//represents an ArrayList of HashMap of <String, ArrayList<Integer>> pair that represents a list of all the activities of a task
	// note that an activity is specified by a String (tells you the type of an activity: initiate, request, release, and terminate)
	// and four unsigned integers that indicate information about the activity.
	private ArrayList<HashMap<String, ArrayList<Integer>>> activities;

	private int index; //represents the index of the task, i.e. Task 1 has an index of 0
	private int taskNum; //represents the number of the task, i.e. Task 1 has a taskNum of 1
	private int waitTime; //represents the time the task waits to execute an activity
	private int delayForRequest; //represents the delay counter for request activity
	private int delayForRelease; //represents the delay counter for release activity
	private int delayForTerminate; //represents the delay counter for terminate activity
	private int timeTerminated; //represents the time the task terminated
	private boolean taskAborted; //represents if the task has been aborted or not, i.e. false means task has NOT been aborted
	private HashMap<Integer,Integer> resourcesHeld; //represents the list of resources HELD by the task. A resource held is represented as
														//a (Integer,Integer) pair: the index of the resource held, the number of units held 
	private HashMap<Integer,Integer> claims; //represents the list of resources CLAIMED by the task. A resource claimed is represented as
												//a (Integer,Integer) pair: the index of the resource claimed, the number of units claimed 
	
	/**
	 * This constructor initializes the values for its private instance variables
	 * and sets the index of the task as the value passed in by the parameter.
	 * @param i represents the index of the task.
	 */
	Task (int i){
		activities = new ArrayList<HashMap<String, ArrayList<Integer>>>();
		index = i;
		taskNum = i + 1;
		waitTime = 0;
		delayForRequest = 0;
		delayForRelease = 0;
		delayForTerminate = 0;
		timeTerminated = 0;
		taskAborted = false;
		resourcesHeld = new HashMap<>();
		claims = new HashMap<>();
	}
	
	/**
	 * This method returns the list of the activities of the task. 
	 * @return activities, an ArrayList of HashMaps<String, ArrayList<Integer>> 
	 * 			that represent the activities of the task.
	 */
	public ArrayList<HashMap<String, ArrayList<Integer>>> getActivities() {
		return activities;
	}
	
	/**
	 * This method sets the ArrayList of HashMaps<String, ArrayList<Integer>> that was passed in from the parameter 
	 * as its list of the activities of the task.
	 * @param actvts, an ArrayList of HashMaps<String, ArrayList<Integer>> that represent a list of activities.
	 * 			In context of this project, the actvts would actually represent the modified list of activities of the task.
	 */
	public void setActivities(ArrayList<HashMap<String, ArrayList<Integer>>> actvts) {
		activities = actvts;
	}
	
	/**
	 * This method returns the index of the task.
	 * @return index, an int value that represents the index of the task.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * This method returns the numbering of the task, 
	 *  i.e. Task 1 has taskNum of 1. 
	 *    Then 1 is the numbering of the task so 1 gets returned.
	 * @return taskNum,an int value that represents the numbering of the task.
	 */
	public int getTaskNum() {
		return taskNum;
	}
	
	/**
	 * This method returns the time task waits until it is terminated or aborted.
	 * @return waitTime, an int value that represents the waiting time of the task.
	 */
	public int getWaitTime() {
		return waitTime;
	}
	
	/**
	 * This method increases the waiting time of the task by one unit.
	 */
	public void increaseWaitTime() {
		waitTime ++;
	}
	
	/**
	 * This method returns the delay counter of the task for request activity.
	 * @return delayForRequest, an int value that represents the delay counter of the task for request activity.
	 */
	public int getRequestdelay() {
		return delayForRequest;
	}
	
	/**
	 * This method increase the delay counter for the request activity by one unit. 
	 */
	public void increaseRequestdelay() {
		delayForRequest ++;
	}
	
	/**
	 * This method resets the delay counter for the request activity to 0. 
	 */
	public void resetRequestdelay() {
		delayForRequest = 0;
	}
	
	/**
	 * This method returns the delay counter of the task for release activity.
	 * @return delayForRelease, an int value that represents the delay counter of the task for release activity.
	 */
	public int getReleasedelay() {
		return delayForRelease;
	}
	
	/**
	 * This method increase the delay counter for the release activity by one unit. 
	 */
	public void increaseReleasedelay() {
		delayForRelease ++;
	}
	
	/**
	 * This method resets the delay counter for the release activity to 0. 
	 */
	public void resetReleasedelay() {
		delayForRelease = 0;
	}
	
	/**
	 * This method returns the delay counter of the task for terminate activity.
	 * @return delayForTerminate, an int value that represents the delay counter of the task for terminate activity.
	 */
	public int getTerminatedelay() {
		return delayForTerminate;
	}
	
	/**
	 * This method increase the delay counter for the terminate activity by one unit. 
	 */
	public void increaseTerminatedelay() {
		delayForTerminate ++;
	}
	
	/**
	 * This method resets the delay counter for the terminate activity to 0. 
	 */
	public void resetTerminatedelay() {
		delayForTerminate = 0;
	}
	
	/**
	 * This method returns the time the task terminated at.
	 * @return timeTerminated, an int value that represents the time the task terminated at.
	 */
	public int getTimeTerminated() {
		return timeTerminated;
	}
	
	/**
	 * This method sets the time the task terminated as the int value that was passed in as a parameter.
	 * @param finishT, an int value that represents the time the task terminated at.
	 */
	public void setTimeTerminated(int finishT) {
		timeTerminated = finishT;
	}
	
	/**
	 * This method returns a boolean value that indicates if the task has been aborted.
	 * 	i.e. The return value of 'true' would mean that the task HAS been aborted
	 *  and the return value of 'false' would mean that the task has NOT bee aborted.
	 * @return taskAborted, a boolean value that indicates if the task has been aborted.
	 */
	public boolean isAborted() {
		return taskAborted;
	}
	
	/**
	 * This method carries out appropriate changes to the task since it is to be aborted.
	 * taskAborted boolean variable is set to true (indicating that yes, the task HAS been aborted).
	 * waitTime is set to 0 since the task has been aborted and cannot complete any of its activities, 
	 *   i.e. no waiting time.
	 * timeTerminated is set to 0 since the task has been aborted and cannot complete any of its activities, 
	 *   i.e. no time the task terminated.
	 */
	public void doAbort() {
		taskAborted = true;
		waitTime = 0;
		timeTerminated = 0;
	}
	
	/**
	 * This method returns HashMap that contains (Integer,Integer) pairs, which represent the resources held by the task.
	 * @return resourcesHeld, a HashMap that contains <Integer,Integer> pairs: (the index of the resource held, the number of units held) 
	 */
	public HashMap<Integer,Integer> getResourcesHeld() {
		return resourcesHeld;
	}
	
	/**
	 * This method sets resourcesHeld, the list of resources held by the task, as the HashMap<Integer,Integer> 'l'.
	 * In context of this project, the 'l' would actually represent the modified list of resources held by the task.
	 * @param l, a HashMap of <Integer,Integer> that represent a list of resources held by the task.
	 */
	public void setResourcesHeld(HashMap<Integer,Integer> l) {
		resourcesHeld = l;
	}
	
	/**
	 * This method returns HashMap that contains (Integer,Integer) pairs, which represent the resources claimed by the task.
	 * @return claims, a HashMap that contains <Integer,Integer> pairs: (the index of the resource claimed, the number of units claimed)
	 */
	public HashMap<Integer,Integer> getClaims() {
		return claims;
	}
	
	/**
	 * This method sets claims, the list of resources claimed by the task, as the HashMap<Integer,Integer> 'c'.
	 * In context of this project, the 'c' would actually represent the modified list of resources claimed by the task.
	 * @param c, a HashMap of <Integer,Integer> that represent a list of resources claimed by the task.
	 */
	public void setClaims(HashMap<Integer,Integer> c) {
		claims = c;
	}
	
	/**
	 * This method prints the output for the task as standard output, System.out.println.
	 * The output differs depending on whether the task has been aborted or not.
	 * If the task has been aborted, a "aborted" message is printed.
	 * Otherwise, the time taken, the waiting time, and the percentage of time spent waiting is printed in a formatted way.
	 */
	public void printTaskInfo() {
		//first store the task number to the string, 'taskResult'
		String taskResult = String.format("Task %d", getTaskNum());
		//if the task has been aborted
		if (taskAborted) { 
			//abort message is stored to taskResult
			taskResult += String.format("       %s","aborted");
		} else { //else, the task has NOT been aborted		
			//store the time taken, the waiting time, and the percentage of time spent waiting to taskResult
			taskResult += String.format("%8d %4d %5d%s",timeTerminated, waitTime, Math.round((waitTime/(timeTerminated * 1.0)) * 100),"%");			
		}
		//prints taskResult, the task's result information, as standard output
		System.out.println(taskResult);	
	}

}
