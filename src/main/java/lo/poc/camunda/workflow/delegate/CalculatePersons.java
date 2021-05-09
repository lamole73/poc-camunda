package lo.poc.camunda.workflow.delegate;

import lo.poc.camunda.variables.Variable;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class CalculatePersons implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable(Variable.PERSONS, Arrays.asList("person1", "person2"));
    }
}
