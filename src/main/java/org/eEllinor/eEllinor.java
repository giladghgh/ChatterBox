package org.eEllinor;

import org.ChatterBox.eBot;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;

import java.io.IOException;





/**
 * She's just like the real thing!
 */
public class eEllinor extends eBot {
    public final String name = "Ellinor";
    
    private final MessageWindowChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);
    private final ChatLanguageModel model = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("llama3")
        .build();
    
    
    public void boot() throws IOException {
        Runtime.getRuntime().exec("ollama run llama3");
    }

    public void init() {}
    
    
    public void chat() {
        if (prompt.isBlank()) {
            return;
        }
        
        // If you use AiMessage.from() instead here, the AI will try and complete/extend your sentences! Could be useful.
        memory.add(UserMessage.from(prompt));
        
        answer = model.generate(memory.messages()).content().text();
    }
}
