/*
 * Copyright 2018 John Grosh (john.a.grosh@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.dialogflowbot;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;


/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Listener extends ListenerAdapter
{
    private String mention1, mention2;
    private OkHttpClient client;
    private final AIDataService ai;
    
    public Listener(AIDataService ai)
    {
        this.ai = ai;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        // init
        if(client==null)
        {
            mention1 = "<@"+event.getJDA().getSelfUser().getId()+">";
            mention2 = "<@!"+event.getJDA().getSelfUser().getId()+">";
            client = ((JDAImpl)event.getJDA()).getHttpClientBuilder().build();
        }
        
        // respond to all dms, and all messages in guilds starting with a ping
        String content = event.getMessage().getContentRaw();
        if(event.getGuild()!=null)
        {
            if(content.startsWith(mention1))
                content = content.substring(mention1.length()).trim();
            else if(content.startsWith(mention2))
                content = content.substring(mention2.length()).trim();
            else
                content = null;
        }
        
        // respond
        if(content!=null)
        {
            if(content.isEmpty())
            {
                event.getMessage().addReaction("\uD83D\uDC40").queue();
            }
            else
            {
                event.getChannel().sendTyping().queue();
                try
                {
                    AIResponse response = ai.request(new AIRequest(content));
                    if (response.getStatus().getCode() == 200 && !response.getResult().getFulfillment().getSpeech().isEmpty())
                        event.getChannel().sendMessage(response.getResult().getFulfillment().getSpeech()).queue();
                    else
                        event.getMessage().addReaction("\u274C").queue();
                }
                catch (AIServiceException ex)
                {
                    event.getMessage().addReaction("\u274C").queue();
                }
            }
        }
    }
    
    
}
