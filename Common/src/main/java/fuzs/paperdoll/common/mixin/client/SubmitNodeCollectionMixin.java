package fuzs.paperdoll.common.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.paperdoll.common.client.util.PaperDollRenderer;
import fuzs.puzzleslib.common.api.client.renderer.v2.RenderStateExtraData;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.OptionalInt;

@Mixin(SubmitNodeCollection.class)
abstract class SubmitNodeCollectionMixin {

    @ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true)
    public <S> RenderType submitModel(RenderType renderType, @Local(argsOnly = true) S state) {
        if (state instanceof EntityRenderState entityRenderState) {
            OptionalInt alpha = RenderStateExtraData.getOrDefault(entityRenderState,
                    PaperDollRenderer.MODEL_ALPHA_KEY,
                    OptionalInt.empty());
            if (alpha.isPresent() && !renderType.hasBlending()) {
                if (renderType.state.textures.containsKey("Sampler0")) {
                    RenderSetup.TextureBinding textureBinding = renderType.state.textures.get("Sampler0");
                    return RenderTypes.entityTranslucent(textureBinding.location());
                }
            }
        }

        return renderType;
    }

    @ModifyVariable(method = "submitModel", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    public <S> int submitModel(int tintedColor, @Local(argsOnly = true) S state) {
        if (state instanceof EntityRenderState entityRenderState) {
            OptionalInt alpha = RenderStateExtraData.getOrDefault(entityRenderState,
                    PaperDollRenderer.MODEL_ALPHA_KEY,
                    OptionalInt.empty());
            if (alpha.isPresent()) {
                return ARGB.color(alpha.getAsInt(), tintedColor);
            }
        }

        return tintedColor;
    }
}
