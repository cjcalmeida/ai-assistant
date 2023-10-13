package com.cjcalmeida.studies.aiassistant.services;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import dev.hilla.BrowserCallable;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@BrowserCallable
@AnonymousAllowed
public class ChatService {

    private final String OPEN_IA_API_KEY;

    private Assistant assistant;

    public ChatService(@Value("${ai-assistant.open-ia.api-key}") String openIaApiKey) {
        OPEN_IA_API_KEY = openIaApiKey;
    }

    interface Assistant {
        String chat(String message);
    }

    @PostConstruct
    public void init() {
        var memory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer("gpt-3.5-turbo"));
        assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(OpenAiChatModel.builder()
                .timeout(Duration.of(30, ChronoUnit.SECONDS))
                .apiKey(OPEN_IA_API_KEY)
                .build())
            .chatMemory(memory)
            .build();
    }

    public Flux<String> chat(String message) {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        sink.tryEmitNext(assistant.chat(message));
        return sink.asFlux();
    }

}
