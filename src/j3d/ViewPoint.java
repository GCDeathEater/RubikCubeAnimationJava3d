package j3d;

import java.util.logging.Logger;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;

/**
 * ���_�ύX�̃N���X
 * ���_������z���̃}�C�i�X����
 * baseTransform ������̉�]�𒴂��ėݐς��Ă���
 * fixedTransform ���ʂł͂Ȃ�������Ƃ���Ă݂邽�߂̂��́B
 * viewTransform baseTransform �̂��ƁAfixedTransform �ł��ꂽ���_�B
 * 
 * @author M. Saito
 */
public class ViewPoint {
    private static final Logger logger = Logger.getLogger(ViewPoint.class.getCanonicalName());
    private final double viewAngleX = -Math.PI / 8;
    private final double viewAngleY = Math.PI / 8;
    private double viewDistance;
    private Transform3D baseTransform;
    private Transform3D viewTransform;
    private Transform3D fixedTransform;
    private Transform3D saveBase;
    private TransformGroup parent;
    private Transform3D rotateTransform = new Transform3D();

    /**
     * �R���X�g���N�^
     * @param distance ���_����̋���
     */
    public ViewPoint(double distance) {
        this.viewDistance = distance;
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D();
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        saveBase = new Transform3D(baseTransform);
    }

    /**
     * ���_�̐e�m�[�h�ł���TransformGroup�̏�񂪕K�v
     * �Ȃ̂ł��Ƃ�����炤
     * @param parent
     */
    public void setParent(TransformGroup parent) {
        this.parent = parent;
    }

    /**
     * ���_��Transform3D����Ԃ�
     * @return
     */
    public Transform3D getTransform3D() {
        return viewTransform;
    }

    /**
     * ���_�������ʒu�Ƀ��Z�b�g����
     */
    public void reset() {
        fixedTransform = makeFixedTransform();
        baseTransform = new Transform3D(saveBase);
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
    }

    /**
     * ��A�̎��_�ړ������̊J�n���_�ŕK�v�Ȑݒ������
     * @param type ���_�̈ړ�����
     * @param angle �A�j���[�V�����̈�R�}�œ����p�x
     */
    public void setupRotate(CommandType type, double angle) {
        if (type == CommandType.VIEW_LEFT || type == CommandType.VIEW_RIGHT) {
            rotateTransform = new Transform3D();
            AxisAngle4d axisY = new AxisAngle4d(0, 1, 0, angle);
            rotateTransform.setRotation(axisY);
        } else if (type == CommandType.VIEW_DOWN || type == CommandType.VIEW_UP) {
            rotateTransform = new Transform3D();
            AxisAngle4d axisX = new AxisAngle4d(-1, 0, 0, -angle);
            rotateTransform.setRotation(axisX);
        }
    }

    /**
     * ���炵�Č��镔��
     * @return
     */
    private Transform3D makeFixedTransform() {
        AxisAngle4d axisX = new AxisAngle4d(1.0, 0.0, 0.0, viewAngleX);
        AxisAngle4d axisY = new AxisAngle4d(0.0, 1.0, 0.0, viewAngleY);
        Transform3D t3dx = new Transform3D();
        t3dx.setRotation(axisX);
        Transform3D t3dy = new Transform3D();
        t3dy.setRotation(axisY);
        Transform3D fixedTransform = new Transform3D();
        fixedTransform.mul(t3dy, t3dx);

        Transform3D viewMove3d = new Transform3D();
        Vector3d move = new Vector3d(0.0, 0.0, 1.0);
        logger.fine("viewDistance:" + viewDistance);
        move.scale(viewDistance);
        logger.fine("move:" + move);
        viewMove3d.set(move);
        fixedTransform.mul(viewMove3d);

        logger.fine("fixedTransform:" + fixedTransform);
        return fixedTransform;
    }

    /**
     * �A�j���[�V�����̈�R�}�ōs�����_�ړ�����
     * @param type
     */
    public void doRotation(CommandType type) {
        baseTransform.mul(rotateTransform);
        viewTransform = new Transform3D(baseTransform);
        viewTransform.mul(fixedTransform);
        parent.setTransform(viewTransform);
     }

    /**
     * ��A�̃A�j���[�V�����̏I�����_�ōs������
     * ���͎g���Ă��Ȃ��B
     * @param com ���_�̈ړ�����������
     */
    public void teardownRotation(CommandType com) {
    }

}
