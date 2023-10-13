import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    long currentTimeMsg, lastTimeMsg = 0;
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
    Thread thread;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        MessageAuthor msgAuthor =  event.getMessageAuthor();
        if(msgAuthor.isYourself()) { return; }
        String message, ping, command, caseMessage;
        message = event.getMessageContent();
        caseMessage = message.toUpperCase();
        ping = "<@"+(GexBot.USERID)+">";

        if(message.indexOf(" ") == -1) { command = caseMessage; }
        else { 
            command = caseMessage.substring(0, caseMessage.indexOf(" "));
            message = message.substring(message.indexOf(" ")+1);
        }

        if(!(command.startsWith("!"))) {
            if(command.equals(ping)) {

                if(GexGPT.userIndex(msgAuthor) == -1) {
                    String[] user = { msgAuthor.toString(), "" };
                    GexGPT.userArr.add(user);
                    System.out.println("[MessageListener] Added user "+user[0]+" to AI context array.");
                }
                GexGPT.replyQueue.add(event);

                if(GexGPT.replyQueue.size() == 1) {
                    thread = (new Thread(new GexGPT()));
                    thread.start();
                }

            } else if(caseMessage.contains("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");

            } else if(caseMessage.contains("GEX") || message.contains(ping)) {
                event.getChannel().sendMessage( "<@" + (msgAuthor.getIdAsString())+"> " + TextReader.generateReply());

            } else if(caseMessage.contains("CRAZY")) {
                event.getChannel().sendMessage( "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");

            } else if(caseMessage.contains("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");

            } else {
                lastTimeMsg = currentTimeMsg;
                currentTimeMsg = System.currentTimeMillis();
                if( (currentTimeMsg - lastTimeMsg) <= GexBot.CHAT_TIME_THRESHOLD ) {
                    chatCount++;
                    if(chatCount >= chatCountThreshold) {
                        chatCount = 0;
                        chatCountThreshold *= 1.5;
                        event.getChannel().sendMessage(TextReader.generateQuip());
                    }
                } else {
                    chatCount -= ( (currentTimeMsg - lastTimeMsg) / (GexBot.CHAT_TIME_THRESHOLD*2) );
                    chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
                    if(chatCount < 0) { chatCount = 0; }
                }
            }

        } else {
            switch (command) {
                case "!TIME" :
                    event.getChannel().sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
                    break;
                case "!GEX" :
                    event.getChannel().sendMessage(TextReader.generateQuip());
                    break;
                case "!STATUS" :
                    if(msgAuthor.getDisplayName().equals("bread.java")) { 
                        GexBot.api.updateActivity( ActivityType.PLAYING, message);
                        System.out.println("[MessageListener] Activity status changed to \""+message+"\".");
                    }
                    break;
            }
        }
    }
}