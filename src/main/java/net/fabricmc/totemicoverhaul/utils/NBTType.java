package net.fabricmc.totemicoverhaul.utils;

public enum NBTType {
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11),
    LONG_ARRAY(12),
    ANY_NUMBER(99);

    public final int id;

    private NBTType(int id) {
        this.id = id;
    }
}
