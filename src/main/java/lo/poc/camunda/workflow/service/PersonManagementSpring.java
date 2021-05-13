package lo.poc.camunda.workflow.service;

import lo.poc.camunda.variables.Variable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Slf4j
@Service("personManagementSpring")
@AllArgsConstructor
public class PersonManagementSpring {

    private final @Lazy
    RuntimeService runtimeService;

    /**
     * Calculates the list of persons to be used for multiinstance subprocess call activity
     * <pre>
     *     This is trigger by the main process prior to multiinstance subprocess
     * </pre>
     *
     * @param execution  the execution
     * @param group the group identifier, i.e. "1" or "2"
     * @return the list of persons
     */
    public List<Person> calculatePersons(DelegateExecution execution, String group) {
        int elements = 2;
        List<Person> persons = IntStream.range(0, elements)
                .mapToObj(i -> Person.builder().perid("G" + group + "_" + (100 + i)).name("Name_G" + group + "_" + (100 + i)).build())
                .collect(Collectors.toList());
        log.info("Group {}: Calculated persons, persons {}", group, persons);
        return persons;
    }

    /**
     * Initialize the Map of results per subprocess identifier and adds as a process variable {@value Variable#SUBPROCESS_RESULTS}
     * <pre>
     *     This is trigger by the main process prior to multiinstance subprocess
     * </pre>
     *
     * @param execution  the execution
     * @param persons    the list of persons so that we initialize the relevant list of results
     * @param group the group identifier, i.e. "1" or "2"
     */
    public void initializeResults(DelegateExecution execution, List<Person> persons, String group) {
        List<String> results = persons.stream().map(s -> "").collect(Collectors.toList());
        Map<String, List<String>> resVariable = (Map<String, List<String>>) execution.getVariable(Variable.SUBPROCESS_RESULTS);
        if (null == resVariable) {
            resVariable = new HashMap<>();
            execution.setVariable(Variable.SUBPROCESS_RESULTS, resVariable);
        }
        resVariable.put(group, results);
        log.info("Group {}: Initialized person results, put on variable {} empty results {}", group, Variable.SUBPROCESS_RESULTS, resVariable);
    }

    /**
     * Collect the result of a single index (identified by "loopCounter") and put it on results process variable {@value Variable#SUBPROCESS_RESULTS}
     * <pre>
     *     This is triggered by the "end" listener of the multiinstance subprocess
     * </pre>
     *
     * @param execution  the execution
     * @param group the group identifier, i.e. "1" or "2"
     */
    public void collectResults(DelegateExecution execution, String group) {
        log.info("Group {}: Debuging... id={}, processInstanceId={}, activityInstanceId={}", group, execution.getId(), execution.getProcessInstanceId(), execution.getActivityInstanceId());
        log.info("Group {}: Debuging... parentId={}, parentActivityInstanceId={}", group, execution.getParentId(), execution.getParentActivityInstanceId());
        Integer loopCounter = (Integer) execution.getVariable("loopCounter");
        Map<String, List<String>> resVariable = (Map<String, List<String>>) execution.getVariable(Variable.SUBPROCESS_RESULTS);
        List<String> results = resVariable.get(group);
        log.info("Group {}: Collecting result of task with index {}, current results on execution is {}", group, loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS));
        String currentResult = (String) execution.getVariableLocal("subResult");
        log.info("Group {}: Result for task with index {}, currentResult {}", group, loopCounter, currentResult);
        if (null != currentResult) {
            // Only set if completed, not when it is destroyed
            log.info("Group {}: SET Result for task with index {}, currentResult {}", group, loopCounter, currentResult);
            results.set(loopCounter, currentResult);
        }

        log.info("Group {}: After setting result for task with index {}, results on execution is {}", group, loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS));
    }

    /**
     * Collect the result of a single index (identified by "loopCounter") and put it on results process variable {@value Variable#SUBPROCESS_RESULTS}
     * <pre>
     *     This is triggered by the "end" listener of the multiinstance subprocess
     * </pre>
     *
     * @param execution  the execution
     * @param group the group identifier, i.e. "1" or "2"
     */
    public boolean completionCondition(DelegateExecution execution, String group) {
        // We disregard group, both should complete
        log.info("Group {}: Debuging... id={}, processInstanceId={}, activityInstanceId={}", group, execution.getId(), execution.getProcessInstanceId(), execution.getActivityInstanceId());
        log.info("Group {}: Debuging... parentId={}, parentActivityInstanceId={}", group, execution.getParentId(), execution.getParentActivityInstanceId());
        Map<String, List<String>> resVariable = (Map<String, List<String>>) execution.getVariable(Variable.SUBPROCESS_RESULTS);
        log.info("Group {}: Current results on execution is {}", group, execution.getVariable(Variable.SUBPROCESS_RESULTS));
        // To take into account both groups of multiinstance sub process results
//        List<String> results = resVariable.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
        // To take into account only the current group group of multiinstance sub process results
        List<String> results = resVariable.get(group);
        log.info("Group {}: Flattened all results is {}", group, results);

        boolean foundFPC = results.stream().anyMatch(o -> "FPC".equals(o));
        log.info("Group {}: foundFPC is {}", group, foundFPC);
        return foundFPC;
    }

    /**
     * Calculate the aggregate of the results from the process variable {@value Variable#SUBPROCESS_RESULTS}
     * <pre>
     *     This is trigger by the main process after both multiinstance subprocess finish
     * </pre>
     *
     * @param execution the execution
     */
    public String calculateFinalAggregateResult(DelegateExecution execution) {
        // We disregard subProcess, both should complete
        log.info("Debuging... id={}, processInstanceId={}, activityInstanceId={}", execution.getId(), execution.getProcessInstanceId(), execution.getActivityInstanceId());
        log.info("Debuging... parentId={}, parentActivityInstanceId={}", execution.getParentId(), execution.getParentActivityInstanceId());
        Map<String, List<String>> resVariable = (Map<String, List<String>>) execution.getVariable(Variable.SUBPROCESS_RESULTS);
        log.info("Current final results on execution is {}", execution.getVariable(Variable.SUBPROCESS_RESULTS));
        List<String> allResults = resVariable.entrySet().stream().flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
        log.info("Flattened final results is {}", allResults);

        String aggregate = allResults.stream().filter(o -> "FPC".equals(o)).findAny().orElse("OK");
        log.info("Final Aggregate is {}", aggregate);
        return aggregate;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person implements Serializable {
        static final long serialVersionUID = 42L;
        private String perid;
        private String name;
    }
}
