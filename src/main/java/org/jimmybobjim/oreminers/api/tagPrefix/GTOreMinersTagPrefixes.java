package org.jimmybobjim.oreminers.api.tagPrefix;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jimmybobjim.oreminers.api.propertyKeys.GTOreMinersPropertyKeys;

import java.util.Map;

public class GTOreMinersTagPrefixes {
    public static final Map<TagPrefix, VeinCoreData> VEIN_CORES = new Object2ObjectLinkedOpenHashMap<>();

    public record VeinCoreData(String stoneName, TagPrefix.OreType oreType) {}

    @SuppressWarnings("unused")
    public static final TagPrefix
            stoneVeinCore = veinCoreTagPrefix(TagPrefix.ore),
            graniteVeinCore = veinCoreTagPrefix(TagPrefix.oreGranite),
            dioriteVeinCore = veinCoreTagPrefix(TagPrefix.oreDiorite),
            andesiteVeinCore = veinCoreTagPrefix(TagPrefix.oreAndesite),
            redGraniteVeinCore = veinCoreTagPrefix(TagPrefix.oreRedGranite),
            marbleVeinCore = veinCoreTagPrefix( TagPrefix.oreMarble),
            deepslateVeinCore = veinCoreTagPrefix(TagPrefix.oreDeepslate),
            tuffVeinCore = veinCoreTagPrefix(TagPrefix.oreTuff),
            sandVeinCore = veinCoreTagPrefix(TagPrefix.oreSand),
            redSandVeinCore = veinCoreTagPrefix(TagPrefix.oreRedSand),
            gravelVeinCore = veinCoreTagPrefix(TagPrefix.oreGravel),
            basaltVeinCore = veinCoreTagPrefix(TagPrefix.oreBasalt),
            netherrackVeinCore = veinCoreTagPrefix(TagPrefix.oreNetherrack),
            blackstoneVeinCore = veinCoreTagPrefix(TagPrefix.oreBlackstone),
            endstoneVeinCore = veinCoreTagPrefix(TagPrefix.oreEndstone);

    // TODO maybe a tag for the level of the vein core?
    public static TagPrefix veinCoreTagPrefix(TagPrefix oreTag) {
        TagPrefix.OreType oreType = TagPrefix.ORES.get(oreTag);
        if (oreType == null) throw new IllegalArgumentException(oreTag + " is not contained in TagPrefix.ORES");

        TagPrefix veinCoreTag = new TagPrefix(oreTag.name + "_vein_core")
                .defaultTagPath("vein_cores/%s")
                .unformattedTagPath("vein_core_materials/" + FormattingUtil.toLowerCaseUnderscore(oreTag.name))
                .unformattedTagPath("vein_cores")
                .langValue(FormattingUtil.toEnglishName(oreTag.name) + " %s Vein Core")
                .unificationEnabled(true)
                .generationCondition(material -> material.hasProperty(GTOreMinersPropertyKeys.VEIN_CORE_BLOCK));

        VEIN_CORES.put(veinCoreTag, new VeinCoreData(oreTag.name(), oreType));
        return veinCoreTag;
    }

    @ApiStatus.Internal
    public static void init() {}
}
