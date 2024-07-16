package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.task.NotificationTask;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class NotificationTaskScheduler {

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = notificationTaskRepository.findByNotificationDateTime(now);

        for (NotificationTask task : tasks) {
            SendMessage message = new SendMessage(task.getChatId(), task.getNotificationText());
            telegramBot.execute(message);
        }
    }
}