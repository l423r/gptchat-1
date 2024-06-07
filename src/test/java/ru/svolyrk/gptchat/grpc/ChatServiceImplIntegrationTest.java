package ru.svolyrk.gptchat.grpc;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.svolyrk.gptchat.repository.ConversationRepository;
import ru.svolyrk.gptchat.service.ChatService;
import ru.svolyrk.gptchat.service.Gpt4Service;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatServiceImplIntegrationTest {

//    @GrpcCleanupRule
//    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private ChatServiceGrpc.ChatServiceBlockingStub blockingStub;

    @Autowired
    private ChatService chatService;

    @Autowired
    private Gpt4Service gpt4Service;

    @Autowired
    private ConversationRepository conversationRepository;

    @BeforeEach
    public void setUp() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
//        grpcCleanup.register(channel);
        blockingStub = ChatServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void testChat() {
        // Given
        String conversationId = "test-conversation";
        String message = "Hello";
        ChatRequest request = ChatRequest.newBuilder()
                .setConversationId(conversationId)
                .setMessage(message)
                .build();

        // When
        ChatResponse response = blockingStub.chat(request);

        // Then
        assertThat(response.getResponse()).isNotNull();
    }

    @Test
    public void testTextToSpeech() {
        // Given
        String text = "Hello";
        TextToSpeechRequest request = TextToSpeechRequest.newBuilder().setText(text).build();

        // When
        TextToSpeechResponse response = blockingStub.textToSpeech(request);

        // Then
        assertThat(response.getAudioContent().toStringUtf8()).isNotEmpty();
    }

    @Test
    public void testSpeechToText() {
        // Given
        byte[] audioData = "AudioData".getBytes();
        SpeechToTextRequest request = SpeechToTextRequest.newBuilder().setAudioData(ByteString.copyFrom(audioData)).build();

        // When
        SpeechToTextResponse response = blockingStub.speechToText(request);

        // Then
        assertThat(response.getTranscript()).isNotNull();
    }

    @Test
    public void testExtractMainIdea() {
        // Given
        String text = "This is a long text.";
        ExtractMainIdeaRequest request = ExtractMainIdeaRequest.newBuilder().setText(text).build();

        // When
        ExtractMainIdeaResponse response = blockingStub.extractMainIdea(request);

        // Then
        assertThat(response.getMainIdea()).isNotNull();
    }
}
