package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class CommandPanel extends JPanel implements ActionListener {
    /**
     * serial version UID needed by JPanel
     */
    private static final long serialVersionUID = 1L;
    private JTextField command;
    private JTextField speed;
    private JButton setColor;
    private JButton setupButton;
    private JLabel message;
    private CubeBehavior animation;
    private RubikInputFrame inputFrame;
    private HashMap<JButton, CommandType> commandMap;

    public CommandPanel(int count, CubeBehavior behavior,
            HashMap<JButton, CommandType> map) {
        commandMap = map;
        if (count == 2) {
            inputFrame = new RubikInputFrame("Input 2x2x2", 2);
        } else {
            inputFrame = new RubikInputFrame("Input 3x3x3", 3);
        }
        animation = behavior;
        setLayout(new GridLayout(8, 1));
        // north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        // color = new JTextField(15);
        command = new JTextField(15);
        speed = new JTextField(15);
        setColor = new JButton("�J���[�ݒ�");
        setColor.addActionListener(this);
        setupButton = new JButton("�ݒ�");
        setupButton.addActionListener(this);
        message = new JLabel("    ");
        add(new JLabel("�����F�̐ݒ�"));
        add(setColor);
        add(new JLabel("����R�}���h"));
        add(command);
        add(new JLabel("���x"));
        add(speed);
        speed.setText(RubikProperties.get("speed"));
        add(setupButton);
        add(message);
        setBorder(new TitledBorder("�ݒ�"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();
        if (src == setupButton) {
            // setColor(color.getText());
            setOperation(command.getText());
            setSpeed(speed.getText());
        } else if (src == setColor) {
            inputFrame.addCubeBehavior(animation);
            inputFrame.setLocationRelativeTo(setColor);
            inputFrame.setVisible(true);
        } else if (src instanceof JButton) {
            JButton bt = (JButton) src;
            CommandType t = commandMap.get(bt);
            if (t != null) {
                animation.addCommand(new Command(t, ""));
                animation.start();
            }
        }
    }

    /**
     * ��{����p�L�[�������ꂽ�Ƃ��ɁACubeBehavior2x2x2�̃R�}���h�L���[�� �R�}���h�𑗂�
     * 
     * @param text
     */
    public void setOperation(String text) {
        if (text.length() == 0) {
            return;
        }
        text = convertText(text);
        animation.stop();
        for (int i = 0; i < text.length(); i += 2) {
            String s = text.substring(i, i + 2);
            CommandType t = Command.getCommandType(s);
            if (t != null) {
                animation.addCommand(new Command(t, ""));
            }
        }
        command.setText("");
        animation.start();
    }

    public void setWarning(String mess) {
        message.setForeground(Color.RED);
        message.setText(mess);
    }

    public void setMessage(String mess) {
        message.setForeground(Color.BLACK);
        message.setText(mess);
    }
    
    /**
     * ��]���x��ݒ肷�� ���l�͑傫���قǍ���
     * 
     * @param speed
     *            ��]���x
     */
    private void setSpeed(String speed) {
        if (speed.length() == 0) {
            return;
        }
        animation.stop();
        animation.addCommand(new Command(CommandType.SPEED, speed));
        animation.start();
    }

    /**
     * �R�}���h��\��������𐳋K������
     * 
     * @param text
     * @return ���K�����ꂽ������
     */
    public static String convertText(String text) {
        StringBuilder sb = new StringBuilder();
        char pre = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == 'U' || c == 'R' || c == 'F') {
                if (pre == 0) {
                    pre = c;
                } else {
                    sb.append(pre);
                    sb.append('1');
                    pre = c;
                }
            } else if (c == '1' || c == '2' || c == '3') {
                sb.append(pre);
                sb.append(c);
                pre = 0;
            }
        }
        if (pre != 0) {
            sb.append(pre);
            sb.append('1');
            pre = 0;
        }
        return sb.toString();
    }

}
