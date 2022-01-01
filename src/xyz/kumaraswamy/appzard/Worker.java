package xyz.kumaraswamy.appzard;

import javax.swing.JButton;
import javax.swing.SwingWorker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Worker extends SwingWorker<Void, Void> {

    private final Appzard appzard;
    private final JButton button;

    public Worker(Appzard appzard, JButton button) {
        this.appzard = appzard;
        this.button = button;
    }

    @Override
    protected Void doInBackground() throws InterruptedException, IOException {
        Process proc = Runtime.getRuntime().exec("appzard start -d");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        proc.getInputStream()));

        StringBuilder logs = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            StringBuilder builder = new StringBuilder();
            char[] charArray = line.toCharArray();
            boolean first = false, used = charArray[0] == '\u001B';
            for (int i = 0; i < charArray.length; i++) {
                char ch = charArray[i];
                if (ch == '\u001B') {
                    if (!first) {
                        i = i + 4;
                        first = true;
                        continue;
                    }
                    break;
                }
                builder.append(ch);
            }
            System.out.println(line);
            if (used) {
                button.setText(builder.toString());
            }
            logs.append("\n").append(builder);
            appzard.logs = logs.toString();
        }
        proc.waitFor();
        reader.close();

        appzard.logs = logs.toString();
        publish();
        return null;
    }
}