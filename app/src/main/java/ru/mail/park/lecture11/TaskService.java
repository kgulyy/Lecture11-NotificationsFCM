package ru.mail.park.lecture11;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import static ru.mail.park.lecture11.MainActivity.CHANNEL_DEFAULT;

public class TaskService extends IntentService {
    public static final String APPEND_LINE = "append_line";
    public static final String REMOVE_LINE = "remove_line";

    private static final String RANDOM_LINE = "Random line";
    private static final int NOTIFICATION_ID_TASK = 4;
    private static final List<String> lines = new ArrayList<>();

    private NotificationManager manager;

    public TaskService() {
        super("TaskService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case REMOVE_LINE:
                    removeLine();
                    break;
                case APPEND_LINE:
                default:
                    addLine();
            }
        }

        updateNotification();
    }

    private void addLine() {
        lines.add(RANDOM_LINE);
    }

    private void removeLine() {
        if (!lines.isEmpty()) {
            lines.remove(lines.size() - 1);
        }
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_DEFAULT);

        builder.setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(getString(R.string.task));
        for (String line : lines) {
            style.addLine(line);
        }

        builder.setStyle(style);

        StringBuilder linesBuilder = new StringBuilder();
        for (String line : lines) {
            linesBuilder.append(line).append("\n");
        }
        addTaskIntentShow(builder, linesBuilder.toString());

        addTaskIntentAddLine(builder);
        addTaskIntentRemoveLine(builder);

        manager.notify(NOTIFICATION_ID_TASK, builder.build());
    }

    private void addTaskIntentShow(NotificationCompat.Builder builder, String message) {
        Intent contentIntent = new Intent(this, MessageActivity.class);
        contentIntent.putExtra(MessageActivity.EXTRA_TEXT, message);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 5, contentIntent, flags);

        builder.setContentIntent(pendingIntent);
    }

    private void addTaskIntentAddLine(NotificationCompat.Builder builder) {
        Intent contentIntent = new Intent(this, TaskService.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contentIntent.setAction(APPEND_LINE);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getService(this, 4, contentIntent, flags);

        builder.addAction(new NotificationCompat.Action(0, getString(R.string.add), pendingIntent));
    }

    private void addTaskIntentRemoveLine(NotificationCompat.Builder builder) {
        Intent contentIntent = new Intent(this, TaskService.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contentIntent.setAction(REMOVE_LINE);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getService(this, 3, contentIntent, flags);

        builder.addAction(new NotificationCompat.Action(0, getString(R.string.remove), pendingIntent));
    }
}
