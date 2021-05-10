package lo.poc.camunda.workflow.service;

import lo.poc.camunda.variables.Variable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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

    public List<Person> calculatePersons(DelegateExecution execution) {
        int elements = 4;
        List<Person> persons = IntStream.range(0, elements)
                .mapToObj(i->Person.builder().perid(""+(100+i)).name("Name"+(100+i)).build())
                .collect(Collectors.toList());
        log.info("Calculated persons, persons {}", persons);
        return persons;
    }

    public void initializeResults(DelegateExecution execution, List<Person> persons) {
        List<Object> results = persons.stream().map(s->null).collect(Collectors.toList());
        execution.setVariable(Variable.SUBPROCESS_RESULTS, results);
        log.info("Initialized person results, put on variable {} empty results {}", Variable.SUBPROCESS_RESULTS, results);
    }

    public void collectResults(DelegateExecution execution, int loopCounter) {
        List<Object> results = (List<Object>) execution.getVariable(Variable.SUBPROCESS_RESULTS);
        log.info("Collecting result of task with index {}, current results on execution is {}", loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS));
        String currentResult = (String) execution.getVariableLocal("sub1Result");
        log.info("Result for task with index {}, currentResult {}", loopCounter, currentResult);
        results.set(loopCounter, currentResult);

        log.info("After setting result for task with index {}, results on execution is {}", loopCounter, execution.getVariable(Variable.SUBPROCESS_RESULTS));
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
