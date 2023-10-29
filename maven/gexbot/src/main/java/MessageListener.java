import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    long currentTimeMsg, lastTimeMsg = 0;
    static long firstTime = System.currentTimeMillis();
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
    Thread thread;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        MessageAuthor msgAuthor =  event.getMessageAuthor();
        if(msgAuthor.isBotUser()) { return; }

        TextChannel channel = event.getChannel();
        boolean isAdmin = msgAuthor.getDisplayName().equals(GexBot.ADMIN_USER);
        String msg, selfPing, command, msgCaps;

        msg = event.getMessageContent();
        msgCaps = msg.toUpperCase();
        selfPing = "<@"+(GexBot.USERID)+">";

        if(!msg.contains(" ")) { command = msgCaps; }
        else {
            command = msgCaps.substring(0, msgCaps.indexOf(" "));
            msg = msg.substring(msg.indexOf(" ")+1);
        }

        if(command.startsWith(GexBot.PREFIX)) {
            command = command.substring(1);
            switch (command) {
                case "TIME" :
                    channel.sendMessage(GexCommands.time());
                    break;
                case "GEX" :
                    channel.sendMessage(GexCommands.quip());
                    break;
                case "STATUS" :
                    if(isAdmin) { 
                        channel.sendMessage(GexCommands.status(msg));
                    } break;
                case "QUEUE" :
                    channel.sendMessage(GexCommands.queue());
                    break;
                case "CONTEXT" :
                    channel.sendMessage(GexCommands.context());
                    break;
                case "PREFIX" :
                    if(isAdmin) {
                        GexCommands.prefix(msg);
                    }
                    break;
                case "MODEL" :
                    if(isAdmin && !(GexBot.PREFIX+command).equals(msgCaps))
                        channel.sendMessage(GexCommands.model(command, msgCaps.substring(msgCaps.indexOf(" ")+1)));
                    else
                        channel.sendMessage(GexCommands.model());
                    break;
                case "STATS" :
                    channel.sendMessage(GexCommands.stats());
                    break;
                case "SHUTDOWN" :
                    if(isAdmin) {
                        channel.sendMessage("Bye bye!");
                        GexCommands.shutdown();
                    }
                    break;
                case "RESTART" :
                    if(isAdmin) {
                        channel.sendMessage("I always come back.");
                        GexCommands.restart();
                    }
                    break;
            }

        } else {
            if(command.equals(selfPing)) {
                if(GexGPT.getUserIndex(msgAuthor.getIdAsString()) == -1) {
                    String[] user = { msgAuthor.getIdAsString(), "" };
                    GexGPT.userArr.add(user);
                    System.out.println("[MessageListener] Added user "+user[0]+" to AI context array.");
                }
                GexGPT.replyQueue.add(event);

                if(GexGPT.replyQueue.size() == 1) {
                    System.out.println("[MessageListener] Starting new GexGPT thread.");
                    thread = (new Thread(new GexGPT()));
                    thread.start();
                }
            }
            else if(msgCaps.contains("TIME")) {
                channel.sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            }
            else if(msgCaps.contains("GEX") || msgCaps.contains(selfPing)) {
                channel.sendMessage( "<@" + (msgAuthor.getIdAsString())+"> " + GexCommands.reply());
            }
            else if(msgCaps.contains("CRAZY")) {
                channel.sendMessage( "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");
            }
            else {
                lastTimeMsg = currentTimeMsg;
                currentTimeMsg = System.currentTimeMillis();
                if( (currentTimeMsg - lastTimeMsg) <= GexBot.CHAT_TIME_THRESHOLD ) {
                    chatCount++;
                    if(chatCount >= chatCountThreshold) {
                        chatCount = 0;
                        chatCountThreshold *= 1.5;
                        channel.sendMessage(GexCommands.quip());
                    }
                } else {
                    chatCount -= ( (currentTimeMsg - lastTimeMsg) / (GexBot.CHAT_TIME_THRESHOLD*2) );
                    chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
                    if(chatCount < 0) { chatCount = 0; }
                }
            }
        }
        return;
    }
}