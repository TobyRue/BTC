package io.github.tobyrue.btc.entity.animation;// Save this class in your mod and generate all required imports

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

/**
 * Made with Blockbench 5.0.7
 * Exported for Minecraft version 1.19 or later with Yarn mappings
 * @author Toby Wasinger
 */
public class KeyGolemAnimations {
	public static final Animation BASE = Animation.Builder.create(0.0417F).looping()
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 0.2F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 0.1F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.01F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.02F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.01F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.02F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation SLEEP = Animation.Builder.create(9.25F).looping()
			.addBoneAnimation("key_golem", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.625F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(5.125F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(6.625F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(7.875F, AnimationHelper.createRotationalVector(-3.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(9.25F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(5.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(5.8333F, AnimationHelper.createScalingVector(1.0F, 0.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.25F, AnimationHelper.createScalingVector(1.0F, 0.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.0F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.75F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.125F, AnimationHelper.createScalingVector(1.0F, 0.4975F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.25F, AnimationHelper.createScalingVector(1.0F, 0.55F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(5.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(5.5F, AnimationHelper.createScalingVector(1.0F, 0.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.0F, AnimationHelper.createScalingVector(1.0F, 0.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.0F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.75F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.2083F, AnimationHelper.createScalingVector(1.0F, 0.6136F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.3333F, AnimationHelper.createScalingVector(1.0F, 0.6757F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 0.5F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(4.75F, AnimationHelper.createScalingVector(1.0F, 0.5F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.0F, AnimationHelper.createScalingVector(1.0F, 0.5F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(9.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation IDLE = Animation.Builder.create(10.0F).looping()
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0417F, AnimationHelper.createRotationalVector(0.0F, 30.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.5F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.0F, AnimationHelper.createRotationalVector(0.0F, -30.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(5.3333F, AnimationHelper.createRotationalVector(15.0F, 20.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.0F, AnimationHelper.createRotationalVector(15.0F, 20.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.5F, AnimationHelper.createRotationalVector(15.0F, 20.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.1667F, AnimationHelper.createRotationalVector(15.0F, -20.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.0F, AnimationHelper.createRotationalVector(15.0F, -20.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(8.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.9583F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5833F, AnimationHelper.createRotationalVector(5.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.6667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.9583F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.2917F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(5.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.1667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.3333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.6667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.6667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.9583F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.2917F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(5.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(6.1667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.3333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(7.6667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.7917F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(1.7917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.2083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation ALERT = Animation.Builder.create(1.0F)
			.addBoneAnimation("key_golem", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-38.0F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createRotationalVector(-30.0F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-38.0F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createRotationalVector(-30.0F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createScalingVector(1.0F, 2.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.4583F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5833F, AnimationHelper.createScalingVector(1.0F, 2.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(-0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(-0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 1.6F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation RUN = Animation.Builder.create(1.3333F).looping()
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, -4.9574F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, 4.9574F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, -4.9574F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, 4.9574F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, -4.9574F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(-50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(50.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.55F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.6667F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createScalingVector(1.0F, 1.55F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation PANIC = Animation.Builder.create(4.0F).looping()
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.8333F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.3333F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.5F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.6667F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.8333F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.1667F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.3333F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.6667F, AnimationHelper.createRotationalVector(-7.4718F, 0.6518F, -7.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.8333F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(4.0F, AnimationHelper.createRotationalVector(-7.4718F, -0.6518F, 7.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.6667F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.6667F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.8333F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.6667F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.8333F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.1667F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.6667F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.8333F, AnimationHelper.createTranslationalVector(0.0F, -1.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(4.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.6667F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.3333F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.6667F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.3333F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.6667F, AnimationHelper.createRotationalVector(55.0F, 9.5438F, -8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.0F, AnimationHelper.createRotationalVector(-55.0F, -9.5438F, -8.1102F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.6667F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.6667F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.3333F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.6667F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.3333F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.6667F, AnimationHelper.createRotationalVector(-55.0F, 9.5438F, 8.1102F), Transformation.Interpolations.LINEAR),
					new Keyframe(4.0F, AnimationHelper.createRotationalVector(55.0F, -9.5438F, 8.1102F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0417F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0833F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.1667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0417F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0833F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.1667F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.55F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.25F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.75F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.25F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.5F, AnimationHelper.createScalingVector(1.0F, 1.55F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.75F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.75F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(4.0F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0417F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.1667F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.2083F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.125F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.7083F, AnimationHelper.createTranslationalVector(-1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.2917F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.9167F, AnimationHelper.createTranslationalVector(-0.5F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(-0.5F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0417F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.1667F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.2083F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.9167F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.2917F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.7083F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.9167F, AnimationHelper.createTranslationalVector(0.5F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.3333F, AnimationHelper.createTranslationalVector(0.5F, -0.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.5833F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation FALL_ASLEEP = Animation.Builder.create(3.0F)
			.addBoneAnimation("key_golem", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9167F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -2.4F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0417F, AnimationHelper.createTranslationalVector(0.0F, -2.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.2083F, AnimationHelper.createTranslationalVector(0.0F, -4.1F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, -4.15F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, -5.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -2.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -0.5F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0833F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 1.25F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.1667F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.5F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.2083F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 2.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.2917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -0.43F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.3333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -0.8F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.4167F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -0.3F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.4583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.5417F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(-60.5F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5417F, AnimationHelper.createRotationalVector(-60.5F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(-60.5F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5417F, AnimationHelper.createRotationalVector(-60.5F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.5417F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(-0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(2.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation PLAYER_DIE_1 = Animation.Builder.create(1.5F)
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.75F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.125F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.25F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.375F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.5F, AnimationHelper.createRotationalVector(0.2257F, 0.1801F, 0.4111F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.125F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.375F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.625F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.875F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.125F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.25F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.375F, AnimationHelper.createTranslationalVector(0.0F, -0.7F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.0F, -1.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.125F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.25F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.375F, AnimationHelper.createScalingVector(1.0F, 0.3F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.125F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.375F, AnimationHelper.createScalingVector(1.0F, 0.8F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.5F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();

	public static final Animation PLAYER_DIE_2 = Animation.Builder.create(3.75F)
			.addBoneAnimation("body", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.05F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.875F, AnimationHelper.createScalingVector(1.0F, 1.12F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.125F, AnimationHelper.createScalingVector(1.0F, 1.05F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(1.875F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.125F, AnimationHelper.createScalingVector(1.0F, 1.1F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(3.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(-0.5F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-1.03F, -0.03F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.5F, AnimationHelper.createTranslationalVector(-0.52F, 0.49F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.75F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.5F, AnimationHelper.createTranslationalVector(0.5F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.0F, AnimationHelper.createTranslationalVector(-0.03F, -0.03F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(2.5F, AnimationHelper.createTranslationalVector(0.48F, 0.49F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createTranslationalVector(1.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(3.25F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();
	public static final Animation WAKE_UP = Animation.Builder.create(1.0F)
			.addBoneAnimation("key_golem", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -4.5F, 0.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.4167F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createTranslationalVector(0.0F, 8.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7083F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.CUBIC)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createRotationalVector(-2.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createRotationalVector(-17.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createRotationalVector(7.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("legs", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(-90.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("legs", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, -0.5F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, -30.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-38.0F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createRotationalVector(-30.0F, -12.5F, -6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 30.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.3333F, AnimationHelper.createRotationalVector(-38.0F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createRotationalVector(-30.0F, 12.5F, 6.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createRotationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(-0.3F, -2.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(1.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_eyelid", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0417F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.875F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9167F, AnimationHelper.createScalingVector(1.0F, 0.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("mouth", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.9F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.3333F, AnimationHelper.createScalingVector(1.0F, 2.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.4583F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.CUBIC),
					new Keyframe(0.5833F, AnimationHelper.createScalingVector(1.0F, 2.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.9583F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("eyes", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(-0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createTranslationalVector(-0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(-0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("right_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.5F, AnimationHelper.createScalingVector(1.0F, 1.8F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_white", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 2.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createTranslationalVector(0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createTranslationalVector(0.5F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createTranslationalVector(0.33F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.8333F, AnimationHelper.createTranslationalVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
			))
			.addBoneAnimation("left_black", new Transformation(Transformation.Targets.SCALE,
					new Keyframe(0.0F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.0833F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.125F, AnimationHelper.createScalingVector(1.0F, 1.4F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.25F, AnimationHelper.createScalingVector(1.0F, 1.2F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.375F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.625F, AnimationHelper.createScalingVector(1.0F, 1.6F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.75F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
					new Keyframe(0.7917F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
			))
			.build();
}