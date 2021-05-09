# poc-camunda
POC to test various aspects using camunda with spring boot


# How to setup
Just run the `PocCamundaApplication` spring boot main class.
The camunda workflow will startup and be exposed on http://localhost:8080
You can login using admin/admin to cockpit and access the various deployed processes.

> Please note that it is using an in-memory database, as such you will loose the process instances when you stop the application.

# POC contents
The various high level POCs are included

## Main process with multi subprocess over list
This is on resource folder `bpmn/multisubprocess`
The main process over a List of persons will spawn multiple subprocesses one for each person.
The result of each subprocess is being returned to the main process for further processing, i.e. to decide the flow.

