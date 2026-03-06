package tschipp.carryon.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.Packet81RightClick;
import net.minecraft.RightClickFilter;

@Mixin(Packet81RightClick.class)
public interface Packet81RightClickAccessor {

    @Accessor("filter")
    RightClickFilter getFilter();

    @Accessor("partial_tick")
    float getPartial_tick();
}