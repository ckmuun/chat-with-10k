package de.koware.cfs.chatwith10k.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    public ChatService(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    public Flux<String> ragMessage(String message) {
        return this.chatClient.prompt(message)
                .stream()
                .content();
    }

    public Flux<String> sendMessage(String message) {
        return chatModel
                .stream(message);
    }


}
