package gui.swingui;

import controller.*;
import datamodel.Dictionary;
import datamodel.Record;
import gui.swingui.record.AddRecordWindow;
import gui.swingui.record.EditRecordWindow;
import utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Prifiz on 03.01.2017.
 */
public class MainWindow extends JFrame {

    TableRowSorter<MainTableModel> sorter;

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    private String byTopicComboCurrentlySelected = Constants.NO_TOPIC;

    public void refresh() {
        mainTable.updateUI();
        java.util.Set<String> topics = new TreeSet<>();
        topics.add(Constants.NO_TOPIC);
        topics.addAll(new SimpleSearch().findAllTopics(dictionary));
        byTopicCombo.setModel(new DefaultComboBoxModel(topics.toArray()));
        byTopicCombo.setSelectedItem(byTopicComboCurrentlySelected);
        byTopicCombo.updateUI();
    }

    private Dictionary dictionary = new Dictionary();
    private JLabel byTopicLabel = new JLabel("By Topic");
    final TableModel model = new MainTableModel(dictionary);
    final JTable mainTable = new JTable(model);
    private JButton removeTopicButton;


    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    java.util.Set<String> topics = new TreeSet<>();


    private JComboBox byTopicCombo;

    private void createNewRecord() {
        final AddRecordWindow addRecordWindow = new AddRecordWindow(this);
    }

    private void editRecord(Record record) {
        final EditRecordWindow editRecordWindow = new EditRecordWindow(this, record);
    }


//

    private void initLayout() {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);

        pane.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        JButton newButton = new JButton("New...");
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewRecord();
            }
        });

//        JComboBox similarityCombo = new JComboBox();
//        for(Similarity similarity : Similarity.values()) {
//            similarityCombo.addItem(similarity.toString());
//        }
        //mainTable.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(similarityCombo));
        JScrollPane scrollPane = new JScrollPane(mainTable);
        mainTable.getColumnModel().getColumn(4).setMinWidth(0);
        mainTable.getColumnModel().getColumn(4).setMaxWidth(0);



        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    int viewIndex = mainTable.getSelectedRow();
                    if (viewIndex != -1) {
                        int modelIndex = mainTable.convertRowIndexToModel(viewIndex);
                        MainTableModel model = (MainTableModel) mainTable.getModel();
                        model.removeRow(modelIndex);
                    }
                    for (ActionListener a : byTopicCombo.getActionListeners()) {
                        a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
                        });
                    }
                }
            }
        });

        try {
            Command loadCommand = new LoadFromXmlCommand("Dictionary.xml");
            loadCommand.execute(dictionary);
            mainTable.updateUI();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
        topics.add(Constants.NO_TOPIC);
        topics.addAll(new SimpleSearch().findAllTopics(dictionary));
        final ComboBoxModel comboBoxModel = new DefaultComboBoxModel(topics.toArray());
        byTopicCombo = new JComboBox(comboBoxModel);

        sorter = new TableRowSorter<MainTableModel>((MainTableModel) model);

        byTopicCombo.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                sorter.setModel((MainTableModel) model);
                RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
                    public boolean include(Entry entry) {
                        if (Constants.NO_TOPIC.equals(byTopicCombo.getSelectedItem())) {
                            return true;
                        } else {
                            String topic = (String) entry.getValue(4);
                            return topic.equals(byTopicCombo.getSelectedItem());
                        }
                    }
                };
                sorter.setRowFilter(filter);
                mainTable.setRowSorter(sorter);
                byTopicComboCurrentlySelected = byTopicCombo.getSelectedItem().toString();
            }
        });

        removeTopicButton = new JButton("Remove Topic");
        removeTopicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!topics.isEmpty()) {
                    String topic = byTopicCombo.getSelectedItem().toString();
                    if (!topic.equals(Constants.NO_TOPIC)) {
                        topics.remove(topic);
                        byTopicCombo.removeItem(topic);
                        byTopicCombo.updateUI();
                        dictionary.removeAllTopicOccurences(topic);
                    }
                }
            }
        });

        JButton resetButton = new JButton("Show All");

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                ((gui.swingui.MainTableModel)model).setDictionary(dictionary);
//                mainTable.updateUI();
                sorter.setRowFilter(null);
                byTopicCombo.setSelectedItem(Constants.NO_TOPIC);
                mainTable.setRowSorter(sorter);
            }
        });
        mainTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2 && me.getButton() == 1) {
                    JTable table = (JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (row != -1) {
                        int modelIndex = mainTable.convertRowIndexToModel(row);
                        MainTableModel model = (MainTableModel) mainTable.getModel();
                        editRecord(dictionary.getAllRecordsAsList().get(modelIndex));
                        model.setDictionary(dictionary);
                    }
                }
            }
//            public void mouseReleased(MouseEvent e) {
//                int r = mainTable.rowAtPoint(e.getPoint());
//                if (r >= 0 && r < mainTable.getRowCount()) {
//                    mainTable.setRowSelectionInterval(r, r);
//                } else {
//                    mainTable.clearSelection();
//                }
//
//                int rowindex = mainTable.getSelectedRow();
//                if (rowindex < 0)
//                    return;
//                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
//                    JPopupMenu popup = new JPopupMenu();
//                    JMenuItem makeIdenticalItem = new JMenuItem("Mark as IDENTICAL");
//                    JMenuItem makeSimilarItem = new JMenuItem("Mark as SIMALAR");
//                    JMenuItem makeDifferentItem = new JMenuItem("Mark as SIMALAR");
//                    popup.add();
//                    popup.show(e.getComponent(), e.getX(), e.getY());
//                }
//            }

        });


        mainTable.setAutoCreateRowSorter(true);
        mainTable.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        mainTable.setDefaultRenderer(String.class, new TextTableRenderer());
        mainTable.setDefaultRenderer(File.class, centerRenderer);
        mainTable.setDefaultRenderer(
                File.class,
                new TableCellRenderer() {
                    public Component getTableCellRendererComponent
                            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        try {
                            JLabel label = new JLabel();
                            Image fullSize = new ImageIcon(((File) value).getCanonicalPath()).getImage();
                            Image resized = ImageUtils.resize(ImageUtils.toBufferedImage(fullSize), 150, 150);
                            if (resized != null) {
                                ImageIcon imageIcon = new ImageIcon(
                                        resized.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                                label.setIcon(imageIcon);
                            }
                            return label;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new JLabel("Error: " + value);
                    }
                }
        );
        mainTable.setRowHeight(160);

        GroupLayout.SequentialGroup horizontalGroup = gl.createSequentialGroup();
        horizontalGroup.addGroup(gl.createParallelGroup()
                .addGroup(gl.createSequentialGroup()
                        .addComponent(newButton)
                        .addComponent(removeButton))
                .addComponent(scrollPane));
        horizontalGroup.addGroup(gl.createParallelGroup()
                .addComponent(byTopicLabel)
                .addGroup(gl.createSequentialGroup()
                        .addComponent(byTopicCombo)
                        .addComponent(removeTopicButton))
                .addComponent(resetButton));

        gl.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup vGroup = gl.createSequentialGroup();
        vGroup.addGroup(gl.createParallelGroup()
                .addComponent(newButton)
                .addComponent(removeButton)
                .addComponent(byTopicLabel));
        vGroup.addGroup(gl.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(gl.createSequentialGroup()
                        .addGroup(gl.createParallelGroup()
                                .addComponent(byTopicCombo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(removeTopicButton))
                        .addComponent(resetButton)));


        gl.setVerticalGroup(vGroup);
    }


    private void initMainForm() {
        setTitle("Dictionary - Main Page");
        setSize(1024, 768);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Dictionary Actions");
        menuBar.add(menu);

        JMenuItem saveItem = new JMenuItem("Save");
        menu.add(saveItem);

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new SaveToXmlCommand("Dictionary.xml").execute(dictionary);
                    JOptionPane.showMessageDialog(null, "Successfully saved!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        this.setJMenuBar(menuBar);
        initLayout();
    }

    public MainWindow() throws HeadlessException {
        initMainForm();
    }
}
