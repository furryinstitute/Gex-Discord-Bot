/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.2a
 */

import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {
    
    final String  selfPing = String.format("<@%s>", GexBot.USERID); 
    final static long firstTime = System.currentTimeMillis();
    long currentTimeMsg, lastTimeMsg = 0;
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
    Thread thread;

    @Override
    public void onMessageCreate(final MessageCreateEvent event) {
        final MessageAuthor msgAuthor =  event.getMessageAuthor();
        if (msgAuthor.isBotUser()) return;

        final TextChannel channel = event.getChannel();
        final String threadChannel = channel.asServerThreadChannel().toString();
        final boolean isAdmin = msgAuthor.getDisplayName().equals(GexBot.ADMIN_USER);
        final String userID = msgAuthor.getIdAsString();

        String msg = event.getMessageContent();
        String msgCaps = msg.toUpperCase();

        String command = !msg.contains(" ") ? msgCaps : msgCaps.substring(0, msgCaps.indexOf(" "));
        msg = !msg.contains(" ") ? msg : msg.substring(msg.indexOf(" ")+1);

        if (command.startsWith(GexBot.PREFIX)) {
            command = command.substring(1);
            switch (command) {
            case "TIME" :
                channel.sendMessage(GexCommands.time());
                break;
            case "GEX" :
                channel.sendMessage(GexCommands.quip());
                break;
            case "STATUS" :
                if(isAdmin)
                    channel.sendMessage(GexCommands.status(msg));
                break;
            case "QUEUE" :
                channel.sendMessage(GexCommands.queue());
                break;
            case "CONTEXT" :
                channel.sendMessage(GexCommands.context(userID, threadChannel));
                break;
            case "TEMP" :
                channel.sendMessage(GexCommands.temp(msgCaps.substring(msgCaps.indexOf(" ")+1)));
                break;
            case "PREFIX" :
                if (isAdmin)
                    GexCommands.prefix(msg);
                break;
            case "MODEL" :
                if (isAdmin && !(GexBot.PREFIX+command).equals(msgCaps))
                    channel.sendMessage(GexCommands.model(command, msgCaps.substring(msgCaps.indexOf(" ")+1)));
                else
                    channel.sendMessage(GexCommands.model());
                break;
            case "STATS" :
                channel.sendMessage(GexCommands.stats());
                break;
            case "SHUTDOWN" :
                if (isAdmin) {
                    channel.sendMessage("Bye bye!");
                    GexCommands.shutdown();
                }
                break;
            case "RESTART" :
                if (isAdmin) {
                    channel.sendMessage("I always come back.");
                    GexCommands.restart();
                }
            }
        }
        else {
            if (command.equals(selfPing)) {
                channel.type();
                final boolean isRegularChannel = threadChannel.equals("Optional.empty");
                final boolean threadExists =  (GexGPT.getIndex(GexGPT.channelThreadArr, threadChannel) != -1);
                final boolean userExists =    (GexGPT.getIndex(GexGPT.userArr, userID) != -1);
                String[] item = {"", "", ""};
                if (!(isRegularChannel || threadExists)) {
                    item[0] = threadChannel;
                    GexGPT.channelThreadArr.add(item);
                    System.out.printf("%n[MessageListener] Added thread %s to AI context array.%n", item[0]);
                } else if (!(userExists)) {
                    item[0] = userID;
                    GexGPT.userArr.add(item);
                    System.out.printf("%n[MessageListener] Added user %s to AI context array.%n", item[0]);
                }

                GexGPT.replyQueue.add(event);
                if (GexGPT.replyQueue.size() == 1) {
                    System.out.printf("%n[MessageListener] Starting new GexGPT thread.%n");
                    thread = new Thread(new GexGPT());
                    thread.start();
                }
            }
            else if (msgCaps.contains("TIME")) {
                channel.sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            }
            else if (msgCaps.contains("GEX") || msgCaps.contains(selfPing)) {
                channel.sendMessage(String.format("<@%s> %s", userID, GexCommands.reply()));
            }
            else if (msgCaps.contains("CRAZY")) {
                channel.sendMessage("Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");
            }
            else {
                lastTimeMsg = currentTimeMsg;
                currentTimeMsg = System.currentTimeMillis();
                if ((currentTimeMsg - lastTimeMsg) <= GexBot.CHAT_TIME_THRESHOLD) {
                    chatCount++;
                    if (chatCount >= chatCountThreshold) {
                        chatCount = 0;
                        chatCountThreshold *= 1.5;
                        channel.sendMessage(GexCommands.quip());
                    }
                } else {
                    chatCount -= ((currentTimeMsg - lastTimeMsg) / (GexBot.CHAT_TIME_THRESHOLD*2));
                    chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
                    if (chatCount < 0) chatCount = 0;
                }
            }
        }
        return;
    }
}
