package tschipp.carryon;

import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.classloading.Mod;
import net.xiaoyu233.fml.config.InjectionConfig;
import net.xiaoyu233.fml.reload.event.MITEEvents;

import tschipp.carryon.api.CarryOnPluginLoader;
import tschipp.carryon.mixin.MixinPackageMarker;

import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.annotation.Nonnull;

@Mod
public class CarryOnMod extends AbstractMod {

    public CarryOnMod() {}

    public void preInit() {}

    @Nonnull
    @Override
    public InjectionConfig getInjectionConfig() {
        return InjectionConfig.Builder.of("carryon", MixinPackageMarker.class.getPackage(), MixinEnvironment.Phase.INIT).setRequired().build();
    }

    public void postInit() {
        super.postInit();
        CarryOnPluginLoader.loadFromEntrypoints();
    }

    public String modId() {
        return "carryon";
    }

    public int modVerNum() {
        return 100;
    }

    public String modVerStr() {
        return "1.0.0";
    }
}