package xyz.kumaraswamy.appzard;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import static xyz.kumaraswamy.appzard.Tools.setTopMargin;

public class Appzard extends JFrame {

    public final JPanel panel = new JPanel();
    public final Box vertical;

    private JButton initializeBtn = null;

    public String logs = "";

    static {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public Appzard() {
        setTitle("Appzard");
        setJMenuBar(createMenuBar());

        vertical = Box.createVerticalBox();
        setTopMargin(vertical);

        Box operationBox = Box.createHorizontalBox();
        operationBox.add(
                new JButton("Initialize") {{
                    initializeBtn = this;
                    addActionListener(e -> {
                        setText("Initializing");
                        SwingWorker<Void, Void> worker = new Worker(
                                Appzard.this, this);
                        worker.execute();
                    });
                }});

        operationBox.add(new JButton("Kill Instance") {{
            addActionListener(e -> {
                try {
                    killInstance();
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
        }});
        vertical.add(operationBox);

        Box copyLogsBox = new Box(BoxLayout.X_AXIS) {{
            add(
                    new JButton("Copy Logs") {{
                        addActionListener(e ->
                                Toolkit.getDefaultToolkit()
                                        .getSystemClipboard()
                                        .setContents(new StringSelection(
                                                logs.trim()), null));
                    }});
        }};
        vertical.add(copyLogsBox);

        Box versionBox = new Box(BoxLayout.X_AXIS) {{
            add(new JLabel() {{
                setTopMargin(this);
                setText("Version: " + appzardVersion());
            }});
        }};
        vertical.add(versionBox);

        Box extra = new Box(BoxLayout.X_AXIS) {{
            add(new JButton("Appzard Info") {{
                addActionListener(e -> browseURL("https://github.com/AppZard1/AppzardOffline"));
            }});
            add(new JButton("Help") {{
                addActionListener(e -> browseURL("https://community.appzard.com"));
            }});
        }};
        vertical.add(extra);

        panel.add(vertical);
        showGUIFrame();

        // for testing purpose only
    }

    private void browseURL(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(URI.create(url));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void killInstance() throws IOException, InterruptedException {
        killPort(8888);
        killPort(9990);
        initializeBtn.setText("Initialize");
    }

    private void killPort(int port) throws IOException {
        String command = "netstat -ano";
        Process proc = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        proc.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append("\n").append(line);
        }
        for (String tcp : builder.toString().split("\n")) {
            while (tcp.contains("  ")) {
                tcp = tcp.replaceAll(" {2}", " ");
            }
            final String[] elements = tcp.trim().split(" ");
            if (elements.length >= 2) {
                final String[] ports = elements[1].split(":");
                if (ports.length >= 2) {
                    if (ports[1].equals(Integer.toString(port))) {
                        System.out.println("PID for " + port + ": " + elements[4]);
                        Runtime.getRuntime().exec(
                                "taskkill /PID "
                                        + elements[4] + " /F");
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String appzardVersion() {
        try {
            Process process = Runtime.getRuntime()
                    .exec("appzard -v");
            InputStream stream = process.getInputStream();
            process.waitFor();
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            return new String(bytes);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "<....>";
    }

    private void showGUIFrame() {
        add(panel);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JMenu createEditMenu() {
        return new JMenu("More") {{
            add(
                    new JMenuItem("Community") {{
                        addMouseListener(new MouseClickListener() {
                            @Override
                            public void whenClick() {
                                browseURL("https://community.appzard.com");
                            }
                        });
                    }});
            add(
                    new JMenuItem("Install Appzard") {{
                        addMouseListener(new MouseClickListener() {
                            @Override
                            public void whenClick() {
                                Installation installation = new Installation(Appzard.this);
                                installation.install();
                            }
                        });
                    }});
        }};
    }

    private JMenuBar createMenuBar() {
        return new JMenuBar() {{
            add(createEditMenu());
        }};
    }
}
