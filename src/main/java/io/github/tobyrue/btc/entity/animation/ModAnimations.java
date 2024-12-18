package io.github.tobyrue.btc.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class ModAnimations {

	public static final Animation ELDRITCH_LUMINARY_WALK = Animation.Builder.create(1.5f).looping()
			.addBoneAnimation("leg_2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg_1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("legs",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_IDLE = Animation.Builder.create(4f).looping()
			.addBoneAnimation("sidearm1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("middlearm1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("sidearm2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("middlearm2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_ATTACK_CHARGE = Animation.Builder.create(4f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-45f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createRotationalVector(-45f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arms",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(4f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arm1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-45f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arm1",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(2.5f, AnimationHelper.createScalingVector(1f, 1f, 1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.5416765f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arm2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-45f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arm2",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(2.5f, AnimationHelper.createScalingVector(1f, 1f, 1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.5416765f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(2.5f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.5416765f, AnimationHelper.createScalingVector(1f, 1f, 1f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createRotationalVector(90f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(2.5f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.5416765f, AnimationHelper.createScalingVector(1f, 1f, 1f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_CHARGE = Animation.Builder.create(0.2916767f).looping()
			.addBoneAnimation("leg1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(82.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armscrossed",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(0f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_SPRINT = Animation.Builder.create(0.25f).looping()
			.addBoneAnimation("leg1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leg2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-12.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_CAST = Animation.Builder.create(2.25f)
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(82.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armscrossed",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(0f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation ELDRITCH_LUMINARY_LARGE_CAST = Animation.Builder.create(2.5f)
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(16.81f, -26.49f, -7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-8.19f, -26.49f, -7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-8.19f, -26.49f, -7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.25f, AnimationHelper.createRotationalVector(16.81f, -26.49f, -7.67f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(80f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(16.81f, 26.49f, 7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-8.19f, 26.49f, 7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-8.19f, 26.49f, 7.67f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.25f, AnimationHelper.createRotationalVector(16.81f, 26.49f, 7.67f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armscrossed",
					new Transformation(Transformation.Targets.SCALE,
							new Keyframe(0f, AnimationHelper.createScalingVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
}
