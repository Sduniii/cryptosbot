package guiswing;

import com.bulenkov.darcula.DarculaLaf;
import com.bulenkov.darcula.DarculaLookAndFeelInfo;
import com.bulenkov.darcula.ui.DarculaButtonUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class Main extends JFrame {

    public Main(){
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Main frame=new Main();
        frame.setTitle("Word Cloud");
        frame.setSize(1000, 620);
        frame.setResizable(false);
        frame.setLocation(50, 50);
        frame.setVisible(true);

    }

    private void initComponents() {
        JButton jButton1 = new javax.swing.JButton();
        JButton jButton2 = new javax.swing.JButton();
        JButton jButton3 = new javax.swing.JButton();
        JButton jButton4 = new javax.swing.JButton();
        JButton jButton5 = new javax.swing.JButton();
        JButton jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jButton1.setText("jButton1");
        getContentPane().add(jButton1);

        jButton2.setText("jButton2");
        getContentPane().add(jButton2);

        jButton3.setText("jButton3");
        getContentPane().add(jButton3);

        jButton4.setText("jButton4");
        getContentPane().add(jButton4);

        jButton5.setText("jButton5");
        getContentPane().add(jButton5);

        jButton6.setText("jButton6");
        getContentPane().add(jButton6);

        pack();
    }
}
