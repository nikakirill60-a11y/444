package net.minecraftforge.fml.common;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
public @interface Mod {String modid();String name();String version();String dependencies();String acceptableRemoteVersions();
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface EventBusSubscriber {String modid();}}
