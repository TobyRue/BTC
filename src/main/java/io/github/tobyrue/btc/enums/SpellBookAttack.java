//package io.github.tobyrue.btc.enums;
//
//import net.minecraft.util.StringIdentifiable;
//import net.minecraft.util.function.ValueLists;
//
//import java.util.function.IntFunction;
//
//public enum SpellBookAttack {
//    FIREBALL(1),
//    DRAGON_FIREBALL(2),
//    WATER_BLAST(3),
//    WIND_CHARGE(4),
//    EARTH_PLACEHOLDER(5);
//
//    private static final IntFunction<AttackType> BY_ID = ValueLists.createIdToValueFunction((AttackType attackType) -> {
//        return attackType.id;
//    }, values(), ValueLists.OutOfBoundsHandling.ZERO);
//    public final int id;
//
//
//    SpellBookAttack(final int id) {
//        this.id = id;
//    }
//
//    public static AttackType byId(int id) {
//        return (AttackType)BY_ID.apply(id);
//    }
//}
