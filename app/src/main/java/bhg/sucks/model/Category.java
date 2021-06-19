package bhg.sucks.model;

import android.content.Context;

import bhg.sucks.R;

public enum Category {

    Weapon,
    Armor,
    Jewelry,
    Pottery,
    Legendary,
    WarWeapon,
    WarArmor,
    WarEquipment,
    WarLegedary,
    UNDEFINED;

    public String getText(Context c) {
        String[] items = c.getResources().getStringArray(R.array.array_categories);
        return items[this.ordinal()];
    }

    public boolean isWar() {
        switch (this) {
            case Weapon:
            case Armor:
            case Jewelry:
            case Pottery:
            case Legendary:
                return false;
            case WarWeapon:
            case WarArmor:
            case WarEquipment:
            case WarLegedary:
                return true;
            default:
                throw new RuntimeException("Undefined category: " + this);
        }
    }

}
