package org.lanstard.doomsday.common.echo;

import org.lanstard.doomsday.common.echo.preset.*;
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
    },

    SUOQIANSHAN("缩千山", EchoType.CONTINUOUS, ActivationType.AWAKENING, 100, 1) {
        @Override
        public Echo createEcho() {
            return new SuoQianShanEcho();
        }
    },

    MIQIANKUN("覔乾坤", EchoType.CONTINUOUS, ActivationType.AWAKENING, 0, 1) {
        @Override
        public Echo createEcho() {
            return new MiQianKunEcho();
        }
    },

    LIXI("离析", EchoType.ACTIVE, ActivationType.AWAKENING, 10, 0) {
        @Override
        public Echo createEcho() {
            return new LiXiEcho();
        }
    },

    YUSHENJUN("御神君", EchoType.CONTINUOUS, ActivationType.AWAKENING, 200, 1) {
        @Override
        public Echo createEcho() {
            return new YuShenJunEcho();
        }
    },
    
    ZHAOZAI("招灾", EchoType.CONTINUOUS, ActivationType.AWAKENING, 0, 1) {
        @Override
        public Echo createEcho() {
            return new ZhaoZaiEcho();
        }
    },

    HUOSHUI("祸水", EchoType.CONTINUOUS, ActivationType.AWAKENING, 0, 0) {
        @Override
        public Echo createEcho() {
            return new HuoShuiEcho();
        }
    },

    NUOYI("挪移", EchoType.ACTIVE, ActivationType.AWAKENING, 30, 0) {
        @Override
        public Echo createEcho() {
            return new NuoYiEcho();
        }
    },

    YANPIN("赝品", EchoType.ACTIVE, ActivationType.AWAKENING, 200, 0) {
        @Override
        public Echo createEcho() {
            return new YanPinEcho();
        }
    },

    KUILEI("傀儡", EchoType.ACTIVE, ActivationType.AWAKENING, 150, 0) {
        @Override
        public Echo createEcho() {
            return new KuiLeiEcho();
        }
    },

    JINFENG("劲风", EchoType.ACTIVE, ActivationType.AWAKENING, 200, 0) {
        @Override
        public Echo createEcho() {
            return new JinFengEcho();
        }
    },

    ZHIKONG("滞空", EchoType.CONTINUOUS, ActivationType.AWAKENING, 30, 1) {
        @Override
        public Echo createEcho() {
            return new ZhiKongEcho();
        }
    },

    BAOSHAN("爆闪", EchoType.ACTIVE, ActivationType.IMPLANT, 20, 0) {
        @Override
        public Echo createEcho() {
            return new BaoShanEcho();
        }
    },

    YINNI("隐匿", EchoType.CONTINUOUS, ActivationType.IMPLANT, 30, 1) {
        @Override
        public Echo createEcho() {
            return new YinNiEcho();
        }
    },

    MANLI("蛮力", EchoType.ACTIVE, ActivationType.IMPLANT, 150, 0) {
        @Override
        public Echo createEcho() {
            return new ManLiEcho();
        }
    },

    LINGSHI("灵视", EchoType.ACTIVE, ActivationType.IMPLANT, 20, 0) {
        @Override
        public Echo createEcho() {
            return new LingShiEcho();
        }
    },

    TIZUI("替罪", EchoType.ACTIVE, ActivationType.TRIGGER, 50, 0) {
        @Override
        public Echo createEcho() {
            return new TiZuiEcho();
        }
    },

    QIAOWU("巧物", EchoType.PASSIVE, ActivationType.TRIGGER, 0, 0) {
        @Override
        public Echo createEcho() {
            return new QiaoWuEcho();
        }
    },

    YUEQIAN("跃迁", EchoType.ACTIVE, ActivationType.TRIGGER, 50, 0) {
        @Override
        public Echo createEcho() {
            return new YueQianEcho();
        }
    },

    BAORAN("爆燃", EchoType.ACTIVE, ActivationType.TRIGGER, 20, 0) {
        @Override
        public Echo createEcho() {
            return new BaoRanEcho();
        }
    },

    BUMIE("不灭", EchoType.ACTIVE, ActivationType.TRIGGER, 500, 0) {
        @Override
        public Echo createEcho() {
            return new BuMieEcho();
        }
    },

    WANGYOU("忘忧", EchoType.CONTINUOUS, ActivationType.TRIGGER, 10, 2) {
        @Override
        public Echo createEcho() {
            return new WangYouEcho();
        }
    },

    ZHIYU("治愈", EchoType.CONTINUOUS, ActivationType.TRIGGER, 20, 2) {
        @Override
        public Echo createEcho() {
            return new ZhiYuEcho();
        }
    },

    JINGLEI("惊雷", EchoType.ACTIVE, ActivationType.TRIGGER, 20, 0) {
        @Override
        public Echo createEcho() {
            return new JingLeiEcho();
        }
    },

    HANBING("寒冰", EchoType.ACTIVE, ActivationType.TRIGGER, 20, 0) {
        @Override
        public Echo createEcho() {
            return new HanBingEcho();
        }
    },

    YUANWU("原物", EchoType.ACTIVE, ActivationType.TRIGGER, 10, 0) {
        @Override
        public Echo createEcho() {
            return new YuanWuEcho();
        }
    },

    WUGU("无垢", EchoType.ACTIVE, ActivationType.TRIGGER, 100, 0) {
        @Override
        public Echo createEcho() {
            return new WuGouEcho();
        }
    },

    MAOMU("茂木", EchoType.ACTIVE, ActivationType.TRIGGER, 20, 0) {
        @Override
        public Echo createEcho() {
            return new MaoMuEcho();
        }
    },

    JIAJIE("嫁接", EchoType.PASSIVE, ActivationType.AWAKENING, 0, 0) {
        @Override
        public Echo createEcho() {
            return new JiaJieEcho();
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

    EchoPreset(String name, EchoType type, ActivationType activationType, int sanityConsumption, int continuousSanityConsumption, int sanityConsumptionTrigger, int continuousSanityConsumptionTrigger) {
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