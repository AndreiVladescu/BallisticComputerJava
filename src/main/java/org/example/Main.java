package org.example;

import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static DefaultTableModel tableModel;  // Declare the table model as a class variable
    private static JFrame frame;
    private static JPanel leftPanel;
    private static JTable table;
    private static JLabel infoLabel;
    private static JLabel esLabel;
    private static JLabel stdLabel;
    private static JButton addRowButton;
    private static JButton deleteRowButton;
    private static DrawingPanel drawingPanel;
    private static List<Pair<Float, Float>> ballisticEntries = new ArrayList<>();

    private static void initUI(){
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
            esLabel = new JLabel("Extreme spread: 0.0mm");
            stdLabel = new JLabel("Standard Deviation: 0.0mm");
            leftPanel.add(infoLabel);
            leftPanel.add(esLabel);
            leftPanel.add(stdLabel);

            addRowButton = new JButton("Add New Entry");

            addRowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addNewRow();
                }
            });
            leftPanel.add(addRowButton);

            deleteRowButton = new JButton("Delete Selected Entry");
            deleteRowButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteSelectedRow(table);
                }
            });
            leftPanel.add(deleteRowButton);

            drawingPanel = new DrawingPanel();
            drawingPanel.setSize(400, 400);


            frame.add(leftPanel, BorderLayout.WEST);
            frame.add(drawingPanel, BorderLayout.EAST);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);

            // Add a mouse listener to the table to detect right-click events
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        int row = table.rowAtPoint(e.getPoint());
                        int col = table.columnAtPoint(e.getPoint());
                        table.getSelectionModel().setSelectionInterval(row, row);

                        // Create a popup menu with the option to delete the selected row
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem deleteItem = new JMenuItem("Delete Selected Entry");
                        deleteItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                deleteSelectedRow(table);
                            }
                        });
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

    public static void main(String[] args) {
        initUI();
    }

    private static void updateInformation() {
        int numRows = tableModel.getRowCount();
        infoLabel.setText("Ballistic Entries: " + numRows);
        esLabel.setText("Extreme Spread: " + EquationCalculator.computeExtremeSpread(ballisticEntries) + "mm");
        stdLabel.setText("Standard Deviation: " + EquationCalculator.computeStandardDerivation(ballisticEntries) + "mm");

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
    private static class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw the large circle (x pixels in diameter)
            int diameter = 200;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g.setColor(Color.BLACK);
            g.drawOval(centerX - diameter / 2, centerY - diameter / 2, diameter, diameter);

            // Vertical line
            g.drawLine(centerX, 0, centerX, getHeight());

            // Horizontal line
            g.drawLine(0, centerY, getWidth(), centerY);

            // Draw mini red circles based on table coordinates
            int numRows = tableModel.getRowCount();
            int lastAddedShotOrder = numRows - 1;

            for (int i = 0; i < numRows; i++) {
                float xCoordinate = (float) tableModel.getValueAt(i, 0);
                float yCoordinate = (float) tableModel.getValueAt(i, 1);
                int x = centerX + Math.round(xCoordinate);
                int y = centerY - Math.round(yCoordinate); // Invert y-axis to match coordinate system

                // Calculate the color based on the shot order
                Color circleColor = calculateColor(i, lastAddedShotOrder);

                g.setColor(circleColor);
                g.fillOval(x - 2, y - 2, 4, 4);

            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500); // Set a preferred size for the panel
        }
    }

    private static Color calculateColor(int shotOrder, int lastAddedShotOrder) {
        // Calculate color based on the shot order and the last added shot order
        int maxShots = 6; // Adjust as needed
        float minHue = 0.0f; // Red
        float maxHue = 0.66f; // Blue

        // Calculate the difference between the shot order and the last added shot order
        int shotDifference = (shotOrder - lastAddedShotOrder + maxShots) % maxShots;

        float hue = minHue - ((float) shotDifference / maxShots) * (maxHue - minHue);
        if (hue > -0.1 && shotOrder != lastAddedShotOrder)
            hue = (float) -0.1;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}
