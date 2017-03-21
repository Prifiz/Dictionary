package gui.swingui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

    public class TextTableRenderer extends JTextArea implements TableCellRenderer {

    private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    private Color unselectedForeground;
    private Color unselectedBackground;

    public TextTableRenderer() {

        super();
        setBorder(getNoFocusBorder());
        setName("Table.cellRenderer");
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object arg1, boolean isSelected, boolean hasFocus, int row, int column) {

        if (table == null) {
            return this;
        }

        Color foregroundColor = Color.GRAY;
        Color backgroundColor = Color.WHITE;

        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsertRow()
                && !dropLocation.isInsertColumn()
                && dropLocation.getRow() == row
                && dropLocation.getColumn() == column) {
            isSelected = true;
        }

        if (isSelected) {
            super.setForeground(foregroundColor);
            super.setBackground(backgroundColor);
        } else {
            Color background = unselectedBackground != null
                    ? unselectedBackground
                    : table.getBackground();
            super.setForeground(unselectedForeground != null
                    ? unselectedForeground
                    : table.getForeground());
            super.setBackground(background);
        }

        setFont(table.getFont());

        if (hasFocus) {
            Border border = new LineBorder(Color.BLACK);
            if (isSelected) {
                border = new LineBorder(Color.BLACK);
            }
            setBorder(border);

            if (!isSelected && table.isCellEditable(row, column)) {
                Color col = Color.RED;
                super.setForeground(col);
            }
        } else {
            setBorder(getNoFocusBorder());
        }

        String data = arg1.toString();

        this.setText(data);
        return this;
    }

    private Border getNoFocusBorder() {
        return DEFAULT_NO_FOCUS_BORDER;
    }

    public void setBackground(Color c) {
        super.setBackground(c);
        unselectedBackground = c;
    }

    public void setForeground(Color c) {
        super.setForeground(c);
        unselectedForeground = c;
    }
}
