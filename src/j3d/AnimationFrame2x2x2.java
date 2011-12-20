package j3d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.TransformGroup;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

/**
 * �����ȗ�����8����Ȃ郋�[�r�b�N�L���[�u�B
 * @author M. Saito
 */
public class AnimationFrame2x2x2 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private HashMap<JButton, CommandType> commandMap;
    private CubeBehavior2x2x2 animation;
    //private JTextField color;
    private JButton setColor;
    private JTextField command;
    private JTextField speed;
    private JButton setupButton;
    private JLabel message;
    private Canvas3D canvas;
    private RubikInputFrame inputFrame;
	
    /**
     * �{�^���̐ݒ�
     */
	private void setUpButton() {
	    inputFrame = new RubikInputFrame("Input 2x2x2", 2);
		commandMap = new HashMap<JButton, CommandType>();
		JPanel outer = new JPanel();
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        getContentPane().add(outer, BorderLayout.EAST);
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(8, 1));
		//color = new JTextField(15);
		setColor = new JButton("�J���[�ݒ�");
		setColor.addActionListener(this);
		command = new JTextField(15);
		speed = new JTextField(15);
		setupButton = new JButton("�ݒ�");
		message = new JLabel("    ");
		//north.add(new JLabel("�����F�̐ݒ�"));
		north.add(setColor);
		north.add(new JLabel("����̐ݒ�"));
		north.add(command);
		north.add(new JLabel("���x"));
		north.add(speed);
		speed.setText(RubikProperties.get("speed"));
		north.add(setupButton);
		north.add(message);
		outer.add(north);
		setupButton.addActionListener(this);
        outer.add(makeOperation());
        outer.add(makeViewOperation());
        outer.add(Box.createVerticalGlue());
        outer.revalidate();
	}

	/**
	 * ���[�r�b�N�L���[�u�̊�{����p�{�^���ݒ�
	 * @return ��{����{�^���̔z�u���ꂽ�p�l��
	 */
	private JPanel makeOperation() {
		JButton[][] operationButtons = new JButton[3][];
		String pre = "<html>";
		String[] op = {"U", "R", "F"};
		String[] num = {"", "<sup>2</sup>", "<sup>-1</sup>"};
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new GridLayout(3, 3, 2, 2));
		for (int i = 0; i < 3; i++) {
			operationButtons[i] = new JButton[3];
			for (int j = 0; j < 3; j++) {
				operationButtons[i][j] = new JButton(pre + op[i] + num[j]);
		        operationPanel.add(operationButtons[i][j]);
		        operationButtons[i][j].addActionListener(this);
		        commandMap.put(operationButtons[i][j], Command.getCommandType(op[i] + (j+1)));
			}
		}
		return operationPanel;
	}
	
	/**
	 * ���_�ړ��p�{�^���̐ݒ�
	 * @return ���_�ړ��{�^���̔z�u���ꂽ�p�l��
	 */
	private JPanel makeViewOperation() {
        JPanel viewOperation = new JPanel();
        viewOperation.setLayout(new BoxLayout(viewOperation, BoxLayout.Y_AXIS));
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));
        JButton vup = new JButton("��");
        commandMap.put(vup, Command.getCommandType("VIEW_UP"));
        top.add(Box.createHorizontalGlue());
        top.add(vup);
        top.add(Box.createHorizontalGlue());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        JButton vleft = new JButton("��");
        JButton vright = new JButton("��");
        JButton vreset = new JButton("Reset");
        center.add(vleft);
        center.add(vreset);
        center.add(vright);
        commandMap.put(vright, Command.getCommandType("VIEW_RIGHT"));
        commandMap.put(vleft, Command.getCommandType("VIEW_LEFT"));
        commandMap.put(vreset, Command.getCommandType("VIEW_RESET"));
        JButton vdown = new JButton("��");
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.add(Box.createHorizontalGlue());
        bottom.add(vdown);
        commandMap.put(vdown, Command.getCommandType("VIEW_DOWN"));
        bottom.add(Box.createHorizontalGlue());
        viewOperation.add(top);
        viewOperation.add(center);
        viewOperation.add(bottom);
        vup.addActionListener(this);
        vdown.addActionListener(this);
        vleft.addActionListener(this);
        vright.addActionListener(this);
        vreset.addActionListener(this);
		return viewOperation;
	}
	
	/**
	 * �R���X�g���N�^
	 */
    public AnimationFrame2x2x2() {
    	super("Rubik Cube 2x2x2");
        getContentPane().setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        getContentPane().add(canvas, BorderLayout.CENTER);
        setUpButton();        
        initialize(canvas);        
      }

    /**
     * �V�[���O���t�̍쐬
     * @return �V�[���O���t
     */
	private BranchGroup createSceneGraph() {
	    BranchGroup root = new BranchGroup();
        BoundingSphere bounds=new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        animation = new CubeBehavior2x2x2();
        animation.setSchedulingBounds(bounds);
        TransformGroup[] target = animation.getTarget();
        for (TransformGroup c: target) {
        	root.addChild(c);
        }
        root.addChild(animation);
        Background background = new Background(new Color3f(0.6f, 0.6f, 0.8f));
        background.setApplicationBounds(bounds);
        root.addChild(background);
        Room room = new Room(8);
        root.addChild(room.getTransformGroup());
        //root.compile();
        return root;
      }
	
	/**
	 * �����̐ݒ�
	 * ������1��, ���s������2��(�\�Ɨ�����Ƃ炷)
	 * @return ������\��BranchGroup
	 */
    private BranchGroup createLight() {
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f lightColor2 = new Color3f(0.8f, 0.8f, 0.8f);
        AmbientLight ambient = new AmbientLight(lightColor);
        Vector3f direction = new Vector3f(-2.0f, -2.0f, -2.0f);
        Vector3f direction2 = new Vector3f(2.0f, 2.0f, 2.0f);
        DirectionalLight light = new DirectionalLight(lightColor, direction);
        DirectionalLight light2 = new DirectionalLight(lightColor2, direction2);
        BoundingSphere lightBounds = new BoundingSphere(
        		new Point3d(),Double.POSITIVE_INFINITY);
        ambient.setInfluencingBounds(lightBounds);
        light.setInfluencingBounds(lightBounds);
        light2.setInfluencingBounds(lightBounds);
        BranchGroup br = new BranchGroup();
        br.addChild(ambient);
        br.addChild(light);
        br.addChild(light2);
        return br;
    }
    
    /**
     * ������
     * @param canvas
     */
	private void initialize(Canvas3D canvas) {
        SimpleUniverse universe = new SimpleUniverse(canvas);
		BranchGroup scene = createSceneGraph();
		
        ViewingPlatform vp = universe.getViewingPlatform();
        vp.setNominalViewingTransform();
        TransformGroup viewingTG = vp.getViewPlatformTransform();
        viewingTG.setTransform(animation.getViewTransform());
        animation.setParent(viewingTG);
        universe.addBranchGraph(scene);

        universe.addBranchGraph(createLight());
        setColor("BBBBGGGGOOOOYYYYRRRRWWWW");
	}

    /**
     * �N���p���C������
     * @param args
     */
    public static void main(String[] args) {
        AnimationFrame2x2x2 sample = new AnimationFrame2x2x2();
        sample.setBounds(10, 10, 1000, 1000);
        sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sample.setVisible(true);
    }
    
	@Override
	public void actionPerformed(ActionEvent event) {
		Object src = event.getSource();
		if (src == setupButton) {
			//setColor(color.getText());
			setOperation(command.getText());
			setSpeed(speed.getText());
		} else if (src == setColor) {
		    inputFrame.addCubeBehavior(animation);
		    inputFrame.setLocationRelativeTo(setColor);
		    inputFrame.setVisible(true);
 		} else if (src instanceof JButton) {
			JButton bt = (JButton)src;
			CommandType t = commandMap.get(bt);
			if (t != null) {
				animation.addCommand(new Command(t, ""));
				animation.start();
			}
		}
	}

	/**
	 * ��{����p�L�[�������ꂽ�Ƃ��ɁACubeBehavior2x2x2�̃R�}���h�L���[��
	 * �R�}���h�𑗂�
	 * @param text
	 */
	private void setOperation(String text) {
		if (text.length() == 0) {
			return;
		}
		text = convertText(text);
		animation.stop();
		for (int i = 0; i < text.length(); i += 2) {
			String s = text.substring(i, i+2);
			CommandType t = Command.getCommandType(s);
			if (t != null) {
				animation.addCommand(new Command(t, ""));
			}
		}
		command.setText("");
		animation.start();
	}

	/**
	 * �F��ύX����R�}���h�B
	 * ���ۂɂ́A���ꂩ������ׂ����[�r�b�N�L���[�u�̐F���w�肷��B
	 * @param text �F��\��������
	 */
	private void setColor(String text) {
		if (text.length() == 0) {
			return;
		}
		if (text.length() < 24) {
			message.setForeground(Color.RED);
			message.setText("�����񂪒Z�������܂�");
			return;
		}
		animation.stop();
		animation.addCommand(new Command(CommandType.COLOR, text));
		animation.start();
		//color.setText("");
	}
	
	/**
	 * ��]���x��ݒ肷��
	 * ���l�͑傫���قǍ���
	 * @param speed ��]���x
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
	 * @param text
	 * @return ���K�����ꂽ������
	 */
	private String convertText(String text) {
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
