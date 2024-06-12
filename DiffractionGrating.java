package wykres;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.JFileChooser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

public class DiffractionGrating extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArrayList<WykresyDyfrakcji> wykresyList;
    private ArrayList<WykresyPrazki> wykresy2List;
    JSlider slider1;
	private JSlider slider2;
    private JTextField waveField, gratingField;
    private JCheckBox red, green, blue, black;
    private JMenuItem save, open, restart, exit, saveImage;
    private Map<Color, Boolean> wavelengthSet;
    private Color sliderColor;
    JPanel chartsPanel;
    private JPanel pradkiPanel;
    static Color purple = new Color(102, 0, 153);
    static Color redo = new Color(254, 0, 0);
    static Color bulu = new Color(0, 0, 254);
    static Color geen = new Color(0, 254, 0);
    JFrame frame;
    private JMenuItem languageItem;
    JLabel waveLabel;
    JLabel gratingLabel;

    public DiffractionGrating() {
        setTitle("Diffraction Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame = this; 

        wykresyList = new ArrayList<>();
        wykresy2List = new ArrayList<>();
        wavelengthSet = new HashMap<>();

        chartsPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                for (WykresyDyfrakcji wykres : wykresyList) {
                	if (wavelengthSet.getOrDefault(wykres.getLineColor(), true) || wykres.getLineColor().equals(sliderColor)) {
                        wykres.paintComponent(g);
                    }
                }
            }
        };
        chartsPanel.setLayout(new BorderLayout());
        add(chartsPanel, BorderLayout.CENTER);
        
        pradkiPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                for (WykresyPrazki wykres2 : wykresy2List) {
                	if (wavelengthSet.getOrDefault(wykres2.getLineColor(), true) || wykres2.getLineColor().equals(sliderColor)) {
                        wykres2.paintComponent(g);
                    }
                }
            }
        };
        pradkiPanel.setLayout(new BorderLayout());
        
        slider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        slider1 = new JSlider(380, 720);
        slider1.setValue(380);
        slider2.setValue(50);
        Color redColor = redo;
        Color greenColor = geen;
        Color blueColor = bulu;
        Color blackColor = Color.WHITE;

        addWykresDyfrakcji(redColor);
        addWykresDyfrakcji(greenColor);
        addWykresDyfrakcji(blueColor);
        addWykresDyfrakcji(blackColor);

        red = new JCheckBox("Red");
        green = new JCheckBox("Green");
        blue = new JCheckBox("Blue");
        black = new JCheckBox("Other");

        red.setBackground(redo);
        green.setBackground(geen);
        blue.setBackground(bulu);
        black.setBackground(Color.white);
        
        red.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(redo, isChecked);
                updateWykresyWithInitialValues();
                chartsPanel.repaint();
            }
        });

        green.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(geen, isChecked);
                updateWykresyWithInitialValues();
                chartsPanel.repaint();
            }
        });

        blue.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(bulu, isChecked);
                updateWykresyWithInitialValues();
                chartsPanel.repaint();
            }
        });

        black.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;
                wavelengthSet.put(purple, isChecked);
                wavelengthSet.put(Color.BLUE, isChecked);
                wavelengthSet.put(Color.GREEN, isChecked);
                wavelengthSet.put(Color.YELLOW, isChecked);
                wavelengthSet.put(Color.ORANGE, isChecked);
                wavelengthSet.put(Color.RED, isChecked);
                updateWykresyWithInitialValues();
                chartsPanel.repaint();
            }
        });

        JPanel right = new JPanel();
        JPanel left = new JPanel();
        right.setLayout(new GridLayout(8, 1));
        left.setLayout(new BorderLayout());
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setBackground(Color.BLACK);
        right.setPreferredSize(new Dimension(150, 200));
        left.setPreferredSize(new Dimension(150, 200));

        waveLabel = new JLabel("Wavelength (nm):");
        waveLabel.setForeground(Color.WHITE);
        waveField = new JTextField(5);
        waveField.setPreferredSize(new Dimension(50, 30));

        gratingLabel = new JLabel("Grating constant:");
        gratingLabel.setForeground(Color.WHITE);
        gratingField = new JTextField(5);
        
        languageItem = new JMenuItem();

        save = new JMenuItem("Save");
        saveImage = new JMenuItem("Save Image");
        open = new JMenuItem("Open");
        restart = new JMenuItem("Restart");
        exit = new JMenuItem("Exit");

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menu.add(save);
        menu.add(saveImage);
        menu.add(open);
        menu.add(restart);
        menu.add(exit);
        menu.addSeparator();
        menu.add(languageItem);
        
        red.setSelected(true);
        green.setSelected(true);
        blue.setSelected(true);
        black.setSelected(true);
        
        waveField.setText("380");
        gratingField.setText("50");
        
        right.add(red);
        right.add(green);
        right.add(blue);
        right.add(black);
    
        right.add(slider1);
        
        menuBar.add(menu);
        setJMenuBar(menuBar);

        right.add(waveLabel);

        right.add(waveField);
        left.add(pradkiPanel);
        bottom.add(gratingLabel);
        bottom.add(gratingField);
        bottom.add(slider2);
        add(right, BorderLayout.WEST);
        add(left, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        slider1.addChangeListener(new SliderChangeListener(this));
        slider2.addChangeListener(new SliderChangeListener2(this));
        exit.addActionListener(exitListener);
        restart.addActionListener(restartListener);
        save.addActionListener(saveListener);
        open.addActionListener(openListener);
        saveImage.addActionListener(saveimageListener);
        
        waveField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int value = Integer.parseInt(waveField.getText());
                    if (value >= 380 && value <= 720) {
                        slider1.setValue(value);
                        setLambdaAndUpdate(value, Color.WHITE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Warto�� musi by� pomi�dzy 380 a 720 nm.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Wprowad� poprawn� liczb�.");
                }
            }
        });

        gratingField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int value = Integer.parseInt(gratingField.getText());
                    if (value >= 0 && value <= 100) {
                        slider2.setValue(value);
                        updateGratingConstant(value);
                    } else {
                        JOptionPane.showMessageDialog(null, "Warto�� musi by� pomi�dzy 0 a 100.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Wprowad� poprawn� liczb�.");
                }
            }
        });
        updateWykresyWithInitialValues();
        updateCheckboxes();
        setVisible(true);
        
        languageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] languages = {"English", "Polski"};
                String selectedLanguage = (String) JOptionPane.showInputDialog(frame, "Select Language", "Language", JOptionPane.QUESTION_MESSAGE, null, languages, languages[0]);
                if (selectedLanguage != null) {
                    if (selectedLanguage.equals("English")) {
                        updateLanguage("en", "US");
                    } else if (selectedLanguage.equals("Polski")) {
                        updateLanguage("pl", "PL");
                    }
                }
            }
        });
        updateLanguage("en", "US");
    }
    
    private void updateLanguage(String lang, String country) {
        Locale locale = new Locale(lang, country);
        ResourceBundle bundle = ResourceBundle.getBundle("LabelsBundle", locale);

        red.setText(bundle.getString("red"));
        green.setText(bundle.getString("green"));
        blue.setText(bundle.getString("blue"));
        black.setText(bundle.getString("black"));

        save.setText(bundle.getString("save"));
        saveImage.setText(bundle.getString("saveImage"));
        open.setText(bundle.getString("open"));
        restart.setText(bundle.getString("restart"));
        exit.setText(bundle.getString("exit"));
        languageItem.setText(bundle.getString("language"));
        
        waveLabel.setText(bundle.getString("wavelength"));
        gratingLabel.setText(bundle.getString("gratingconstant"));
    }

    
    private void updateWykresyWithInitialValues() {
        double initialD = slider2.getValue() * 0.00001;
        gratingField.setText(String.valueOf(slider2.getValue()));
        for (WykresyDyfrakcji wykres : wykresyList) {
            wykres.updateD(initialD);
        }
        for (WykresyPrazki wykres : wykresy2List) {
            wykres.updateD(initialD);
        }
        chartsPanel.repaint();
        pradkiPanel.repaint();
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
            waveField.setText("380");
            gratingField.setText("50");
            red.setSelected(true);
            green.setSelected(true);
            blue.setSelected(true);
            black.setSelected(true);
            wavelengthSet.clear();
            wykresyList.clear();
            wykresy2List.clear();
            addWykresDyfrakcji(redo);
            addWykresDyfrakcji(geen);
            addWykresDyfrakcji(bulu);
            addWykresDyfrakcji(Color.WHITE);
            updateWykresyWithInitialValues();
            chartsPanel.repaint();
            pradkiPanel.repaint();
        }
    };
    ActionListener saveListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                saveParametersToFile(selectedFile.getAbsolutePath());
            }
        }
    };
    ActionListener saveimageListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            savePanelAsImage(frame);
        }
    };

    ActionListener openListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                loadParametersFromFile(selectedFile.getAbsolutePath());
            }
        }
    };
    
    private void saveParametersToFile(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            Parameters parameters = new Parameters(wykresyList, slider1.getValue(), slider2.getValue(), wavelengthSet);
            Parameters2 parameters2 = new Parameters2(wykresy2List);
            outputStream.writeObject(parameters);
            outputStream.writeObject(parameters2);
            System.out.println("Parameters have been saved to the file.");
        } catch (IOException e) {
            System.out.println("Error saving parameters: " + e.getMessage());
        }
    }

    private void loadParametersFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            Parameters parameters = (Parameters) inputStream.readObject();
            Parameters2 parameters2 = (Parameters2) inputStream.readObject();
            parameters.applyToWykresy(wykresyList);
            parameters2.applyToWykresy2(wykresy2List);
            slider1.setValue(parameters.getSlider1Value());
            slider2.setValue(parameters.getSlider2Value());
            wavelengthSet = parameters.getWavelengthSet();
            updateCheckboxes();
            System.out.println("Parameters have been loaded from the file.");
            chartsPanel.repaint();
            pradkiPanel.repaint();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading parameters: " + e.getMessage());
        }
    }

    private void updateCheckboxes() {
        red.setSelected(wavelengthSet.getOrDefault(redo, true));
        green.setSelected(wavelengthSet.getOrDefault(geen, true));
        blue.setSelected(wavelengthSet.getOrDefault(bulu, true));
        black.setSelected(wavelengthSet.getOrDefault(purple, true));
    }
    
    private void addWykresDyfrakcji(Color color) {
        double lambda = 0.0005;
        double d = 0.002;

        if (color.equals(redo) && wavelengthSet.getOrDefault(redo, true)) {
            lambda = 650 * 0.0000001;
        } else if (color.equals(geen) && wavelengthSet.getOrDefault(geen, true)) {
            lambda = 550 * 0.0000001;
        } else if (color.equals(bulu) && wavelengthSet.getOrDefault(bulu, true)) {
            lambda = 450 * 0.0000001;
        }

        WykresyDyfrakcji wykres = new WykresyDyfrakcji(d, lambda, 10, this);
        wykres.setLineColor(color);
        wykres.updateLambda(lambda);
        
        WykresyPrazki wykres2 = new WykresyPrazki(d, lambda, 10, this);
        wykres2.setLineColor(color);
        wykres2.updateLambda(lambda);

        wykresyList.add(wykres);
        wykresy2List.add(wykres2);
        wavelengthSet.put(color, true);
    }

    public ArrayList<WykresyDyfrakcji> getWykresyList() {
        return wykresyList;
    }
    
    public ArrayList<WykresyPrazki> getWykresy2List() {
        return wykresy2List;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DiffractionGrating();
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
                    parent.setLambdaAndUpdate(value, Color.WHITE);
                } else if (source == parent.slider2) {
                    double d = value * 0.00001;
                    parent.gratingField.setText(String.valueOf(value));
                    for (WykresyDyfrakcji wykres : parent.getWykresyList()) {
                        wykres.updateD(d);
                    }
                    for (WykresyPrazki wykres2 : parent.getWykresy2List()) {
                        wykres2.updateD(d);
                    }
                    parent.chartsPanel.repaint();
                    parent.pradkiPanel.repaint();
                }
            }
        }
    }

    public void setLambdaAndUpdate(int value, Color color) {
        double lambda = value * 0.0000001;
        waveField.setText(String.valueOf(value));
        for (WykresyDyfrakcji wykres : wykresyList) {
            if (wavelengthSet.getOrDefault(wykres.getLineColor(), true)) {
                if (wykres.getLineColor().equals(color) || (wykres.getLineColor().equals(Color.WHITE) && sliderColor.equals(color))) {
                    wykres.updateLambda(lambda);
                    if (wykres.getLineColor().equals(Color.WHITE)) {
                        wykres.setLineColor(funk(value));
                    }
                }
            }
        }
        for (WykresyPrazki wykres2 : wykresy2List) {
            if (wavelengthSet.getOrDefault(wykres2.getLineColor(), true)) {
                if (wykres2.getLineColor().equals(color) || (wykres2.getLineColor().equals(Color.WHITE) && sliderColor.equals(color))) {
                    wykres2.updateLambda(lambda);
                    if (wykres2.getLineColor().equals(Color.WHITE)) {
                        wykres2.setLineColor(funk(value));
                    }
                }
            }
        }
        pradkiPanel.repaint();
        chartsPanel.repaint();
    }

    public class SliderChangeListener2 implements ChangeListener {
        private DiffractionGrating parent;

        public SliderChangeListener2(DiffractionGrating parent) {
            this.parent = parent;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            int value = slider2.getValue();
            int value2 = slider1.getValue();
            gratingField.setText(String.valueOf(value));
            Color color = funk(value2);
            setLambdaAndUpdate(value2, color);
            if (!source.getValueIsAdjusting()) {
                double d = value * 0.00001;
                parent.gratingField.setText(String.valueOf(value));
                for (WykresyDyfrakcji wykres : parent.getWykresyList()) {
                    wykres.updateD(d);
                }
                for (WykresyPrazki wykres2 : parent.getWykresy2List()) {
                    wykres2.updateD(d);
                }
                parent.chartsPanel.repaint();
                parent.pradkiPanel.repaint();
            }
        }
    }

    private void updateGratingConstant(int value) {
        double d = value * 0.00001;
        for (WykresyDyfrakcji wykres : wykresyList) {
            wykres.updateD(d);
        }
        chartsPanel.repaint();
        for (WykresyPrazki wykres2 : wykresy2List) {
            wykres2.updateD(d);
        }
        pradkiPanel.repaint();
    }
    public int getSlider1Value() {
        return slider1.getValue();
    }
    static Color funk(int value) {
        if (value >= 380 && value < 436) {
            return purple;
        } else if (value >= 436 && value < 495) {
            return Color.BLUE;
        } else if (value >= 495 && value < 566) {
            return Color.GREEN;
        } else if (value >= 566 && value < 589) {
            return Color.YELLOW;
        } else if (value >= 589 && value < 627) {
            return Color.ORANGE;
        } else if (value >= 627 && value <= 780) {
            return Color.RED;
        } else {
            return Color.WHITE;
        }
    }
    private void savePanelAsImage(JFrame frame) {
        BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        frame.paint(g2);
        g2.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Image", "png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".png")) {
                fileToSave = new File(fileToSave + ".png");
            }
            try {
                ImageIO.write(image, "png", fileToSave);
                System.out.println("Image saved as " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error saving image: " + e.getMessage());
            }
        }
    }
}