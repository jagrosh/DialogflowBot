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

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class Main
{
    public static void main(String[] args) throws Exception
    {
        List<String> tokens = Files.readAllLines(Paths.get("config.txt"));
        new JDABuilder(AccountType.BOT)
                .setToken(tokens.get(0))
                .addEventListener(new Listener(new AIDataService(new AIConfiguration(tokens.get(2)))))
                .setGame(parse(tokens.get(1)))
                .buildAsync();
    }
    
    private static Game parse(String name)
    {
        String lower = name.toLowerCase();
        if(lower.startsWith("playing"))
            return Game.playing(name.substring(7).trim());
        if(lower.startsWith("listening to"))
            return Game.listening(name.substring(12).trim());
        if(lower.startsWith("listening"))
            return Game.listening(name.substring(9).trim());
        if(lower.startsWith("watching"))
            return Game.watching(name.substring(8).trim());
        return Game.playing(name);
    }
}
