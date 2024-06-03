package ru.svolyrk.gptchat.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ru.svolyrk.gptchat.dto.ChatDTO;
import ru.svolyrk.gptchat.dto.MessageDTO;
import ru.svolyrk.gptchat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public List<ChatDTO> getAllChats() {
        return chatService.getAllChats();
    }

    @GetMapping("/{id}")
    public ChatDTO getChatById(@PathVariable Long id) {
        return chatService.getChatById(id);
    }

    @PostMapping
    public ChatDTO createChat(@RequestBody ChatDTO chatDTO) {
        return chatService.createChat(chatDTO);
    }

    @PostMapping("/{id}/messages")
    public MessageDTO addMessageToChat(@PathVariable Long id, @RequestBody MessageDTO messageDTO) {
        return chatService.addMessageToChat(id, messageDTO);
    }

    @PostMapping("/{id}/gpt")
    public MessageDTO sendMessageToGpt(@PathVariable Long id, @RequestBody MessageDTO messageDTO, @RequestHeader("Authorization") String apiToken, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return chatService.sendMessageToGpt(id, messageDTO, apiToken, clientIp);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            log.info("request.getRemoteAddr() {}", request.getRemoteAddr());
            return request.getRemoteAddr();
        }
        log.info("xfHeader {}", xfHeader);
        return xfHeader.split(",")[0];
    }
}
