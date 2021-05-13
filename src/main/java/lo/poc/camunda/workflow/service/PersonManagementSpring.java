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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Slf4j
@Service("personManagementSpring")
@AllArgsConstructor
public class PersonManagementSpring {

    private final @Lazy
    RuntimeService runtimeService;

    public List<Person> calculatePersons(DelegateExecution execution, String subProcess) {
        int elements = 4;
        List<Person> persons = IntStream.range(0, elements)
                .mapToObj(i->Person.builder().perid("Sub"+subProcess+"_"+(100+i)).name("Name_Sub"+subProcess+"_"+(100+i)).build())
                .collect(Collectors.toList());
        log.info("SubProcess {}: Calculated persons, persons {}", subProcess, persons);
        return persons;
    }

    public void initializeResults(DelegateExecution execution, List<Person> persons, String subProcess) {
        List<Object> results = persons.stream().map(s->null).collect(Collectors.toList());
        execution.setVariable(Variable.SUBPROCESS_RESULTS+subProcess, results);
        log.info("SubProcess {}: Initialized person results, put on variable {} empty results {}", subProcess, Variable.SUBPROCESS_RESULTS+subProcess, results);
    }

    public void collectResults(DelegateExecution execution, String subProcess) {
        log.info("SubProcess {}: Debuging... id={}, processInstanceId={}, activityInstanceId={}", subProcess, execution.getId(), execution.getProcessInstanceId(), execution.getActivityInstanceId());
        log.info("SubProcess {}: Debuging... parentId={}, parentActivityInstanceId={}", subProcess, execution.getParentId(), execution.getParentActivityInstanceId());
        Integer loopCounter = (Integer) execution.getVariable("loopCounter");
        List<Object> results = (List<Object>) execution.getVariable(Variable.SUBPROCESS_RESULTS+subProcess);
        log.info("SubProcess {}: Collecting result of task with index {}, current results on execution is {}", subProcess, loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS+subProcess));
        String currentResult = (String) execution.getVariableLocal("sub1Result");
        log.info("SubProcess {}: Result for task with index {}, currentResult {}", subProcess, loopCounter, currentResult);
        results.set(loopCounter, currentResult);

        log.info("SubProcess {}: After setting result for task with index {}, results on execution is {}", subProcess, loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS+subProcess));
    }

    public boolean completionCondition(DelegateExecution execution, String subProcess) {
        log.info("SubProcess {}: Debuging... id={}, processInstanceId={}, activityInstanceId={}", subProcess, execution.getId(), execution.getProcessInstanceId(), execution.getActivityInstanceId());
        log.info("SubProcess {}: Debuging... parentId={}, parentActivityInstanceId={}", subProcess, execution.getParentId(), execution.getParentActivityInstanceId());
        List<Object> results = (List<Object>) execution.getVariable(Variable.SUBPROCESS_RESULTS+subProcess);
        log.info("SubProcess {}: Current results on execution is {}", subProcess, execution.getVariable(Variable.SUBPROCESS_RESULTS+subProcess));

        boolean foundFPC = results.stream().anyMatch(o -> "FPC".equals(o));
        log.info("SubProcess {}: foundFPC is {}", subProcess, foundFPC);
        return foundFPC;
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
