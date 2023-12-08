package org.example;

import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();
    private static DefaultTableModel tableModel;  // Declare the table model as a class variable
    private static JFrame frame;
    private static JPanel leftPanel;
    private static JTable table;
    private static JLabel infoLabel;
    private static JLabel esLabel;
    private static JLabel stdLabel;
    private static JLabel rsdLabel;
    private static JLabel cepLabel;
    private static JButton addRowButton;
    private static JButton deleteRowButton;
    private static JButton exportToCSVButton;
    private static JButton exportToTXTButton;

    private static DrawingPanel drawingPanel;

    private static void initUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Ballistic Computer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            tableModel = new DefaultTableModel();
            tableModel.addColumn("X Position [mm]");
            tableModel.addColumn("Y Position [mm]");

            table = new JTable(tableModel);

            // Add listener for table model changes
            tableModel.addTableModelListener(e -> {
                if (e.getType() == TableModelEvent.INSERT) {
                    int row = e.getFirstRow();
                    float xCoordinate = Float.parseFloat(tableModel.getValueAt(row, 0).toString());
                    float yCoordinate = Float.parseFloat(tableModel.getValueAt(row, 1).toString());
                    ballisticEntries.add(new Pair<>(xCoordinate, yCoordinate));
                    drawingPanel.repaint();
                } else if (e.getType() == TableModelEvent.DELETE) {
                    int row = e.getFirstRow();
                    ballisticEntries.remove(row);
                    drawingPanel.repaint();
                }
            });

            leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

            /* Labels */
            infoLabel = new JLabel("Ballistic Entries: 0");
            esLabel = new JLabel("Extreme spread: 0.0 mm");
            stdLabel = new JLabel("Average Deviation: 0.0 mm");
            rsdLabel = new JLabel("Radial Standard Deviation: 0.0 mm");
            cepLabel = new JLabel("Circular Error Probable: 0.0 mm");

            int labelVerticalSpacing = 4;
            EmptyBorder labelBorder = new EmptyBorder(labelVerticalSpacing, 0, labelVerticalSpacing, 0);

            infoLabel.setBorder(labelBorder);
            esLabel.setBorder(labelBorder);
            stdLabel.setBorder(labelBorder);
            rsdLabel.setBorder(labelBorder);
            cepLabel.setBorder(labelBorder);

            leftPanel.add(infoLabel);
            leftPanel.add(esLabel);
            leftPanel.add(stdLabel);
            leftPanel.add(rsdLabel);
            leftPanel.add(cepLabel);

            addRowButton = new JButton("Add New Entry");
            addRowButton.addActionListener(e -> addNewRow());

            leftPanel.add(addRowButton);

            deleteRowButton = new JButton("Delete Selected Entry");
            deleteRowButton.addActionListener(e -> deleteSelectedRow(table));

            leftPanel.add(deleteRowButton);

            exportToCSVButton = new JButton("Export To CSV");
            exportToTXTButton = new JButton("Export To TXT");

            leftPanel.add(exportToCSVButton);
            leftPanel.add(exportToTXTButton);

            exportToCSVButton.addActionListener(e -> exportData("csv"));
            exportToTXTButton.addActionListener(e -> exportData("txt"));

            drawingPanel = new DrawingPanel();
            drawingPanel.setSize(400, 400);

            frame.add(leftPanel, BorderLayout.WEST);
            frame.add(drawingPanel, BorderLayout.EAST);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int row = table.rowAtPoint(e.getPoint());
                        table.getSelectionModel().setSelectionInterval(row, row);

                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Delete Selected Entry");
                        deleteItem.addActionListener(e1 -> deleteSelectedRow(table));
                        popupMenu.add(deleteItem);

                        popupMenu.show(table, e.getX(), e.getY());
                    }
                }
            });


            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.add(leftPanel, BorderLayout.WEST);

            frame.setSize(1000, 500);
            frame.setVisible(true);
        });

    }

    private static void exportData(String fileType) {
        String fileName = promptForFileName();
        if (fileName != null && !fileName.isEmpty()) {
            switch (fileType) {
                case "csv":
                    ExportDataClass.exportToCSV(ballisticEntries, fileName + ".csv");
                    break;
                case "txt":
                    ExportDataClass.exportToTXT(ballisticEntries, fileName + ".txt");
                    break;
                // Add more cases if needed for other file types
                default:
                    System.out.println("Invalid file type");
            }
        }
    }

    private static String promptForFileName() {
        return JOptionPane.showInputDialog(null, "Enter file name:", "File Name", JOptionPane.QUESTION_MESSAGE);
    }

    public static void main(String[] args) {
        initUI();
    }

    private static void updateInformation() {

        int numRows = tableModel.getRowCount();
        infoLabel.setText("Ballistic Entries: " + numRows);
        SwingWorker<Double, Void> workerES = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return EquationCalculator.computeExtremeSpread(ballisticEntries);
            }

            @Override
            protected void done() {
                try {
                    double extremeSpread = get();
                    esLabel.setText("Extreme Spread: " + extremeSpread + " mm");
                } catch (Exception e) {
                    esLabel.setText("Extreme Spread: 0 mm");
                }
            }
        };
        SwingWorker<Double, Void> workerAD = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return EquationCalculator.computeAverageDeviation(ballisticEntries);
            }

            @Override
            protected void done() {
                try {
                    double averageDeviation = get();
                    stdLabel.setText("Average Deviation: " + averageDeviation + " mm");
                } catch (Exception e) {
                    stdLabel.setText("Average Deviation: 0 mm");
                }
            }
        };
        SwingWorker<Double, Void> workerRSD = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return EquationCalculator.computeRadialStandardDeviation(ballisticEntries);
            }

            @Override
            protected void done() {
                try {
                    double radialStandardDeviation = get();
                    rsdLabel.setText("Radial Standard Deviation: " + radialStandardDeviation + " mm");
                } catch (Exception e) {
                    rsdLabel.setText("Radial Standard Deviation: 0 mm");
                }
            }
        };
        SwingWorker<Double, Void> workerCEP = new SwingWorker<>() {
            @Override
            protected Double doInBackground() throws Exception {
                return EquationCalculator.computeCircularErrorProbable(ballisticEntries);
            }

            @Override
            protected void done() {
                try {
                    double circularErrorProbable = get();
                    cepLabel.setText("Circular Error Probable: " + circularErrorProbable + " mm");
                } catch (Exception e) {
                    cepLabel.setText("Circular Error Probable: 0 mm");
                }
            }
        };

        workerES.execute();
        workerAD.execute();
        workerRSD.execute();
        workerCEP.execute();


    }

    private static void addNewRow() {
        float xCoordinate = Float.parseFloat(JOptionPane.showInputDialog("Enter X Coordinate (mm):"));
        float yCoordinate = Float.parseFloat(JOptionPane.showInputDialog("Enter Y Coordinate (mm):"));

        Object[] newRow = {xCoordinate, yCoordinate};
        tableModel.addRow(newRow);
        updateInformation();
        drawingPanel.repaint();
    }

    private static void deleteSelectedRow(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        }
        updateInformation();
        drawingPanel.repaint();
    }

    private static Color calculateColor(int shotOrder, int lastAddedShotOrder) {
        int maxShots = 6;
        float minHue = 0.0f;
        float maxHue = 0.66f;

        int shotDifference = (shotOrder - lastAddedShotOrder + maxShots) % maxShots;

        float hue = minHue - ((float) shotDifference / maxShots) * (maxHue - minHue);
        if (hue > -0.1 && shotOrder != lastAddedShotOrder)
            hue = (float) -0.1;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    private static class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int diameter = 200;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g.setColor(Color.BLACK);
            g.drawOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);

            g.drawLine(centerX, 0, centerX, getHeight());

            g.drawLine(0, centerY, getWidth(), centerY);

            ArrayList<Float> circumscribedCircle = null;
            try {
                circumscribedCircle = EquationCalculator.getCircumscribedCircle(ballisticEntries);
            } catch (NullListException ignored) {
            }
            try {
                assert circumscribedCircle != null;
                if (!circumscribedCircle.isEmpty()) {
                    g.setColor(Color.ORANGE);
                    g.drawOval(
                            Math.round((float) getWidth() / 2 + circumscribedCircle.get(0) - circumscribedCircle.get(2)),
                            Math.round((float) getHeight() / 2 - circumscribedCircle.get(1) - circumscribedCircle.get(2)),
                            Math.round(circumscribedCircle.get(2) * 2),
                            Math.round(circumscribedCircle.get(2) * 2));
                }

            } catch (Exception ignored) {
            }

            int numRows = tableModel.getRowCount();
            int lastAddedShotOrder = numRows - 1;

            for (int i = 0; i < numRows; i++) {
                float xCoordinate = (float) tableModel.getValueAt(i, 0);
                float yCoordinate = (float) tableModel.getValueAt(i, 1);
                int x = centerX + Math.round(xCoordinate);
                int y = centerY - Math.round(yCoordinate);

                Color circleColor = calculateColor(i, lastAddedShotOrder);

                g.setColor(circleColor);
                g.fillOval(x - 2, y - 2, 4, 4);

            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }
    }
}
