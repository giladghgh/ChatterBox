package org.ChatterBox;

import org.eEllinor.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import java.io.IOException;





public class ChatterBox {
	private final JFrame frame = new JFrame();
    
    private eEllinor bot;
    
    public String header;
    
    private JScrollPane dash;
    private JTextPane dashChat;
    
    private JPanel desk;
    private JTextField fieldPrompt;
    private JButton buttonSend;
    
    private StyledDocument doc;
    private Style styleBOLD;
    private Style styleNULL;
    
    private boolean isMessageFirst;
    private boolean isLastMessageHuman;
    
    private boolean active;
    private boolean listening;
    
    
    
    void main() throws Exception {
        // root
        header = "You are chatting to...";
        frame.setTitle(header);
        
        frame.setLocation(1000,100);
        frame.setSize(400,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // mechanics
        isMessageFirst = true;
        isLastMessageHuman = false;
        
        // assemble
        frame.setLayout(new BorderLayout());
        
        buildDash();
        buildDesk();
        buildFeatures();
        
        frame.setVisible(true);
        
        // function
        bootBot();
        openDialogue();
        shutDialogue();
    }
    
    
    void buildDash() {
        dashChat = new JTextPane();
        
        dashChat.setEditable(false);
        dashChat.setMargin(new Insets(5,5,5,5));
        
        dash = new JScrollPane(dashChat);
        
        // style
        doc = dashChat.getStyledDocument();
        styleBOLD = dashChat.addStyle("BOLD",null);
        styleNULL = dashChat.addStyle("NULL",null);
        
        StyleConstants.setBold(styleBOLD,true);
        
        frame.add(dash,BorderLayout.CENTER);
    }
    
    
    void buildDesk() {
        desk =  new JPanel(new BorderLayout());
        
        fieldPrompt = new JTextField();
        buttonSend = new JButton("Send");
        
        buttonSend.addActionListener(_ -> sendMessage(fieldPrompt.getText(),true));
        
        desk.add(fieldPrompt,BorderLayout.CENTER);
        desk.add(buttonSend,BorderLayout.EAST);
        
        frame.add(desk,BorderLayout.SOUTH);
    }
    
    
    void buildFeatures() {
        // <ENTER> presses Send button
        frame.getRootPane().setDefaultButton(buttonSend);
        
        // <ESCAPE> ends programme
        fieldPrompt.addKeyListener(new KeyAdapter() {
            public void keyPressed( KeyEvent e ){
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    listening = false;
                }
            }
        });
        
        // focus prompt field on window startup
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened( WindowEvent e ){
                fieldPrompt.requestFocus();
            }
        });
    }
    
    
    public void sendMessage(String msg , boolean isMessageHuman){
        fieldPrompt.requestFocus();
        
        if (msg.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
        if (msg.isBlank()) {
            return;
        }
        
        String title;
        if (!isMessageFirst && (isLastMessageHuman == isMessageHuman)) {
            title = "";
        }
        else {
            title = isMessageHuman?
                "\nYOU:\n":
                "\neELLINOR:\n";
        }
        
        if (isMessageFirst) {
            isMessageFirst = false;
            title = title.substring(1);
        }
        
        int align = isMessageHuman?
            StyleConstants.ALIGN_LEFT:
            StyleConstants.ALIGN_RIGHT;
        StyleConstants.setAlignment(styleBOLD,align);
        StyleConstants.setAlignment(styleNULL,align);
        
        dashChat.setCaretPosition(doc.getLength());
        dashChat.setParagraphAttributes(styleNULL,false);
        
        // write message
        String message = msg.trim() + "\n";
        
        try {
            doc.insertString(doc.getLength() , title , styleBOLD);
            doc.insertString(doc.getLength() , message , styleNULL);
        }
        catch (BadLocationException _) {}
        
        if (isMessageHuman) {
            fieldPrompt.setText("");
            bot.prompt = message;
            active = true;
        }
        
        isLastMessageHuman = isMessageHuman;
    }
    
    
    void bootBot() throws IOException {
        bot = new eEllinor();
        
        header = header + " " + bot.name + "!";
        frame.setTitle(header);
        
        listening = true;
        
        bot.boot();
        bot.init();
    }
    
    
    void openDialogue() throws InterruptedException {
        while (listening) {
            Thread.sleep(1);
//            IO.print("-");
            if (active && bot.prompt != null) {
//                IO.print("!");
                active = false;
                
                bot.chat();
                
                sendMessage(
                    bot.answer,
                    false
                );
            }
        }
    }
    
    
    void shutDialogue() {
        System.exit(0);
    }
}
