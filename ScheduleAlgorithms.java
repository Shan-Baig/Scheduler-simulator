import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Implementation of 6 scheduling algorithms:
 * 1. First Come First Serve (FCFS)
 * 2. Round Robin (RR)
 * 3. Shortest Process Next (SPN)
 * 4. Shortest Remaining Time (SRT)
 * 5. Highest Response Ratio Next (HRRN)
 * 6. Feedback (FB)
 */
public class ScheduleAlgorithms {
    private int[][] jobTimes;       // arrival and service time array
    private int numOfJobs;          // total number of jobs
    private List<String> jobNames;  // job names

public static void main(String[] args) {
    // check if enough args and is formatted correctly
    if (args.length != 1) {
        System.out.println(
            "ERROR: Invalid number of arguments supplied! Please run this program with any of the following arguments: FCFS, RR, SPN, SRT, HRRN, FB, ALL");
        System.exit(1);
    } else if (!args[0].toLowerCase().equals("fcfs") && !args[0].toLowerCase().equals("rr")
            && !args[0].toLowerCase().equals("spn") && !args[0].toLowerCase().equals("srt")
            && !args[0].toLowerCase().equals("hrrn") && !args[0].toLowerCase().equals("fb")
            && !args[0].toLowerCase().equals("all")) {
        System.out.println(
            "ERROR: Invalid argument supplied! Please run this program with any of the following arguments: FCFS, RR, SPN, SRT, HRRN, FB, ALL");
        System.exit(1);
    }
    // Set scheduling algorithm
    String algType = args[0].toUpperCase();

    // Checks if file exists
    File fileName = new File("jobs.txt");
    if (!fileName.isFile()) {
        System.out.println("ERROR: Cannot find jobs.txt! Please provide this text file to run this program.");
        System.exit(1);
    }

        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            List<String> jobNames = new ArrayList<>();
            Queue<Integer> queue = new LinkedList<>();
            String line = br.readLine();
            while (line != null) {
                String[] split = line.split("\\t");
                jobNames.add(split[0]);
                int arrivalTime = Integer.valueOf(split[1]);
                int serviceTime = Integer.valueOf(split[2]);
                queue.add(arrivalTime);
                queue.add(serviceTime);

                line = br.readLine();
            }

            ScheduleAlgorithms schAlg = new ScheduleAlgorithms(queue, jobNames);
            Map<String, Map<String, Queue<Integer>>> result = new HashMap<>();
            switch (algType) {
                case "FCFS": {
                    Map<String, Queue<Integer>> resultMap = schAlg.FCFS();
                    result.put("FCFS", resultMap);
                    break;
                }
                case "RR": {
                    Map<String, Queue<Integer>> resultMap = schAlg.RR();
                    result.put("RR", resultMap);
                    break;
                }
                case "SPN": {
                    Map<String, Queue<Integer>> resultMap = schAlg.SPN();
                    result.put("SPN", resultMap);
                    break;
                }
                case "SRT": {
                    Map<String, Queue<Integer>> resultMap = schAlg.SRT();
                    result.put("SRT", resultMap);
                    break;
                }
                case "HRRN": {
                    Map<String, Queue<Integer>> resultMap = schAlg.HRRN();
                    result.put("HRRN", resultMap);
                    break;
                }
                case "FB": {
                    Map<String, Queue<Integer>> resultMap = schAlg.FB();
                    result.put("FB", resultMap);
                    break;
                }
                case "ALL": {
                    Map<String, Queue<Integer>> resultMapFCFS = schAlg.FCFS();
                    result.put("FCFS", resultMapFCFS);
                    Map<String, Queue<Integer>> resultMapRR = schAlg.RR();
                    result.put("RR", resultMapRR);
                    Map<String, Queue<Integer>> resultMapsSPN = schAlg.SPN();
                    result.put("SPN", resultMapsSPN);
                    Map<String, Queue<Integer>> resultMapSRT = schAlg.SRT();
                    result.put("SRT", resultMapSRT);
                    Map<String, Queue<Integer>> resultMapHRRN = schAlg.HRRN();
                    result.put("HRRN", resultMapHRRN);
                    Map<String, Queue<Integer>> resultMapFB = schAlg.FB();
                    result.put("FB", resultMapFB);
                    break;
                }
            }

            Set<String> key = result.keySet();
            for (String algorithmType : key) {
                System.out.println("The scheduling of " + algorithmType + " is:");
                Map<String, Queue<Integer>> scheduleMap = result.get(algorithmType);

                for (int i = 0; i < scheduleMap.size(); i++) {
                    String jobName = jobNames.get(i);
                    System.out.print(jobName + " ");
                    Queue<Integer> timeIntervals = scheduleMap.get(jobName);
                    int prevTime = 0;
                    while (!timeIntervals.isEmpty()) {
                        int start = timeIntervals.poll();
                        int stop = timeIntervals.poll();

                        // print spaces = job is not executed
                        for (int j = 0; j < start - prevTime; j++)
                            System.out.print(" ");
                        // print X = job executed
                        for (int j = 0; j < stop - start; j++)
                            System.out.print("X");
                        prevTime = stop;
                    }
                    System.out.println();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScheduleAlgorithms(Queue<Integer> queue, List<String> jobNames) {
        this.numOfJobs = queue.size();
        this.jobNames = jobNames;

        initjobTimes(queue);
    }

    //Initialize the arrival and service time array
    private void initjobTimes(Queue<Integer> queue) {
        jobTimes = new int[numOfJobs / 2][2];

        for (int i = 0; i < numOfJobs / 2; i++) {
            jobTimes[i][0] = queue.poll();
            jobTimes[i][1] = queue.poll();
        }

    }

    //First Come First Serve algorithm
    public Map<String, Queue<Integer>> FCFS() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>(); // store job scheduling time intervals

        int current = jobTimes[0][0];
        for (int i = 0; i < jobTimes.length; i++) {
            String jobName = jobNames.get(i);
            Queue<Integer> timeInterval = new LinkedList<>();
            timeInterval.add(current);
            timeInterval.add(current + jobTimes[i][1]);
            resultMap.put(jobName, timeInterval);

            if (i != jobTimes.length - 1)
                // compare the current job's finish time with the next job's arrival time
                // if next job's arrival time is larger, update current to the next job's arrival time
                // otherwise, update current to the current job's finish time
                current = jobTimes[i + 1][0] > current + jobTimes[i][1] ? jobTimes[i + 1][0] : current + jobTimes[i][1];
        }

        return resultMap;
    }

    //Round Robin algorithm (Quantum = 1)
    public Map<String, Queue<Integer>> RR() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainingTimeList = new ArrayList<>();
        for (int i = 0; i < jobNames.size(); i++) {
            arrivalTimeList.add(jobTimes[i][0]);
            remainingTimeList.add(jobTimes[i][1]);
        }

        String selectedJob = jobNames.get(0);
        int selectedJobIdx = 0;
        int selectedJobStartT = jobTimes[0][0];
        int time = jobTimes[0][0]; // time starts from the first job's arrival time
        int jobFinished = 0;

        // add the first job into job queue
        Queue<String> jobQueue = new LinkedList<>();
        jobQueue.add(selectedJob);
        int idx = selectedJobIdx;
        // in case there are multiple jobs arrive at the same time
        while (true) {
            idx++;
            if (idx < jobNames.size() && arrivalTimeList.get(idx).equals(time)) {
                String job1 = jobNames.get(idx);
                jobQueue.add(job1);
            } else {
                break;
            }
        }

        while (true) {
            String job = jobQueue.poll();
            // this is when the previous job finished, but the next job doesn't arrive
            if (job == null) {
                time++;
                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextJob = jobNames.get(index);
                    jobQueue.add(nextJob);  // first add the new arrival job, then add the current job back and the job should not be completed
            
                    // in case there are multiple jobs arrive at the same time
                    while (true) {
                        index++;
                        if (index < jobNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String job1 = jobNames.get(index);
                            jobQueue.add(job1);
                        } else {
                            break;
                        }
                    }
                }
            } else {
                if (!job.equals(selectedJob)) {
                    // only not finished job's status will be saved here
                    if (remainingTimeList.get(selectedJobIdx) != 0) {
                        Queue<Integer> queue = resultMap.get(selectedJob);
                        if (queue == null)
                            queue = new LinkedList<>();

                        // save previous job's time intervals
                        queue.add(selectedJobStartT);
                        queue.add(time);
                        resultMap.put(selectedJob, queue);
                    }

                    // update new selected job information
                    selectedJob = job;
                    selectedJobIdx = jobNames.indexOf(selectedJob);
                    selectedJobStartT = time;
                }

                // update remaining time list
                int remainingTime = remainingTimeList.get(selectedJobIdx);
                remainingTimeList.set(selectedJobIdx, remainingTime - 1);
                time++; // time moves forward

                if (arrivalTimeList.contains(time)) {
                    int index = arrivalTimeList.indexOf(time);
                    String nextJob = jobNames.get(index);
                    jobQueue.add(nextJob);  // first add the new arrival job, then add the current job back and the job should not be completed
                    // in case there are multiple jobs arrive at the same time
                    while (true) {
                        index++;
                        if (index < jobNames.size() && arrivalTimeList.get(index).equals(time)) {
                            String job1 = jobNames.get(index);
                            jobQueue.add(job1);
                        } else {
                            break;
                        }
                    }
                }

                if (remainingTime - 1 == 0) {
                    // finished job's status will be saved here
                    Queue<Integer> queue = resultMap.get(selectedJob);
                    if (queue == null)
                        queue = new LinkedList<>();

                    queue.add(selectedJobStartT);
                    queue.add(time);
                    resultMap.put(selectedJob, queue);

                    jobFinished++;
                    if (jobFinished == jobNames.size())
                        break;
                } else {
                    jobQueue.add(selectedJob); // if the job doesn't complete, add back to the queue
                }
            }

        }

        return resultMap;
    }

    //Shortest Process Next algorithm (Non-Preemptive)
    public Map<String, Queue<Integer>> SPN() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>(); // store job scheduling time intervals
        boolean[] jobFinishedArray = new boolean[jobNames.size()];

        Queue<Integer> jobAQueue = new LinkedList<>();
        jobAQueue.add(jobTimes[0][0]);
        jobAQueue.add(jobTimes[0][0] + jobTimes[0][1]);
        resultMap.put(jobNames.get(0), jobAQueue);
        int current = jobTimes[0][0] + jobTimes[0][1];
        jobFinishedArray[0] = true;
        int jobFinished = 1;
        while (jobFinished < jobNames.size()) {
            String selectedJob = chooseJobSPN(jobFinishedArray, current);
            int index = jobNames.indexOf(selectedJob);
            if (jobTimes[index][0] > current)
                current = jobTimes[index][0];
            Queue<Integer> jobQueue = new LinkedList<>();
            jobQueue.add(current);
            jobQueue.add(current + jobTimes[index][1]);
            resultMap.put(selectedJob, jobQueue);
            current += jobTimes[index][1];
            jobFinishedArray[index] = true;
            jobFinished++;
        }

        return resultMap;
    }

    //chooses the next job according to shortest process time algorithm
    private String chooseJobSPN(boolean[] jobFinishedArray, int current) {
        String selectedJob = "";
        int shortestServiceTime = Integer.MAX_VALUE;

        for (int i = 0; i < jobFinishedArray.length; i++) {
            if (!jobFinishedArray[i] && current >= jobTimes[i][0]) {
                if (jobTimes[i][1] < shortestServiceTime) {
                    shortestServiceTime = jobTimes[i][1];
                    selectedJob = jobNames.get(i);
                }
            } else if (!jobFinishedArray[i] && current < jobTimes[i][0]) {
                if (selectedJob.isEmpty())
                    selectedJob = jobNames.get(i);
                break;
            }
        }

        return selectedJob;
    }

    //Shortest Remaining Time algorithm (Preemptive)
    public Map<String, Queue<Integer>> SRT() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();

        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainingTimeList = new ArrayList<>();
        for (int i = 0; i < jobTimes.length; i++) {
            arrivalTimeList.add(jobTimes[i][0]);
            remainingTimeList.add(jobTimes[i][1]);
        }

        String selectedJob = jobNames.get(0);
        int selectedJobIdx = 0;
        int selectedJobStartT = jobTimes[0][0];
        int time = jobTimes[0][0];
        int jobFinished = 0;
        while (true) {
            if (arrivalTimeList.contains(time)) {
                int newIndex = chooseJobSRT(remainingTimeList, time);
                if (selectedJobIdx == -1) {
                    // update selected job info: the new job has finally come
                    selectedJobIdx = newIndex;
                    selectedJob = jobNames.get(selectedJobIdx);
                    selectedJobStartT = time;
                } else if (newIndex != selectedJobIdx) {
                    Queue<Integer> queue = resultMap.get(selectedJob);
                    if (queue == null)
                        queue = new LinkedList<>();

                    // save previous job's status
                    queue.add(selectedJobStartT);
                    queue.add(time);
                    resultMap.put(selectedJob, queue);

                    // update selected job info
                    selectedJobIdx = newIndex;
                    selectedJob = jobNames.get(selectedJobIdx);
                    selectedJobStartT = time;
                }
            }

            time++; // time moves one forward

            if (selectedJobIdx == -1)
                continue;

            int remainingTime = remainingTimeList.get(selectedJobIdx);
            remainingTimeList.set(selectedJobIdx, remainingTime - 1);
            if (remainingTime - 1 == 0) {
                // finished job's status will be saved here
                Queue<Integer> queue = resultMap.get(selectedJob);
                if (queue == null) {
                    queue = new LinkedList<>();
                }

                // save previous job's status
                queue.add(selectedJobStartT);
                queue.add(time);
                resultMap.put(selectedJob, queue);

                jobFinished++;
                // if all job finished, jump out of while loop
                if (jobFinished == jobNames.size())
                    break;

                selectedJobIdx = chooseJobSRT(remainingTimeList, time);
                if (selectedJobIdx != -1) {
                    selectedJob = jobNames.get(selectedJobIdx);
                    selectedJobStartT = time;
                }
            }
        }

        return resultMap;
    }

    //gets the next selected job's index based on Shortest Remaining Time
    private int chooseJobSRT(List<Integer> remainingTimeList, int currentTime) {
        int shortestRemainTime = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < remainingTimeList.size(); i++) {
            int remainingTime = remainingTimeList.get(i);
            if (remainingTime > 0 && remainingTime < shortestRemainTime && currentTime >= jobTimes[i][0]) {
                shortestRemainTime = remainingTime;
                index = i;
            }
        }

        return index;
    }

    //Highest Response Ratio Next algorithm (Non-Preemptive)
    public Map<String, Queue<Integer>> HRRN() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();  // store job scheduling time intervals
        boolean[] jobFinishedArray = new boolean[jobNames.size()];

        Queue<Integer> jobAQueue = new LinkedList<>();
        jobAQueue.add(jobTimes[0][0]);
        jobAQueue.add(jobTimes[0][0] + jobTimes[0][1]);
        resultMap.put(jobNames.get(0), jobAQueue);
        jobFinishedArray[0] = true;
        int current = jobTimes[0][0] + jobTimes[0][1];
        int jobFinished = 1; // the first job is finished
        while (jobFinished < jobNames.size()) {
            String selectedJob = chooseJobHRRN(jobFinishedArray, current);
            int index = jobNames.indexOf(selectedJob);
            if (jobTimes[index][0] > current)
                current = jobTimes[index][0];
            Queue<Integer> jobQueue = new LinkedList<>();
            jobQueue.add(current);
            jobQueue.add(current + jobTimes[index][1]);
            resultMap.put(selectedJob, jobQueue);
            jobFinishedArray[index] = true;
            current += jobTimes[index][1]; // update current time stamp position
            jobFinished++; // one more job is finished
        }

        return resultMap;
    }

    //get the next job based on the Highest Response Ratio
    private String chooseJobHRRN(boolean[] jobFinishedArray, int current) {
        String selectedJob = "";
        double highestRatio = Double.MIN_VALUE;
        for (int i = 0 ; i < jobFinishedArray.length; i++) {
            // if the job has not been executed and it already arrived, then calculate the ratio
            if (!jobFinishedArray[i] && current >= jobTimes[i][0]) {
                double ratio = (current - jobTimes[i][0] + jobTimes[i][1]) / (double) jobTimes[i][1];
                if (ratio > highestRatio) {
                    highestRatio = ratio;
                    selectedJob = jobNames.get(i);
                }
            } else if (!jobFinishedArray[i] && current < jobTimes[i][0]) {
                if (selectedJob.isEmpty())
                    selectedJob = jobNames.get(i);
                break;
            }
        }

        return selectedJob;
    }

    //Feedback algorithm with 3 job Queues (Quantum = 1)
    public Map<String, Queue<Integer>> FB() {
        Map<String, Queue<Integer>> resultMap = new HashMap<>();
        Map<Integer, Queue<String>> priorityJobQueueMap = new HashMap<>();
        Queue<String> priorityJobQueue1 = new LinkedList<>();
        Queue<String> priorityJobQueue2 = new LinkedList<>();
        Queue<String> priorityJobQueue3 = new LinkedList<>();
        priorityJobQueueMap.put(1, priorityJobQueue1);
        priorityJobQueueMap.put(2, priorityJobQueue2);
        priorityJobQueueMap.put(3, priorityJobQueue3);
        Map<String, Integer> jobPriorityLevel = new HashMap<>();

        List<Integer> arrivalTimeList = new ArrayList<>();
        List<Integer> remainingTimeList = new ArrayList<>();
        for (int i = 0; i < jobNames.size(); i++) {
            arrivalTimeList.add(jobTimes[i][0]);
            remainingTimeList.add(jobTimes[i][1]);
            jobPriorityLevel.put(jobNames.get(i), 1); // initialize job priority level map
        }

        int time = jobTimes[0][0];
        String selectedJob = jobNames.get(0);
        int selectedJobIdx = 0;
        int selectedJobStartT = jobTimes[0][0];
        int jobFinished = 0;

        while (true) {
            if (arrivalTimeList.contains(time)) {
                int index = arrivalTimeList.indexOf(time);
                String job = jobNames.get(index);
                priorityJobQueue1.add(job);
                // in case there are multiple jobs arrive at the same time
                while (true) {
                    index++;
                    if (index < jobNames.size() && arrivalTimeList.get(index).equals(time)) {
                        String job1 = jobNames.get(index);
                        priorityJobQueue1.add(job1);
                    } else
                        break;
                }
            }

            String newjob = "";
            if (!priorityJobQueue1.isEmpty()) {
                newjob = priorityJobQueue1.poll();
            } else if (!priorityJobQueue2.isEmpty()) {
                newjob = priorityJobQueue2.poll();
            } else if (!priorityJobQueue3.isEmpty()) {
                newjob = priorityJobQueue3.poll();
            }

            if (!newjob.isEmpty() && !newjob.equals(selectedJob)) {
                if (remainingTimeList.get(selectedJobIdx) > 0) {
                    Queue<Integer> queue = resultMap.get(selectedJob);
                    if (queue == null)
                        queue = new LinkedList<>();

                    // save unfinished job's status information
                    queue.add(selectedJobStartT);
                    queue.add(time);
                    resultMap.put(selectedJob, queue);

                    int jobLevel = jobPriorityLevel.get(selectedJob);
                    if (jobLevel < 3) {
                        jobLevel++;
                        jobPriorityLevel.put(selectedJob, jobLevel);
                    }
                    Queue<String> jobQueue = priorityJobQueueMap.get(jobLevel);
                    jobQueue.add(selectedJob);
                }

                selectedJob = newjob;
                selectedJobIdx = jobNames.indexOf(selectedJob);
                selectedJobStartT = time;

            }

            int remainingTime = remainingTimeList.get(selectedJobIdx);
            time++;
            if (remainingTime == 0) // if current job has finished, but next job doesn't come, continue the next loop
                continue;

            remainingTimeList.set(selectedJobIdx, remainingTime - 1);
            if (remainingTime - 1 == 0) {
                Queue<Integer> queue = resultMap.get(selectedJob);
                if (queue == null)
                    queue = new LinkedList<>();

                // save finished job's status information
                queue.add(selectedJobStartT);
                queue.add(time);
                resultMap.put(selectedJob, queue);

                jobFinished++;
                if (jobFinished == jobNames.size())
                    break;
            }
        }

        return resultMap;
    }
}