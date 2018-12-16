package me.android.viper;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class ViperDialog extends JDialog implements ChangeListener {
    private JPanel contentPane;
    private JRadioButton baseRadio;
    private JRadioButton activityRadio;
    private JRadioButton fragmentRadio;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField moduleTextField;
    private JRadioButton diRadio;
    private JRadioButton dataRadio;
    private ButtonGroup buttonGroup;

    private ViperDialogCallback callback;


    public ViperDialog(ViperDialogCallback callback) {
        setTitle("Viper Create Helper");
        setContentPane(contentPane);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(baseRadio);
        buttonGroup.add(activityRadio);
        buttonGroup.add(fragmentRadio);
        buttonGroup.add(dataRadio);
        buttonGroup.add(diRadio);
        activityRadio.setSelected(true);
        setModal(true);
        getRootPane().setDefaultButton(okButton);
        setSize(420, 300);
        setLocationRelativeTo(null);
        this.callback = callback;

        diRadio.addChangeListener(this);
        dataRadio.addChangeListener(this);
        baseRadio.addChangeListener(this);
        activityRadio.addChangeListener(this);
        fragmentRadio.addChangeListener(this);

        okButton.addActionListener((e) -> {
            onOK();
        });
        cancelButton.addActionListener((e) -> {
            onCancel();
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction((e) -> {
            onCancel();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        contentPane.registerKeyboardAction((e) -> {
            if (baseRadio.isSelected() || diRadio.isSelected() || dataRadio.isSelected()) {
                onOK();
            } else if (moduleTextField != null && moduleTextField.getText().trim() != null) {
                onOK();
            } else {
                Messages.showInfoMessage("You need to input module name!", "Waring");
            }

        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if (null != callback && (baseRadio.isSelected() || dataRadio.isSelected() || diRadio.isSelected() || !moduleTextField.getText().isEmpty())) {
            callback.onCreate(moduleTextField.getText().trim(), currentType());
            dispose();
        } else {
            Messages.showInfoMessage("Select radio of \"Fragment\" or \"Activity\" must input module name!", "Waring");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        moduleTextField.setEditable(!baseRadio.isSelected() && !dataRadio.isSelected() && !diRadio.isSelected());
    }

    private void onCancel() {
        dispose();
    }

    private ViperCreateAction.CreateType currentType() {
        if (activityRadio.isSelected()) {
            return ViperCreateAction.CreateType.Activity;
        }
        if (fragmentRadio.isSelected()) {
            return ViperCreateAction.CreateType.Fragment;
        }
        if (dataRadio.isSelected()) {
            return ViperCreateAction.CreateType.Data;
        }
        if (diRadio.isSelected()) {
            return ViperCreateAction.CreateType.Di;
        }
        return ViperCreateAction.CreateType.BaseViper;
    }

    public static void main(String[] args) {
    }

    public interface ViperDialogCallback {
        void onCreate(String moduleName, ViperCreateAction.CreateType type);
    }
}
