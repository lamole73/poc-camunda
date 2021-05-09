package lo.poc.camunda.workflow.service;

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

@Slf4j
@Service("personManagementSpring")
@AllArgsConstructor
public class PersonManagementSpring {

    public List<Person> calculatePersons(DelegateExecution execution) {
        return Arrays.asList(
                Person.builder().perid("100").name("Name100").build(),
                Person.builder().perid("101").name("Name101").build()
        );
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
