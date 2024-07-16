package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskService notificationTaskService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String messageText = update.message().text();
                Long chatId = update.message().chat().id();
                if (messageText.startsWith("/start")) {
                    sendWelcomeMessage(chatId);
                } else {
                    try {
                        notificationTaskService.createNotificationTaskFromMessage(chatId, messageText);
                        sendSuccessMessage(chatId);

                    } catch (IllegalArgumentException e) {
                        sendErrorMessage(chatId, "Неверный формат. Пожалуйста, используйте 'dd.MM.yyyy HH:mm Ваш текст напоминания'");
                    }
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = "Привет! Я ваш бот. Как я могу помочь вам сегодня?";
        SendMessage message = new SendMessage(chatId, welcomeText);
        telegramBot.execute(message);
    }

    private void sendErrorMessage(Long chatId, String errorText) {
        SendMessage message = new SendMessage(chatId, errorText);
        telegramBot.execute(message);
    }

    private void sendSuccessMessage(Long chatId) {
        String welcomeText = "сообщение установлено";
        SendMessage message = new SendMessage(chatId, welcomeText);
        telegramBot.execute(message);
    }
}