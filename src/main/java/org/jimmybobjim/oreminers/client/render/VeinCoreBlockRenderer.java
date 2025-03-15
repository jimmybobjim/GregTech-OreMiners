package org.jimmybobjim.oreminers.client.render;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
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
import org.jimmybobjim.oreminers.api.propertyKeys.VeinCoreBlockProperty;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOMPropertyKeys;
import org.jimmybobjim.oreminers.api.tagPrefix.GTOMTagPrefixes;

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

    private final MaterialBlock block;

    public static void create(MaterialBlock block) {
        MODELS.add(new VeinCoreBlockRenderer(block));
    }

    public VeinCoreBlockRenderer(MaterialBlock block) {
        this.block = block;
    }

    public static void reinitModels() {
        for (VeinCoreBlockRenderer model : MODELS) {
            ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(model.block);
            ResourceLocation modelID = blockID.withPrefix("block/");
            cloneBlockModel(modelID, model.block.tagPrefix, model.block.material);
            GTDynamicResourcePack.addBlockState(blockID, createSimpleBlock(model.block, modelID));
            GTDynamicResourcePack.addItemModel(BuiltInRegistries.ITEM.getKey(model.block.asItem()), new DelegatedModel(ModelLocationUtils.getModelLocation(model.block)));
        }
    }

    // TODO different textures for different tiers of vein cores (and just completely remake the existing textures)
    public static void cloneBlockModel(ResourceLocation modelId, TagPrefix prefix, Material material) {
        VeinCoreBlockProperty property = material.getProperty(GTOMPropertyKeys.VEIN_CORE_BLOCK);
        Preconditions.checkNotNull(property, "material %s has no vein core property, but needs one for a vein core model!".formatted(material.getName()));

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
        children.getAsJsonObject("base_stone").addProperty("parent", GTOMTagPrefixes.VEIN_CORES.get(prefix).oreType().baseModelLocation().toString());

        GTDynamicResourcePack.addBlockModel(modelId, newJson);
    }

    public static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation modelLocation) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation));
    }
}
