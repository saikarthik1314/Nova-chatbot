import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Random;
import javax.swing.*;

public class NovaChat extends JFrame {

    private JTextArea displayBox;
    private JTextField userBox;
    private JButton sendBtn;

    private String nextTask = null;

    public NovaChat() {
        setTitle("Nova Assistant");
        setSize(620, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel header = new JPanel();
        header.setBackground(new Color(40, 49, 85));
        header.setPreferredSize(new Dimension(620, 60));
        JLabel title = new JLabel("Nova AI Chat");
        title.setFont(new Font("Verdana", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title);

        displayBox = new JTextArea();
        displayBox.setEditable(false);
        displayBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        displayBox.setBackground(new Color(245, 245, 245));
        displayBox.setMargin(new Insets(10, 10, 10, 10));
        displayBox.setLineWrap(true);
        displayBox.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(displayBox);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        userBox = new JTextField();
        userBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bottom.add(userBox, BorderLayout.CENTER);

        sendBtn = new JButton("Send");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sendBtn.setBackground(new Color(60, 90, 180));
        sendBtn.setForeground(Color.WHITE);
        bottom.add(sendBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(chatScroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        display("Nova: Hello! I'm Nova, your virtual companion.");
        display("Nova: Ask me jokes, weather, time, or simple maths!");
        display("Nova: Type 'exit' to stop.\n");

        sendBtn.addActionListener(e -> handleMessage());
        userBox.addActionListener(e -> handleMessage());

        setVisible(true);
    }

    private void display(String text) {
        displayBox.append(text + "\n");
        displayBox.setCaretPosition(displayBox.getDocument().getLength());
    }

    private void handleMessage() {
        String msg = userBox.getText().trim();
        if (msg.isEmpty()) return;

        display("You: " + msg);
        userBox.setText("");

        if (nextTask != null) {
            handleTask(msg);
            return;
        }

        if (msg.equalsIgnoreCase("exit")) {
            display("Nova: Goodbye human! Turning off my circuits...");
            userBox.setEnabled(false);
            sendBtn.setEnabled(false);
            return;
        }

        display("Nova: " + generateReply(msg) + "\n");
    }

    private void handleTask(String input) {
        String lower = input.toLowerCase();

        try {
            switch (nextTask) {
                case "weather":
                    if (lower.contains("yes")) {
                        nextTask = "weather_city";
                        display("Nova: Which city?");
                    } else {
                        display("Nova: Cancelled.\n");
                        nextTask = null;
                    }
                    break;

                case "weather_city":
                    Desktop.getDesktop().browse(new URI(
                            "https://www.accuweather.com/en/search-locations?query=" +
                            URLEncoder.encode(input, "UTF-8")));
                    display("Nova: Weather page opened!\n");
                    nextTask = null;
                    break;

                case "time":
                    if (lower.contains("yes")) {
                        nextTask = "time_zone";
                        display("Nova: Which city/country?");
                    } else {
                        display("Nova: Cancelled.\n");
                        nextTask = null;
                    }
                    break;

                case "time_zone":
                    Desktop.getDesktop().browse(new URI(
                            "https://time.is/" + URLEncoder.encode(input, "UTF-8")));
                    display("Nova: Time page opened!\n");
                    nextTask = null;
                    break;
            }
        } catch (Exception e) {
            display("Nova: Couldn't open browser.\n");
            nextTask = null;
        }
    }

    private String generateReply(String input) {
        String txt = input.toLowerCase();
        Random rand = new Random();

        if (txt.contains("hi") || txt.contains("hello") || txt.contains("hey")) {
            String[] greet = {"Hello!", "Hey there!", "Hi human!", "Greetings!"};
            return greet[rand.nextInt(greet.length)];
        }

        if (txt.contains("your name"))
            return "I'm Nova. Your friendly AI buddy.";

        if (txt.contains("how are you"))
            return "I am functioning within normal parameters. :)";

        if (txt.contains("joke")) {
            String[] jokes = {
                "Why was the computer cold? It forgot to close its windows!",
                "Why do coders prefer dark mode? Light attracts bugs!",
                "What do you call a singing laptop? A Dell!"
            };
            return jokes[rand.nextInt(jokes.length)];
        }

        if (txt.contains("weather")) {
            nextTask = "weather";
            return "Want me to open a weather website for you? (yes/no)";
        }

        if (txt.contains("time")) {
            nextTask = "time";
            return "Should I fetch the time from the website? (yes/no)";
        }

        if (txt.matches(".[0-9].")) {
            try {
                return solveMath(txt);
            } catch (Exception e) {
                return "I only do simple maths! Try something like 5+3.";
            }
        }

        String[] fallback = {
            "Hmm, not sure I understood.",
            "Can you rephrase that?",
            "I didn't get that, try again?"
        };
        return fallback[rand.nextInt(fallback.length)];
    }

    private String solveMath(String m) {
        m = m.replaceAll("[^0-9+\\-*/]", "").trim();

        if (m.contains("+")) {
            String[] p = m.split("\\+");
            return "Answer: " + (Integer.parseInt(p[0]) + Integer.parseInt(p[1]));
        }
        if (m.contains("-")) {
            String[] p = m.split("-");
            return "Answer: " + (Integer.parseInt(p[0]) - Integer.parseInt(p[1]));
        }
        if (m.contains("*")) {
            String[] p = m.split("\\*");
            return "Answer: " + (Integer.parseInt(p[0]) * Integer.parseInt(p[1]));
        }
        if (m.contains("/")) {
            String[] p = m.split("/");
            if (Integer.parseInt(p[1]) == 0) return "Division by zero? Nope!";
            return "Answer: " + (Double.parseDouble(p[0]) / Double.parseDouble(p[1]));
        }

        return "Try + - * or / symbols.";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NovaChat::new);
    }
}