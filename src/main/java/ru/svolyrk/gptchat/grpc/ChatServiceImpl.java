package ru.svolyrk.gptchat.grpc;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import reactor.core.publisher.Mono;
import ru.svolyrk.gptchat.service.ChatService;
import ru.svolyrk.gptchat.service.Gpt4Service;

@GRpcService
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    private final ChatService chatService;
    private final Gpt4Service gpt4Service;

    public ChatServiceImpl(ChatService chatService, Gpt4Service gpt4Service) {
        this.chatService = chatService;
        this.gpt4Service = gpt4Service;
    }

    @Override
    public void chat(ChatRequest request, StreamObserver<ChatResponse> responseObserver) {
        Mono<ChatResponse> responseMono = chatService.handleChat(request);
        responseMono.subscribe(response -> {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, responseObserver::onError);
    }

    @Override
    public void textToSpeech(TextToSpeechRequest request, StreamObserver<TextToSpeechResponse> responseObserver) {
        Mono<TextToSpeechResponse> responseMono = gpt4Service.textToSpeech(request.getText())
            .map(
                audioContent -> TextToSpeechResponse.newBuilder().setAudioContent(com.google.protobuf.ByteString.copyFromUtf8(audioContent)).build());
        responseMono.subscribe(response -> {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, responseObserver::onError);
    }

    @Override
    public void speechToText(SpeechToTextRequest request, StreamObserver<SpeechToTextResponse> responseObserver) {
        Mono<SpeechToTextResponse> responseMono = gpt4Service.speechToText(request.getAudioData().toByteArray())
            .map(transcript -> SpeechToTextResponse.newBuilder().setTranscript(transcript).build());
        responseMono.subscribe(response -> {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, responseObserver::onError);
    }

    @Override
    public void extractMainIdea(ExtractMainIdeaRequest request, StreamObserver<ExtractMainIdeaResponse> responseObserver) {
        Mono<ExtractMainIdeaResponse> responseMono = gpt4Service.extractMainIdea(request.getText())
            .map(mainIdea -> ExtractMainIdeaResponse.newBuilder().setMainIdea(mainIdea).build());
        responseMono.subscribe(response -> {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, responseObserver::onError);
    }
}
