package gui.swingui;

import controller.Controller;
import controller.SwingApplicationController;
import controller.search.SimpleSearch;
import datamodel.Record;
import gui.swingui.record.AddRecordWindow;
import gui.swingui.record.EditRecordWindow;
import gui.swingui.record.RecordWindow;
import utils.Constants;
import utils.ImageUtils;

import javax.swing.*;
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
import java.util.TreeSet;

public class MainWindow extends JFrame {

    private Controller appController = SwingApplicationController.getInstance();

    private TableModel model;
    private TableRowSorter<MainTableModel> sorter;
    private JTable mainTable;
    private JLabel byTopicLabel;

    private JScrollPane scrollPane;
    private JButton newButton;
    private JButton removeButton;
    private JButton removeTopicButton;


    private JButton resetButton;
    private JComboBox byTopicCombo;
    private String byTopicComboCurrentlySelected = Constants.NO_TOPIC;
    private java.util.Set<String> topics = new TreeSet<>();

    private ComboBoxModel comboBoxModel = new DefaultComboBoxModel(topics.toArray());

    public MainWindow() throws HeadlessException {
        initMainForm();
        initControls();
        initMainTable();
        initButtonsActions();
        initMenu();
        initLayout();
        loadDictionaryData();
    }

    private void initMainForm() {
        setTitle("Dictionary - Main Page");
        setSize(1024, 768);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void initControls() {
        model = new MainTableModel(appController.getDictionary());
        sorter = new TableRowSorter<>((MainTableModel) model);
        mainTable = new JTable(model);
        scrollPane = new JScrollPane(mainTable);
        byTopicLabel = new JLabel("By Topic");
        newButton = new JButton("New...");
        removeButton = new JButton("Remove");
        resetButton = new JButton("Show All");
        byTopicCombo = new JComboBox(comboBoxModel);
        removeTopicButton = new JButton("Remove Topic");
    }

    public void updateFormData() {
        updateMainTable();
        topics.clear();
        topics.add(Constants.NO_TOPIC);
        topics.addAll(new SimpleSearch().findAllTopics(appController.getDictionary()));
        byTopicCombo.setModel(new DefaultComboBoxModel(topics.toArray()));
        if(topics.contains(byTopicComboCurrentlySelected)) {
            byTopicCombo.setSelectedItem(byTopicComboCurrentlySelected);
        } else {
            byTopicCombo.setSelectedItem(Constants.NO_TOPIC);
        }
        byTopicCombo.updateUI();
    }


//    public Set<String> getTopics() {
//        return topics;
//    }

    private void createNewRecord() {
        RecordWindow addRecordWindow = new AddRecordWindow(this);
        addRecordWindow.setVisible(true);
    }

    private void editRecord(int recordIdx) {
        Record record = appController.getDictionary().getAllRecordsAsList().get(recordIdx);
        final EditRecordWindow editRecordWindow = new EditRecordWindow(this, record);
        editRecordWindow.setVisible(true);
    }


    private void initButtonsActions() {
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewRecord();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mainTable.getSelectedRow() != -1) {
                    int viewIndex = mainTable.getSelectedRow();
                    if (viewIndex != -1) {
                        int modelIndex = mainTable.convertRowIndexToModel(viewIndex);
                        try {
                            appController.removeRecord(modelIndex);
                            updateFormData();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                    for (ActionListener a : byTopicCombo.getActionListeners()) {
                        a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
                        });
                    }
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sorter.setRowFilter(null);
                byTopicCombo.setSelectedItem(Constants.NO_TOPIC);
                mainTable.setRowSorter(sorter);
            }
        });

        removeTopicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!topics.isEmpty()) {
                    String topic = byTopicCombo.getSelectedItem().toString();
                    if (!Constants.NO_TOPIC.equals(topic)) {
                        try {
                            appController.removeTopic(topic);
                            updateFormData();
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                    }
                }
            }
        });

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
    }

    private void updateMainTable() {
        ((MainTableModel)mainTable.getModel()).setDictionary(appController.getDictionary());
        mainTable.updateUI();
    }

    private void loadDictionaryData() {
        try {
            appController.loadDictionary();
            updateFormData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void saveDictionaryData() {
        try {
            appController.saveDictionary();
            JOptionPane.showMessageDialog(null, "Successfully saved!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void initMainTable() {
        mainTable.setRowHeight(160);
        mainTable.getColumnModel().getColumn(4).setMinWidth(0);
        mainTable.getColumnModel().getColumn(4).setMaxWidth(0);
        mainTable.setAutoCreateRowSorter(true);
        mainTable.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 20));
        mainTable.setDefaultRenderer(String.class, new TextTableRenderer());

        mainTable.setDefaultRenderer(
                File.class,
                new TableCellRenderer() {
                    public Component getTableCellRendererComponent
                            (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        final int IMAGE_WIDTH = 150;
                        final int IMAGE_HEIGHT = IMAGE_WIDTH;
                        try {
                            JLabel label = new JLabel();
                            Image fullSize = new ImageIcon(((File) value).getCanonicalPath()).getImage();
                            Image resized = ImageUtils.resize(
                                    ImageUtils.toBufferedImage(fullSize), IMAGE_WIDTH, IMAGE_HEIGHT);
                            if (resized != null) {
                                ImageIcon imageIcon = new ImageIcon(
                                        resized.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH));
                                label.setIcon(imageIcon);
                            }
                            return label;
                        } catch (IOException e) {
                            // FIXME change to logger in all such places!
                            e.printStackTrace();
                        }
                        return new JLabel("Error: " + value);
                    }
                }
        );

        mainTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if (me.getClickCount() == 2 && me.getButton() == 1) {
                    JTable table = (JTable) me.getSource();
                    Point p = me.getPoint();
                    int row = table.rowAtPoint(p);
                    if (row != -1) {
                        int modelIndex = mainTable.convertRowIndexToModel(row);
                        MainTableModel model = (MainTableModel) mainTable.getModel();
                        editRecord(modelIndex);
                        model.setDictionary(appController.getDictionary());
                    }
                }
            }
        });
    }

    private void initLayout() {
        Container pane = getContentPane();
        GroupLayout groupLayout = new GroupLayout(pane);
        pane.setLayout(groupLayout);

        groupLayout.setAutoCreateGaps(true);
        groupLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = groupLayout.createSequentialGroup();
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(newButton)
                        .addComponent(removeButton))
                .addComponent(scrollPane));
        horizontalGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(byTopicLabel)
                .addGroup(groupLayout.createSequentialGroup()
                        .addComponent(byTopicCombo)
                        .addComponent(removeTopicButton))
                .addComponent(resetButton));

        groupLayout.setHorizontalGroup(horizontalGroup);

        GroupLayout.SequentialGroup vGroup = groupLayout.createSequentialGroup();
        vGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(newButton)
                .addComponent(removeButton)
                .addComponent(byTopicLabel));
        vGroup.addGroup(groupLayout.createParallelGroup()
                .addComponent(scrollPane)
                .addGroup(groupLayout.createSequentialGroup()
                        .addGroup(groupLayout.createParallelGroup()
                                .addComponent(byTopicCombo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(removeTopicButton))
                        .addComponent(resetButton)));

        groupLayout.setVerticalGroup(vGroup);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Dictionary Actions");
        menuBar.add(menu);

        JMenuItem saveItem = new JMenuItem("Save");
        menu.add(saveItem);

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDictionaryData();
            }
        });
        this.setJMenuBar(menuBar);
    }

}
