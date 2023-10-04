import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    MessageCreateEvent msgEvent;
    long currentTimeMsg, lastTimeMsg = 0;
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String message = event.getMessageContent();
        String command;
        if(message.indexOf(" ") == -1) { command = message.toUpperCase(); }
        else { command = message.toUpperCase().substring(0, message.indexOf(" ")); }
        String ping = "<@"+(GexBot.USERID)+">";
        msgEvent = event;

        if( !(event.getMessageAuthor()).toString().contains(GexBot.USERID) && !(event.getMessageContent().startsWith("!"))) {
            if(event.getMessageContent().startsWith(ping)) {
                event.getChannel().type();
                event.getChannel().sendMessage("<@" + (event.getMessageAuthor().getIdAsString())+"> "+GexGPT.generateReply(message.substring(message.indexOf(">")+1)));
            } else if(scanMessage("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            } else if(scanMessage("GEX") || scanMessage(ping)) {
                event.getChannel().sendMessage( "<@" + (event.getMessageAuthor().getIdAsString())+"> " + TextReader.generateReply());
            } else if(scanMessage("CRAZY")) {
                event.getChannel().sendMessage( "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");
            } else if(scanMessage("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            }

            lastTimeMsg = currentTimeMsg;
            currentTimeMsg = System.currentTimeMillis();
            if( (currentTimeMsg - lastTimeMsg) <= GexBot.CHAT_TIME_THRESHOLD ) {
                chatCount++;
                if(chatCount >= chatCountThreshold) {
                    chatCount = 0;
                    chatCountThreshold *= 1.5;
                    event.getChannel().sendMessage(TextReader.generatePhrase());
                }
            } else {
                chatCount -= ( (currentTimeMsg - lastTimeMsg) / (GexBot.CHAT_TIME_THRESHOLD*2) );
                chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
                if(chatCount < 0) { chatCount = 0; }
            }
        } else {
            switch (command) {
                case "!TIME" :
                    event.getChannel().sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
                    break;
                case "!GEX" :
                    event.getChannel().sendMessage(TextReader.generatePhrase());
                    break;
            }
        }
    }

    boolean scanMessage(String scanWord) {
        return (msgEvent.getMessageContent().toUpperCase().contains(scanWord));
    }
}