package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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

    private JButton calculate;
    private JButton today;
    private JButton swap;
    private JButton clear;
    private JButton max;
    private JButton realTime;
    private JButton timer;
    private JButton stop;
    private JButton pause;

    public AppFrame() {
        //Frame settings
        this.setSize(500,200);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Epoch converter");

        makeAll();
        this.setEnabled(true);
        this.setVisible(true);
    }
    private void makeAll() {
        makeInputOutput();
        makeButtons();
        makeDescriptions();
        makeLayout();
    }
    private void makeInputOutput() {
        input = new JTextField();
        input.setActionCommand("input");
        output = new JTextPane();
        output.setEditable(false);
        output.setBorder(BorderFactory.createMatteBorder(1,1,0,0,Color.BLACK));
        spanOutput = new JTextPane();
        spanOutput.setEditable(false);
        spanOutput.setBorder(BorderFactory.createMatteBorder(1,1,0,0,Color.BLACK));
    }
    private void makeButtons() {
        calculate = new JButton("Calculate");
        calculate.setActionCommand("calculate");
        
        today = new JButton("Today");
        today.setActionCommand("today");
        
        swap = new JButton("Swap");
        swap.setActionCommand("swap");
        
        clear = new JButton("Clear");
        clear.setActionCommand("clear");
        
        max = new JButton("Max");
        max.setActionCommand("max");
        
        realTime = new JButton("Real Time");
        realTime.setActionCommand("realtime");
        
        timer = new JButton("Timer");
        timer.setActionCommand("timer");
        
        stop = new JButton("Stop");
        stop.setActionCommand("stop");
        
        stop.setEnabled(false);
        pause = new JButton("Pause");
        pause.setActionCommand("pause");
        
        pause.setEnabled(false);
    }
    private void makeDescriptions() {
        inputText = new JTextPane();
        inputText.setFont(new Font("Monospaced", Font.PLAIN,14));
        inputText.setText(String.format("%10s","Input"));
        inputText.setEditable(false);
        inputText.setMaximumSize(new Dimension(50,50));
        inputText.setBackground(new Color(240,240,240));
        outputText = new JTextPane();
        outputText.setFont(new Font("Monospaced", Font.PLAIN,14));
        outputText.setText(String.format("%10s","Output"));
        outputText.setEditable(false);
        outputText.setMaximumSize(new Dimension(50,50));
        outputText.setBackground(new Color(240,240,240));
        spanOutputText = new JTextPane();
        spanOutputText.setFont(new Font("Monospaced", Font.PLAIN,14));
        spanOutputText.setText(String.format("%10s","Timespan"));
        spanOutputText.setEditable(false);
        spanOutputText.setMaximumSize(new Dimension(50,50));
        spanOutputText.setBackground(new Color(240,240,240));
    }
    private void makeLayout() {
        this.setLayout(new GridLayout(6,1));

        JPanel inputP = new JPanel();
        inputP.setLayout(new BoxLayout(inputP, BoxLayout.X_AXIS));
        inputP.add(inputText);
        inputP.add(input);
        this.add(inputP);

        JPanel swapP = new JPanel();
        swapP.setLayout(new BoxLayout(swapP, BoxLayout.X_AXIS));
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
        this.add(calculate);
        JPanel extraP = new JPanel();
        extraP.setLayout(new BoxLayout(extraP, BoxLayout.X_AXIS));
        extraP.add(today);
        extraP.add(max);
        extraP.add(new JPanel());
        extraP.add(realTime);
        extraP.add(timer);
        extraP.add(new JPanel());
        extraP.add(pause);
        extraP.add(stop);
        this.add(extraP);
    }
    public void setActionListener(ActionListener al) {
        calculate.addActionListener(al);
        today.addActionListener(al);
        swap.addActionListener(al);
        clear.addActionListener(al);
        max.addActionListener(al);
        realTime.addActionListener(al);
        timer.addActionListener(al);
        stop.addActionListener(al);
        pause.addActionListener(al);
        input.addActionListener(al);
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
    public JButton getPause() {
        return pause;
    }
    public JButton getStop() {
        return stop;
    }
    public void setNonThreadButtons(boolean b) {
        calculate.setEnabled(b);
        swap.setEnabled(b);
        today.setEnabled(b);
        clear.setEnabled(b);
        max.setEnabled(b);
        realTime.setEnabled(b);
        timer.setEnabled(b);
        stop.setEnabled(!b);
        pause.setEnabled(!b);
    }

}
