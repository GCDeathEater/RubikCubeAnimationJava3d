package j3d;

import java.util.logging.Logger;

import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;

/**
 * ���[�r�b�N�L���[�u���\�����鏬���ȗ����� �|�C���g�� ax, ay, az ���e�I�u�W�F�N�g�i�ݒ肵�Ă��Ȃ����A�S�̂Ƃ��Ẵ��[�r�b�N�L���[�u�j
 * �ɂ�����x���W�Ay���W�Az���W��\���Ƃ�������
 * 
 * @author M. Saito
 */
public class Cubie extends TransformGroup {
    private final Logger logger = Logger.getLogger(Cubie.class
            .getCanonicalName());
    private static final String[] defaultColor = { "B", "G", "O", "Y", "R", "W" };
    private static final int[] faces = { Box.TOP, Box.FRONT, Box.RIGHT,
            Box.BACK, Box.LEFT, Box.BOTTOM };
    private ColorAppearance appearance;
    private Vector3d currentAxis;
    private Vector3d ax;
    private Vector3d ay;
    private Vector3d az;
    private Matrix3d matPos;
    private Matrix3d matRot;
    private Matrix3d inverseRot;
    private Vector3d initPos;
    private Box cube;

    /**
     * �R���X�g���N�^�B��ӂ̃T�C�Y�ƒ��S���̈ʒu���痧���̂��쐬����B
     * 
     * @param size
     *            ��ӂ̒���
     * @param x
     *            ���S��x���W
     * @param y
     *            ���S��y���W
     * @param z
     *            ���S��z���W
     */
    public Cubie(double size, double x, double y, double z) {
        this(size, x, y, z, defaultColor);
    }

    /**
     * �R���X�g���N�^�B��ӂ̃T�C�Y�A���S���̈ʒu�A�e�ʂ̐F���痧���̂��쐬����B
     * 
     * @param size
     *            ��ӂ̒���
     * @param x
     *            ���S��x���W
     * @param y
     *            ���S��y���W
     * @param z
     *            ���S��z���W
     * @param colors
     */
    public Cubie(double size, double x, double y, double z, String[] colors) {
        cube = new Box((float) size, (float) size, (float) size,
                Box.GENERATE_NORMALS, null);
        appearance = ColorAppearance.getColorAppearance();
        for (int i = 0; i < faces.length; i++) {
            Shape3D shape = cube.getShape(faces[i]);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            shape.setAppearance(appearance.get(colors[i]));
        }
        initPos = new Vector3d(x, y, z);
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(initPos);
        setTransform(t3dMove);
        addChild(cube);
        setCapability(ALLOW_TRANSFORM_READ);
        setCapability(ALLOW_TRANSFORM_WRITE);
        initAxis();
    }

    /**
     * �����ʒu�ɖ߂��B�e�ʂ̐F�͕ύX���Ȃ��B
     */
    public void reset() {
        Transform3D t3dMove = new Transform3D();
        t3dMove.setTranslation(initPos);
        setTransform(t3dMove);
        initAxis();
    }

    /**
     * �ʂ̐F��ݒ肷��
     * 
     * @param face
     *            �ʂ̎w�� Box.TOP �Ȃ�
     * @param color
     *            �F��\��������
     */
    private void setAppearance(int face, String color) {
        Shape3D shape = cube.getShape(face);
        shape.setAppearance(appearance.get(color));
    }

    /**
     * �����̂̊e�ʂ̐F��ݒ肷��
     * 
     * <pre>
     * colors[0] TOP �̐F
     * colors[1] FRONT �̐F
     * colors[2] RIGHT �̐F
     * colors[3] BACK �̐F
     * colors[4] LEFT �̐F
     * colors[5] BOTTOM �̐F
     * </pre>
     * 
     * @param colors
     *            �F��\��������̔z��
     */
    public void setColor(String[] colors) {
        setAppearance(Box.TOP, colors[0]);
        setAppearance(Box.FRONT, colors[1]);
        setAppearance(Box.RIGHT, colors[2]);
        setAppearance(Box.BACK, colors[3]);
        setAppearance(Box.LEFT, colors[4]);
        setAppearance(Box.BOTTOM, colors[5]);
    }

    /**
     * �e�I�u�W�F�N�g����݂����W��������������
     */
    private void initAxis() {
        ax = new Vector3d(1, 0, 0);
        ay = new Vector3d(0, 1, 0);
        az = new Vector3d(0, 0, 1);
    }

    /**
     * ��A�̉�]�A�j���[�V�����̍ŏ��ɍs���ݒ�
     * 
     * @param type
     *            �R�}���h�^�C�v�A��]������\��
     * @param angle
     *            �A�j���[�V������R�}�ŉ�]����p�x
     */
    public void setupRotation(CommandType type, double angle, double totalAngle) {
        logger.fine("type:" + type);
        logger.fine("angle:" + angle);
        logger.fine("totalAngle:" + totalAngle);
        // cubie �̒��S�ʒu�̈ړ��s�� matPos ����щ�]����ݒ肷��
        if (type.isUp()) {
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            matPos = new Matrix3d(cos, 0, -sin, 0, 1, 0, sin, 0, cos);
            currentAxis = ay;
        } else if (type.isRight()) {
            double sin = Math.sin(-angle);
            double cos = Math.cos(-angle);
            matPos = new Matrix3d(1, 0, 0, 0, cos, -sin, 0, sin, cos);
            currentAxis = ax;
        } else if (type.isFront()) {
            double sin = Math.sin(-angle);
            double cos = Math.cos(-angle);
            matPos = new Matrix3d(cos, -sin, 0, sin, cos, 0, 0, 0, 1);
            currentAxis = az;
        }
        // cubie �̉�]�s�� matRot ��ݒ肷��
        AxisAngle4d axis = new AxisAngle4d(currentAxis.x, currentAxis.y,
                currentAxis.z, -angle);
        Transform3D rot = new Transform3D();
        rot.setRotation(axis);
        matRot = new Matrix3d();
        rot.get(matRot);
        //matRot.set(axis);
        // ���W���̉�]�s�� inverseMat ��ݒ肷��
        axis = new AxisAngle4d(currentAxis.x, currentAxis.y,
                currentAxis.z, totalAngle);
        inverseRot = new Matrix3d();
        inverseRot.set(axis);
    }

    /**
     * �A�j���[�V�����I�����ɁA���W����ύX����B �r���ŕύX����K�v�͂Ȃ��B
     * 
     * @param type
     */
    public void teardownRotation(CommandType type) {
        inverseRot.transform(ax);
        inverseRot.transform(ay);
        inverseRot.transform(az);
    }

    /**
     * �A�j���[�V������1�R�}���̉�]����
     */
    public void rotate() {
        Matrix3d oldRot = new Matrix3d();
        Vector3d oldPos = new Vector3d();
        Transform3D tr = new Transform3D();
        getTransform(tr);
        tr.get(oldRot, oldPos);
        matPos.transform(oldPos);
        oldRot.mul(matRot);
        setTransform(new Transform3D(oldRot, oldPos, 1));
    }
}
