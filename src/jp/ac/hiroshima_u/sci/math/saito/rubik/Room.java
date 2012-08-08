package jp.ac.hiroshima_u.sci.math.saito.rubik;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.j3d.Appearance;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * �蔲���H���̕����i�̕ǁj
 * ���������āA�ł��������̈�ł悩�����̂��B
 */
public class Room {
    private static final Logger logger = Logger.getLogger(Room.class.getCanonicalName());
    /*
     * TOP FRONT RIGHT BOTTOM BACK LEFT
     */
    private static final float[][] wallSize = { { 1.0f, 0.01f, 1.0f },
            { 1.0f, 1.0f, 0.01f }, { 0.01f, 1.0f, 1.0f },
            { 1.0f, 0.01f, 1.0f }, { 1.0f, 1.0f, 0.01f }, { 0.01f, 1.0f, 1.0f } };
    private static final float[][] wallPos = {
        {0.0f, 1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f},
        {1.0f, 0.0f, 0.0f},
        {0.0f, -1.0f, 0.0f},
        {0.0f, 0.0f, -1.0f},
        {-1.0f, 0.0f, 0.0f}};
//    private static final Texture wall = makeTexture("wall038.jpg"); 
    private static final Texture wall1 = makeTexture("free_highres_texture_3.jpg"); 
    private static final Texture wall2 = makeTexture("free_highres_texture_6.jpg"); 
    private static final Texture floor = makeTexture("free_highres_texture_1.jpg");
    private static final Texture ceil = makeTexture("free_highres_texture_15.jpg");
    private static final Appearance[] appearance = makeAppearance();
    private TransformGroup transGrp;

    private static Appearance[] makeAppearance() {
        Appearance[] app = new Appearance[6];
        Texture[] tex = {ceil, wall1, wall2, floor, wall1, wall2};
        for (int i = 0; i < 6; i++) {
            app[i] = new Appearance();
            app[i].setCapability(Appearance.ALLOW_TEXTURE_ATTRIBUTES_WRITE);
            app[i].setTexture(tex[i]);
        }
        return app;
    }
    private static Texture makeTexture(String imageFileName) {
        BufferedImage image;
        Texture texture;
        try {
            image = ImageIO.read(RubikProperties.getURL(imageFileName));
            texture = new TextureLoader(image).getTexture();
            texture.setMagFilter(Texture.FASTEST);
            texture.setBoundaryModeS(Texture.CLAMP);
            texture.setBoundaryModeT(Texture.CLAMP);
        } catch (IOException e) {
            logger.severe("Can't get texture");
            throw new RuntimeException(e);
        }        
        return texture;
    }
    /**
     * ���[�r�b�N�L���[�u�̂��镔��
     * �Z�����ɓ����͗l�̕ǂ�u���Ă��邾��
     * @param size �ǂ̈�ӂ̑傫��
     */
    public Room(double size) {
        transGrp = new TransformGroup();
//        Transform3D texTrans = new Transform3D();
//        TextureAttributes attribute = new TextureAttributes();
        //texTrans.setScale(size);
        //attribute.setTextureTransform(texTrans);
        float fsize = (float) size;
        for (int i = 0; i < 6; i++) {
            Box wall;
            Appearance ap = appearance[i];
//            ap.setTextureAttributes(attribute);
            wall = new Box(wallSize[i][0] * fsize,
                    wallSize[i][1] * fsize,
                    wallSize[i][2] * fsize,
                    Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS,
                    ap);
            TransformGroup child = new TransformGroup();
            child.addChild(wall);
            Transform3D trans = new Transform3D();
            trans.setTranslation(new Vector3d(wallPos[i][0] * size,
                        wallPos[i][1] * size,
                        wallPos[i][2] * size));
            child.setTransform(trans);
            transGrp.addChild(child);
        }
    }
    
    /**
     * ���̕�����\��TransformGroup��Ԃ�
     * @return ���̕�����\��TransformGroup
     */
    public TransformGroup getTransformGroup() {
        return transGrp;
    }
}
