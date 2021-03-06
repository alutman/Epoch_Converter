package view;

import main.Program;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.lang.*;

/**
 * Created by alutman on 19/02/14.
 *
 * Draws the GUI. Zero functionality on its own.
 *
 */
public class AppFrame extends JFrame {

    private JTextPane inputText;
    private JTextPane outputText;
    private JTextPane spanOutputText;

    private JTextField input;
    private JTextPane output;
    private JTextPane spanOutput;

    private JCheckBox bcEra;

    private JButton today;
    private JButton swap;
    private JButton clear;
    private JButton max;
    private JButton min;
    private JButton zero;
    private JButton timer;
    private JButton stop;

    private Color JAVA_DEFAULT_GRAY = new Color(240,240,240);
    private Font FONT = new Font("Monospaced", Font.PLAIN, DPIController.scaleToDPI(14));

    public AppFrame() {
        //Frame settings
        this.setSize(DPIController.scaleToDPI(500) ,DPIController.scaleToDPI(200));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Epoch Converter v"+ Program.VERSION);

        this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("image/epoch-white-64.png")).getImage());

        makeAll();
        this.setEnabled(true);
        this.setVisible(true);
    }
    private void makeAll() {
        makeInputOutput();
        makeButtons();
        makeDescriptions();
        makeLayout();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    private void makeInputOutput() {
        input = new JTextField();
        input.setFont(FONT);
        input.setActionCommand("input");

        output = new JTextPane();
        output.setFont(FONT);
        output.setEditable(false);
        output.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.BLACK));

        spanOutput = new JTextPane();
        spanOutput.setFont(FONT);
        spanOutput.setEditable(false);
        spanOutput.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK));

        bcEra = new JCheckBox("BC");
        bcEra.setEnabled(false);
        bcEra.setActionCommand("era");
    }
    private void makeButtons() {

        today = new JButton("Today");
        today.setActionCommand("today");
        
        swap = new JButton("Swap");
        swap.setActionCommand("swap");
        
        clear = new JButton("Clear");
        clear.setActionCommand("clear");
        
        max = new JButton("Max");
        max.setActionCommand("max");

        min = new JButton("Min");
        min.setActionCommand("min");

        zero = new JButton("Zero");
        zero.setActionCommand("zero");

        timer = new JButton("Timer");
        timer.setActionCommand("timer");

        stop = new JButton("Stop");
        stop.setActionCommand("stop");
        stop.setVisible(false);

    }



    private void makeDescriptions() {
        inputText = new JTextPane();
        inputText.setFont(FONT);
        inputText.setText(String.format("%10s","Input"));
        inputText.setEditable(false);
        inputText.setMaximumSize(new Dimension(DPIController.scaleToDPI(50), DPIController.scaleToDPI(50)));
        inputText.setBackground(JAVA_DEFAULT_GRAY);
        outputText = new JTextPane();
        outputText.setFont(FONT);
        outputText.setText(String.format("%10s", "Output"));
        outputText.setEditable(false);
        outputText.setMaximumSize(new Dimension(DPIController.scaleToDPI(50), DPIController.scaleToDPI(50)));
        outputText.setBackground(JAVA_DEFAULT_GRAY);
        spanOutputText = new JTextPane();
        spanOutputText.setFont(FONT);
        spanOutputText.setText(String.format("%10s", "Timespan"));
        spanOutputText.setEditable(false);
        spanOutputText.setMaximumSize(new Dimension(DPIController.scaleToDPI(50), DPIController.scaleToDPI(50)));
        spanOutputText.setBackground(JAVA_DEFAULT_GRAY);
    }
    private void makeLayout() {
        this.setLayout(new GridLayout(6,1));

        JPanel inputP = new JPanel();
        inputP.setLayout(new BoxLayout(inputP, BoxLayout.X_AXIS));
        inputP.add(inputText);
        inputP.add(input);
        this.add(inputP);

        JPanel swapP = new JPanel();
        swapP.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 2));
        swapP.setLayout(new BoxLayout(swapP, BoxLayout.X_AXIS));
        swapP.add(new JPanel());
        swapP.add(bcEra);
        swapP.add(new JPanel());
        swapP.add(new JPanel());
        swapP.add(new JPanel());
        swapP.add(clear);
        swapP.add(swap);
        this.add(swapP);

        JPanel outputP = new JPanel();
        outputP.setLayout(new BoxLayout(outputP, BoxLayout.X_AXIS));
        outputP.add(outputText);
        outputP.add(output);
        this.add(outputP);

        JPanel spanP = new JPanel();
        spanP.setLayout(new BoxLayout(spanP, BoxLayout.X_AXIS));
        spanP.add(spanOutputText);
        spanP.add(spanOutput);
        this.add(spanP);
        this.add(new JPanel());

        JPanel extraP = new JPanel();
        extraP.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        extraP.setLayout(new BoxLayout(extraP, BoxLayout.X_AXIS));
        extraP.add(today);
        extraP.add(max);
        extraP.add(min);
        extraP.add(zero);

        extraP.add(new JPanel());

        extraP.add(timer);
        extraP.add(stop);

        this.add(extraP);
    }
    public void setActionListeners(ActionListener al) {
        today.addActionListener(al);
        swap.addActionListener(al);
        clear.addActionListener(al);
        max.addActionListener(al);
        min.addActionListener(al);
        zero.addActionListener(al);
        timer.addActionListener(al);
        input.addActionListener(al);
        stop.addActionListener(al);
        bcEra.addActionListener(al);
    }
    public void setInputKeyListener(KeyListener kl) {
        input.addKeyListener(kl);
    }

    public void setInputText(String text) {
        input.setText(text);
    }
    public String getInputText() {
        return input.getText();
    }
    public void setOutputText(String text) {
        output.setText(text);
    }
    public String getOutputText() {
        return output.getText();
    }
    public void setSpanOutputText(String text) {
        spanOutput.setText(text);
    }
    public void setNonThreadButtons(boolean b) {
        swap.setEnabled(b);
        today.setEnabled(b);
        clear.setEnabled(b);
        max.setEnabled(b);
        min.setEnabled(b);
        zero.setEnabled(b);
        timer.setVisible(b);
        stop.setVisible(!b);
    }

    public boolean isBCSelected() {
        return bcEra.isSelected();
    }

    public void setBCSelected(boolean b) {
        bcEra.setSelected(b);
    }

    public void enableBCSelection(boolean b) {
        bcEra.setEnabled(b);
    }

}
