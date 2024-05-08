package wykres;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class DiffractionGrating extends JFrame {
    private static final long serialVersionUID = 1L;
    ArrayList<WykresyDyfrakcji> wykresyList;
    JSlider slider1, slider2;
    JTextField waveField, gratingField;
    JCheckBox red, green, blue, black;
    JMenuItem save, open, restart, exit;
    Map<Color, Boolean> wavelengthSet;
    JPanel chartsPanel;

    public DiffractionGrating() {
        setTitle("Diffraction Simulation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        wykresyList = new ArrayList<>();
        wavelengthSet = new HashMap<>();

        chartsPanel = new JPanel(new GridLayout(0, 1));
        add(new JScrollPane(chartsPanel), BorderLayout.CENTER);

        Color redColor = Color.RED;
        Color greenColor = Color.GREEN;
        Color blueColor = Color.BLUE;
        Color blackColor = Color.BLACK;

        addWykresDyfrakcji(redColor);
        addWykresDyfrakcji(greenColor);
        addWykresDyfrakcji(blueColor);
        addWykresDyfrakcji(blackColor);

        red = new JCheckBox("Red");
        green = new JCheckBox("Green");
        blue = new JCheckBox("Blue");
        black = new JCheckBox("Other");

        red.setBackground(Color.red);
        green.setBackground(Color.green);
        blue.setBackground(Color.blue);
        black.setBackground(Color.black);

        red.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(Color.RED, isChecked);
                drawLinesForSelectedColors();
            }
        });

        green.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(Color.GREEN, isChecked);
                drawLinesForSelectedColors();
            }
        });

        blue.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(Color.BLUE, isChecked);
                drawLinesForSelectedColors();
            }
        });
        black.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(Color.BLACK, isChecked);
                drawLinesForSelectedColors();
            }
        });

        JPanel right = new JPanel();
        right.setLayout(new GridLayout(8, 1));
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(Color.WHITE);
        right.setPreferredSize(new Dimension(135, 200));

        JLabel waveLabel = new JLabel("Wavelength (nm):");
        waveField = new JTextField(5);
        waveField.setPreferredSize(new Dimension(20, 30));

        JLabel gratingLabel = new JLabel("Grating constant:");
        gratingField = new JTextField(5);

        save = new JMenuItem("Save");
        open = new JMenuItem("Open");
        restart = new JMenuItem("Restart");
        exit = new JMenuItem("Exit");

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(save);
        menu.add(open);
        menu.add(restart);
        menu.add(exit);
        
        red.setSelected(true);
        green.setSelected(true);
        blue.setSelected(true);
        black.setSelected(true);

        right.add(red);
        right.add(green);
        right.add(blue);
        right.add(black);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        bottom.add(gratingLabel);
        bottom.add(gratingField);

        right.add(waveLabel);

        slider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        bottom.add(slider2);
        right.add(waveField);

        slider1 = new JSlider(380, 720);
        right.add(slider1);

        add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        slider1.addChangeListener(new SliderChangeListener(this));
        slider2.addChangeListener(new SliderChangeListener2(this));
        exit.addActionListener(exitListener);
        restart.addActionListener(restartListener);
        save.addActionListener(saveListener);
        open.addActionListener(openListener);
        setVisible(true);
    }

    ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };
    ActionListener restartListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            slider1.setValue(380);
            slider2.setValue(50);
            waveField.setText("");
            gratingField.setText("");
            red.setSelected(true);
            green.setSelected(true);
            blue.setSelected(true);
            black.setSelected(true);
            wavelengthSet.clear();
            chartsPanel.removeAll();
            wykresyList.clear();
            addWykresDyfrakcji(Color.RED);
            addWykresDyfrakcji(Color.GREEN);
            addWykresDyfrakcji(Color.BLUE);
            addWykresDyfrakcji(Color.BLACK);
            drawLinesForSelectedColors();
        }
    };
    ActionListener saveListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	saveParametersToFile("parameters.txt");
        }
    };
    ActionListener openListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
        	loadParametersFromFile("parameters.txt");
        }
    };
    
    private void saveParametersToFile(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            Parameters parameters = new Parameters(wykresyList);
            outputStream.writeObject(parameters);
            System.out.println("Parametry zostały zapisane do pliku.");
        } catch (IOException e) {
            System.out.println("Błąd podczas zapisywania parametrów: " + e.getMessage());
        }
    }

    private void loadParametersFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            Parameters parameters = (Parameters) inputStream.readObject();
            parameters.applyToWykresy(wykresyList);
            drawLinesForSelectedColors();
            System.out.println("Parametry zostały wczytane z pliku.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Błąd podczas wczytywania parametrów: " + e.getMessage());
        }
    }
    
    public void drawLinesForSelectedColors() {
        for (WykresyDyfrakcji wykres : wykresyList) {
            boolean isSelected = true;

            if (wavelengthSet.containsKey(wykres.getLineColor())) {
                isSelected = wavelengthSet.get(wykres.getLineColor());
            }

            if (isSelected) {
                wykres.setVisible(true);
            } else {
                wykres.setVisible(false);
            }
        }
    }

    private void addWykresDyfrakcji(Color color) {
        double lambda = 0.0005;
        double d = 0.002;

        if (color.equals(Color.RED)) {
            lambda = 650 * 0.0000001;
        } else if (color.equals(Color.GREEN)) {
            lambda = 550 * 0.0000001;
        } else if (color.equals(Color.BLUE)) {
            lambda = 450 * 0.0000001;
        }

        WykresyDyfrakcji wykres = new WykresyDyfrakcji(d, lambda, 10);
        wykres.setLineColor(color);
        wykres.updateLambda(lambda);

        wykresyList.add(wykres);
        chartsPanel.add(wykres);
        wavelengthSet.put(color, true);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DiffractionGrating().setVisible(true);
            }
        });
    }

    public class SliderChangeListener implements ChangeListener {
        private DiffractionGrating parent;

        public SliderChangeListener(DiffractionGrating parent) {
            this.parent = parent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int value = source.getValue();
                if (source == parent.slider1) {
                    parent.setLambdaAndUpdate(value);
                } else if (source == parent.slider2) {
                    double d = value * 0.00001;
                    parent.gratingField.setText(String.valueOf(value));
                    for (WykresyDyfrakcji wykres : wykresyList) {
                        wykres.updateD(d);
                    }
                }
            }
        }
    }

    public class SliderChangeListener2 implements ChangeListener {
        private DiffractionGrating parent;

        public SliderChangeListener2(DiffractionGrating parent) {
            this.parent = parent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int value = source.getValue();
                double d = value * 0.00001;
                parent.gratingField.setText(String.valueOf(value));
                for (WykresyDyfrakcji wykres : wykresyList) {
                    wykres.updateD(d);
                }
            }
        }
    }

    public void setLambdaAndUpdate(double wavelength) {
        double lambda = wavelength * 0.0000001;
        waveField.setText(String.valueOf(wavelength));
        slider1.setValue((int) wavelength);

        if (wavelengthSet.containsKey(Color.BLACK)) {
            for (WykresyDyfrakcji wykres : wykresyList) {
                if (wykres.getLineColor() == Color.BLACK) {
                    wykres.updateLambda(lambda);
                }
            }
        }
    }
}