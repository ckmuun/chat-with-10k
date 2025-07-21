package de.koware.cfs.chatwith10k.api;

import de.koware.cfs.chatwith10k.chat.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ChatController.class)
class ChatControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ChatService chatService;

    @Test
    void test_hello() {
        webTestClient.get().uri("/hello")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .value(ret -> Assertions.assertEquals("Hello World!", ret));
    }

    @Test
    void sendMessage_statusOk() {
        webTestClient.post().uri("/send-message")
                .body(BodyInserters.fromValue(""))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void sendMessage_Content() {
        var text = "hello world";
        when(chatService.sendMessage(anyString())).thenReturn(Flux.just(text));
        webTestClient.post().uri("/send-message")
                .body(BodyInserters.fromValue(""))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(
                        response ->
                                Assertions.assertEquals(text,
                                        new String(Objects.requireNonNull(response.getResponseBody()))));
    }
}