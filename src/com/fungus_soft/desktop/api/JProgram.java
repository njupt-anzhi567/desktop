package com.fungus_soft.desktop.api;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import jthemes.StyledJInternalFrame;
import com.fungus_soft.desktop.Main;
import com.fungus_soft.desktop.SystemBar;
import com.fungus_soft.desktop.theme.IconPack;

/**
 * API class for Program developers
 * 
 * In most cases you can replace JFrames with this as this is a JInternalFrame
 */
@ProgramInfo(name="Untitled Program")
public class JProgram extends StyledJInternalFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Main constructor for programs
     * The window will be closable, resizable, & maximizable.
     */
    public JProgram(String title) {
        this(title, true, true, true);
    }

    /**
     * Main constructor for programs
     * The window will be closable, resizable, & maximizable.
     */
    public JProgram() {
        this(null);
        this.setTitle(getInfo().name());
    }

    public JProgram(String title, boolean resizable, boolean closable, boolean maximizable) {
        super(title, resizable, closable, maximizable);
        this.toFront();
        this.min = true;
        this.moveToFront();
        this.validate();
        this.setIconifiable(true);
        if (SystemBar.get != null) {
            this.setMaximumSize(new Dimension((int)this.getMaximumSize().getWidth(), (int)this.getMaximumSize().getHeight() - SystemBar.get.getHeight()));
            this.setMaximizedBounds(new Rectangle((int)this.getMaximumSize().getWidth(), (int)this.getMaximumSize().getHeight() - SystemBar.get.getHeight()));
        }
        try {
            Icon icon = IconPack.getIcon("res/icons/menu/" + getClass().getName() + ".png", false);
            if (icon == null)
                icon = IconPack.get().blank;
            this.setFrameIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Dimension oldSize;

    /**
     * When Maximized internal frame will fully fit the JDesktopPane minus the SystemBar 
     */
    @Override
    public void setMaximum(boolean b) throws PropertyVetoException {
        if (isMaximum) {
            setSize(oldSize);
            super.setMaximum(b);
            return;
        }
        oldSize = this.getSize();
        super.setMaximum(true);
        this.isMaximum = true;
        this.setSize(new Dimension(Main.p.getWidth(), Main.p.getHeight() - SystemBar.get.getHeight()));
    }

    //Override
    public void setFrsameIcon(Icon icon) {
        if (icon == null || !(icon instanceof ImageIcon)) return;

        super.setFrameIcon(new ImageIcon( ((ImageIcon) icon).getImage().getScaledInstance(16, 16, 0) ));
    }

    public BasicInternalFrameUI getUI() {
        return ((BasicInternalFrameUI)super.getUI());
    }

    public ProgramInfo getInfo() {
        return getClass().getAnnotation(ProgramInfo.class);
    }

    public void setDisplayInSystemBar(boolean b) {
        this.putClientProperty("dontDisplayInWindowBar", b ? null : true);
    }
    
    @Override
    public void setState(int i) {
        if (i == JFrame.ICONIFIED) {
            setVisible(false);
        } else super.setState(i);
    }

}