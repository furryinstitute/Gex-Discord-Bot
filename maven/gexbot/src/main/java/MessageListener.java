import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    MessageCreateEvent msgEvent;

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String phrase = event.getMessageContent();
        String command = phrase.toUpperCase();
        String words = command;
        String ping = "<@"+(GexBot.USERID)+">";
        msgEvent = event;
        if(command.indexOf(" ") != -1) {
            words = phrase.substring(command.indexOf(" "));
            command = command.substring(0, command.indexOf(" "));
        }

        if( !(event.getMessageAuthor()).toString().contains(GexBot.USERID) && !(event.getMessageContent().startsWith("!"))) {
            if(event.getMessageContent().startsWith(ping)) {
                event.getChannel().type();
                event.getChannel().sendMessage("<@" + (event.getMessageAuthor().getIdAsString())+"> "+GexGPT.generateReply(words));
            } else if(scanMessage("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            } else if(scanMessage("GEX")) {
                event.getChannel().sendMessage( "<@" + (event.getMessageAuthor().getIdAsString())+"> " + TextReader.generateReply());
            } else if(scanMessage("CRAZY")) {
                event.getChannel().sendMessage( "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");
            } else if(scanMessage("TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
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