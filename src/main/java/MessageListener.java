/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3a
 */

import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {
    
   // Class variables
    final String selfPing = String.format("<@%s>", GexBot.USERID); 
    static final long firstTime = System.currentTimeMillis();
    long currentTimeMsg, lastTimeMsg = 0;
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;

    @Override
    public void onMessageCreate(final MessageCreateEvent event) {
        final MessageAuthor msgAuthor =  event.getMessageAuthor();
        if (msgAuthor.isBotUser()) return;

        // Variables derived from where message was received
        final TextChannel channel = event.getChannel();
        final String threadChannel = channel.asServerThreadChannel().toString();
        final boolean isAdmin = msgAuthor.getDisplayName().equals(GexBot.ADMIN_USER);
        final String userID = msgAuthor.getIdAsString();

        // Parses message
        String msg = event.getMessageContent(), msgCaps = msg.toUpperCase();

        String command = !msg.contains(" ") ? msgCaps : msgCaps.substring(0, msgCaps.indexOf(" "));
        msg = !msg.contains(" ") ? msg : msg.substring(msg.indexOf(" ")+1);

        // Runs when command prefix is first char in message
        if (command.startsWith(GexBot.PREFIX)) {
            command = command.substring(1);
            switch (command) {
            case "TIME" :
                channel.sendMessage(GexCommands.time()); break;
            case "GEX" :
                channel.sendMessage(GexCommands.quip()); break;
            case "STATUS" :
                if (isAdmin) channel.sendMessage(GexCommands.status(msg)); break;
            case "QUEUE" :
                channel.sendMessage(GexCommands.queue()); break;
            case "CONTEXT" :
                channel.sendMessage(GexCommands.context(userID, threadChannel)); break;
            case "TEMP" :
                channel.sendMessage(GexCommands.temp(msgCaps.substring(msgCaps.indexOf(" ")+1))); break;
            case "PREFIX" :
                if (isAdmin) GexCommands.prefix(msg); break;
            case "MODEL" :
                if (isAdmin && !(GexBot.PREFIX+command).equals(msgCaps))
                    channel.sendMessage(GexCommands.model(
                            command, msgCaps.substring(msgCaps.indexOf(" ")+1)
                    ));
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
                break;
            }
        }
        else {
            // Run when Gex is pinged
            if (command.equals(selfPing)) {
                channel.type();
                final boolean isThread = !threadChannel.equals("Optional.empty");
                final String key = isThread ? threadChannel : userID;
                final boolean contains = GexGPT.users.containsKey(key);

                final String[] empty = {"", ""}, item = contains ? GexGPT.users.get(key) : empty;
                if (!contains) System.out.printf("%n[MessageListener] Adding user %s to context map.%n", key);
                GexGPT.users.put(key, item);

                GexGPT.replyQueue.add(event);
                if (GexGPT.replyQueue.size() == 1) {
                    System.out.printf("%n[MessageListener] Starting new GexGPT thread.%n");
                    new Thread(new GexGPT()).start();
                }
            }
            // Run when key word is found
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
            // Run when nothing else does; calculates the
            // time between messages and sends quips
            else {
                lastTimeMsg = currentTimeMsg;
                currentTimeMsg = System.currentTimeMillis();
                if ((currentTimeMsg - lastTimeMsg) <= GexBot.CHAT_TIME_THRESHOLD) {
                    chatCount++;
                    if (chatCount >= chatCountThreshold) {
                        chatCount = 0;
                        chatCountThreshold *= 1.75;
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
