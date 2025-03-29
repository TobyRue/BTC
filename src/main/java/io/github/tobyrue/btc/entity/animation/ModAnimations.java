package io.github.tobyrue.btc.entity.animation;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

public class ModAnimations {

	//Luminary

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
	public static final Animation ELDRITCH_LUMINARY_CAST = Animation.Builder.create(2f)
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(2f, 0f, -1f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside1",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.75f, AnimationHelper.createRotationalVector(-98.06f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-75.56f, 2.31f, -27.41f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(-2f, 0f, -1f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("armfullside2",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.75f, AnimationHelper.createRotationalVector(-97.97f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(-75.47f, -2.11f, 24.92f),
									Transformation.Interpolations.LINEAR))).build();

	//Copper Golem


	public static final Animation COPPER_WAKE_UP = Animation.Builder.create(4f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, -360f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, -720f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2.25f, AnimationHelper.createRotationalVector(0f, -1080f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3.75f, AnimationHelper.createRotationalVector(0f, -1440f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, -360f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(0f, -720f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createRotationalVector(0f, -1080f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -2f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3.75f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("legs",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(4f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_WALK = Animation.Builder.create(1f).looping()
			.addBoneAnimation("leftleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.08343333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.20834334f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.4583433f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5834334f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.625f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.7083434f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.875f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightleg",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.08343333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.125f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.20834334f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.3433333f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.375f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.4583433f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5834334f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.625f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.7083434f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.875f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_IDLE = Animation.Builder.create(3f).looping()
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(3f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_PRESS_BUTTON_UP = Animation.Builder.create(2f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-25f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-25f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(-10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(-10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-1f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("arms",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.2916767f, AnimationHelper.createRotationalVector(10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.0416767f, AnimationHelper.createRotationalVector(-7.5f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(10f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_PRESS_BUTTON_DOWN = Animation.Builder.create(2f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.2f, -1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.2f, -1f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(15f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-1f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
	public static final Animation COPPER_BUTTON_FAR_DOWN = Animation.Builder.create(2f)
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -0.5f, -1.6f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, -1.6f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("head",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(35f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(35f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rod",
					new Transformation(Transformation.Targets.TRANSLATE,
							new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createTranslationalVector(0f, -0.5f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createTranslationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("body",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(35f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.5f, AnimationHelper.createRotationalVector(35f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(2f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("rightarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(-1f, 0f, 0f),
									Transformation.Interpolations.LINEAR)))
			.addBoneAnimation("leftarm",
					new Transformation(Transformation.Targets.ROTATE,
							new Keyframe(0.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.5f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.6766666f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.75f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.8343334f, AnimationHelper.createRotationalVector(-125f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(0.9583434f, AnimationHelper.createRotationalVector(-60f, 0f, 0f),
									Transformation.Interpolations.LINEAR),
							new Keyframe(1.25f, AnimationHelper.createRotationalVector(0f, 0f, 0f),
									Transformation.Interpolations.LINEAR))).build();
}
