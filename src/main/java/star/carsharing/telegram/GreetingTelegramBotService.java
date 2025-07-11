package star.carsharing.telegram;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import star.carsharing.exception.unchecked.TelegramApiException;
import star.carsharing.service.UserService;

@Service
@RequiredArgsConstructor
public class GreetingTelegramBotService {
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private final RestTemplate restTemplate;
    private final UserService userService;
    @Value("${telegram.bot.token}")
    private String botToken;
    private int lastUpdateId = 0;

    public void pollTelegramUpdates() {
        String url = String.format("%s%s/getUpdates?offset=%d",
                TELEGRAM_API_URL, botToken, lastUpdateId + 1);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> updates = (List<Map<String, Object>>) response.get("result");

            if (updates != null && !updates.isEmpty()) {
                for (Map<String, Object> update : updates) {
                    lastUpdateId = (int) update.get("update_id");
                    Map<String, Object> message = (Map<String, Object>) update.get("message");
                    if (message != null) {
                        String text = (String) message.get("text");
                        if ("/start".equalsIgnoreCase(text)) {
                            String username = String.valueOf(
                                    ((Map<String, Object>) message.get("chat"))
                                            .get("username").toString());
                            String chatId = String.valueOf(
                                    ((Map<String, Object>) message.get("chat"))
                                            .get("id").toString());
                            userService.setUserTelegramChatId(username, chatId);
                            sendGreetingMessage(chatId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new TelegramApiException("Failed to read Telegram response", e);
        }
    }

    public void sendGreetingMessage(String chatId) {
        String message = "Welcome to the Car Sharing Notification Bot!\n"
                + "You will now receive updates here.";

        String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                TELEGRAM_API_URL, botToken, chatId, message);
        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new TelegramApiException("Failed to send greeting", e);
        }
    }
}
