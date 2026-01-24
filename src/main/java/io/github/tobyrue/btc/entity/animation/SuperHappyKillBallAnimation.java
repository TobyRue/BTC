package io.github.tobyrue.btc.entity.animation;// Save this class in your mod and generate all required imports

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

/**
 * Made with Blockbench 5.0.7
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Author
 */
public class SuperHappyKillBallAnimation {
	public static final Animation ROTATE = Animation.Builder.create(3.0F).looping()
		.addBoneAnimation("Core", new Transformation(Transformation.Targets.ROTATE,
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(-180.0F, -180.0F, 180.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(3.0F, AnimationHelper.createRotationalVector(-360.0F, -360.0F, 360.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("Layer1", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(-90.0F, -180.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(3.0F, AnimationHelper.createRotationalVector(-270.0F, -360.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.addBoneAnimation("Layer2", new Transformation(Transformation.Targets.ROTATE, 
			new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(1.5F, AnimationHelper.createRotationalVector(180.0F, 360.0F, 0.0F), Transformation.Interpolations.LINEAR),
			new Keyframe(3.0F, AnimationHelper.createRotationalVector(360.0F, 720.0F, 0.0F), Transformation.Interpolations.LINEAR)
		))
		.build();
}