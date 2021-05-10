package lo.poc.camunda.workflow.delegate;

import lo.poc.camunda.variables.Variable;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalculatePersons implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        List<String> persons = Arrays.asList("person1", "person2", "person2");
        List<Object> results = persons.stream().map(s->null).collect(Collectors.toList());
        execution.setVariable(Variable.PERSONS, persons);
        execution.setVariable(Variable.SUBPROCESS_RESULTS, results);
    }
}
