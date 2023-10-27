import org.javacord.api.listener.message.MessageCreateListener;
import java.awt.Color;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    long currentTimeMsg, lastTimeMsg = 0;
    long firstTime = System.currentTimeMillis();
    int chatCount = 0;
    int chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
    static int countAIReply, countQuip, countGex, countTailTimes = 0;
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
            EmbedBuilder embed = new EmbedBuilder();

            switch (command) {
                case "TIME" :
                    channel.sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
                    countTailTimes++;
                    break;
                case "GEX" :
                    channel.sendMessage(TextReader.generateQuip());
                    break;
                case "STATUS" :
                    if(isAdmin) { 
                        GexBot.api.updateActivity( ActivityType.PLAYING, msg);
                        System.out.println("[MessageListener] Activity status changed to \""+msg+"\".");
                        channel.sendMessage("Status successfully changed! <:GexSmirk:1154237747544997930>");
                    } break;
                case "QUEUE" :
                    channel.sendMessage(GexGPT.embedQueue());
                    break;
                case "CONTEXT" :
                    if((GexBot.PREFIX+command).equals(msgCaps)) {
                        GexGPT.clearContext();
                        channel.sendMessage("AI context is cleared!");
                    }
                    break;
                case "PREFIX" :
                    if(isAdmin) {
                        GexBot.PREFIX = msg.substring(0, 1);
                        System.out.println("[MessageListener] Command prefix changed to "+GexBot.PREFIX+".");
                        channel.sendMessage("Changed command prefix to "+GexBot.PREFIX+".");
                    } break;
                case "MODEL" :
                    if(isAdmin) {
                        if((GexBot.PREFIX+command).equals(msgCaps)) {
                            embed
                                .setTitle("AI Chat Model List")
                                .setColor(Color.GREEN)
                                .addField("", "\n- Falcon\n- Wizard\n- Llama\n- Hermes\n- Uncensored")
                                .addField("Type \""+GexBot.PREFIX+"model NAME\" to change the model.", "")
                            ;
                            channel.sendMessage(embed);
                            break;
                        }

                        String temp = GexBot.convertModelName(msg);
                        if(temp.equals("")) {
                            System.out.println("[MessageListener] AI chat model could not be changed to \""+msg+"\".");
                            channel.sendMessage("AI chat model could not be changed to "+msg+"!");
                            break;
                        } else {
                            GexBot.AI_MODEL = temp;
                            GexGPT.loadModel();
                            System.out.println("[MessageListener] AI chat model successfully changed to \""+msg+"\".");
                            channel.sendMessage("AI chat model successfully changed to "+msg+"!");
                        }
                    } break;
                case "STATS" :
                    long days, hours, minutes, seconds;
                    seconds = (System.currentTimeMillis()-firstTime) / 1000;
                    minutes = seconds / 60;
                    hours = minutes / 60;
                    days = hours / 24;
                    seconds %= 60;
                    minutes %= 60;
                    hours %= 24;

                    embed
                        .setAuthor("GexBot "+GexBot.VERSION, "http://github.com/burntbread007/GexBot/", "https://cdn.discordapp.com/avatars/1154245369488756778/cc6e4baf92995a63317c1dad9e265d23.webp")
                        .setTitle("Global Statistics")
                        .setColor(Color.GREEN)
                        .addField("Total Messages Written:", "")
                        .addInlineField("Quips", countQuip+"")
                        .addInlineField("AI Replies", countAIReply+"")
                        .addInlineField("Gex References", countGex+"")
                        .addField("Current AI Model", GexBot.AI_MODEL)
                        .addField("Elapsed Runtime", days+" days, "+hours+" hours, "+minutes+" minutes, "+seconds+" seconds")
                        .setFooter("\"It's tail time!\" - Gexy")
                    ;
                    channel.sendMessage(embed);
                    break;
                case "SHUTDOWN" :
                    if(isAdmin) {
                        System.out.println("[MessageListener] Shutting down program by request...");
                        channel.sendMessage("Bye bye!");
                        System.exit(0);
                    } break;
            }

        } else {
            if(command.equals(selfPing)) {
                if(GexGPT.userIndex(msgAuthor) == -1) {
                    String[] user = { msgAuthor.toString(), "" };
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
                countTailTimes++;
            }
            else if(msgCaps.contains("GEX") || msgCaps.contains(selfPing)) {
                channel.sendMessage( "<@" + (msgAuthor.getIdAsString())+"> " + TextReader.generateReply());
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
                        channel.sendMessage(TextReader.generateQuip());
                    }
                } else {
                    chatCount -= ( (currentTimeMsg - lastTimeMsg) / (GexBot.CHAT_TIME_THRESHOLD*2) );
                    chatCountThreshold = GexBot.CHAT_COUNT_THRESHOLD;
                    if(chatCount < 0) { chatCount = 0; }
                }
            }
        }
    }
}