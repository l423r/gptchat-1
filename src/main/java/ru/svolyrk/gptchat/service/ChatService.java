package ru.svolyrk.gptchat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.svolyrk.gptchat.dto.ChatDTO;
import ru.svolyrk.gptchat.dto.MessageDTO;
import ru.svolyrk.gptchat.model.Chat;
import ru.svolyrk.gptchat.model.Message;
import ru.svolyrk.gptchat.repository.ChatRepository;
import ru.svolyrk.gptchat.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ChatDTO> getAllChats() {
        return chatRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ChatDTO getChatById(Long id) {
        return chatRepository.findById(id).map(this::convertToDTO).orElse(null);
    }

    public ChatDTO createChat(ChatDTO chatDTO) {
        Chat chat = new Chat();
        chat.setTitle(chatDTO.getTitle());
        chat.setMessages(chatDTO.getMessages().stream().map(this::convertToEntity).collect(Collectors.toList()));
        Chat savedChat = chatRepository.save(chat);
        return convertToDTO(savedChat);
    }

    public MessageDTO addMessageToChat(Long chatId, MessageDTO messageDTO) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        Message message = new Message();
        message.setSender(messageDTO.getSender());
        message.setContent(messageDTO.getContent());
        message.setChat(chat);
        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    public MessageDTO sendMessageToGpt(Long chatId, MessageDTO messageDTO, String apiToken, String clientIp) {
        // Добавляем сообщение от пользователя
        MessageDTO userMessage = addMessageToChat(chatId, messageDTO);

        // Создаем запрос к GPT-4 API
        String gptApiUrl = "https://api.openai.com/v1/engines/davinci-codex/completions";
        String requestBody = String.format("{\"prompt\": \"%s\", \"max_tokens\": 150}", messageDTO.getContent());

        HttpHeaders headers = createHeaders(apiToken);
        headers.set("X-Forwarded-For", clientIp);

        String response = restTemplate.postForObject(
            gptApiUrl,
            new HttpEntity<>(requestBody, headers),
            String.class
        );

        // Обрабатываем ответ GPT-4 и добавляем его как сообщение от GPT
        String gptResponseContent = extractGptResponseContent(response);
        MessageDTO gptMessage = new MessageDTO();
        gptMessage.setSender("gpt");
        gptMessage.setContent(gptResponseContent);

        return addMessageToChat(chatId, gptMessage);
    }

    private HttpHeaders createHeaders(String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiToken);
        return headers;
    }

    private String extractGptResponseContent(String response) {
        // Реализуйте парсинг ответа GPT-4 API и извлечение текста сообщения
        return response; // Пример, необходимо адаптировать в зависимости от структуры ответа
    }

    private ChatDTO convertToDTO(Chat chat) {
        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setId(chat.getId());
        chatDTO.setTitle(chat.getTitle());
        chatDTO.setMessages(chat.getMessages().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return chatDTO;
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setSender(message.getSender());
        messageDTO.setContent(message.getContent());
        return messageDTO;
    }

    private Message convertToEntity(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSender(messageDTO.getSender());
        message.setContent(messageDTO.getContent());
        return message;
    }
}
