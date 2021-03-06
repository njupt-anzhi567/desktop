package com.fungus_soft.desktop.bartray;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.fungus_soft.desktop.Main;
import com.fungus_soft.desktop.SystemBar;
import com.fungus_soft.desktop.Utils;
import com.fungus_soft.desktop.api.JProgram;

@Deprecated
public class InternetIcon extends TrayIcon {

    private static final long serialVersionUID = 1L;

    public InternetIcon() {
        super(null, "wifi.png");

        this.addMouseListener(Utils.click(e -> {

            JProgressBar pb = new JProgressBar();
            pb.setStringPainted(true);

            JPanel z = new JPanel();
            JLabel l = new JLabel("");
            z.add(l);
            z.add(pb);

            refresh(pb,l);
            Timer t = new Timer(2000, a -> refresh(pb, l));
            t.start();

            JProgram p = new JProgram("Network");
            p.setMaximizable(false);
            p.setIconifiable(false);
            p.setVisible(true);
            p.setContentPane(z);
            p.addInternalFrameListener(new InternalFrameAdapter(){
                public void internalFrameClosing(InternalFrameEvent e) { t.stop(); }
            });
            Main.p.add(p, 200, 300);
            effect(p);
        }));

    }

    public void effect(JProgram p) {
        new Thread(() -> {
            boolean b = true;
            for (int i = SystemBar.get.getY(); i > (SystemBar.get.getY() - p.getHeight()); i--) {
                p.setLocation(((TaskBarTray.get().getX() - 15) - (getX() + getWidth())), i--);
                try {
                    if ((b = !b) && !Main.isLowMemory) Thread.sleep(1);
                } catch (InterruptedException e1) { e1.printStackTrace(); }
                p.validate();
            }
        }).start();

        p.setLocation(SystemBar.get.getWidth(), SystemBar.get.getY() - p.getHeight());
        p.validate();
    }

    public void refresh(JProgressBar pb, JLabel ssidl) {
        Utils.runAsync(() -> {
            String[] txt = system("netsh wlan show interfaces", true);

            String ssid = "Not Connected";
            int progress = 0;

            for (String str : txt) {
                str = str.trim();
                if (!str.contains(":")) continue;
    
                String after = str.substring(str.indexOf(":") + 1).trim();
                if (str.startsWith("SSID")) ssid = after;
    
                if (str.startsWith("Signal"))
                    progress = Integer.valueOf(after.replace("%", ""));
            }
    
            pb.setValue(progress);
            pb.setStringPainted(true);
    
            ssidl.setText(ssid);
        });
    }

}