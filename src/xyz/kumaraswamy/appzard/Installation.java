package xyz.kumaraswamy.appzard;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.StringJoiner;

public class Installation {

    private final Appzard appzard;
    private JTextArea logs;

    private static final String BASH_SCRIPT = "" +
            "https://raw.githubusercontent.com/AppZard1/" +
            "AppzardOffline/main/scripts/install.sh";

    public Installation(Appzard appzard) {
        this.appzard = appzard;
    }

    public void install() {
        showGUI();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void showGUI() {
        Box box = new Box(BoxLayout.Y_AXIS) {{
            Tools.setTopMargin(this);
            add(
                    new Box(BoxLayout.X_AXIS) {{
                        add(
                                new JButton("Go Back") {{
                                    addActionListener(e -> {
                                        getParent().getParent().setVisible(false);
                                        appzard.vertical.setVisible(true);
                                    });
                                }}
                        );
                        add(
                                new JButton("Install Appzard") {{
                                    addActionListener(e -> {
                                        try {
                                            beginInstallation();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    });
                                }}
                        );
                    }}
            );
            add(
                    new Box(BoxLayout.X_AXIS) {{
                        add(new JButton("Add Path To Environment") {{
                            addActionListener(e -> {
                                File[] users = new File("C:\\Users\\").listFiles();
                                if (users == null) throw new IllegalArgumentException("Unexpected");
                                File file = null;
                                for (File user : users) {
                                    File test = new File("C:\\Users\\" + user.getName() + "\\.appzard");
                                    if (test.exists()) {
                                        file = test;
                                    }
                                }
                                if (file == null) {
                                    throw new IllegalArgumentException("File Not Found: "
                                            + Arrays.toString(users));
                                }
                                File destination = new File("C:\\appzard-inst\\");
                                file.renameTo(destination);
                                runQuietly("cmd", "/c", "start", "setx", "PATH \"" +
                                        destination + "\\bin\\" + ";%PATH%\"");
                            });
                        }});
                    }});
            add(
                    new Box(BoxLayout.X_AXIS) {{
                        Tools.setTopMargin(this);

                        add(
                                new JScrollPane(logs = new JTextArea() {{
                                    setAutoscrolls(true);
                                }}) {{
                                    setPreferredSize(new Dimension(
                                            500, 200));
                                    getVerticalScrollBar().setBorder(null);
                                }});
                    }});
        }};
        appzard.vertical.setVisible(false);
        appzard.panel.add(box);
    }

    private void runQuietly(String... commands) {
        StringJoiner joiner = new StringJoiner(" ");
        for (String command : commands) joiner.add(command);
        try {
            Runtime.getRuntime().exec(joiner.toString());
        } catch (IOException ignored) {

        }
    }

    private void beginInstallation() throws IOException {
        new Worker().execute();
    }

    class Worker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws IOException {
            URL yahoo = new URL(BASH_SCRIPT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yahoo.openStream()));

            String inputLine;
            File file = File.createTempFile("tempfilescript", "sh");
            FileWriter writer = new FileWriter(file);

            while ((inputLine = in.readLine()) != null) {
                writer.write("\n" + inputLine);
            }
            System.out.println(file.getAbsolutePath());
            in.close();
            writer.close();

            Process proc = Runtime.getRuntime().exec(new String[]{"bash", file.getAbsolutePath()});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            proc.getInputStream()));

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
                logs.setText(builder.toString());
            }
            file.deleteOnExit();
            return null;
        }
    }
}
