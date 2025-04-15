package org.jimmybobjim.oreminers.client.render;

import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.data.pack.GTDynamicResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import org.jimmybobjim.oreminers.GTOreMiners;
import org.jimmybobjim.oreminers.common.block.VeinCoreBlock;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * essentially a clone of {@link com.gregtechceu.gtceu.client.renderer.block.OreBlockRenderer}
 * with different parameters
 */
public class VeinCoreBlockRenderer {
    private static final Set<VeinCoreBlockRenderer> MODELS = new HashSet<>();

    private final VeinCoreBlock block;

    public static void create(VeinCoreBlock block) {
        MODELS.add(new VeinCoreBlockRenderer(block));
    }

    public VeinCoreBlockRenderer(VeinCoreBlock block) {
        this.block = block;
    }

    public static void reinitModels() {
        for (VeinCoreBlockRenderer model : MODELS) {
            ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(model.block);
            ResourceLocation modelID = blockID.withPrefix("block/");
            cloneBlockModel(modelID, model.block.getOreTag(), model.block.getRecipeData().veinCoreTier());
            GTDynamicResourcePack.addBlockState(blockID, createSimpleBlock(model.block, modelID));
            GTDynamicResourcePack.addItemModel(BuiltInRegistries.ITEM.getKey(model.block.asItem()), new DelegatedModel(ModelLocationUtils.getModelLocation(model.block)));
        }
    }

    // TODO different textures for different tiers of vein cores (and just completely remake the existing textures)
    public static void cloneBlockModel(ResourceLocation modelId, TagPrefix oreTag, int veinCoreTier) {
        // read the base ore model JSON
        JsonObject original;
        try(BufferedReader reader = Minecraft.getInstance().getResourceManager().openAsReader(GTOreMiners.id("models/block/vein_core.json"))) {
            original = GsonHelper.parse(reader, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // clone it
        JsonObject newJson = original.deepCopy();
        JsonObject children = newJson.getAsJsonObject("children");
        // add the base stone texture.
        children.getAsJsonObject("base_stone").addProperty("parent", TagPrefix.ORES.get(oreTag).baseModelLocation().toString());

        GTDynamicResourcePack.addBlockModel(modelId, newJson);
    }

    public static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation modelLocation) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation));
    }
}
