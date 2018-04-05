package io.chazza.presets.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Chazmondo
 */
public class ItemBuilder {

    private String translate(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private List<String> translate(List<String> message){
        ArrayList<String> newLore = new ArrayList<>();

        for(String msg : message)
            newLore.add(translate(msg));

        return newLore;
    }

    public ItemStack is;
    public ItemMeta im;

    public ItemBuilder(Material item, int amount){
        this.is = new ItemStack(item, amount);
        this.im = is.getItemMeta();
    }

    public ItemBuilder withName(String name){
        im.setDisplayName(translate(name));
        return this;
    }

    public ItemBuilder withLore(List<String> lore){
        im.setLore(translate(lore));
        return this;
    }

    public ItemBuilder addLore(String lore){
        im.getLore().add(translate(lore));
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        Arrays.asList(lore).forEach(s -> im.getLore().add(translate(s)));
        return this;
    }

    public ItemBuilder clearLore() {
        im.getLore().clear();
        return this;
    }

    public ItemBuilder removeLore(String lore){
        im.getLore().remove(translate(lore));
        return this;
    }

    public ItemBuilder withData(int data) {
        is.setDurability((short) data);
        return this;
    }

    public ItemBuilder withEnchant(Enchantment enchant, int level) {
        is.addEnchantment(enchant, level);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchant) {
        is.removeEnchantment(enchant);
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, InstantiationException {
        if (glowing) setNBT(DataType.STRING, "ench", ReflectionHandler.getConstructor("NBTTagList", ReflectionHandler.PackageType.MINECRAFT_SERVER, null).newInstance(null));
        else setNBT(DataType.STRING, "ench", null);
        return this;
    }

    /*
     * Warning: untested
     */
    public ItemBuilder setNBT(DataType type, String nbt, Object value) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
        InvocationTargetException, NoSuchFieldException, ClassCastException {
        Method getNMSItem = ReflectionHandler.getMethod("CraftItemStack", ReflectionHandler.PackageType.CRAFTBUKKIT_INVENTORY, "asNMSCopy", ItemStack.class);
        Object nmsItem = getNMSItem.invoke(is);
        Object tag = ReflectionHandler.getValue(nmsItem, true, "tag");
        if (tag == null) tag = ReflectionHandler.getClass("NBTTagCompund", ReflectionHandler.PackageType.MINECRAFT_SERVER);
        switch (type) {
            case FLOAT: {
                float f = (Float) value;
                tag.getClass().getDeclaredMethod("setFloat", String.class, Float.class).invoke(tag, nbt, f);
            }
            case DOUBLE: {
                double d = (Double) value;
                tag.getClass().getDeclaredMethod("setDouble", String.class, Double.class).invoke(tag, nbt, d);
            }
            case STRING: {
                String s = (String) value;
                tag.getClass().getDeclaredMethod("setString", String.class, String.class).invoke(tag, nbt, s);
            }
            case BOOLEAN: {
                boolean b = (Boolean) value;
                tag.getClass().getDeclaredMethod("setBoolean", String.class, Boolean.class).invoke(tag, nbt, b);
            }
            case INTARRAY: {
                int[] array = (int[]) value;
                tag.getClass().getDeclaredMethod("setIntArray", String.class, Integer[].class).invoke(tag, nbt, array);
            }
            case BYTEARRAY: {
                byte[] array = (byte[]) value;
                tag.getClass().getDeclaredMethod("setByteArray", String.class, Byte[].class).invoke(tag, nbt, array);
            }
        }
        this.is = (ItemStack) ReflectionHandler.getClass("CraftItemStack", ReflectionHandler.PackageType.CRAFTBUKKIT_INVENTORY).getDeclaredMethod("asCraftMirror", nmsItem.getClass()).invoke(null, nmsItem);
        return this;
    }

    /**
     * Get ItemStack from ItemBuilder
     * @return is
     */
    public ItemStack build() {
        is.setItemMeta(im);
        return is;
    }

    public enum DataType {
        FLOAT, DOUBLE, STRING, BYTEARRAY, INTARRAY, BOOLEAN
    }

    /**
     * Author: unknown, someone sent me this a while ago.
     */
    public static final class ReflectionHandler
    {
        public static Constructor<?> getConstructor(Class<?> clazz, Class<?>[] parameterTypes)
            throws NoSuchMethodException
        {
            @SuppressWarnings("rawtypes")
            Class[] primitiveTypes = DataType.getPrimitive(parameterTypes);
            for (@SuppressWarnings("rawtypes") Constructor constructor : clazz.getConstructors())
                if (DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes))
                {
                    return constructor;
                }
            throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
        }

        public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>[] parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException
        {
            return getConstructor(packageType.getClass(className), parameterTypes);
        }

        public static Object instantiateObject(Class<?> clazz, Object[] arguments)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
        {
            return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
        }

        public static Object instantiateObject(String className, PackageType packageType, Object[] arguments)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException
        {
            return instantiateObject(packageType.getClass(className), arguments);
        }

        public static Class<?> getClass(String className, PackageType type) throws ClassNotFoundException {
            return type.getClass(className);
        }

        public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException
        {
            @SuppressWarnings("rawtypes")
            Class[] primitiveTypes = DataType.getPrimitive(parameterTypes);
            for (Method method : clazz.getMethods())
                if ((method.getName().equals(methodName)) && (DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)))
                {
                    return method;
                }
            throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
        }

        public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException
        {
            return getMethod(packageType.getClass(className), methodName, parameterTypes);
        }

        public static Object invokeMethod(Object instance, String methodName, Object[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
        {
            return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
        }

        public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
        {
            return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
        }

        public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName, Object[] arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException
        {
            return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
        }

        public static Field getField(Class<?> clazz, boolean declared, String fieldName)
            throws NoSuchFieldException, SecurityException
        {
            Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
            field.setAccessible(true);
            return field;
        }

        public static Field getField(String className, PackageType packageType, boolean declared, String fieldName)
            throws NoSuchFieldException, SecurityException, ClassNotFoundException
        {
            return getField(packageType.getClass(className), declared, fieldName);
        }

        public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
        {
            return getField(clazz, declared, fieldName).get(instance);
        }

        public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException
        {
            return getValue(instance, packageType.getClass(className), declared, fieldName);
        }

        public static Object getValue(Object instance, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
        {
            return getValue(instance, instance.getClass(), declared, fieldName);
        }

        public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
        {
            getField(clazz, declared, fieldName).set(instance, value);
        }

        public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException
        {
            setValue(instance, packageType.getClass(className), declared, fieldName, value);
        }

        public static void setValue(Object instance, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
        {
            setValue(instance, instance.getClass(), declared, fieldName, value);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public enum DataType
        {
            BYTE(Byte.TYPE, Byte.class),
            SHORT(Short.TYPE, Short.class),
            INTEGER(Integer.TYPE, Integer.class),
            LONG(Long.TYPE, Long.class),
            CHARACTER(Character.TYPE, Character.class),
            FLOAT(Float.TYPE, Float.class),
            DOUBLE(Double.TYPE, Double.class),
            BOOLEAN(Boolean.TYPE, Boolean.class);

            private static final Map<Class<?>, DataType> CLASS_MAP;
            private final Class<?> primitive;
            private final Class<?> reference;

            static { CLASS_MAP = new HashMap();

                for (DataType type : values()) {
                    CLASS_MAP.put(type.primitive, type);
                    CLASS_MAP.put(type.reference, type);
                }
            }

            DataType(Class<?> primitive, Class<?> reference)
            {
                this.primitive = primitive;
                this.reference = reference;
            }

            public Class<?> getPrimitive()
            {
                return this.primitive;
            }

            public Class<?> getReference()
            {
                return this.reference;
            }

            public static DataType fromClass(Class<?> clazz)
            {
                return CLASS_MAP.get(clazz);
            }

            public static Class<?> getPrimitive(Class<?> clazz)
            {
                DataType type = fromClass(clazz);
                return type == null ? clazz : type.getPrimitive();
            }

            public static Class<?> getReference(Class<?> clazz)
            {
                DataType type = fromClass(clazz);
                return type == null ? clazz : type.getReference();
            }

            public static Class<?>[] getPrimitive(Class<?>[] classes)
            {
                int length = classes == null ? 0 : classes.length;
                Class[] types = new Class[length];
                for (int index = 0; index < length; index++) {
                    types[index] = getPrimitive(classes[index]);
                }
                return types;
            }

            public static Class<?>[] getReference(Class<?>[] classes)
            {
                int length = classes == null ? 0 : classes.length;
                Class[] types = new Class[length];
                for (int index = 0; index < length; index++) {
                    types[index] = getReference(classes[index]);
                }
                return types;
            }

            public static Class<?>[] getPrimitive(Object[] objects)
            {
                int length = objects == null ? 0 : objects.length;
                Class[] types = new Class[length];
                for (int index = 0; index < length; index++) {
                    types[index] = getPrimitive(objects[index].getClass());
                }
                return types;
            }

            public static Class<?>[] getReference(Object[] objects)
            {
                int length = objects == null ? 0 : objects.length;
                Class[] types = new Class[length];
                for (int index = 0; index < length; index++) {
                    types[index] = getReference(objects[index].getClass());
                }
                return types;
            }

            public static boolean compare(Class<?>[] primary, Class<?>[] secondary)
            {
                if ((primary == null) || (secondary == null) || (primary.length != secondary.length)) {
                    return false;
                }
                for (int index = 0; index < primary.length; index++) {
                    Class primaryClass = primary[index];
                    Class secondaryClass = secondary[index];
                    if ((!primaryClass.equals(secondaryClass)) && (!primaryClass.isAssignableFrom(secondaryClass)))
                    {
                        return false;
                    }
                }
                return true;
            }
        }

        public enum PackageType
        {
            MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
            CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
            CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"),
            CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"),
            CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"),
            CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"),
            CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"),
            CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"),
            CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"),
            CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"),
            CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"),
            CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"),
            CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"),
            CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"),
            CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"),
            CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"),
            CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"),
            CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"),
            CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"),
            CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

            private final String path;

            PackageType(String path)
            {
                this.path = path;
            }

            PackageType(PackageType parent, String path)
            {
                this(parent + "." + path);
            }

            public String getPath()
            {
                return this.path;
            }

            public Class<?> getClass(String className)
                throws ClassNotFoundException
            {
                return Class.forName(this + "." + className);
            }

            public String toString()
            {
                return this.path;
            }

            public static String getServerVersion()
            {
                return Bukkit.getServer().getClass().getPackage().getName().substring(23);
            }
        }
    }

}
