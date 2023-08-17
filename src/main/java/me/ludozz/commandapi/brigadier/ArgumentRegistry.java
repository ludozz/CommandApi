package me.ludozz.commandapi.brigadier;

import me.ludozz.reflectionutils.ReflectionUtils;
import me.ludozz.reflectionutils.nms.NmsUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.function.Supplier;

@ApiStatus.Experimental
public class ArgumentRegistry {

    private static final String version = NmsUtils.getBukkitVersion();
    private static final Class<?> argumentTypeInfosClass, singletonArgumentInfoClass,
            argumentTypeInfoClass, registryMaterialsClass;
    private static final Field frozenField;
    private static final Method createSingletonArgumentInfoMethod, registerArgumentTypeMethod;
    private static final Object iRegistry;

    static {
        try {
            iRegistry = getIRegistry();
            if (NmsUtils.useNewClasses()) {
                argumentTypeInfosClass = ReflectionUtils.getClass(
                        "net.minecraft.commands.synchronization.ArgumentTypeInfos");
                if (argumentTypeInfosClass != null) {
                    registryMaterialsClass = Class.forName("net.minecraft.core.RegistryMaterials");
                    frozenField = ReflectionUtils.getField(registryMaterialsClass, "l");
                    singletonArgumentInfoClass = Class.forName(
                            "net.minecraft.commands.synchronization.SingletonArgumentInfo");
                    createSingletonArgumentInfoMethod = ReflectionUtils.getMethodByClasses(singletonArgumentInfoClass, "a",
                            Supplier.class);
                    argumentTypeInfoClass = Class.forName(
                            "net.minecraft.commands.synchronization.ArgumentTypeInfo");
                    registerArgumentTypeMethod = ReflectionUtils.getMethodByClasses(argumentTypeInfosClass, "a",
                            iRegistry.getClass(), String.class, Class.class, argumentTypeInfoClass);
                } else {
                    registryMaterialsClass = null;
                    singletonArgumentInfoClass = null;
                    argumentTypeInfoClass = null;
                    createSingletonArgumentInfoMethod = null;
                    registerArgumentTypeMethod = null;
                    frozenField = null;
                }
            } else {
                registryMaterialsClass = null;
                argumentTypeInfosClass = null;
                singletonArgumentInfoClass = null;
                argumentTypeInfoClass = null;
                createSingletonArgumentInfoMethod = null;
                registerArgumentTypeMethod = null;
                frozenField = null;
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void init() {
    }

    public static void registerArgumentType(@NotNull ArgumentTypeImp<?> argumentTypeImp, @NotNull Plugin plugin,
                                             @NotNull String id) {
        registerArgumentType(argumentTypeImp, plugin.getName(), id);
    }

    private static void registerArgumentType(@NotNull ArgumentTypeImp<?> argumentTypeImp, @NotNull String namespace,
                                            @NotNull String id) {
        if (registerArgumentTypeMethod == null) return;
        namespace = namespace.trim().toLowerCase(Locale.ENGLISH);
        id = id.trim().toLowerCase(Locale.ENGLISH);
        try {
            final Object singletonArgumentInfo = createSingletonArgumentInfoMethod.invoke(null,
                    (Supplier<?>) () -> argumentTypeImp);
            frozenField.set(iRegistry, false);
            registerArgumentTypeMethod.invoke(null, iRegistry, namespace + ":" + id, argumentTypeImp.getClass(),
                    singletonArgumentInfo);
            frozenField.set(iRegistry, true);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static Object getIRegistry() throws ReflectiveOperationException {
        Class<?> builtInRegistries;
        if (NmsUtils.useNewClasses()) {
            builtInRegistries = ReflectionUtils.getClass("net.minecraft.core.registries.BuiltInRegistries");
        } else {
            builtInRegistries = ReflectionUtils.getClass("net.minecraft.server." + version + ".BuiltInRegistries");
        }
        if (builtInRegistries != null) {
            return ReflectionUtils.getValue(builtInRegistries, "as");
        }
        final Class<?> iRegistries;
        if (NmsUtils.useNewClasses()) {
            iRegistries = ReflectionUtils.getClass("net.minecraft.core.IRegistry");
        } else {
            iRegistries = ReflectionUtils.getClass("net.minecraft.server." + version + ".IRegistry");
        }
        if (iRegistries == null) {
            throw new RuntimeException("Could not find IRegistry");
        }
        return ReflectionUtils.getValue(iRegistries, "e");
    }

}
