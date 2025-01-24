package org.lanstard.doomsday.echo;

import org.lanstard.doomsday.echo.preset.*;

public enum EchoPreset {
    TIANXINGJIAN("天行健", EchoType.ACTIVE, ActivationType.AWAKENING, 100, 0) {
        @Override
        public Echo createEcho() {
            return new TianXingJianEcho();
        }
    },
    
    DUOXINPO("夺心魄", EchoType.CONTINUOUS, ActivationType.AWAKENING, 50, 5) {
        @Override
        public Echo createEcho() {
            return new DuoXinPoEcho();
        }
    },
    
    BREAKALL("破万法", EchoType.ACTIVE, ActivationType.AWAKENING, 200, 0) {
        @Override
        public Echo createEcho() {
            return new BreakAllEcho();
        }
    },
    
    SHENGSHENGBUXI("生生不息", EchoType.CONTINUOUS, ActivationType.AWAKENING, 10, 2) {
        @Override
        public Echo createEcho() {
            return new ShengShengBuXiEcho();
        }
    },
    
    SHUANGSHENGHUA("双生花", EchoType.CONTINUOUS, ActivationType.AWAKENING, 50, 2) {
        @Override
        public Echo createEcho() {
            return new ShuangShengHuaEcho();
        }
    };

    private final String name;
    private final EchoType type;
    private final ActivationType activationType;
    private final int sanityConsumption;
    private final int continuousSanityConsumption;

    EchoPreset(String name, EchoType type, ActivationType activationType, int sanityConsumption, int continuousSanityConsumption) {
        this.name = name;
        this.type = type;
        this.activationType = activationType;
        this.sanityConsumption = sanityConsumption;
        this.continuousSanityConsumption = continuousSanityConsumption;
    }

    public abstract Echo createEcho();

    public String getDisplayName() {
        return name;
    }

    public EchoType getType() {
        return type;
    }

    public ActivationType getActivationType() {
        return activationType;
    }

    public int getSanityConsumption() {
        return sanityConsumption;
    }

    public int getContinuousSanityConsumption() {
        return continuousSanityConsumption;
    }

    public static EchoPreset getByName(String name) {
        for (EchoPreset preset : values()) {
            if (preset.name.equalsIgnoreCase(name)) {
                return preset;
            }
        }
        return null;
    }
} 