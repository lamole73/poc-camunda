# poc-camunda
POC to test various aspects using camunda with spring boot


# How to setup
Just run the `PocCamundaApplication` spring boot main class.
The camunda workflow will startup and be exposed on http://localhost:8180
You can login using admin/admin to cockpit and access the various deployed processes.

> Please note that it is using an in-memory database, as such you will loose the process instances when you stop the application.

In order to test the various pocs you can start the processes via the Camunda Modeller. 
Please start the various `tmpX.bpmn` which actually call the main processes. This was after the main process ends you
can inspect via the camunda cockpit the result of the main process.

# POC contents
The various high level POCs are included

## Main process with multi subprocess over list on which we need to aggregate the results of each subprocess
This is on resource folder `bpmn/multisubprocess`
The main process over a List of persons will spawn 2 groups of multiinstance subprocesses one instance on each group for each person.
The result of each subprocess is being returned to the main process for further processing, i.e. to decide the flow.

There are currently 2 implementations depending on the number of results of each multiinstance subprocess group.

### Main1UsingMultiSubProcessProcessVariables.bpmn
On this sample we need ALL the results of all instances, as such we store them as a process variable and collect them
one by one as each instance is finished.
Finally, we aggregate the results (from the process variable) to a single result which is returned by this process.

This process uses and need results as it is using the 'completion condition' to stop all the multiinstance sub processes.
So we need
1. Initialize results as process variable prior to multiinstance
2. On every loop instance completion of each group we collect the result onto the results process variable via an 'end' process listener
3. The completion condition looks up the results process variable for BREAK in order to kill potential remaining multiinstances
4. We need to aggregate the results after both multiinstance groups complete.

It waits for both groups of multiinstance sub processes to complete.

### Main2UsingMultiSubProcessEscalation.bpmn
On this sample we only care about the fact that at least 1 instance completes with BREAK, as such we do not store anything.
We just use the 'escalation' boundary events to stop all the multiinstance sub processes and gather the fact that one instance
finished with BREAK.
Finally, on aggregation of the results we statically define whether the result of the process is "OK" or "BREAK" depending on
whether the 'escalation' was triggers.

In addition when one of the escalation for BREAK is received from one of the subprocess instances it goes 
to 'Terminate' End event which kill the whole process and as such the other group of multiinstance sub processes
