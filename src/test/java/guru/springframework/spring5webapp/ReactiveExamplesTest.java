package guru.springframework.spring5webapp;

import guru.springframework.spring5webapp.models.Person;
import guru.springframework.spring5webapp.models.PersonCommand;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExamplesTest {

    Person amine = new Person("Amine", "Ouasmine");
    Person sansa = new Person("Sansa", "Stark");
    Person jhon = new Person("Jhon", "Stark");
    Person alex = new Person("Alex", "Hunter");

    @Test
    public void monoTest() throws Exception{
        // create new Person mono
        Mono<Person> personMono = Mono.just(amine);

        //get person object from mono publisher
        Person person = personMono.block();

        //output name
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform() throws Exception {
        // create new Person mono
        Mono<Person> personMono = Mono.just(jhon);
        PersonCommand command = personMono
                .map(PersonCommand::new)
                .block();
        log.info(command.toString());
    }

    @Test(expected = NullPointerException.class)
    public void monoFilter() throws Exception {
        Mono<Person> personMono = Mono.just(sansa);
        Person sansaAxe = personMono
                .filter(person -> person.getFirstName().equalsIgnoreCase("Lannister"))
                .block();
        log.info(sansaAxe.sayMyName()); //throw NullPointerException
    }

    @Test
    public void fluxTest() throws Exception {
        Flux<Person> people = Flux.just(amine, sansa, jhon, alex);
        people.subscribe(person -> log.info(person.toString()));
    }

    @Test
    public void fluxTestFilter() throws Exception {
        Flux<Person> people = Flux.just(amine, sansa, jhon, alex);
        people
                .filter(person -> person.getFirstName().equals(alex.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelayNoOutput() throws Exception {
        Flux<Person> people = Flux.just(amine, sansa, jhon, alex);
        people
                .delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    public void fluxTestDelay() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(amine, sansa, jhon, alex);
        people
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));
        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> people = Flux.just(amine, sansa, jhon, alex);
        people
                .delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().startsWith("A"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));
        countDownLatch.await();
    }

}
