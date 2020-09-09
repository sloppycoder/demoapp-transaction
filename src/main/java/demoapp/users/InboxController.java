package demoapp.users;

import demoapp.users.data.Inbox;
import demoapp.users.data.InboxNotFoundException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@Slf4j
public class InboxController {

  final ReactiveMongoTemplate mongo;

  public InboxController(ReactiveMongoTemplate mongo) {
    this.mongo = mongo;
  }

  @RequestMapping(value = "/inbox/{userId}/unread")
  public Mono<Integer> getInboxUnreadCount(@PathVariable("userId") String userId) {
    log.info("retrieving unread count for user {}", userId);

    return mongo.query(Inbox.class)
            .matching(query(where("userId").is(userId)))
            .first()
      .flatMap(
            inbox -> Mono.just(inbox.getUnreadCount())
      ).switchIfEmpty(
            Mono.error(new InboxNotFoundException())
      );
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "not found")
  @ExceptionHandler(InboxNotFoundException.class)
  public void notFoundHandler() {
  }
}
