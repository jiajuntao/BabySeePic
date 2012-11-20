package cn.babysee.picture;

public class ResourcesHelper {

    public static int[] getPicList(int item) {

        switch (item) {
            case 0:
                return new int[] { R.drawable.animal_1_duck, R.drawable.animal_1_elephant,
                        R.drawable.animal_2_cow, R.drawable.animal_2_sheep,
                        R.drawable.animal_3_cock, R.drawable.animal_3_dog, R.drawable.animal_4_cat,
                        R.drawable.animal_4_donkey, R.drawable.animal_5_bird,
                        R.drawable.animal_5_pigeon, R.drawable.animal_6_horse,
                        R.drawable.animal_6_tiger, };
            case 1:
                return new int[] { R.drawable.fruit_1_pineapple, R.drawable.fruit_1_strawberry,
                        R.drawable.fruit_2_mango, R.drawable.fruit_2_orange,
                        R.drawable.fruit_3_apple, R.drawable.fruit_3_grape,
                        R.drawable.fruit_4_cherry, R.drawable.fruit_4_pear,
                        R.drawable.fruit_5_banana, R.drawable.fruit_5_watermelon,
                        R.drawable.fruit_6_carambola, R.drawable.fruit_6_peach,
                        R.drawable.fruit_7_apple, R.drawable.fruit_7_apricots,
                        R.drawable.fruit_8_bananas, R.drawable.fruit_8_cantaloupe,
                        R.drawable.fruit_9_carambola, R.drawable.fruit_9_cherry,
                        R.drawable.fruit_10_coconut, R.drawable.fruit_10_dates,
                        R.drawable.fruit_11_grapefruit, R.drawable.fruit_11_grapes,
                        R.drawable.fruit_12_hawthorn, R.drawable.fruit_12_kiwifruit,
                        R.drawable.fruit_13_lemon, R.drawable.fruit_13_loquat,
                        R.drawable.fruit_14_mango, R.drawable.fruit_14_olives,
                        R.drawable.fruit_15_peaches, R.drawable.fruit_15_pears,
                        R.drawable.fruit_16_persimmon, R.drawable.fruit_16_pineapple,
                        R.drawable.fruit_17_plums, R.drawable.fruit_17_pomegranate,
                        R.drawable.fruit_18_strawberry, R.drawable.fruit_18_watermelon,

                };
            case 2:
                return new int[] { R.drawable.vegetable_1_celery, R.drawable.vegetable_1_tomato,
                        R.drawable.vegetable_2_cabbage, R.drawable.vegetable_2_onion,
                        R.drawable.vegetable_3_carrot, R.drawable.vegetable_3_melon,
                        R.drawable.vegetable_4_cucumber, R.drawable.vegetable_4_pepper,
                        R.drawable.vegetable_5_eggplant, R.drawable.vegetable_5_lotusroot,
                        R.drawable.vegetable_6_cauliflowe, R.drawable.vegetable_6_ginger, };
            case 3:
                return new int[] { R.drawable.transport_1_motorcycle, R.drawable.transport_1_plane,
                        R.drawable.transport_2_ambulance, R.drawable.transport_2_bicycle,
                        R.drawable.transport_3_bus, R.drawable.transport_3_excavators,
                        R.drawable.transport_4_fireengines, R.drawable.transport_4_ship,
                        R.drawable.transport_5_taxi, R.drawable.transport_5_train,
                        R.drawable.transport_6_carriage, R.drawable.transport_6_harvester, };

            default:
                break;
        }
        return null;
    }

    public static int[] getSoundList(int item) {

        switch (item) {
            case 0:
                return new int[] { R.raw.animal_1_duck, R.raw.animal_1_elephant,
                        R.raw.animal_2_cow, R.raw.animal_2_sheep, R.raw.animal_3_cock,
                        R.raw.animal_3_dog, R.raw.animal_4_cat, R.raw.animal_4_donkey,
                        R.raw.animal_5_bird, R.raw.animal_5_pigeon, R.raw.animal_6_horse,
                        R.raw.animal_6_tiger, };
            case 1:
            case 2:
                return new int[] { R.raw.box_click, R.raw.box_jump1, R.raw.box_jump2, R.raw.jump,
                        R.raw.touch, R.raw.touches, };
            case 3:
                return new int[] { R.raw.radom_sound_1_, R.raw.radom_sound_2_,
                        R.raw.radom_sound_5_, R.raw.box_click, R.raw.box_jump1, R.raw.box_jump2,
                        R.raw.jump, R.raw.touch, R.raw.touches, R.raw.radom_sound_8_,
                        R.raw.radom_sound_9_, R.raw.radom_sound_10_, R.raw.radom_sound_11_,
                        R.raw.radom_sound_12_, };

            default:
                break;
        }
        return null;
    }
}
