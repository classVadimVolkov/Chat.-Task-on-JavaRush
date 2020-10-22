package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class BotClient extends Client {
    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    @Override
    protected String getUserName() {
        String botName = "date_bot_" + (int) (Math.random()*100);
        return botName;
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected void sendTextMessage(String text) {
        super.sendTextMessage(text);
    }


    public class BotSocketThread extends Client.SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (message.contains(":")) {
                String[] nameAndText = message.split(":");
                String name = nameAndText[0];
                String text = nameAndText[1].trim();
                if (text.equals("дата")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d.MM.YYYY");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("день")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("месяц")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("год")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("время")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("час")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("H");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("минуты")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("m");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
                if (text.equals("секунды")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("s");
                    String date = dateFormat.format(Calendar.getInstance().getTime());
                    sendTextMessage("Информация для " + name + ": " + date);
                }
            }
        }
    }
}
