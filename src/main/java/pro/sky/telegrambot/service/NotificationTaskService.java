package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.task.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    public void createNotificationTaskFromMessage(Long chatId, String messageText) {
        //String regex = "([0-9\\.\\:\\s]{16})(\\s)([\\w+]+)";
        String regex = "([0-9\\.\\:\\s]{16})(\\s)(([\\w\\s]|[\\W+])+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.matches()) {
            String dateTimeString = matcher.group(1);
            String notificationText = matcher.group(3).trim();

            LocalDateTime notificationDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            NotificationTask notificationTask = new NotificationTask();
            notificationTask.setChatId(chatId);
            notificationTask.setNotificationText(notificationText);
            notificationTask.setNotificationDateTime(notificationDateTime);

            notificationTaskRepository.save(notificationTask);
        } else {
            throw new IllegalArgumentException("Текст сообщения не соответствует ожидаемому формату");
        }
    }
}