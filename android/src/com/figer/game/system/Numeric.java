package com.figer.game.system;

import com.badlogic.gdx.math.MathUtils;

public class Numeric {

    private Numeric() {}

    public static int applyConstraintToInt(int value, int min, int max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    public static int applyMinConstraintToInt(int value, int min) {
        return applyConstraintToInt(value, min, Integer.MAX_VALUE);
    }

    public static int applyMaxConstraintToInt(int value, int max) {
        return applyConstraintToInt(value, Integer.MIN_VALUE, max);
    }

    public static float applyConstraintToFloat(float value, float min, float max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    public static int incrementAndLoop(int value, int min, int max) {
        value++;
        if (value > max) {
            value = min;
        }
        return value;
    }

    public static int random(int min, int max) {
        return MathUtils.random(min, max);
    }

}